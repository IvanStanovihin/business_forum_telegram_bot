package irnitu.forum.bot.buttons;

import irnitu.forum.bot.constants.UserCommands;
import irnitu.forum.bot.models.entities.BusinessExpert;
import irnitu.forum.bot.models.entities.ConsultationTimeSlot;
import irnitu.forum.bot.models.entities.EducationBlock;
import irnitu.forum.bot.repositories.EducationBlockRepository;
import irnitu.forum.bot.services.BusinessExpertService;
import irnitu.forum.bot.services.ConsultationTimeSlotService;
import irnitu.forum.bot.utils.TimeUtil;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class Keyboards {

    private final BusinessExpertService businessExpertService;
    private final ConsultationTimeSlotService consultationTimeSlotService;
    private final EducationBlockRepository educationBlockRepository;

    public Keyboards(BusinessExpertService businessExpertService,
                     ConsultationTimeSlotService consultationTimeSlotService,
                     EducationBlockRepository educationBlockRepository) {
        this.businessExpertService = businessExpertService;
        this.consultationTimeSlotService = consultationTimeSlotService;
        this.educationBlockRepository = educationBlockRepository;
    }

    private InlineKeyboardButton createButton(String callback, String text){
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setCallbackData(callback);
        button.setText(text);
        return button;
    }

//    public InlineKeyboardMarkup mainKeyboard() {
//        Buttons.HELP_BUTTON.setCallbackData(UserCommands.HELP);
//        Buttons.SCHEDULE_BUTTON.setCallbackData(UserCommands.SCHEDULE);
//
//        List<InlineKeyboardButton> rowInline1 = List.of(Buttons.HELLO_BUTTON);
//        List<InlineKeyboardButton> rowInline2 = List.of(Buttons.HELP_BUTTON);
//        List<InlineKeyboardButton> rowInline3 = List.of(Buttons.SCHEDULE_BUTTON);
//        List<List<InlineKeyboardButton>> rowsInLine = List.of(rowInline1, rowInline2, rowInline3);
//
//        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
//        markupInline.setKeyboard(rowsInLine);
//
//        return markupInline;
//    }

    /**
     * Клавиатура со списком экспертов доступных для записи на консультацию
     * @return клавиатуру, которая состоит из списка экспертов
     */
    public InlineKeyboardMarkup expertsKeyboard(){
        List<InlineKeyboardButton> expertButtons = new ArrayList<>();
        List<BusinessExpert> allExperts = businessExpertService.getAll();
        for(BusinessExpert expert : allExperts){
            String buttonCallback = UserCommands.EXPERT + "_" + expert.getName();
            String buttonText = expert.getName() + ": \"" + expert.getDescription() + "\"";
            InlineKeyboardButton button = createButton(buttonCallback, buttonText);
            expertButtons.add(button);
        }

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
    public SendMessage expertFreeTimeSlotKeyboard(String expertName) {
        List<ConsultationTimeSlot> expertFreeTimeSlot = consultationTimeSlotService.getExpertFreeSlot(expertName);
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
        String message;
        if(expertFreeTimeSlot.isEmpty()){
            message = "Свободные тайм-слоты для консультации с экспертом уже закончились";
        }else{
            message = "Выберите тайм-слот для записи к эксперту:";
        }

        SendMessage sendMessage = new SendMessage();
        sendMessage.setReplyMarkup(markupInLine);
        sendMessage.setText(message);
        return sendMessage;
    }

    /**
     * Метод для создания списка кнопок, который состоит из названий блоков форума
     */
    public InlineKeyboardMarkup educationSectionsKeyboard() {
        List<EducationBlock> educationBlocks = educationBlockRepository.findAll();
        List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();
        for (EducationBlock educationBlock : educationBlocks){
            String buttonText = educationBlock.getName();
            String buttonCallback = UserCommands.FEEDBACK_EDUCATION_BLOCK
                    + "_"
                    + educationBlock.getId();
            InlineKeyboardButton educationBlockButton = createButton(buttonCallback, buttonText);
            rowsInLine.add(List.of(educationBlockButton));
        }
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        markup.setKeyboard(rowsInLine);
        return markup;
    }
}
