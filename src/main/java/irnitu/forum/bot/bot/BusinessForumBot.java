package irnitu.forum.bot.bot;

import irnitu.forum.bot.buttons.Keyboards;
import irnitu.forum.bot.configuration.BotConfig;
import irnitu.forum.bot.constants.UserCommands;
import irnitu.forum.bot.handlers.ButtonHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

/**
 * Главный класс обработчик команд из телеграмма
 */
@Slf4j
@Component
public class BusinessForumBot extends TelegramLongPollingBot {

    private final BotConfig botConfig;
    private final ButtonHandler buttonHandler;
    private final Keyboards keyboards;

    public BusinessForumBot(BotConfig botConfig,
                            ButtonHandler buttonHandler,
                            Keyboards keyboards) {
        this.botConfig = botConfig;
        this.buttonHandler = buttonHandler;
        this.keyboards = keyboards;
    }

    @Override
    public String getBotToken() {
        return botConfig.getToken();
    }

    @Override
    public String getBotUsername() {
        return botConfig.getBotName();
    }

    @Override
    public void onUpdateReceived(Update update) {
        log.info("OnUpdateReceived invoked");

        if (update.hasCallbackQuery()){
            log.info("Update has callback query");
            SendMessage reply = buttonHandler.handleButton(update);
            sendToUser(reply);
        } else {

            log.info("Update is simple message");
            String message = update.getMessage().getText();
            switch (message) {
                case UserCommands.START:
                    startCommand(update);
                    break;
                default:
                    log.error("Unexpected user command!");
            }
        }
    }

    private void startCommand(Update update) {
        SendMessage sendMessage = new SendMessage();
        long chatId = update.getMessage().getChatId();
        sendMessage.setText("Добро пожаловать в бот форума предпринимателей!");
        sendMessage.setReplyMarkup(keyboards.mainKeyboard());
        sendMessage.setChatId(String.valueOf(chatId));
        sendToUser(sendMessage);
    }


    private void sendToUser(SendMessage sendMessage){
        try{
            if (sendMessage != null) {
                execute(sendMessage);
                log.info("StartCommand reply sent");
            }else{
                log.info("Send message == null");
            }
        }catch(TelegramApiException e){
            System.err.println("Occurred exception: " + e);
            e.printStackTrace();
        }
    }




}
