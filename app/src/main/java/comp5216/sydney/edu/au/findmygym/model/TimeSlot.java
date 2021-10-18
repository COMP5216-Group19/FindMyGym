package comp5216.sydney.edu.au.findmygym.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * This class represents a segment of time.
 */
public class TimeSlot {

    private final Calendar beginTime;
    private final Calendar endTime;

    public TimeSlot(Calendar beginTime, Calendar endTime) {
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
    public static TimeSlot timeSlotOnToday(String beginTimeStr, String endTimeStr)
            throws ParseException {
        return new TimeSlot(parseCalendarOnToday(beginTimeStr), parseCalendarOnToday(endTimeStr));
    }

    private static Calendar parseCalendarOnToday(String str) throws ParseException {
        Calendar calendar = Calendar.getInstance();
        str = str.replace(" ", "");
        SimpleDateFormat fmt;
        if (str.contains(":")) fmt = new SimpleDateFormat("hha:mm", Locale.getDefault());
        else fmt = new SimpleDateFormat("hha", Locale.getDefault());
        Date date = fmt.parse(str);
        if (date == null) throw new ParseException("Parse result is null", 0);
        calendar.setTime(date);

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
}
