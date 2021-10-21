package comp5216.sydney.edu.au.findmygym.model;

import android.content.Context;

import androidx.annotation.NonNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import comp5216.sydney.edu.au.findmygym.R;

/**
 * This class represents a segment of time.
 */
public class Timeslot {

    private final Calendar beginTime;
    private final Calendar endTime;

    public Timeslot(Calendar beginTime, Calendar endTime) {
        this.beginTime = beginTime;
        this.endTime = endTime;
    }

    /**
     * Creates a timeslot of today, with begin and end time parsed from strings.
     *
     * @param beginTimeStr string representation of begin time, e.g. 9AM or 9:00AM
     * @param endTimeStr   string representation of end time
     * @return the parsed timeslot
     * @throws ParseException if any of the argument has wrong format
     */
    public static Timeslot timeSlotOnToday(String beginTimeStr, String endTimeStr)
            throws ParseException {
        return new Timeslot(parseCalendarOnToday(beginTimeStr), parseCalendarOnToday(endTimeStr));
    }

    private static Calendar parseCalendarOnToday(String str) throws ParseException {
        Calendar calendar = Calendar.getInstance();
        str = str.replace(" ", "");
        SimpleDateFormat fmt;
        if (str.contains(":")) fmt = new SimpleDateFormat("hh:mma", Locale.getDefault());
        else fmt = new SimpleDateFormat("hha", Locale.getDefault());
        Date date = fmt.parse(str);
        if (date == null) throw new ParseException("Parse result is null", 0);

        // The parsed date is on 01/01/1970
        // We just need the hour and minute field
        Calendar tarCalendar = Calendar.getInstance();
        tarCalendar.setTime(date);
        calendar.set(Calendar.HOUR, tarCalendar.get(Calendar.HOUR));
        calendar.set(Calendar.AM_PM, tarCalendar.get(Calendar.AM_PM));
        calendar.set(Calendar.HOUR_OF_DAY, tarCalendar.get(Calendar.HOUR_OF_DAY));
        calendar.set(Calendar.MINUTE, tarCalendar.get(Calendar.MINUTE));
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return calendar;
    }

    /**
     * @return begin time of this time segment
     */
    public Calendar getBeginTime() {
        return beginTime;
    }

    /**
     * @return end time of this time segment
     */
    public Calendar getEndTime() {
        return endTime;
    }

    public static String calendarToTimeInDay(Context context, Calendar time) {
        int am_pm = time.get(Calendar.AM_PM);
        int hour = time.get(Calendar.HOUR);
        int minutes = time.get(Calendar.MINUTE);
        int fmtStrId;
        if (minutes == 0) {
            fmtStrId = am_pm == Calendar.AM ?
                    R.string.gym_time_format_am_short : R.string.gym_time_format_pm_short;
        } else {
            fmtStrId = am_pm == Calendar.AM ?
                    R.string.gym_time_format_am : R.string.gym_time_format_pm;
        }
        return context.getString(fmtStrId, hour, minutes);
    }

    public String toString(Context context) {
        return context.getString(R.string.gym_timeslot,
                calendarToTimeInDay(context, beginTime),
                calendarToTimeInDay(context, endTime));
    }

    @NonNull
    @Override
    public String toString() {
        return beginTime + " - " + endTime;
    }
}