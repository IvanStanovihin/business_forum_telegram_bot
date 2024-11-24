package irnitu.forum.bot.models.entities;

import javax.persistence.Column;
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

  @Column(name = "description", length = 2048)
  private String description;

  @Column(name = "start_date_time")
  private String startDateTime;

  @Column(name = "end_date_time")
  private String endDateTime;

  @Column(name = "place")
  private String place;

}
