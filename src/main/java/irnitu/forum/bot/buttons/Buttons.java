package irnitu.forum.bot.buttons;

import irnitu.forum.bot.constants.UserCommands;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.List;

public class Buttons {

    private static final InlineKeyboardButton HELLO_BUTTON = new InlineKeyboardButton("Hello");
    private static final InlineKeyboardButton HELP_BUTTON = new InlineKeyboardButton("Help");

    public static InlineKeyboardMarkup mainKeyboard() {
        HELLO_BUTTON.setCallbackData(UserCommands.HELLO);
        HELP_BUTTON.setCallbackData(UserCommands.HELP);

        List<InlineKeyboardButton> rowInline1 = List.of(HELLO_BUTTON);
        List<InlineKeyboardButton> rowInline2 = List.of(HELP_BUTTON);
        List<List<InlineKeyboardButton>> rowsInLine = List.of(rowInline1, rowInline2);

        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        markupInline.setKeyboard(rowsInLine);

        return markupInline;
    }
}
