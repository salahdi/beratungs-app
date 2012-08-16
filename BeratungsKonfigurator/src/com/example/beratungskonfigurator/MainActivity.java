package com.example.beratungskonfigurator;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends Activity {

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_main);

		//wifi
		if(isHighSpeedConnection()){
			
		}else{
			Toast wifiToast = Toast.makeText(this, "Bitte aktivieren Sie Ihr WLan um diese App nutzen zu können!", Toast.LENGTH_LONG);
			wifiToast.setGravity(Gravity.CENTER, wifiToast.getXOffset() / 2, wifiToast.getYOffset() / 2);
			wifiToast.show();
			startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
		}

	}

	public void onButtonClick(View view) {
		switch (view.getId()) {
		case R.id.btn_nberatung:
			startActivity(new Intent(this, TabActivity.class));
			break;
		case R.id.btn_fberatung:
			startActivity(new Intent(this, TabActivity.class));
			break;
		}
	}
	
	public boolean isHighSpeedConnection()
    {
        WifiManager wifiMan = (WifiManager)getSystemService(Context.WIFI_SERVICE);
        
    	// Check for connected WiFi network - if so, return true.
    	WifiInfo wifiInfo = wifiMan.getConnectionInfo();
    	Log.i("WIFI Status", "Status: "+ wifiInfo.getNetworkId());
    	if (wifiInfo.getNetworkId() != -1)
    	{
    		return true;
    	}
    	else{
    		return false;
    	}
    }
}
