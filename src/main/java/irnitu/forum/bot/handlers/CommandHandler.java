package irnitu.forum.bot.handlers;

import irnitu.forum.bot.buttons.Keyboards;
import irnitu.forum.bot.constants.UserCommands;
import irnitu.forum.bot.services.ConsultationTimeSlotService;
import irnitu.forum.bot.services.ForumScheduleService;
import irnitu.forum.bot.services.UserService;
import irnitu.forum.bot.services.BotStatesService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

@Slf4j
@Service
public class CommandHandler {

    private final Keyboards keyboards;
    private final UserService userService;
    private final BotStatesService botStatesService;
    private final ForumScheduleService forumScheduleService;
    private final ConsultationTimeSlotService consultationTimeSlotService;

    public CommandHandler(Keyboards keyboards,
                          UserService userService,
                          BotStatesService botStatesService,
                          ForumScheduleService forumScheduleService,
                          ConsultationTimeSlotService consultationTimeSlotService) {
        this.keyboards = keyboards;
        this.userService = userService;
        this.botStatesService = botStatesService;
        this.forumScheduleService = forumScheduleService;
        this.consultationTimeSlotService = consultationTimeSlotService;
    }

    public SendMessage handleCommand(Update update) {
        log.info("HandleCommand");
        String message = update.getMessage().getText();
        botStatesService.resetState(update.getMessage().getFrom().getUserName());
        switch (message) {
            case UserCommands.REGISTRATION:
                return registrationCommand(update);
            case UserCommands.HELP:
                return helpCommand(update);
            case UserCommands.FORUM_SCHEDULE:
                return forumSchedule(update);
            case UserCommands.ADD_CONSULTATION:
                return scheduleCommand(update);
            case UserCommands.FEEDBACK:
                return feedbackCommand(update);
            case UserCommands.USER_CONSULTATIONS:
                return consultationsCommand(update);
            default:
                log.error("Unexpected command entered!");
                return null;
        }
    }

    /**
     * Метод для кнопки "программа форума".
     */
    private SendMessage forumSchedule(Update update) {
        long chatId = update.getMessage().getChatId();
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));
        String schedule = forumScheduleService.getForumScheduleMessage();
        sendMessage.setText(schedule);
        return sendMessage;
    }


    /**
     * Метод для обработки кнопки "мои консультации".
     */
    private SendMessage consultationsCommand(Update update) {
        // Проверка регистрации пользователя
        if (!userService.isRegistered(update)) {
            return userService.registrationError(update);
        }
        String telegramUsername = update.getMessage().getFrom().getUserName();
        String userConsultations = consultationTimeSlotService.getAllUserConsultations(telegramUsername);
        long chatId = update.getMessage().getChatId();
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setText(userConsultations);
        return sendMessage;
    }

    /**
     * Метод для обработки нажатия пользователем на кнопку "Оставить отзыв". В методе создаётся
     * клавиатура со всеми блоками форума на который пользователь может написать отзыв.
     */
    private SendMessage feedbackCommand(Update update) {
        // Проверка регистрации пользователя
        if (!userService.isRegistered(update)) {
            return userService.registrationError(update);
        }
        InlineKeyboardMarkup educationBlocksMarkup =  keyboards.educationSectionsKeyboard();
        long chatId = update.getMessage().getChatId();
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText("Выберите блок форума на который хотите оставить отзыв:");
        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setReplyMarkup(educationBlocksMarkup);
        return sendMessage;
    }

    private SendMessage helpCommand(Update update) {
        long chatId = update.getMessage().getChatId();
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setText("При помощи данного боты вы можете: \n" +
                "\n* Записаться на консультацию к эксперту" +
                "\n* Посмотреть на какие консультации вы уже записались" +
                "\n* Оставить отзыв на любой из блоков форума" +
                "\n\n Если возникнут какие-либо вопросы по работе бота, обращайтесь к https://t.me/TelegramUser");
        return sendMessage;
    }

    private SendMessage registrationCommand(Update update) {
        String userTelegramName = update.getMessage().getFrom().getUserName();
        botStatesService.setRegistrationState(userTelegramName);
        long chatId = update.getMessage().getChatId();
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText("Введите ФИО и номер телефона:");
        sendMessage.setChatId(String.valueOf(chatId));
        return sendMessage;
    }

    /**
     * Метод, который отправляет пользователю список кнопок для выбора эксперта на консультацию
     * @param update
     * @return
     */
    private SendMessage scheduleCommand(Update update){
        log.info("HandleButton scheduleCommand");
        // Проверка регистрации пользователя
        if (!userService.isRegistered(update)) {
            return userService.registrationError(update);
        }
        long chatId = update.getMessage().getChatId();
        SendMessage sendMessage = new SendMessage();
        InlineKeyboardMarkup businessExpertKeyboard = keyboards.expertsKeyboard();
        sendMessage.setText("Выберите эксперта, к которому хотите записаться на консультацию");
        sendMessage.setReplyMarkup(businessExpertKeyboard);
        sendMessage.setChatId(String.valueOf(chatId));
        return sendMessage;
    }

}
