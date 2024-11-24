package irnitu.forum.bot.models.entities;

import java.time.LocalDateTime;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import lombok.experimental.Accessors;

/**
 * Моделька для фиксации победителя конкурса
 */
@Data
@Accessors(chain = true)
@Entity(name = "contest_winner")
public class ContestWinner {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User student;

    /**
     * Время ввода пользователем найденной фразы
     */
    private LocalDateTime phraseEntryTime;

    private Boolean isWinnerSet;
}
