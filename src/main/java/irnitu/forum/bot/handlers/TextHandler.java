package irnitu.forum.bot.handlers;

import irnitu.forum.bot.models.common.ResponseForUser;
import irnitu.forum.bot.models.entities.BotState;
import irnitu.forum.bot.services.BotStatesService;
import irnitu.forum.bot.services.FeedbackService;
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

    public TextHandler(BotStatesService botStatesService,
                       UserService userService,
                       FeedbackService feedbackService) {
        this.botStatesService = botStatesService;
        this.userService = userService;
        this.feedbackService = feedbackService;
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
            default:
                log.error("Unrecognized user text {}", update.getMessage().getText());
        }
        return null;
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
