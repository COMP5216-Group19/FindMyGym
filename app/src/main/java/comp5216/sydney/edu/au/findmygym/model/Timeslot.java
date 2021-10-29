package comp5216.sydney.edu.au.findmygym.model;

import android.content.Context;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import comp5216.sydney.edu.au.findmygym.R;
import comp5216.sydney.edu.au.findmygym.Utils.CalendarUtil;

/**
 * This class represents a segment of time.
 */
public class Timeslot implements Serializable {

    private final Calendar beginTime;
    private final int lengthMinutes;

    public Timeslot(Calendar beginTime, int lengthMinutes) {
        this.beginTime = beginTime;
        this.lengthMinutes = lengthMinutes;
    }

    public static Timeslot fromDate(Date beginDate, int lengthMinutes) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(beginDate);
        return new Timeslot(calendar, lengthMinutes);
    }

    public static Timeslot fromDatabaseString(String dbString) {
        String[] parts = dbString.split("/");
        return new Timeslot(CalendarUtil.stringToCalendar(parts[0]), Integer.parseInt(parts[1]));
    }

    public String toDatabaseString() {
        return CalendarUtil.calendarToString(beginTime) + "/" + lengthMinutes;
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
        Calendar begin = parseCalendarOnToday(beginTimeStr);
        Calendar end = parseCalendarOnToday(endTimeStr);
        return new Timeslot(begin,
                (int) ((end.getTimeInMillis() - begin.getTimeInMillis()) / 60_000));
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

    public Calendar getEndTime() {
        Calendar calendar = (Calendar) beginTime.clone();
        calendar.add(Calendar.MINUTE, lengthMinutes);
        return calendar;
    }

    /**
     * @return the length of this timeslot, in minutes
     */
    public int getLengthMinutes() {
        return lengthMinutes;
    }

    public static String hourMinutesToString(Context context, int hourOfDay, int minutes,
                                             boolean noMinutesIfZero) {
        boolean isAm = hourOfDay < 12;
        int hour = isAm ? hourOfDay : (hourOfDay == 12 ? 12 : hourOfDay - 12);
        int fmtStrId;
        if (minutes == 0 && noMinutesIfZero) {
            fmtStrId = isAm ?
                    R.string.gym_time_format_am_short : R.string.gym_time_format_pm_short;
        } else {
            fmtStrId = isAm ?
                    R.string.gym_time_format_am : R.string.gym_time_format_pm;
        }
        return context.getString(fmtStrId, hour, minutes);
    }

    public static String calendarToTimeInDay(Context context, Calendar time) {
        int hourOfDay = time.get(Calendar.HOUR_OF_DAY);
        int minutes = time.get(Calendar.MINUTE);
        return hourMinutesToString(context, hourOfDay, minutes, true);
    }

    public String toString(Context context) {
        Calendar endTime = Calendar.getInstance();
        endTime.setTimeInMillis(beginTime.getTimeInMillis() + lengthMinutes * 60_000L);
        DateFormat dateFormat =
                DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.SHORT);
        return context.getString(R.string.gym_timeslot,
                dateFormat.format(beginTime.getTime()),
                dateFormat.format(endTime.getTime()));
    }

    public String toStringWithoutDate(Context context) {
        Calendar endTime = Calendar.getInstance();
        endTime.setTimeInMillis(beginTime.getTimeInMillis() + lengthMinutes * 60_000L);
        return context.getString(R.string.gym_timeslot,
                calendarToTimeInDay(context, beginTime),
                calendarToTimeInDay(context, endTime));
    }

    @NonNull
    @Override
    public String toString() {
        return beginTime.getTime().toString() + " - " + lengthMinutes;
    }
}
