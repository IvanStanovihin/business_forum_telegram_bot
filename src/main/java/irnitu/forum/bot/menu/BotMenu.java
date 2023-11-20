package irnitu.forum.bot.menu;

import irnitu.forum.bot.constants.UserCommands;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;

import java.util.ArrayList;
import java.util.List;

public class BotMenu {

    public static SetMyCommands getMenuCommands(){
        List<BotCommand> botCommands = new ArrayList<>();
        botCommands.add(new BotCommand(UserCommands.HELP, "Помощь по функциям бота"));
        botCommands.add(new BotCommand(UserCommands.ADD_CONSULTATION, "Записаться на консультацию"));
        botCommands.add(new BotCommand(UserCommands.FEEDBACK, "Оставить отзыв"));
        botCommands.add(new BotCommand(UserCommands.USER_CONSULTATIONS, "Посмотреть моё расписание консультаций"));
        botCommands.add(new BotCommand(UserCommands.REGISTRATION, "Зарегистрироваться"));
        return new SetMyCommands(botCommands, new BotCommandScopeDefault(), null);
    }
}
