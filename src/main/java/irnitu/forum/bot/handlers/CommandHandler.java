package irnitu.forum.bot.handlers;

import irnitu.forum.bot.buttons.Keyboards;
import irnitu.forum.bot.constants.UserCommands;
import irnitu.forum.bot.models.common.ResponseForUser;
import irnitu.forum.bot.services.ConsultationTimeSlotService;
import irnitu.forum.bot.services.FeedbackService;
import irnitu.forum.bot.services.ForumScheduleService;
import irnitu.forum.bot.services.UserService;
import irnitu.forum.bot.services.BotStatesService;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import static irnitu.forum.bot.constants.UserCommands.*;

@Slf4j
@Service
public class CommandHandler {

    private final Keyboards keyboards;
    private final UserService userService;
    private final BotStatesService botStatesService;
    private final ForumScheduleService forumScheduleService;
    private final FeedbackService feedbackService;
    private final ConsultationTimeSlotService consultationTimeSlotService;

    public CommandHandler(Keyboards keyboards,
                          UserService userService,
                          FeedbackService feedbackService,
                          BotStatesService botStatesService,
                          ForumScheduleService forumScheduleService,
                          ConsultationTimeSlotService consultationTimeSlotService) {
        this.keyboards = keyboards;
        this.userService = userService;
        this.feedbackService = feedbackService;
        this.botStatesService = botStatesService;
        this.forumScheduleService = forumScheduleService;
        this.consultationTimeSlotService = consultationTimeSlotService;
    }

    public ResponseForUser handleCommand(Update update) {
        log.info("HandleCommand");
        String message = update.getMessage().getText();
        botStatesService.resetState(update.getMessage().getFrom().getUserName());
        switch (message) {
            case UserCommands.START:
                return startCommand(update);
            case UserCommands.REGISTRATION:
                return registrationCommand(update);
            case UserCommands.HELP:
                return helpCommand(update);
            case UserCommands.FORUM_SCHEDULE:
                return forumSchedule(update);
            case UserCommands.ADD_CONSULTATION:
                return scheduleCommand(update);
            case UserCommands.ADD_FEEDBACK:
                return feedbackCommand(update);
            case UserCommands.USER_CONSULTATIONS:
                return consultationsCommand(update);
            case UserCommands.CONSULTATIONS_SCHEDULE:
                return consultationsScheduleCommand(update);
            case UserCommands.ALL_FEEDBACKS:
                return feedbacksCommand(update);
            default:
                log.error("Unexpected command entered!");
                return null;
        }
    }

    /**
     * Обработка нажатия пользователем кнопки, которая показывает все отзывы участников
     */
    private ResponseForUser feedbacksCommand(Update update) {
        long chatId = update.getMessage().getChatId();
        SendDocument sendDocument = null;
        try {
            sendDocument = new SendDocument();
            sendDocument.setChatId(String.valueOf(chatId));
            sendDocument.setDocument(feedbackService.getAllFeedbacks());
        }catch (IOException ex){
            log.error("Error while creating excel file with user feedbacks");
            ex.printStackTrace();
        }
        return new ResponseForUser(sendDocument);
    }

    /**
     * Обработка команды, которая показывает пользователю расписание всех консультаций с экспертами
     * и занятых и свободных
     */
    private ResponseForUser consultationsScheduleCommand(Update update) {
        long chatId = update.getMessage().getChatId();
        SendMessage sendMessage = new SendMessage();
        String consultationsSchedule = consultationTimeSlotService.getAllExpertsTimeSlots();
        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setText(consultationsSchedule);
        return new ResponseForUser(sendMessage);
    }

