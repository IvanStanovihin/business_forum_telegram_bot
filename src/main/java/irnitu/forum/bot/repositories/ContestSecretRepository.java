package irnitu.forum.bot.repositories;

import irnitu.forum.bot.models.entities.ContestSecret;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Репа для получения инфы о конкурсе "Угадай фразу"
 */
@Repository
public interface ContestSecretRepository extends JpaRepository<ContestSecret, Long>  {

    ContestSecret findContestById(Long contestId);

    @Query(value = "update contest_secret set is_all_words_found = true where true",
           nativeQuery = true)
    void markAllWordsGuessed();
}
