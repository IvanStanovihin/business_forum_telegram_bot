package irnitu.forum.bot.repositories;

import irnitu.forum.bot.models.entities.Contest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Репа для получения инфы о конкурсе "Угадай фразу"
 */
@Repository
public interface ContestRepository extends JpaRepository<Contest, Long>  {

    Contest findContestById(Long contestId);
}