    /**
     * Метод для кнопки "программа форума".
     */
    private ResponseForUser forumSchedule(Update update) {
        long chatId = update.getMessage().getChatId();
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));
        String schedule = forumScheduleService.getForumScheduleMessage();
        sendMessage.setText(schedule);
        sendMessage.setParseMode(ParseMode.HTML);
        return new ResponseForUser(sendMessage);
    }


    /**
     * Метод для обработки кнопки "мои консультации".
     */
    private ResponseForUser consultationsCommand(Update update) {
        // Проверка регистрации пользователя
        if (!userService.isRegistered(update)) {
            return new ResponseForUser(userService.registrationError(update));
        }
        String telegramUsername = update.getMessage().getFrom().getUserName();
        String userConsultations = consultationTimeSlotService.getAllUserConsultations(telegramUsername);
        long chatId = update.getMessage().getChatId();
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setText(userConsultations);
        return new ResponseForUser(sendMessage);
    }

    /**
     * Метод для обработки нажатия пользователем на кнопку "Оставить отзыв". В методе создаётся
     * клавиатура со всеми блоками форума на который пользователь может написать отзыв.
     */
    private ResponseForUser feedbackCommand(Update update) {
        // Проверка регистрации пользователя
        if (!userService.isRegistered(update)) {
            return new ResponseForUser(userService.registrationError(update));
        }
        InlineKeyboardMarkup educationBlocksMarkup =  keyboards.educationSectionsKeyboard();
        long chatId = update.getMessage().getChatId();
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText("Выберите блок форума на который хотите оставить отзыв:");
        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setReplyMarkup(educationBlocksMarkup);
        return new ResponseForUser(sendMessage);
    }

    private ResponseForUser helpCommand(Update update) {
        long chatId = update.getMessage().getChatId();
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setText("При помощи данного бота вы можете: \n" +
                "\n* Записаться на консультацию к эксперту" +
                "\n* Посмотреть на какие консультации вы уже записались" +
                "\n* Оставить отзыв на любой из блоков форума" +
                "\n\n Если возникнут какие-либо вопросы по работе бота, обращайтесь к https://t.me/IvanStanovihin");
        return new ResponseForUser(sendMessage);
    }

    private ResponseForUser startCommand(Update update) {
        long chatId = update.getMessage().getChatId();

        String message = String.format("Добрый день, уважаемые участники!\n" +
                        "\nПриветствуем вас в боте молодежного форума «PROпредпринимателей» С помощью этого бота вы сможете:" +
                        "\n" +
                        "\n1. Посмотреть актуальную программу мероприятия - %s" +
                        "\n" +
                        "\n2. Оставить отзыв о работе форума и о каждой отдельном этапе программы - %s" +
                        "\n" +
                        "\n3. Записаться на консультацию к экспертам-предпринимателям г. Иркутска - %s" +
                        "\n" +
                        "\n4. Посмотреть рассписание консультаций на которые Вы записались - %s" +
                        "\n" +
                        "\n5. Посмотреть рассписание консультаций всех экспертов - %s" +
                        "\n" +
                        "\n6. Посмотреть рассписание консультаций конкретного эксперта - %s" +
                        "\n" +
                        "\nДля получения информации по функциям Бота используйте - %s",
                FORUM_SCHEDULE,
                ADD_FEEDBACK,
                ADD_CONSULTATION,
                USER_CONSULTATIONS,
                "//TODO",
                "//TODO",
                HELP
        );

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setText(message);
        return new ResponseForUser(sendMessage);
    }

    private ResponseForUser registrationCommand(Update update) {
        String userTelegramName = update.getMessage().getFrom().getUserName();
        botStatesService.setRegistrationState(userTelegramName);
        long chatId = update.getMessage().getChatId();
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText("Введите ФИО и номер телефона:");
        sendMessage.setChatId(String.valueOf(chatId));
        return new ResponseForUser(sendMessage);
    }

    /**
     * Метод, который отправляет пользователю список кнопок для выбора эксперта на консультацию
     * @param update
     * @return
     */
    private ResponseForUser scheduleCommand(Update update){
        log.info("HandleButton scheduleCommand");
        // Проверка регистрации пользователя
        if (!userService.isRegistered(update)) {
            return new ResponseForUser(userService.registrationError(update));
        }
        long chatId = update.getMessage().getChatId();
        SendMessage sendMessage = new SendMessage();
        InlineKeyboardMarkup businessExpertKeyboard = keyboards.expertsKeyboard();
        sendMessage.setText("Выберите эксперта, к которому хотите записаться на консультацию:");
        sendMessage.setReplyMarkup(businessExpertKeyboard);
        sendMessage.setChatId(String.valueOf(chatId));
        return new ResponseForUser(sendMessage);
    }

}
