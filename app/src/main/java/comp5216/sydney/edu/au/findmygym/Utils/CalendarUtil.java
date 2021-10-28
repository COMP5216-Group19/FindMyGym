package comp5216.sydney.edu.au.findmygym.Utils;

import android.annotation.SuppressLint;

import com.google.firebase.Timestamp;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

@SuppressLint("SimpleDateFormat")
public class CalendarUtil
{

    private static final DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    private static final DateFormat formatNoDate = new SimpleDateFormat("HH:mm");

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

    /**
     * String to calendar, contains time only (no year, month, or date).
     */
    public static Calendar stringToCalendarNoDate(String s) {
        Calendar cal = Calendar.getInstance();
        try {
            Date d = formatNoDate.parse(s);
            if (d != null) {
                cal.setTime(d);
                return cal;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Calendar timestampToCalendar(Timestamp timestamp) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(timestamp.toDate());
        return calendar;
    }
}
