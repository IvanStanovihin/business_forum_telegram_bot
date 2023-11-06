package irnitu.forum.bot.menu;

import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;

import java.util.ArrayList;
import java.util.List;

public class BotMenu {

    public static List<BotCommand> getMenuCommands(){
        List<BotCommand> botCommands = new ArrayList<>();
        botCommands.add(new BotCommand("/start", "get a welcome message"));
        botCommands.add(new BotCommand("/mydata", "get your data stored"));
        botCommands.add(new BotCommand("/deletedata", "delete my data"));
        botCommands.add(new BotCommand("/help", "info how to use this bot"));
        botCommands.add(new BotCommand("/settings", "set your preferences"));
        return botCommands;
    }
}
