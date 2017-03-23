package edu.hartnell.iris.time;

import java.time.LocalDateTime;

public class TimeManager {

    public static String getStringTimeFormatted(){
        int Seconds = LocalDateTime.now().getSecond(),
                Minutes = LocalDateTime.now().getMinute(),
                Hours = LocalDateTime.now().getHour(),
                Day = LocalDateTime.now().getDayOfMonth(),
                Month = LocalDateTime.now().getMonthValue(),
                Year = LocalDateTime.now().getYear();
        return (Month + "-" + Day + "-" + Year + " | " + Hours + ":" + Minutes
        + ":" + Seconds);
    }

}
