package irnitu.forum.bot.repositories;


import irnitu.forum.bot.constants.BotStateNames;
import irnitu.forum.bot.models.entities.BotState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BotStateRepository extends JpaRepository<BotState, Long> {


    BotState findByUserTelegramName(String userTelegramName);

}
