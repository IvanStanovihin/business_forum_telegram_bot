package irnitu.forum.bot.services;

import irnitu.forum.bot.models.entities.User;
import irnitu.forum.bot.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Slf4j
@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Метод проверяет зарегистрирован ли пользователь
     * @param update объект с данными о пользователе
     * @return true - пользователь уже зарегистрирован / false - пользователь не зарегистрирован
     */
    public boolean isRegistered(Update update){
        String userName = update.getMessage().getFrom().getUserName();
        User user = userRepository.findByTelegramUserName(userName);
        if (user != null){
            //значит пользователь уже прошёл регистрацию
            log.info("User registered");
            return true;
        }
        log.info("User not registered");
        return false;
    }

    /**
     * Метод регистрации пользователей
     * @param update объект с данными пользователя
     */
    public void register(Update update){
        String registrationInfo = update.getMessage().getText();
        User newUser = new User()
                .setTelegramUserName(update.getMessage().getFrom().getUserName())
                .setTelegramFirstName(update.getMessage().getFrom().getFirstName())
                .setTelegramLastName(update.getMessage().getFrom().getLastName())
                .setRegistrationInformation(registrationInfo);
        userRepository.save(newUser);
    }


    public SendMessage registrationError(Update update) {
        long chatId = update.getMessage().getChatId();
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setText("Чтобы пользоваться ботом, необходимо зарегистрироваться!" +
                " Нажмите кнопку \"Зарегистрироваться\"");
        return sendMessage;
    }

}
