package irnitu.forum.bot.handlers;

import irnitu.forum.bot.buttons.Keyboards;
import irnitu.forum.bot.constants.UserCommands;
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

    public CommandHandler(Keyboards keyboards,
                          UserService userService,
                          BotStatesService botStatesService) {
        this.keyboards = keyboards;
        this.userService = userService;
        this.botStatesService = botStatesService;
    }

    public SendMessage handleCommand(Update update) {
        log.info("HandleCommand");
        String message = update.getMessage().getText();
        botStatesService.resetState(update.getMessage().getFrom().getUserName());
        switch (message) {
            case UserCommands.REGISTRATION:
                return registrationCommand(update);
            case UserCommands.HELLO:
                return helloCommand(update);
            case UserCommands.HELP:
                return helpCommand(update);
            case UserCommands.SCHEDULE:
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

    private SendMessage consultationsCommand(Update update) {
        // Проверка регистрации пользователя
        if (!userService.isRegistered(update)) {
            return userService.registrationError(update);
        }
        return null;
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
        InlineKeyboardMarkup educationBlocksMarkup =  keyboards.educationSectionsKeyboard(update);
        long chatId = update.getMessage().getChatId();
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText("Выберите блок форума на который хотите оставить отзыв:");
        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setReplyMarkup(educationBlocksMarkup);
        return sendMessage;
    }

    private SendMessage helpCommand(Update update) {
        // Проверка регистрации пользователя
        if (!userService.isRegistered(update)) {
            return userService.registrationError(update);
        }
        return null;
    }

    private SendMessage helloCommand(Update update) {
        // Проверка регистрации пользователя
        if (!userService.isRegistered(update)) {
            return userService.registrationError(update);
        }
        return null;
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
