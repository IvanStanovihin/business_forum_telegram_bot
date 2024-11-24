package irnitu.forum.bot.repositories;

import irnitu.forum.bot.models.entities.SecretWord;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface SecretWordRepository extends JpaRepository<SecretWord, Long> {

  @Query(value = "select * from secret_words s where s.is_guessed is false", nativeQuery = true)
  List<SecretWord> findAllNotGuessed();
}
