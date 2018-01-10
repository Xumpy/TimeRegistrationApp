package android.timesheets.xumpy.com.timesheetservice;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.timesheets.xumpy.com.timesheetservice.dao.TimesheetDB;
import android.timesheets.xumpy.com.timesheetservice.dao.TimesheetDao;
import android.timesheets.xumpy.com.timesheetservice.restbuilder.RestService;
import android.timesheets.xumpy.com.timesheetservice.restservice.restTimesheet;
import android.timesheets.xumpy.com.timesheetservice.service.IPAddress;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private Calendar myCalender;
    private TextView textview;
    private TextView batchAddTxt;
    private TimesheetDB timesheetDB = new TimesheetDB(this, null, null, 1);
    private TimesheetDao timesheetDao = new TimesheetDao();

    private void setBatchAddTxtCalendar(final TextView finalTextView){
        finalTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myCalender = Calendar.getInstance();
                try{
                    Date date = (Date)dateFormat.parse(new String(finalTextView.getText().toString()));
                    myCalender=Calendar.getInstance();
                    myCalender.setTime(date);
                } catch (Exception ex){

                }

                int mYear = myCalender.get(Calendar.YEAR);
                int mMonth = myCalender.get(Calendar.MONTH);
                int mDay = myCalender.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                            myCalender.set(Calendar.YEAR, year);
                            myCalender.set(Calendar.MONTH, monthOfYear);
                            myCalender.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                            int hour = myCalender.get(Calendar.HOUR_OF_DAY);
                            int minute = myCalender.get(Calendar.MINUTE);
                            TimePickerDialog.OnTimeSetListener myTimeListener = new TimePickerDialog.OnTimeSetListener() {
                                @Override
                                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                    if (view.isShown()) {
                                        myCalender.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                        myCalender.set(Calendar.MINUTE, minute);
                                        finalTextView.setText(dateFormat.format(myCalender.getTime()));
                                        refreshData();
                                    }
                                }
                            };
                            TimePickerDialog timePickerDialog = new TimePickerDialog(MainActivity.this, myTimeListener, hour, minute, true);
                            timePickerDialog.setTitle("Choose hour:");
                            timePickerDialog.show();
                        }
                };
                DatePickerDialog datePickerDialog = new DatePickerDialog(MainActivity.this, datePickerListener, mYear, mMonth, mDay);
                datePickerDialog.setTitle("Choose date");
                datePickerDialog.show();
            }
        });
    }

    private void setButton(Button button, CharSequence value){
        if (value.length() != 0){
            button.setEnabled(false);
        } else {
            button.setEnabled(true);
            button.setOnClickListener(new Button.OnClickListener(){
                public void onClick(View view){
                    timesheetDao.insertTimesheet(timesheetDB.getWritableDatabase(), dateFormat.format(new Date()));
                    refreshData();
                }
            });
        }
    }

    private void refreshData(){
        TextView batchInTxt = (TextView)findViewById(R.id.batchInTxt);
        TextView batchOutTxt = (TextView)findViewById(R.id.batchOutTxt);
        TextView batchTotalTxt = (TextView)findViewById(R.id.batchTotalTxt);
        Button batchInBtn = (Button)findViewById(R.id.batchInBtn);
        Button batchOutBtn = (Button)findViewById(R.id.batchOutBtn);

        batchInTxt.setText(timesheetDao.selectBatchIn(timesheetDB.getWritableDatabase()));
        batchOutTxt.setText(timesheetDao.selectBatchOut(timesheetDB.getWritableDatabase()));

        try{
            batchTotalTxt.setText(timesheetDao.getTotalWorkedTime(timesheetDB.getWritableDatabase()));
        } catch (ParseException ex){
            throw new RuntimeException(ex);
        }

        setButton(batchInBtn, batchInTxt.getText());
        setButton(batchOutBtn, batchOutTxt.getText());

        Button batchAddBtn = (Button)findViewById(R.id.batchAddBtn);
        if (batchAddTxt.getText().length() == 0){
            batchAddBtn.setEnabled(false);
        } else {
            batchAddBtn.setEnabled(true);
        }

        try{
            RestService restService = new RestService();
            restService.register(new restTimesheet(timesheetDB.getWritableDatabase()));
        } catch (IOException io){
            System.out.println("Couldn't start server:\n" + io);
        }
    }

    private void removeLastRecordButton(Button removeLastBatchBtn){
        removeLastBatchBtn.setOnClickListener(new Button.OnClickListener(){
            public void onClick(View view){
                timesheetDao.deleteLastRow(timesheetDB.getWritableDatabase());
                refreshData();
            }
        });
    }

    private void batchAddButton(Button batchAddBtn){
        batchAddBtn.setOnClickListener(new Button.OnClickListener(){
            public void onClick(View view){
                timesheetDao.insertTimesheet(timesheetDB.getWritableDatabase(), batchAddTxt.getText().toString());
                refreshData();
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textview = (TextView)findViewById(R.id.textView);
        batchAddTxt = (TextView) findViewById(R.id.batchAddTxt);

        textview.setText("Webservice is active on " + new IPAddress().getIP(this) + ":8888");
        setBatchAddTxtCalendar(batchAddTxt);

        refreshData();
        removeLastRecordButton((Button)findViewById(R.id.removeLastBatchBtn));
        batchAddButton((Button)findViewById(R.id.batchAddBtn));
    }
}
