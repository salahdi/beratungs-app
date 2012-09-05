package com.example.beratungskonfigurator.tabs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.TextView;

import com.example.beratungskonfigurator.MainActivity;
import com.example.beratungskonfigurator.R;
import com.example.beratungskonfigurator.dialog.GeraeteDialog;
import com.example.beratungskonfigurator.dialog.RaumauswahlDialog;
import com.example.beratungskonfigurator.dialog.SzenarioDialog;
import com.example.beratungskonfigurator.server.ServerInterface;
import com.example.beratungskonfigurator.server.ServerInterfaceListener;

public class KonfigurationActivity extends Fragment {

	private ProgressDialog pDialog;
	private int kundeId;
	private int szenarioId;
	private JSONArray la = new JSONArray();
	private JSONArray szIdArray = new JSONArray();
	private JSONArray szKonf;
	private ListView konfigurationList;
	private ListView einstellungenList;

	private static final String ANWENDUNGSFALL_TAB = "Anwendungsfall";

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		kundeId = this.getArguments().getInt("sendKundeId");

		pDialog = new ProgressDialog(getActivity());
		pDialog.setMessage("Lade Daten!");
		pDialog.setIndeterminate(false);
		pDialog.setCancelable(false);

		final View konfigurationView = (View) inflater.inflate(R.layout.tab_konfiguration_layout, container, false);
		final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

		// ----------------------------------------------------------------------------------//
		// gibSelectedSzenario
		// ----------------------------------------------------------------------------------//

		try {
			JSONObject params = new JSONObject();

			ServerInterface si;
			pDialog.show();
			params.put("kundeId", kundeId);

			si = new ServerInterface();
			si.addListener(new ServerInterfaceListener() {

				public void serverSuccessHandler(JSONObject result) throws JSONException {
					List<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();

					la = result.getJSONArray("data");
					szIdArray = result.getJSONArray("szenarioId");

					Log.i("szIdArray", szIdArray.toString());

					Log.i("szIdArray", "Array is NULL: " + szIdArray.isNull(0));

					if (!szIdArray.isNull(0)) {

						for (int i = 0; i < la.length(); i++) {
							HashMap<String, String> hm = new HashMap<String, String>();
							hm.put("name", la.getString(i));
							list.add(hm);
						}
						Log.i("data", la.toString());
						final SimpleAdapter adapterMainList = new SimpleAdapter(getActivity(), list, R.layout.listview_main, new String[] { "name" },
								new int[] { R.id.name });
						konfigurationList = (ListView) konfigurationView.findViewById(R.id.konfigurationList);
						konfigurationList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
						konfigurationList.setAdapter(adapterMainList);
						konfigurationList.setItemChecked(0, true);

						gibSzenarioEinstellungen(0, konfigurationView);

						pDialog.dismiss();
						konfigurationList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
							public void onItemClick(AdapterView<?> arg0, View v, int position, long id) {
								
								gibSzenarioEinstellungen(position, konfigurationView);

							}
						});
					} else {

						AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
						builder.setTitle("Kein Szenario ausgewählt!");
						builder.setIcon(R.drawable.icon_kategorie1);
						builder.setMessage(
								"Sie haben noch keinen Anwendungsfall mit entsprechendem Szenario ausgewählt, welches Sie konfigurien können! Gehen Sie zurück zu Anwendungsfall und wählen Sie dort das zu Ihrem vorliegendem Problemfall passende Szenario aus!")
								.setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog, int id) {
									}
								});
						AlertDialog alert = builder.create();
						alert.show();
					}
				}
				public void serverErrorHandler(Exception e) {
					// z.B. Fehler Dialog aufploppen lassen
					Log.e("error", "called");
				}
			});
			si.call("gibSelectedSzenario", params);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return konfigurationView;
	}

	static final ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();


	private void gibSzenarioEinstellungen( int position, final View konfigurationView ) {

		try {
			// ----------------------------------------------------------------------------------//
			// gibSzenarioEinstellungen
			// ----------------------------------------------------------------------------------//

			JSONObject params = new JSONObject();
			pDialog.show();
			szenarioId = szIdArray.getInt(position);
			params.put("szenarioId", szenarioId);

			ServerInterface si = new ServerInterface();
			si.addListener(new ServerInterfaceListener() {

				public void serverSuccessHandler(JSONObject result) throws JSONException {

					List<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();

					szKonf = result.getJSONArray("data");

					for (int i = 0; i < szKonf.length(); i++) {
						HashMap<String, String> hm = new HashMap<String, String>();
						hm.put("name", szKonf.getString(i));
						hm.put("icon", String.valueOf(R.drawable.icon_kategorie1));
						list.add(hm);
					}
					Log.i("data", szKonf.toString());
					final SimpleAdapter adapterMainList = new SimpleAdapter(getActivity(), list, R.layout.listview_einstellungen, new String[] {
							"name", "icon" }, new int[] { R.id.name, R.id.icon });
					einstellungenList = (ListView) konfigurationView.findViewById(R.id.einstellungenList);
					einstellungenList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
					einstellungenList.setAdapter(adapterMainList);
					pDialog.dismiss();
					einstellungenList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
						public void onItemClick(AdapterView<?> arg0, View v, int position, long id) {
							String selListItem = einstellungenList.getItemAtPosition(position).toString();
							Log.i("in onClick", "Selected Item Name: "+ selListItem + " Position: " + position);
							if(selListItem.contains("Raumauswahl")){
								RaumauswahlDialog customDialog = new RaumauswahlDialog(getActivity(), kundeId);
								customDialog.setTitle("Raumauswahl");
								customDialog.show();
							}else if(selListItem.contains("Geräteauswahl")){
								GeraeteDialog customDialog = new GeraeteDialog(getActivity(), kundeId);
								customDialog.setTitle("Geräteauswahl");
								customDialog.show();
							}

						}
					});
				}
				public void serverErrorHandler(Exception e) {
					// z.B. Fehler Dialog aufploppen
					// lassen
					Log.e("error", "called");
				}
			});
			si.call("gibSzenarioEinstellungen", params);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
