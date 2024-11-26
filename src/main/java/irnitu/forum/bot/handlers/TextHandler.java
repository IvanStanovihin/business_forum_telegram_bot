package irnitu.forum.bot.handlers;

import irnitu.forum.bot.models.common.ResponseForUser;
import irnitu.forum.bot.models.entities.BotState;
import irnitu.forum.bot.models.entities.ContestWinner;
import irnitu.forum.bot.models.entities.PhraseInputLog;
import irnitu.forum.bot.services.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

/**
 * Класс для обработки текста, введённого пользователем
 */
@Slf4j
@Service
public class TextHandler {

    private final BotStatesService botStatesService;
    private final UserService userService;
    private final FeedbackService feedbackService;

    private final SecretPhraseContestService secretPhraseContestService;

    private final ContestWinnerService contestWinnerService;

    private final PhraseInputLoggerService phraseInputLoggerService;

    public TextHandler(BotStatesService botStatesService,
                       UserService userService,
                       FeedbackService feedbackService,
                       SecretPhraseContestService secretPhraseContestService,
                       ContestWinnerService contestWinnerService,
                       PhraseInputLoggerService phraseInputLoggerService
    ) {
        this.botStatesService = botStatesService;
        this.userService = userService;
        this.feedbackService = feedbackService;
        this.secretPhraseContestService = secretPhraseContestService;
        this.contestWinnerService = contestWinnerService;
        this.phraseInputLoggerService = phraseInputLoggerService;
    }

    public ResponseForUser handleText(Update update) {
        log.info("HandleText");
        BotState botState = botStatesService.getState(update.getMessage().getFrom().getUserName());
        botStatesService.resetState(update.getMessage().getFrom().getUserName());
        switch (botState.getState()) {
            case WAIT_REGISTRATION:
                return registrationText(update);
            case WAIT_FEEDBACK:
                return feedbackText(update, botState);
            case WAIT_PHRASE:
                return phraseText(update);
            case WAIT_WORD:
                return wordText(update);

            default:
                log.error("Unrecognized user text {}", update.getMessage().getText());
        }
        return null;
    }

    /**
     * Метод для обработки текста, который пользователь ввёл при отгадывании фразы
     */
    private ResponseForUser phraseText(Update update) {
        SendMessage sendMessage = new SendMessage();
        Long chatId = update.getMessage().getChatId();
        String tgUserName = update.getMessage().getFrom().getUserName();
        sendMessage.setChatId(String.valueOf(chatId));
        String inputPhrase = update.getMessage().getText();
        log.info("phraseText - {}", inputPhrase);

        List<ContestWinner> winners = contestWinnerService.getWinners();

        if (winners.isEmpty()) {
            boolean phraseIsCorrect = secretPhraseContestService.checkPhrase(tgUserName, inputPhrase);
            if (phraseIsCorrect) {
                sendMessage.setText("Вы угадали фразу! ПОЗДРАВЛЯЕМ!\nДля получение приза обратитесь к @elenagoncharova18");
            } else {
                String userName = update.getMessage().getFrom().getUserName();
                PhraseInputLog phraseInputLog = new PhraseInputLog()
                        .setInputPhrase(inputPhrase)
                        .setUserTgName(userName);

                phraseInputLoggerService.addInputPhraseLog(phraseInputLog);

                sendMessage.setText("Вы не угадали фразу :(\nПопробуйте еще раз!");
            }
        } else {
            StringBuilder result = new StringBuilder();
            result.append("Фразу уже угадали @");
            winners.forEach(winner -> result.append(winner.getStudent().getTelegramUserName()));

            sendMessage.setText(result.toString());

        }
        botStatesService.setPhraseState(update.getMessage().getFrom().getUserName());
        return new ResponseForUser(sendMessage);
    }

    /**
     * Метод для обработки текста, который пользователь ввёл при вводе слов из фразы
     */
    private ResponseForUser wordText(Update update) {
        SendMessage sendMessage = new SendMessage();
        Long chatId = update.getMessage().getChatId();
        sendMessage.setChatId(String.valueOf(chatId));

        String inputPhrase = update.getMessage().getText();
        boolean wordIsCorrect = secretPhraseContestService.checkWord(inputPhrase);
        if (wordIsCorrect) {
            sendMessage.setText("Вы ввели слово которое есть в фразе!");
        } else {
            sendMessage.setText("Этого слова нет в фразе, попробуйте еще раз!");
        }
        botStatesService.setWordState(update.getMessage().getFrom().getUserName());
        return new ResponseForUser(sendMessage);
    }

    /**
     * Метод для обработки текста, который пользователь ввёл для регистрации в боте
     */
    private ResponseForUser registrationText(Update update) {
        SendMessage sendMessage = new SendMessage();
        Long chatId = update.getMessage().getChatId();
        sendMessage.setChatId(String.valueOf(chatId));
        boolean registerSuccess = userService.register(update);
        if (!registerSuccess) {
            sendMessage.setText("Ошибка при регистрации! Не получилось распознать ваше @Имя пользователя");
        }
        sendMessage.setText("Вы успешно зарегистрированы! Можете пользоваться ботом");
        return new ResponseForUser(sendMessage);
    }

    /**
     * Обработка состояния бота, когда он ожидает от пользователя ввода отзыва.
     */
    private ResponseForUser feedbackText(Update update, BotState botState) {
        log.info("HandleText handleFeedbackText");
        String userTelegramName = update.getMessage().getFrom().getUserName();
        String feedback = update.getMessage().getText();
        Long educationBlockId = botState.getEducationBlockId();
        feedbackService.addFeedback(userTelegramName, educationBlockId, feedback);
        long chatId = update.getMessage().getChatId();
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setText("Спасибо за отзыв!");
        return new ResponseForUser(sendMessage);
    }
}
