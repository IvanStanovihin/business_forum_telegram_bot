package irnitu.forum.bot.models.entities;

import javax.persistence.Column;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * Моделька с инфой о конкурсе "Угадай фразу"
 */
@Data
@Entity(name = "contest_secret")
public class ContestSecret {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    /**
     * Секретная фраза, загаданная для участников конкурса
     */
    @Column(name = "phrase", length = 1024)
    private String phrase;

    /**
     * Флаг показывающий, что все слова и фразы найдены участниками конкурса и введены в бота
     */
    @Column(name = "is_all_words_found")
    private Boolean isAllWordsFound;
}
