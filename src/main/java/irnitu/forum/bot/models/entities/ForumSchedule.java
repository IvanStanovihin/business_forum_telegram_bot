package irnitu.forum.bot.models.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.Data;

@Data
@Entity(name = "forum_schedule")
public class ForumSchedule {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;
  private String description;
  private String startDateTime;
  private String endDateTime;

}
