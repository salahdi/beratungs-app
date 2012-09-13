package com.example.beratungskonfigurator.dialog;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.example.beratungskonfigurator.R;
import com.example.beratungskonfigurator.server.ServerInterface;
import com.example.beratungskonfigurator.server.ServerInterfaceListener;

public class ExportDatenDialog extends Dialog {

	private static final String KUNDENDATEN = "Kundendaten";
	private static final String WOHNUNGSDATEN = "Wohnungsdaten";
	private static final String PROBLEMDATEN = "gesundheitliche Problemstellung";
	private static final String ANWENDUNGSFALL = "Anwendungsfall";
	private static final String SZENARIO = "persönliche Szenarien";

	ListView exportList;
	private ProgressDialog pDialog;

	// Constructor
	public ExportDatenDialog(Context context) {
		super(context);
		
		setContentView(R.layout.exportdaten_dialog_layout);

		pDialog = new ProgressDialog(context);
		pDialog.setMessage("Lade Daten!");
		pDialog.setIndeterminate(false);
		pDialog.setCancelable(false);

		WindowManager.LayoutParams paramsLayout = getWindow().getAttributes();

		paramsLayout.x = 0;
		paramsLayout.y = 0;
		this.getWindow().setAttributes(paramsLayout);

		setCancelable(true);

		String[] values = new String[] { KUNDENDATEN, WOHNUNGSDATEN, PROBLEMDATEN, ANWENDUNGSFALL, SZENARIO };

		List<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
		for (int i = 0; i < values.length; i++) {
			HashMap<String, String> hm = new HashMap<String, String>();
			hm.put("txt", values[i]);
			list.add(hm);
		}
		SimpleAdapter dataAdapter = new SimpleAdapter(getContext(), list, R.layout.listview_checkable, new String[] { "txt" },
				new int[] { R.id.txt });
		exportList = (ListView) findViewById(R.id.exportList);
		exportList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		exportList.setAdapter(dataAdapter);

		exportList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View v, int position, long id) {
				exportList.setItemChecked(position, true);
			}
		});
		
		Button bClose = (Button) findViewById(R.id.closeButton);
		bClose.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				dismiss();
			}
		});


	}

}
