package android.timesheets.xumpy.com.timesheetservice;

import android.app.TimePickerDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.Formatter;
import android.timesheets.xumpy.com.timesheetservice.restbuilder.RestService;
import android.timesheets.xumpy.com.timesheetservice.restservice.restTimesheet;
import android.timesheets.xumpy.com.timesheetservice.service.IPAddress;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.TimePicker;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;

public class MainActivity extends AppCompatActivity {
    private SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
    private Calendar myCalender;
    private TextView textview;
    private TextView batchAddTxt;

    private String IPaddress;
    private Boolean IPValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textview = (TextView)findViewById(R.id.textView);
        batchAddTxt = (TextView) findViewById(R.id.batchAddTxt);

        textview.setText("Webservice is active on " + new IPAddress().getIP(this) + ":8888");
        batchAddTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myCalender = Calendar.getInstance();
                try{
                    Date date = (Date)dateFormat.parse(new String(batchAddTxt.getText());
                    myCalender=Calendar.getInstance();
                    myCalender.setTime(date);
                } catch (Exception ex){

                }

                int hour = myCalender.get(Calendar.HOUR_OF_DAY);
                int minute = myCalender.get(Calendar.MINUTE);

                TimePickerDialog.OnTimeSetListener myTimeListener = new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        if (view.isShown()) {
                            myCalender.set(Calendar.HOUR_OF_DAY, hourOfDay);
                            myCalender.set(Calendar.MINUTE, minute);
                            batchAddTxt.setText(dateFormat.format(myCalender.getTime()));
                        }
                    }
                };
                TimePickerDialog timePickerDialog = new TimePickerDialog(MainActivity.this, myTimeListener, hour, minute, true);
                timePickerDialog.setTitle("Choose hour:");
                timePickerDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                timePickerDialog.show();
            }
        });
        try{
            RestService restService = new RestService();
            restService.register(new restTimesheet());
        } catch (IOException io){
            System.out.println("Couldn't start server:\n" + io);
        }
    }
}
