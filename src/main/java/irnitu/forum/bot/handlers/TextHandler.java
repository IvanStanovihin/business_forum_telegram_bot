package irnitu.forum.bot.handlers;

import irnitu.forum.bot.models.entities.BotState;
import irnitu.forum.bot.services.BotStatesService;
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

    public TextHandler(BotStatesService botStatesService,
                       UserService userService) {
        this.botStatesService = botStatesService;
        this.userService = userService;
    }

    public SendMessage handleText(Update update){
        log.info("HandleText");
        BotState botState = botStatesService.getState(update.getMessage().getFrom().getUserName());
        botStatesService.resetState(update.getMessage().getFrom().getUserName());
        switch (botState.getState()){
            case WAIT_REGISTRATION:
                return registrationText(update);
            case WAIT_FEEDBACK:
                return feedbackText(update);
            default:
                log.error("Unrecognized user text {}", update.getMessage().getText());
        }
        return null;
    }

    /**
     * Метод для обработки текста, который пользователь ввёл для регистрации в боте
     */
    private SendMessage registrationText(Update update){
        userService.register(update);
        SendMessage sendMessage = new SendMessage();
        Long chatId = update.getMessage().getChatId();
        sendMessage.setText("Вы успешно зарегистрированы! Можете пользоваться ботом");
        sendMessage.setChatId(String.valueOf(chatId));
        return sendMessage;
    }

    private SendMessage feedbackText(Update update){
        log.info("HandleText handleFeedbackText");
        return null;
    }
}
