package irnitu.forum.bot.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TimeUtil {

    private static DateTimeFormatter dayFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    private static DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

    public static String getTimeInterval(LocalDateTime startInterval,
                                         LocalDateTime endInterval){
        String day = startInterval.format(dayFormatter);
        String startTime = startInterval.format(timeFormatter);
        String endTime = endInterval.format(timeFormatter);
//        String startTime = timeFormatter.format(startInterval);
//        String endTime = timeFormatter.format(endInterval);
        return day + " " + startTime + " - " + endTime;
    }

}
