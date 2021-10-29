package comp5216.sydney.edu.au.findmygym.model;

import android.annotation.SuppressLint;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class ScheduleList {

    //private int cost;
    private String gymName;
    private String trainer;
    private Calendar time;
    private String gymId;

    public ScheduleList(String gymName, String trainer, Calendar time, String gymId) {
        this.gymName = gymName;
        this.trainer = trainer;
        this.time = time;
        this.gymId = gymId;
    }

    public String getGymName() {
        return gymName;
    }

    public void setGymName(String gymName) {
        this.gymName = gymName;
    }

    public String getTrainer() {
        return trainer;
    }

    public void setTrainer(String trainer) {
        this.trainer = trainer;
    }

    public Calendar getTime() {
        return time;
    }

    public String getTimeStr()
    {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar cal = new GregorianCalendar();
        cal.setTimeInMillis(this.time.getTimeInMillis());
        // Log.d("[TEST]",cal.getTime().toString());
        return df.format(cal.getTime());
    }

    public void setTime(Calendar time) {
        this.time = time;
    }

    public String getGymId() {
        return gymId;
    }
}
