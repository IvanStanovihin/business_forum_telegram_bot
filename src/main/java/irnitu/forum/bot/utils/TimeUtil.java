package irnitu.forum.bot.utils;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class TimeUtil {

    private static SimpleDateFormat timeFormatter = new SimpleDateFormat("d MMM HH:mm", Locale.ROOT);

    public static String getTimeInterval(LocalDateTime startInterval,
                                         LocalDateTime endInterval){
//        String startTime = timeFormatter.format(startInterval);
//        String endTime = timeFormatter.format(endInterval);
        return startInterval + " - " + endInterval;
    }

}
