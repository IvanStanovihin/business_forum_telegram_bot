package irnitu.forum.bot.repositories;

import irnitu.forum.bot.models.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<User, Long> {

    User findByTelegramUserName(String userName);

    User findByChatId(String chatId);

    @Query(value = "SELECT * FROM users WHERE telegram_user_name = ?1 AND registration_information IS NULL",
     nativeQuery = true)
    User findByUserNameWithNullRegistration(String userName);
}
