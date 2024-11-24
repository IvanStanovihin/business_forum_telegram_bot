package irnitu.forum.bot.handlers;

import irnitu.forum.bot.buttons.Keyboards;
import irnitu.forum.bot.constants.UserCommands;
import irnitu.forum.bot.models.common.ResponseForUser;
import irnitu.forum.bot.models.entities.ContestWinner;
import irnitu.forum.bot.services.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

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

    private final ContestWinnerService contestWinnerService;

    private List<String> whiteList = Arrays.asList("slavikir", "IvanStanovihin", "rocknrollalalala", "VikaRinchinova", "elenagoncharova18");

    public CommandHandler(Keyboards keyboards,
                          UserService userService,
                          FeedbackService feedbackService,
                          BotStatesService botStatesService,
                          ForumScheduleService forumScheduleService,
                          ConsultationTimeSlotService consultationTimeSlotService,
                          ContestWinnerService contestWinnerService) {
        this.keyboards = keyboards;
        this.userService = userService;
        this.feedbackService = feedbackService;
        this.botStatesService = botStatesService;
        this.forumScheduleService = forumScheduleService;
        this.consultationTimeSlotService = consultationTimeSlotService;
        this.contestWinnerService = contestWinnerService;
    }

    public ResponseForUser handleCommand(Update update) {
        log.info("HandleCommand");
        String message = update.getMessage().getText();
        String user = update.getMessage().getFrom().getUserName();
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
            case UserCommands.CONTEST:
                return contest(update);
            case UserCommands.CONSULTATIONS_SCHEDULE:
                if (whiteList.contains(user)) {
                    return consultationsScheduleCommand(update);
                }
                log.error("CONSULTATIONS_SCHEDULE whitelist error!");
                return null;
            case UserCommands.ALL_FEEDBACKS:
                if (whiteList.contains(user)) {
                    return feedbacksCommand(update);
                }
                log.error("ALL_FEEDBACKS whitelist error!");
                return null;
            case SEE_THE_WINNER:
                if (whiteList.contains(user)) {
                    return seeWinner(update);
                }
                log.error("SEE_THE_WINNER whitelist error!");
                return null;
            case ACTIVATE_PHRASE_ENTRY:
                if (whiteList.contains(user)) {
                    return activatePhraseEntry(update);
                }
                log.error("ACTIVATE_PHRASE_ENTRY whitelist error!");
                return null;
            default:
                log.error("Unexpected command entered!");
                return null;
        }
    }

    /**
     * Обработка нажатия организатором кнопки "Активировать ввод фразы"
     */
    private ResponseForUser activatePhraseEntry(Update update) {

        //TODO добавить обработку нажатия
        //Нужно дергать ручку на бэке которая активирует отгадывание фразы
        return null;
    }

    /**
     * Обработка нажатия организатором кнопки "Посмотреть победителя конкурса"
     */
    private ResponseForUser seeWinner(Update update) {
        long chatId = update.getMessage().getChatId();
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));

        ContestWinner winner = contestWinnerService.getWinner();

        if (winner.getIsWinnerSet()) {
            sendMessage.setText(winner.getStudent().getChatId());
        } else {
            sendMessage.setText("еще никто не удагал фразу");
        }

        return new ResponseForUser(sendMessage);
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
        } catch (IOException ex) {
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
        sendMessage.setParseMode(ParseMode.HTML);
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
        InlineKeyboardMarkup educationBlocksMarkup = keyboards.educationSectionsKeyboard();
        long chatId = update.getMessage().getChatId();
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText("Выберите блок форума на который хотите оставить отзыв:");
        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setReplyMarkup(educationBlocksMarkup);
        return new ResponseForUser(sendMessage);
    }

    /**
     * Метод для обработки нажатия на "Конкурс"
     * @param update
     * @return
     */
    private ResponseForUser contest(Update update) {
        InlineKeyboardMarkup contestBlocksMarkup = keyboards.contestBlockKeyboard();
        long chatId = update.getMessage().getChatId();
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText("Выберите что вы хотите ввести:");
        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setReplyMarkup(contestBlocksMarkup);
        return new ResponseForUser(sendMessage);
    }

    private ResponseForUser helpCommand(Update update) {
        long chatId = update.getMessage().getChatId();
        String user = update.getMessage().getFrom().getUserName();
        String mainMessage = String.format("При помощи данного бота вы можете: \n" +
                        "\n<b>1.</b> Посмотреть актуальную программу мероприятия - %s" +
                        "\n" +
                        "\n<b>2.</b> Оставить отзыв о работе форума и о каждой отдельном этапе программы - %s" +
                        "\n" +
                        "\n<b>3.</b> Записаться на консультацию к экспертам-предпринимателям г. Иркутска - %s" +
                        "\n" +
                        "\n<b>4.</b> Посмотреть рассписание консультаций на которые Вы записались - %s" +
                        "\n" +
                        "\n<b>5.</b> Участвовать в конкурсе - «Угадай фразу» - %s" +
                        "\n\n Если возникнут какие-либо вопросы по работе бота, то "
                        + "обращайтесь к: \nhttps://t.me/IvanStanovihin  \nhttps://t.me/slavikir",
                FORUM_SCHEDULE,
                ADD_FEEDBACK,
                ADD_CONSULTATION,
                USER_CONSULTATIONS,
                CONTEST
        );

        StringBuilder message = new StringBuilder(mainMessage);

        if (whiteList.contains(user)) {
            String extraMessage = String.format("\n\nКоманды для организаторов: \n" +
                            "<b>1.</b> Посмотреть общее расписание консультаций - %s \n" +
                            "<b>2.</b> Посмотреть отзывы участников - %s \n" +
                            "<b>3.</b> Посмотреть победителя конкурса - %s \n" +
                            "<b>4.</b> Активировать ввод фразы - %s \n",
                    CONSULTATIONS_SCHEDULE,
                    ALL_FEEDBACKS,
                    SEE_THE_WINNER,
                    ACTIVATE_PHRASE_ENTRY
            );
            message.append(extraMessage);
        }

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setText(message.toString());
        sendMessage.setParseMode(ParseMode.HTML);
        return new ResponseForUser(sendMessage);
    }

    private ResponseForUser startCommand(Update update) {
        long chatId = update.getMessage().getChatId();

        String message = String.format("<b>Добрый день, уважаемые участники!</b>\n" +
                        "\n<b>Приветствуем вас в боте молодежного форума «PROпредпринимателей» С помощью этого бота вы сможете:</b>" +
                        "\n" +
                        "\n<b>1.</b> Посмотреть актуальную программу мероприятия - %s" +
                        "\n" +
                        "\n<b>2.</b> Оставить отзыв о работе форума и о каждой отдельном этапе программы - %s" +
                        "\n" +
                        "\n<b>3.</b> Записаться на консультацию к экспертам-предпринимателям г. Иркутска - %s" +
                        "\n" +
                        "\n<b>4.</b> Посмотреть расписание консультаций на которые Вы записались - %s" +
                        "\n" +
                        "\n<b>5.</b> Участвовать в конкурсе - «Угадай фразу» - %s\"\n" +
                        "\nДля получения информации по функциям Бота используйте - %s",
                FORUM_SCHEDULE,
                ADD_FEEDBACK,
                ADD_CONSULTATION,
                USER_CONSULTATIONS,
                CONTEST,
                HELP
                //TODO добавить команды для оргов
        );

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setText(message);
        sendMessage.setParseMode(ParseMode.HTML);
        return new ResponseForUser(sendMessage);
    }

    private ResponseForUser registrationCommand(Update update) {
        String userTelegramName = update.getMessage().getFrom().getUserName();
        long chatId = update.getMessage().getChatId();
        if (userTelegramName == null || userTelegramName.isEmpty()) {
            SendMessage sendMessage = new SendMessage();
            sendMessage.setText("Мы не можем Вас зарегистрировать у Вас не установлен Telegram логин (начинается с @), " +
                    "установите логин в информации о пользователе и используйте команду /registration заново");
            sendMessage.setChatId(String.valueOf(chatId));
            return new ResponseForUser(sendMessage);
        } else {
            botStatesService.setRegistrationState(userTelegramName);
            SendMessage sendMessage = new SendMessage();
            sendMessage.setText("Введите ФИО и номер телефона:");
            sendMessage.setChatId(String.valueOf(chatId));
            return new ResponseForUser(sendMessage);
        }
    }

    /**
     * Метод, который отправляет пользователю список кнопок для выбора эксперта на консультацию
     *
     * @param update
     * @return
     */
    private ResponseForUser scheduleCommand(Update update) {
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
