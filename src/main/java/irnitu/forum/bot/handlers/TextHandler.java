package irnitu.forum.bot.handlers;

import irnitu.forum.bot.models.common.ResponseForUser;
import irnitu.forum.bot.models.entities.BotState;
import irnitu.forum.bot.services.BotStatesService;
import irnitu.forum.bot.services.FeedbackService;
import irnitu.forum.bot.services.SecretPhraseContestService;
import irnitu.forum.bot.services.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

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

    public TextHandler(BotStatesService botStatesService,
                       UserService userService,
                       FeedbackService feedbackService,
                       SecretPhraseContestService secretPhraseContestService
    ) {
        this.botStatesService = botStatesService;
        this.userService = userService;
        this.feedbackService = feedbackService;
        this.secretPhraseContestService = secretPhraseContestService;
    }

    public ResponseForUser handleText(Update update){
        log.info("HandleText");
        BotState botState = botStatesService.getState(update.getMessage().getFrom().getUserName());
        botStatesService.resetState(update.getMessage().getFrom().getUserName());
        switch (botState.getState()){
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
        sendMessage.setChatId(String.valueOf(chatId));
        String inputPhrase = update.getMessage().getText();
        log.info("phraseText - {}", inputPhrase);

        boolean phraseIsCorrect = secretPhraseContestService.checkPhrase(chatId, inputPhrase);

        // TODO дергать ручку с проверкой введеной фразы (должна возвращать true/fasle),
        // также нужно учесть что если фразу уже отгадали до этого нужно вывести сообщение об этом
        // также нужно учесть что пользователь может несколько раз подряд фразу (несколько сообщений подряд)
        // TODO формулировка
        if (phraseIsCorrect) {
            sendMessage.setText("Вы ввели корректную фразу");
            //TODO дергать ручку с установкой ника победителя
        } else {
            sendMessage.setText("Вы ввели не корректную фразу, попробуйте еще раз");
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
            //TODO дергать ручку с проверкой введеного слова (должна возвращать true/fasle)
            //также нужно учесть что пользователь может несколько раз подряд фразу (несколько сообщений подряд)
            // TODO формулировка
            sendMessage.setText("Вы ввели слово которое есть во фразе");
        } else {
            sendMessage.setText("Этого слова нет во фразе");
        }
        botStatesService.setWordState(update.getMessage().getFrom().getUserName());
        return new ResponseForUser(sendMessage);
    }

    /**
     * Метод для обработки текста, который пользователь ввёл для регистрации в боте
     */
    private ResponseForUser registrationText(Update update){
        SendMessage sendMessage = new SendMessage();
        Long chatId = update.getMessage().getChatId();
        sendMessage.setChatId(String.valueOf(chatId));
        boolean registerSuccess = userService.register(update);
        if (!registerSuccess){
            sendMessage.setText("Ошибка при регистрации! Не получилось распознать ваше @Имя пользователя");
        }
        sendMessage.setText("Вы успешно зарегистрированы! Можете пользоваться ботом");
        return new ResponseForUser(sendMessage);
    }

    /**
     * Обработка состояния бота, когда он ожидает от пользователя ввода отзыва.
     */
    private ResponseForUser feedbackText(Update update, BotState botState){
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
