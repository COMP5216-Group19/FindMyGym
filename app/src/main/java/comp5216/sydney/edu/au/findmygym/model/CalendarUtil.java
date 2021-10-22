package comp5216.sydney.edu.au.findmygym.model;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class CalendarUtil {

    private static DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    public static String calendarToString(Calendar calendar) {
        return format.format(calendar.getTime());
    }

    public static Calendar stringToCalendar(String s) {
        Calendar cal = Calendar.getInstance();
        try {
            Date d = format.parse(s);
            if (d != null) {
                cal.setTime(d);
                return cal;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
}
