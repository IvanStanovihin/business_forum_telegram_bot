package irnitu.forum.bot.handlers;

import irnitu.forum.bot.buttons.Keyboards;
import irnitu.forum.bot.constants.UserCommands;
import irnitu.forum.bot.services.UserService;
import irnitu.forum.bot.states.BotStates;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Slf4j
@Service
public class CommandHandler {

    private final Keyboards keyboards;
    private final UserService userService;

    public CommandHandler(Keyboards keyboards,
                          UserService userService) {
        this.keyboards = keyboards;
        this.userService = userService;
    }

    public SendMessage handleCommand(Update update) {
        log.info("HandleCommand");
        String message = update.getMessage().getText();

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
                SendMessage sendMessage = checkState(update);
                if (sendMessage != null){
                    return sendMessage;
                }
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

    private SendMessage feedbackCommand(Update update) {
        // Проверка регистрации пользователя
        if (!userService.isRegistered(update)) {
            return userService.registrationError(update);
        }
        return null;
    }

    private SendMessage scheduleCommand(Update update) {
        // Проверка регистрации пользователя
        if (!userService.isRegistered(update)) {
            return userService.registrationError(update);
        }
        return null;
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
        BotStates.WAIT_REGISTRATION = true;
        long chatId = update.getMessage().getChatId();
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText("Введите ФИО и номер телефона:");
        sendMessage.setChatId(String.valueOf(chatId));
        return sendMessage;
    }

    private SendMessage checkState(Update update){
        long chatId = update.getMessage().getChatId();
        if (BotStates.WAIT_REGISTRATION){
            userService.register(update);
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(String.valueOf(chatId));
            BotStates.WAIT_REGISTRATION = false;
            sendMessage.setText("Вы зарегистрированы! Можете пользоваться ботом");
            return sendMessage;
        }else{
            return null;
        }
    }
}
