package irnitu.forum.bot.handlers;

import irnitu.forum.bot.buttons.Keyboards;
import irnitu.forum.bot.constants.UserCommands;
import irnitu.forum.bot.models.ConsultationTimeSlot;
import irnitu.forum.bot.services.ConsultationTimeSlotService;
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

    public ButtonHandler(Keyboards keyboards,
                         ConsultationTimeSlotService consultationTimeSlotService) {
        this.keyboards = keyboards;
        this.consultationTimeSlotService = consultationTimeSlotService;
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
            case UserCommands.EXPERT:
                return businessExpertCommand(update);
            case UserCommands.TIME_SLOT:
                return takeExpertTimeslot(update);
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


    /**
     * Метод который отправляет пользователю клавиатуру из тайм-слотов на консультацию,
     * конкретного эксперта
     * @param update
     * @return
     */
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

    /**
     * Метод для записи пользователя на консультацию к эксперту
     * @param update
     * @return
     */
    private SendMessage takeExpertTimeslot(Update update){
        log.info("HandleButton takeExpertTimeslot");
        long chatId = update.getCallbackQuery().getMessage().getChatId();
        Long timeslotId = Long.parseLong(update.getCallbackQuery().getData().split("_")[1]);
        Long expertId = Long.parseLong(update.getCallbackQuery().getData().split("_")[2]);
        String telegramUsername = update.getCallbackQuery().getFrom().getUserName();
        log.info("HandleButton takeExpertTimeslot timeslot id {}", timeslotId);
        if (consultationTimeSlotService.isUserAlreadyTakeTimeslot(telegramUsername, expertId)){
            //Проверка чтобы пользователь не записался к эксперту несколько раз
            SendMessage sendMessage = new SendMessage();
            sendMessage.setText("Вы уже записаны на консультацию к данному эксперту. Нельзя" +
                    "записать к одному эксперту несколько раз");
            sendMessage.setChatId(String.valueOf(chatId));
            return sendMessage;
        }
        consultationTimeSlotService.takeTimeslot(timeslotId, telegramUsername, expertId);
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText("Вы записаны на консультацию!");
        sendMessage.setChatId(String.valueOf(chatId));
        return sendMessage;
    }
}
