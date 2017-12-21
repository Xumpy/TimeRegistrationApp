package android.timesheets.xumpy.com.timesheetservice.model;


import java.util.List;

public class Timesheet {
    private List<TimesheetDetail> batches;

    public List<TimesheetDetail> getBatches() {
        return batches;
    }

    public void setBatches(List<TimesheetDetail> batches) {
        this.batches = batches;
    }
}
