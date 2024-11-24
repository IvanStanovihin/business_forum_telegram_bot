package irnitu.forum.bot.repositories;

import irnitu.forum.bot.models.entities.ContestState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Репа для получения состояния конкурса
 * (может быть два состояния - "Участникам открыт ввод слова и им недоступен ввод фразы",  "Участникам открыт ввод фразы" )
 */
@Repository
public interface ContestStateRepository extends JpaRepository<ContestState, Long> { }
