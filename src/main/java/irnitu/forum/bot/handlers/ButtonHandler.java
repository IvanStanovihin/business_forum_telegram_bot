package irnitu.forum.bot.handlers;

import irnitu.forum.bot.buttons.Keyboards;
import irnitu.forum.bot.constants.UserCommands;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

@Slf4j
@Component
public class ButtonHandler {

    private final Keyboards keyboards;

    public ButtonHandler(Keyboards keyboards) {
        this.keyboards = keyboards;
    }

    public SendMessage handleButton(Update update){
        log.info("HandleButton ");
        String message = update.getCallbackQuery().getData();
        String messageCallback = message.split("_")[0];
        log.info("HandleButton message callBack {}", messageCallback);
        switch (messageCallback){
            case UserCommands.HELLO:
                return helloCommand(update);
            case UserCommands.HELP:
                return helpCommand(update);
            case UserCommands.SCHEDULE:
                return scheduleCommand(update);
            case UserCommands.EXPERT:
                return businessExpertCommand(update);
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

    private SendMessage scheduleCommand(Update update){
        log.info("HandleButton scheduleCommand");
        long chatId = update.getCallbackQuery().getMessage().getChatId();
        SendMessage sendMessage = new SendMessage();
        InlineKeyboardMarkup businessExpertKeyboard = keyboards.expertsKeyboard();
        sendMessage.setText("Выберите эксперта, к которому хотите записаться на консультацию");
        sendMessage.setReplyMarkup(businessExpertKeyboard);
        sendMessage.setChatId(String.valueOf(chatId));
        return sendMessage;
    }

    private SendMessage businessExpertCommand(Update update){
        log.info("HandleButton businessExpertCommand");
        long chatId = update.getCallbackQuery().getMessage().getChatId();
        SendMessage sendMessage = new SendMessage();
        String expertName = update.getCallbackQuery().getData().split("_")[1];
        log.info("HandleButton \ngetData {}", expertName);
        InlineKeyboardMarkup expertFreeTimeSlotKeyboard = keyboards.expertFreeTimeSlotKeyboard(expertName);
        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setText("Выберите время для записи к эксперту:");
        sendMessage.setReplyMarkup(expertFreeTimeSlotKeyboard);
        return sendMessage;
    }
}
