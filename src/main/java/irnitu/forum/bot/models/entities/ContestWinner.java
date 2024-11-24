package irnitu.forum.bot.models.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

/**
 * Моделька для фиксации победителя конкурса
 */
@Getter
@Setter
@Entity(name = "contest_winner")
public class ContestWinner {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User student;

    private String phraseEntryTime;

    private Boolean isWinnerSet;
}
