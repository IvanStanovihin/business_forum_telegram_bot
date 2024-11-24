package irnitu.forum.bot.models.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.Data;

@Data
@Entity(name = "secret_words")
public class SecretWord {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  /**
   * Слово из секретной фразы
   */
  private String word;

  /**
   * Флаг показывает угадано ли слово кем то из участников
   */
  private Boolean isGuessed;
}
