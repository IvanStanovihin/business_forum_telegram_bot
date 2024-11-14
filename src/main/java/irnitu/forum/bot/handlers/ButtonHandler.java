package irnitu.forum.bot.handlers;

import irnitu.forum.bot.buttons.Keyboards;
import irnitu.forum.bot.constants.UserCommands;
import irnitu.forum.bot.models.common.ResponseForUser;
import irnitu.forum.bot.services.BotStatesService;
import irnitu.forum.bot.services.ConsultationTimeSlotService;
import irnitu.forum.bot.services.FeedbackService;
import irnitu.forum.bot.services.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

/**
 * Главный класс для обработки нажатий на кнопки
 */
@Slf4j
@Component
public class ButtonHandler {

    private final Keyboards keyboards;
    private final ConsultationTimeSlotService consultationTimeSlotService;
    private final BotStatesService botStatesService;
    private final UserService userService;
    private final FeedbackService feedbackService;

    public ButtonHandler(Keyboards keyboards,
                         ConsultationTimeSlotService consultationTimeSlotService,
                         BotStatesService botStatesService,
                         UserService userService,
                         FeedbackService feedbackService) {
        this.keyboards = keyboards;
        this.consultationTimeSlotService = consultationTimeSlotService;
        this.botStatesService = botStatesService;
        this.userService = userService;
        this.feedbackService = feedbackService;
    }

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
            default:
                log.error("Unexpected button pressed!");
                return null;
        }
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
