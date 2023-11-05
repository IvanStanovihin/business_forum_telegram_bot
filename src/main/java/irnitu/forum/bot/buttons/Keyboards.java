package irnitu.forum.bot.buttons;

import irnitu.forum.bot.constants.Buttons;
import irnitu.forum.bot.constants.UserCommands;
import irnitu.forum.bot.repositories.BusinessExpertRepository;
import irnitu.forum.bot.services.BusinessExpertService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class Keyboards {

    private final BusinessExpertService businessExpertService;

    public Keyboards(BusinessExpertService businessExpertService) {
        this.businessExpertService = businessExpertService;
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

    public InlineKeyboardMarkup expertsKeyboard(){
        List<InlineKeyboardButton> expertButtons = new ArrayList<>();
        expertButtons = businessExpertService.getExpertNames()
                .stream()
                .map(expertName -> createButton("/" + expertName, expertName))
                .collect(Collectors.toList());

        List<List<InlineKeyboardButton>> rowsInLine = expertButtons
                .stream()
                .map(List::of)
                .collect(Collectors.toList());

        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        markupInline.setKeyboard(rowsInLine);
        return markupInline;
    }

    private InlineKeyboardButton createButton(String callback, String text){
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setCallbackData(callback);
        button.setText(text);
        return button;
    }
}
