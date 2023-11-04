package irnitu.forum.bot.handlers;

import irnitu.forum.bot.constants.UserCommands;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Slf4j
@Component
public class ButtonHandler {

    public SendMessage handleButton(Update update){
        log.info("HandleButton ");
        String message = update.getCallbackQuery().getData();
        switch (message){
            case UserCommands.HELLO:
                return helloCommand(update);
            case UserCommands.HELP:
                return helpCommand(update);
            default:
                log.error("Unexpected button pressed!");
                return null;
        }
    }

    private SendMessage helloCommand(Update update){
        log.info("HandleButton helloCommand");
        SendMessage sendMessage = new SendMessage();
        String replyMessage = "Привет! " + update.getCallbackQuery().getFrom().getUserName();
        long chatId = update.getCallbackQuery().getMessage().getChatId();
        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setText(replyMessage);
        return sendMessage;
    }


    private SendMessage helpCommand(Update update){
        log.info("HandleButton helpCommand");
        SendMessage sendMessage = new SendMessage();
        String replyMessage = "Уважаемый " + update.getCallbackQuery().getFrom().getUserName()
                + "! Скоро вам помогут, ожидайте :) ";
        long chatId = update.getCallbackQuery().getMessage().getChatId();
        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setText(replyMessage);
        return sendMessage;
    }
}
