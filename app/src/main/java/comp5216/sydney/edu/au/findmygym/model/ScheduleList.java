package comp5216.sydney.edu.au.findmygym.model;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class ScheduleList {

    //private int cost;
    private String title;
    private String trainer;
    private Calendar time;
    private Bitmap image;

    public ScheduleList(String title, String trainer, Calendar time, Bitmap image) {
        this.title = title;
        this.trainer = trainer;
        this.time = time;
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }


}
