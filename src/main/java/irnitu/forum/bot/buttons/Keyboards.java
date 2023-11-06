package irnitu.forum.bot.buttons;

import irnitu.forum.bot.constants.Buttons;
import irnitu.forum.bot.constants.UserCommands;
import irnitu.forum.bot.models.ConsultationTimeSlot;
import irnitu.forum.bot.repositories.BusinessExpertRepository;
import irnitu.forum.bot.repositories.ConsultationTimeSlotRepository;
import irnitu.forum.bot.services.BusinessExpertService;
import irnitu.forum.bot.services.ConsultationTimeSlotService;
import irnitu.forum.bot.utils.TimeUtil;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class Keyboards {

    private final BusinessExpertService businessExpertService;
    private final ConsultationTimeSlotService consultationTimeSlotService;

    public Keyboards(BusinessExpertService businessExpertService,
                     ConsultationTimeSlotService consultationTimeSlotService) {
        this.businessExpertService = businessExpertService;
        this.consultationTimeSlotService = consultationTimeSlotService;
    }

    private InlineKeyboardButton createButton(String callback, String text){
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setCallbackData(callback);
        button.setText(text);
        return button;
    }

    public InlineKeyboardMarkup mainKeyboard() {
        Buttons.HELLO_BUTTON.setCallbackData(UserCommands.HELLO);
        Buttons.HELP_BUTTON.setCallbackData(UserCommands.HELP);
        Buttons.SCHEDULE_BUTTON.setCallbackData(UserCommands.SCHEDULE);

        List<InlineKeyboardButton> rowInline1 = List.of(Buttons.HELLO_BUTTON);
        List<InlineKeyboardButton> rowInline2 = List.of(Buttons.HELP_BUTTON);
        List<InlineKeyboardButton> rowInline3 = List.of(Buttons.SCHEDULE_BUTTON);
        List<List<InlineKeyboardButton>> rowsInLine = List.of(rowInline1, rowInline2, rowInline3);

        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        markupInline.setKeyboard(rowsInLine);

        return markupInline;
    }

    /**
     * Клавиатура со списком экспертов доступных для записи на консультацию
     * @return клавиатуру, которая состоит из списка экспертов
     */
    public InlineKeyboardMarkup expertsKeyboard(){
        List<InlineKeyboardButton> expertButtons = new ArrayList<>();
        expertButtons = businessExpertService.getExpertNames()
                .stream()
                .map(expertName -> createButton((UserCommands.EXPERT + "_" + expertName), expertName))
                .collect(Collectors.toList());

        List<List<InlineKeyboardButton>> rowsInLine = expertButtons
                .stream()
                .map(List::of)
                .collect(Collectors.toList());

        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        markupInline.setKeyboard(rowsInLine);
        return markupInline;
    }

    /**
     * Клавиатура со свободными тайм слотами на консультацию, для эксперта
     * @param expertName имя эксперта для которого выполняется поиск свободных тайм слотов
     * @return список свободных тайм слотов для эксперта
     */
    public InlineKeyboardMarkup expertFreeTimeSlotKeyboard(String expertName) {
        List<ConsultationTimeSlot> expertFreeTimeSlot = consultationTimeSlotService.getFreeSlot(expertName);
        List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();
        for (ConsultationTimeSlot timeSlot : expertFreeTimeSlot){
            String buttonText = TimeUtil.getTimeInterval(timeSlot.getStartConsultationTime(), timeSlot.getEndConsultationTime());
            String buttonCallback = UserCommands.TIME_SLOT
                    + "_" + timeSlot.getId()
                    + "_" + timeSlot.getExpert().getId()
                    + "_" + timeSlot.getStartConsultationTime()
                    + "_" + timeSlot.getEndConsultationTime();
            InlineKeyboardButton freeSlotButton = createButton(buttonCallback, buttonText);
            rowsInLine.add(List.of(freeSlotButton));
        }
        InlineKeyboardMarkup markupInLine = new InlineKeyboardMarkup();
        markupInLine.setKeyboard(rowsInLine);
        return markupInLine;
    }
}
