package android.timesheets.xumpy.com.timesheetservice.service;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.text.format.Formatter;
import android.util.Log;
import android.widget.TextView;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

/**
 * Created by nico on 22/12/2017.
 */

public class IPAddress {
    public String getIP(Activity activity) {
        boolean WIFI = false;
        boolean MOBILE = false;
        ConnectivityManager CM = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo[] networkInfo = CM.getAllNetworkInfo();

        for (NetworkInfo netInfo : networkInfo) {
            if (netInfo.getTypeName().equalsIgnoreCase("WIFI"))
                if (netInfo.isConnected()) WIFI = true;
            if (netInfo.getTypeName().equalsIgnoreCase("MOBILE"))
                if (netInfo.isConnected()) MOBILE = true;
        }

        if(WIFI == true) {
            return GetDeviceipWiFiData(activity);
        }

        if(MOBILE == true) {
            return GetDeviceipMobileData();
        }
        return null;
    }


    private String GetDeviceipMobileData(){
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
                 en.hasMoreElements();) {
                NetworkInterface networkinterface = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = networkinterface.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        return inetAddress.getHostAddress().toString();
                    }
                }
            }
        } catch (Exception ex) {
            Log.e("Current IP", ex.toString());
        }
        return null;
    }

    private String GetDeviceipWiFiData(Activity activity)
    {
        WifiManager wm = (WifiManager) activity.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        String ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());

        return ip;

    }
}
