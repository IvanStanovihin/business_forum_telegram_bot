package irnitu.forum.bot.handlers;

import irnitu.forum.bot.buttons.Keyboards;
import irnitu.forum.bot.constants.UserCommands;
import irnitu.forum.bot.models.common.ResponseForUser;
import irnitu.forum.bot.models.entities.ContestWinner;
import irnitu.forum.bot.services.BotStatesService;
import irnitu.forum.bot.services.ConsultationTimeSlotService;
import irnitu.forum.bot.services.ContestWinnerService;
import irnitu.forum.bot.services.SecretPhraseContestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

/**
 * Главный класс для обработки нажатий на кнопки
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ButtonHandler {

    private final Keyboards keyboards;
    private final ConsultationTimeSlotService consultationTimeSlotService;
    private final BotStatesService botStatesService;
    private final SecretPhraseContestService secretPhraseContestService;

    private final ContestWinnerService contestWinnerService;


    public ResponseForUser handleButton(Update update){
        log.info("HandleButton ");
        String messageCallback = update.getCallbackQuery().getData().split("_")[0];
        botStatesService.resetState(update.getCallbackQuery().getFrom().getUserName());
        log.info("HandleButton message callBack {}", messageCallback);
        switch (messageCallback){
            case UserCommands.EXPERT:
                return listOfExpertButtons(update);
            case UserCommands.TIME_SLOT:
                return timeslotButton(update);
            case UserCommands.FEEDBACK_EDUCATION_BLOCK:
                return educationBlockButton(update);
            case UserCommands.CONTEST_GUESS_WORD:
                return contestGuessWordButton(update);
            case UserCommands.CONTEST_GUESS_PHRASE:
                return contestGuessPhraseButton(update);
            default:
                log.error("Unexpected button pressed!");
                return null;
        }
    }

    /**
     * Кнопка "Введите слово" (часть конкурса "Угадай фразу")
     */
    private ResponseForUser contestGuessWordButton(Update update) {
        String userTelegramName = update.getCallbackQuery().getFrom().getUserName();
        long chatId = update.getCallbackQuery().getMessage().getChatId();
        SendMessage sendMessage = new SendMessage();
        boolean allWordsIsFound = secretPhraseContestService.isAllWordsFound();
        if (!allWordsIsFound) {
            sendMessage.setText("Введите слово");
            botStatesService.setWordState(userTelegramName);
        } else {
            sendMessage.setText("Все слова введены, теперь вам нужно угадать фразу!");
        }
        sendMessage.setChatId(String.valueOf(chatId));

        return new ResponseForUser(sendMessage);
    }

    /**
     * Кнопка "Введите фразу" (часть конкурса "Угадай фразу")
     */
    private ResponseForUser contestGuessPhraseButton(Update update) {
        String userTelegramName = update.getCallbackQuery().getFrom().getUserName();
        long chatId = update.getCallbackQuery().getMessage().getChatId();
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));
        boolean allWordsIsFound = secretPhraseContestService.isAllWordsFound();

        List<ContestWinner> winners = contestWinnerService.getWinners();

        if (winners.isEmpty()) {
            if (allWordsIsFound) {
                sendMessage.setText("Вам нужно угадать фразу состоящую из следующих слов:\n(подсказка - вы можете не заморачиваться на счёт знаков препинания, главное соблюсти правильную последовательность слов)\n\nреализовать, бизнеса, них, равно, уже, все, экстремальный, особенный, проект, на, вы, рисках, вид, чтобы, о, идете, старте, как, спорта, предпринимательство, знаете, такой, свой, это, технологическое, вообще, и, вид, на, проект");
                botStatesService.setPhraseState(userTelegramName);
            } else {
                sendMessage.setText("Вы не можете отгадывать фразу, потому что ввели не все слова");
            }

        } else {
            StringBuilder result = new StringBuilder();
            result.append("Фразу уже отгадали, конкурс завершен. Победитель - @");
            winners.forEach(winner -> result.append(winner.getStudent().getTelegramUserName()));
            result.append("\n\nФраза была - \"Технологическое предпринимательство - это вообще особенный вид бизнеса. Такой, как экстремальный вид спорта. Вы знаете о рисках уже на старте и все равно вы на них идете, чтобы реализовать свой проект\"");
            sendMessage.setText(result.toString());

        }
        return new ResponseForUser(sendMessage);
    }

    /**
     * Кнопка выбора блока форума, на которую пользователь хочет оставить отзыв.
     */
    private ResponseForUser educationBlockButton(Update update) {
        String userTelegramName = update.getCallbackQuery().getFrom().getUserName();
        String educationBlockId = update.getCallbackQuery().getData().split("_")[1];
        botStatesService.setFeedbackState(userTelegramName, educationBlockId);
        log.info("feedbackBlockCommand educationBlockId {}", educationBlockId);
        long chatId = update.getCallbackQuery().getMessage().getChatId();
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setText("Введите ваш отзыв:");
        return new ResponseForUser(sendMessage);
    }

    /**
     * Метод который отправляет пользователю клавиатуру из тайм-слотов на консультацию,
     * конкретного эксперта
     * @param update
     * @return
     */
    private ResponseForUser listOfExpertButtons(Update update){
        log.info("HandleButton businessExpertCommand");
        long chatId = update.getCallbackQuery().getMessage().getChatId();
        String expertName = update.getCallbackQuery().getData().split("_")[1];
        log.info("HandleButton \ngetData {}", expertName);
        SendMessage sendMessage = keyboards.expertFreeTimeSlotKeyboard(expertName);
        sendMessage.setChatId(String.valueOf(chatId));
        return new ResponseForUser(sendMessage);
    }

    /**
     * Метод для записи пользователя на консультацию к эксперту
     * @param update
     * @return
     */
    private ResponseForUser timeslotButton(Update update){
        log.info("HandleButton takeExpertTimeslot");
        long chatId = update.getCallbackQuery().getMessage().getChatId();
        Long timeslotId = Long.parseLong(update.getCallbackQuery().getData().split("_")[1]);
        Long expertId = Long.parseLong(update.getCallbackQuery().getData().split("_")[2]);
        String telegramUsername = update.getCallbackQuery().getFrom().getUserName();
        log.info("HandleButton takeExpertTimeslot timeslot id {}", timeslotId);
        if (consultationTimeSlotService.isUserAlreadyTakeTimeslot(telegramUsername, expertId)){
            //Проверка чтобы пользователь не записался к эксперту несколько раз
            SendMessage sendMessage = new SendMessage();
            sendMessage.setText("Вы уже записаны на консультацию к данному эксперту. Нельзя " +
                    "записаться к одному эксперту несколько раз");
            sendMessage.setChatId(String.valueOf(chatId));
            return new ResponseForUser(sendMessage);
        }
        consultationTimeSlotService.takeTimeslot(timeslotId, telegramUsername, expertId);
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText("Вы записаны на консультацию!");
        sendMessage.setChatId(String.valueOf(chatId));
        return new ResponseForUser(sendMessage);
    }
}
