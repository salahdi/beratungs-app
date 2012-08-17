package com.example.beratungskonfigurator;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.support.v4.app.NavUtils;

public class FolgeberatungActivity extends Activity {


	// Progress Dialog
	private ProgressDialog pDialog;
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_folgeberatung);
        
        
        JSONObject params = new JSONObject();

		pDialog = new ProgressDialog(this);
		pDialog.setMessage("Lade Daten!");
		pDialog.setIndeterminate(false);
		pDialog.setCancelable(false);
		pDialog.show();

		ServerInterface si = new ServerInterface();
		// si.setVerbose( true );
		si.addListener(new ServerInterfaceListener() {

			public void serverSuccessHandler(JSONObject result) throws JSONException {

				// z.B. Swirl jetzt verstecken und Grid mit Daten füllen
				pDialog.dismiss();

				List<String> list = new ArrayList<String>();

				JSONArray alleKunden = result.getJSONArray("data");

				for (int i = 0; i < alleKunden.length(); i=i+2) {
					list.add(alleKunden.getString(i));
				}
				Log.i("data", alleKunden.toString());
				Log.i("msg", result.getString("msg"));

				ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(FolgeberatungActivity.this, android.R.layout.simple_list_item_1, list);
				ListView lv = (ListView) findViewById(R.id.listView1);
				lv.setClickable(true);
				lv.setAdapter(dataAdapter);

			}

			public void serverErrorHandler(Exception e) {

				// z.B. Fehler Dialog aufploppen lassen
				Log.e("error", "called");
			}
		});
		params = new JSONObject();
		si.call("gibAlleKunden", params);
        
        
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_folgeberatung, menu);
        return true;
    }

    
}
