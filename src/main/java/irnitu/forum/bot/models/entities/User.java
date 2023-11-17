package irnitu.forum.bot.models.entities;


import lombok.Data;
import lombok.experimental.Accessors;


import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Data
@Accessors(chain = true)
@Entity(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String chatId;
    private String telegramFirstName;
    private String telegramLastName;
    private String telegramUserName;
    /**
     * Информация, которую пользователь введёт при регистрации: телефон, ФИО
     */
    private String registrationInformation;
    private String phoneNumber;
    private String telegramName;
}
