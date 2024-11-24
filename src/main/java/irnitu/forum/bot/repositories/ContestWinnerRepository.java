package irnitu.forum.bot.repositories;

import irnitu.forum.bot.models.entities.ContestWinner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Репа для поиска и записи в БД информации про победителя конкурса
 */
@Repository
public interface ContestWinnerRepository extends JpaRepository<ContestWinner, Long> {

}
