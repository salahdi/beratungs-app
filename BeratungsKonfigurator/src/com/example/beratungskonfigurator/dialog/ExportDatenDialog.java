package com.example.beratungskonfigurator.dialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.example.beratungskonfigurator.R;
import com.example.beratungskonfigurator.server.ServerInterface;
import com.example.beratungskonfigurator.server.ServerInterfaceListener;

public class ExportDatenDialog extends Dialog {

	private static final String KUNDENDATEN = "Kundendaten";
	private static final String WOHNUNGSDATEN = "Wohnungsdaten";
	private static final String PROBLEMDATEN = "gesundheitliche Problemstellung";
	private static final String SZENARIO = "persönliche Szenario Einstellungen";

	private int mKundeId;
	ListView exportList;
	private ProgressDialog pDialog;

	// Constructor
	public ExportDatenDialog(Context context, int kundeId) {
		super(context);

		setContentView(R.layout.exportdaten_dialog_layout);

		mKundeId = kundeId;

		pDialog = new ProgressDialog(context);
		pDialog.setMessage("Lade Daten!");
		pDialog.setIndeterminate(false);
		pDialog.setCancelable(false);

		WindowManager.LayoutParams paramsLayout = getWindow().getAttributes();

		paramsLayout.x = 0;
		paramsLayout.y = 0;
		this.getWindow().setAttributes(paramsLayout);

		setCancelable(false);

		String[] values = new String[] { KUNDENDATEN, WOHNUNGSDATEN, PROBLEMDATEN, SZENARIO };

		List<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
		for (int i = 0; i < values.length; i++) {
			HashMap<String, String> hm = new HashMap<String, String>();
			hm.put("txt", values[i]);
			list.add(hm);
		}
		SimpleAdapter dataAdapter = new SimpleAdapter(getContext(), list, R.layout.listview_checkable, new String[] { "txt" }, new int[] { R.id.txt });
		exportList = (ListView) findViewById(R.id.exportList);
		exportList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		exportList.setAdapter(dataAdapter);

		// ----------------------------------------------------------------------------------//
		// gibKundeExportdaten
		// ----------------------------------------------------------------------------------//

		try {
			JSONObject params = new JSONObject();
			pDialog.show();

			params.put("kundeId", mKundeId);

			ServerInterface si = new ServerInterface();
			si.addListener(new ServerInterfaceListener() {

				public void serverSuccessHandler(JSONObject result) throws JSONException {

					pDialog.dismiss();

					JSONArray wd = result.getJSONArray("data");

					for (int i = 0; i < wd.length(); i++) {
						exportList.setItemChecked((wd.getInt(i)), true);
					}
				}

				public void serverErrorHandler(Exception e) {
					// z.B. Fehler Dialog aufploppen lassen
					Log.e("error", "called");
				}
			});
			si.call("gibKundeExportdaten", params);
		} catch (JSONException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}

		exportList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View v, int position, long id) {

			}
		});

		Button bClose = (Button) findViewById(R.id.closeButton);
		bClose.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {

				// ----------------------------------------------------------------------------------//
				// insertExportdaten
				// ----------------------------------------------------------------------------------//

				try {
					String selected = "";
					int count = 0;
					int cntChoice = exportList.getCount();
					SparseBooleanArray sparseBooleanArray = exportList.getCheckedItemPositions();

					for (int i = 0; i < cntChoice; i++) {
						if (sparseBooleanArray.get(i)) {
							selected += (exportList.getItemIdAtPosition(i)) + ".";
							count++;
						}
					}
					if (count != 0) {
						selected = selected.substring(0, selected.length() - 1);
						Log.i("Button OK: ", "Selected nach entfernen: " + selected + "  Count != 0: " + count);
					}

					JSONObject updateParams = new JSONObject();
					updateParams.put("exportdatenId", selected);
					updateParams.put("kundeId", mKundeId);
					updateParams.put("countAnz", count);

					ServerInterface si = new ServerInterface();
					si.addListener(new ServerInterfaceListener() {
						public void serverSuccessHandler(JSONObject result) throws JSONException {
							Log.i("INSERT Exportdaten: ", result.getString("msg"));
						}

						public void serverErrorHandler(Exception e) {
							// TODO Auto-generated method
							// stub
						}
					});
					si.call("insertExportdaten", updateParams);
				} catch (JSONException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				dismiss();
			}
		});

	}

}
