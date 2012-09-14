package com.example.beratungskonfigurator;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.beratungskonfigurator.server.ServerInterface;
import com.example.beratungskonfigurator.server.ServerInterfaceListener;

public class MainActivity extends Activity {

	private int getKundeId = -1;
	private int getAngehoerigerId = -1;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);

		// WLAN Überprüfung
		if (isHighSpeedConnection() == false) {

			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage(
					"Sie sind nicht mit einem WLAN verbunden! Bitte verbinden Sie sich mit einem WLAN, um diese Anwendung nutzen zu können!")
					.setCancelable(false).setPositiveButton("Beenden", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							MainActivity.this.finish();
						}
					}).setNegativeButton("WLAN Einstellungen", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							dialog.cancel();
							startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
							MainActivity.this.finish();
						}
					});
			AlertDialog alert = builder.create();
			alert.show();
		}
	}

	public void onButtonClick(View view) {
		switch (view.getId()) {
		case R.id.btn_nberatung:
			
			// ----------------------------------------------------------------------------------//
			// insertNeuerKunde
			// ----------------------------------------------------------------------------------//

			JSONObject updateParams = new JSONObject();

			ServerInterface si = new ServerInterface();
			si.addListener(new ServerInterfaceListener() {
				public void serverSuccessHandler(JSONObject result) throws JSONException {

					getKundeId = result.getInt("kundeId");
					getAngehoerigerId = result.getInt("angehoerigerId");
					Log.i("Neuer Kunde ID: ", "KundeId: " + getKundeId);
					Log.i("Neuer Angehoeriger ID: ", "AngehoerigerId: " + getAngehoerigerId);
					Log.i("INSERT Neuer Kunde: ", result.getString("msg"));
					
					Toast.makeText(getApplicationContext(), "Kunde ID: " + getKundeId+" Angehoeriger ID: "+getAngehoerigerId, Toast.LENGTH_SHORT).show();
					Intent startBeratung = new Intent(MainActivity.this, TabActivity.class);
					startBeratung.putExtra("kundeId", getKundeId);
					startBeratung.putExtra("angehoerigerId", getAngehoerigerId);
					startActivity(startBeratung);
				}

				public void serverErrorHandler(Exception e) {
					// TODO Auto-generated method
					// stub
				}
			});
			si.call("insertNeuerKunde", updateParams);

			break;
		case R.id.btn_fberatung:
			startActivity(new Intent(this, FolgeberatungActivity.class));
			break;
		}
	}

	public boolean isHighSpeedConnection() {
		WifiManager wifiMan = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		// Check for connected WiFi network - if so, return true.
		WifiInfo wifiInfo = wifiMan.getConnectionInfo();
		Log.i("WIFI Status", "Status: " + wifiInfo.getNetworkId());
		if (wifiInfo.getNetworkId() != -1) {
			return true;
		} else {
			return false;
		}
	}

	public void onBackPressed() {

	}

	public void onResume() {
		super.onResume();
	}

}
