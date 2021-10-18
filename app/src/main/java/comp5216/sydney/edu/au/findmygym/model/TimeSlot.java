package comp5216.sydney.edu.au.findmygym.model;

import java.util.Calendar;

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
