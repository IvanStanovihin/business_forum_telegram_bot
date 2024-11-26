package irnitu.forum.bot.models.entities;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Data
@Accessors(chain = true)
@Entity(name = "phrase_input_log")
public class PhraseInputLog {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    /**
     * Введенная пользователем фраза
     */
    private String inputPhrase;

    private String userTgName;
}
