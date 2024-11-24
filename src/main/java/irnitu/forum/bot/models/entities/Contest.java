package irnitu.forum.bot.models.entities;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * Моделька с инфой о конкурсе "Угадай фразу"
 */
@Data
@Entity(name = "contest")
public class Contest {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String phrase;
}
