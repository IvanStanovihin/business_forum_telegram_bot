package irnitu.forum.bot.repositories;

import irnitu.forum.bot.models.entities.ForumSchedule;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ForumScheduleRepository extends JpaRepository<ForumSchedule, Long> {

  @Query(value = "select * from forum_schedule order by start_date_time",
  nativeQuery = true)
  public List<ForumSchedule> findAllSortedByStartTime();

}
