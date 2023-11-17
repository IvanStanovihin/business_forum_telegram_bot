package irnitu.forum.bot.models.entities;

import irnitu.forum.bot.constants.BotStateNames;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;

@Data
@Accessors(chain = true)
@Entity(name = "bot_states")
public class BotState {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Enumerated(EnumType.STRING)
    private BotStateNames state;
    private String userTelegramName;
    private Long educationBlockId;
}