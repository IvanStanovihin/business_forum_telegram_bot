package irnitu.forum.bot.models.entities;

import irnitu.forum.bot.constants.ContestStateNames;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;

/**
 * Моделька для состояния конкурса
 * (может быть два состояния - "Участникам открыт ввод слова и им недоступен ввод фразы",  "Участникам открыт ввод фразы" )
 */
@Data
@Accessors(chain = true)
@Entity(name = "contest_states")
public class ContestState {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Enumerated(EnumType.STRING)
    private ContestStateNames state;
}
