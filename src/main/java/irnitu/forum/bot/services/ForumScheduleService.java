package irnitu.forum.bot.services;

import irnitu.forum.bot.models.entities.ForumSchedule;
import irnitu.forum.bot.repositories.ForumScheduleRepository;

import java.util.List;

import lombok.Data;
import org.springframework.stereotype.Service;

@Data
@Service
public class ForumScheduleService {

    private final ForumScheduleRepository forumScheduleRepository;

    public ForumScheduleService(
            ForumScheduleRepository forumScheduleRepository) {
        this.forumScheduleRepository = forumScheduleRepository;
    }

    /**
     * Метод возвращает программу форума в виде строки
     */
    public String getForumScheduleMessage() {
        List<ForumSchedule> forumScheduleEvents = forumScheduleRepository.findAllSortedByStartTime();
        StringBuilder schedule = new StringBuilder("<b>Программа мероприятий форума на 30.11:</b>");
        for (ForumSchedule event : forumScheduleEvents) {
            String eventInfo = "\n<b>Время</b> \n" +
                    event.getStartDateTime() +
                    " - " +
                    event.getEndDateTime() +
                    "\n<b>Место</b> \n" +
                    event.getPlace() + "\n<b>Описание</b> \n" +
                    event.getDescription();

            schedule.append("\n-------------------------").append(eventInfo);
        }
        return schedule.toString();
    }
}
