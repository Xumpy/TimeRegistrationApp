package android.timesheets.xumpy.com.timesheetservice.restservice;

import android.timesheets.xumpy.com.timesheetservice.dao.TimesheetConnection;
import android.timesheets.xumpy.com.timesheetservice.dao.TimesheetDao;
import android.timesheets.xumpy.com.timesheetservice.model.Timesheet;
import android.timesheets.xumpy.com.timesheetservice.restbuilder.RestObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class restTimesheet implements RestObject{
    private DateFormat format = new SimpleDateFormat("ddMMyyyy", Locale.ENGLISH);

    @Override
    public String getUrl() {
        return "/timesheet";
    }

    private Date getDateAtQueryParm(List<String> queryParam) {
        if (queryParam != null && queryParam.size() == 1){
            String strDate = queryParam.get(0);
            try {
                return format.parse(strDate);
            } catch (ParseException e) {
                return null;
            }
        }
        return null;
    }

    @Override
    public Object createObject(Map<String, List<String>> queryParams) {
        Timesheet timesheet = new Timesheet();
        TimesheetConnection timesheetConnection = new TimesheetConnection();

        Date startDate = getDateAtQueryParm(queryParams.get("startDate"));
        Date endDate = getDateAtQueryParm(queryParams.get("endDate"));

        if (startDate != null && endDate != null){
            TimesheetDao timesheetDao = new TimesheetDao();
            timesheet = timesheetDao.getTimesheetByDate(timesheetConnection.getDatabase(), startDate, endDate);
        }

        timesheetConnection.close();

        return timesheet;
    }
}
