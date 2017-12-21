package android.timesheets.xumpy.com.timesheetservice;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.timesheets.xumpy.com.timesheetservice.restbuilder.RestService;
import android.timesheets.xumpy.com.timesheetservice.restservice.restTimesheet;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try{
            RestService restService = new RestService();
            restService.register(new restTimesheet());
        } catch (IOException io){
            System.out.println("Couldn't start server:\n" + io);
        }
    }
}
