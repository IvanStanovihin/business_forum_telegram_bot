package irnitu.forum.bot.bot;

import irnitu.forum.bot.buttons.Keyboards;
import irnitu.forum.bot.configuration.BotConfig;
import irnitu.forum.bot.handlers.ButtonHandler;
import irnitu.forum.bot.handlers.CommandHandler;
import irnitu.forum.bot.handlers.TextHandler;
import irnitu.forum.bot.menu.BotMenu;
import irnitu.forum.bot.services.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
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
    private final UserService userService;
    private final CommandHandler commandHandler;
    private final TextHandler textHandler;

    public BusinessForumBot(BotConfig botConfig,
                            ButtonHandler buttonHandler,
                            Keyboards keyboards,
                            UserService userService,
                            CommandHandler commandHandler,
                            TextHandler textHandler) {
        this.botConfig = botConfig;
        this.buttonHandler = buttonHandler;
        this.keyboards = keyboards;
        this.userService = userService;
        this.commandHandler = commandHandler;
        this.textHandler = textHandler;
        initMenu();
    }

    private void initMenu() {
        SetMyCommands botMenu = BotMenu.getMenuCommands();
        try {
            this.execute(botMenu);
        } catch (TelegramApiException e) {
            log.error("Error setting bot's menu: " + e.getMessage());
        }
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

        if (update.hasCallbackQuery()) {
            //Обработка нажатий на кнопки
            log.info("Update has callback query");
            SendMessage reply = buttonHandler.handleButton(update);
            sendToUser(reply);
        } else if (update.getMessage().getText().charAt(0) == '/') {
            //Обработка команд. Текст, который пришёл от пользователя и начинается с символа /
            log.info("Update has command");
            SendMessage sendMessage = commandHandler.handleCommand(update);
            sendToUser(sendMessage);
        } else{
            //Обработка простого текста, введённого пользователем
            log.info("Update is simple text");
            SendMessage sendMessage = textHandler.handleText(update);
            sendToUser(sendMessage);
        }
    }


    private void sendToUser(SendMessage sendMessage) {
        try {
            if (sendMessage != null) {
                execute(sendMessage);
                log.info("StartCommand reply sent");
            } else {
                log.info("Send message == null");
            }
        } catch (TelegramApiException e) {
            System.err.println("Occurred exception: " + e);
            e.printStackTrace();
        }
    }
}
