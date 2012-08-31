package com.example.beratungskonfigurator;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.beratungskonfigurator.server.ServerInterface;
import com.example.beratungskonfigurator.server.ServerInterfaceListener;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class FolgeberatungActivity extends ListActivity implements AdapterView.OnItemSelectedListener {

	// Progress Dialog
	private ProgressDialog pDialog;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_folgeberatung);

		JSONObject params = new JSONObject();

		final String[] items = { "Name", "Vorname" };

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

				// List<String> list = new ArrayList<String>();

				TextView titel = (TextView) findViewById(R.id.titel);
				titel.setText("Folgeberatung");

				TextView textSortList = (TextView) findViewById(R.id.textSortList);
				textSortList.setText("Kundenliste sortieren nach: ");

				Spinner sortList = (Spinner) findViewById(R.id.sortList);
				sortList.setOnItemSelectedListener(FolgeberatungActivity.this);

				ArrayAdapter<String> adapterSortList = new ArrayAdapter<String>(FolgeberatungActivity.this, android.R.layout.simple_spinner_item,
						items);
				adapterSortList.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				sortList.setAdapter(adapterSortList);
				sortList.setVisibility(View.VISIBLE);

				JSONArray alleKunden = result.getJSONArray("data");

				SimpleAdapter adapterList = new SimpleAdapter(FolgeberatungActivity.this, list, R.layout.custom_row_view, new String[] { "name",
						"adresse", "datum", "id" }, new int[] { R.id.name, R.id.adresse, R.id.datum });

				HashMap<String, String> temp = new HashMap<String, String>();
				//temp.clear();
				list.clear();
				for (int i = 0; i < alleKunden.length(); i++) {
					temp.put("name", alleKunden.getString(i) + " " + alleKunden.getString(i = i + 1));
					temp.put("adresse",
							alleKunden.getString(i = i + 1) + " " + alleKunden.getString(i = i + 1) + "		" + alleKunden.getString(i = i + 1) + " "
									+ alleKunden.getString(i = i + 1));
					temp.put("datum", "Datum");
					temp.put("id", alleKunden.getString(i=i+1));
					list.add(temp);
					temp = new HashMap<String, String>();
				}
				Log.i("data", alleKunden.toString());
				Log.i("msg", result.getString("msg"));

				setListAdapter(adapterList);

				ListView lv = getListView();
				lv.setTextFilterEnabled(true);

				lv.setOnItemClickListener(new OnItemClickListener() {
					public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
						
						HashMap<String, String> selectedItem = list.get(position);
						int getKundeId = Integer.parseInt(selectedItem.get("id"));
						
						// When clicked, show a toast with the TextView text
						Toast.makeText(getApplicationContext(), "List Click! + ID: "+id+"	Position: "+position+"	Kunde ID: "+getKundeId, Toast.LENGTH_SHORT).show();
						
						Intent startBeratung = new Intent(FolgeberatungActivity.this, TabActivity.class);
						startBeratung.putExtra("kundeId", getKundeId);
						startActivity(startBeratung);
						//startActivity(new Intent(FolgeberatungActivity.this, TabActivity.class));
					}
				});
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

	static final ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();

	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub
		
	}
}
