package android.timesheets.xumpy.com.timesheetservice.model;

import java.util.List;
import java.util.Map;

/**
 * Created by nico on 20/10/2017.
 */

public class TimesheetDetail {
    private String date;
    private List<TimesheetHour> hoursBatchIn;
    private List<TimesheetHour> hoursBatchOut;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public List<TimesheetHour> getHoursBatchIn() {
        return hoursBatchIn;
    }

    public void setHoursBatchIn(List<TimesheetHour> hoursBatchIn) {
        this.hoursBatchIn = hoursBatchIn;
    }

    public List<TimesheetHour> getHoursBatchOut() {
        return hoursBatchOut;
    }

    public void setHoursBatchOut(List<TimesheetHour> hoursBatchOut) {
        this.hoursBatchOut = hoursBatchOut;
    }
}
