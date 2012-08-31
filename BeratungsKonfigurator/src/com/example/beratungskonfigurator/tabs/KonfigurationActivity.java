package com.example.beratungskonfigurator.tabs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
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

import com.example.beratungskonfigurator.R;
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
					pDialog.dismiss();
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

						// ----------------------------------------------------------------------------------//
						// gibSzenarioEinstellungen
						// ----------------------------------------------------------------------------------//

						JSONObject params = new JSONObject();

						pDialog.show();

						Log.i("szenarioID", "szenarioArray: " + szIdArray);
						szenarioId = szIdArray.getInt(0);
						params.put("szenarioId", szenarioId);

						ServerInterface si = new ServerInterface();
						si.addListener(new ServerInterfaceListener() {

							public void serverSuccessHandler(JSONObject result) throws JSONException {
								pDialog.dismiss();
								List<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();

								szKonf = result.getJSONArray("data");

								for (int i = 0; i < szKonf.length(); i++) {
									HashMap<String, String> hm = new HashMap<String, String>();
									hm.put("name", szKonf.getString(i));
									hm.put("icon", String.valueOf(R.drawable.icon_kategorie1));
									list.add(hm);
								}
								Log.i("data", szKonf.toString());
								final SimpleAdapter adapterMainList = new SimpleAdapter(getActivity(), list, R.layout.listview_einstellungen,
										new String[] { "name", "icon" }, new int[] { R.id.name, R.id.icon });
								einstellungenList = (ListView) konfigurationView.findViewById(R.id.einstellungenList);
								einstellungenList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
								einstellungenList.setAdapter(adapterMainList);
								einstellungenList.setItemChecked(0, true);

								einstellungenList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
									public void onItemClick(AdapterView<?> arg0, View v, int position, long id) {
									}
								});
							}

							public void serverErrorHandler(Exception e) {
								// z.B. Fehler Dialog aufploppen lassen
								Log.e("error", "called");
							}
						});
						si.call("gibSzenarioEinstellungen", params);

						konfigurationList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
							public void onItemClick(AdapterView<?> arg0, View v, int position, long id) {

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
											pDialog.dismiss();
											List<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();

											szKonf = result.getJSONArray("data");

											for (int i = 0; i < szKonf.length(); i++) {
												HashMap<String, String> hm = new HashMap<String, String>();
												hm.put("name", szKonf.getString(i));
												list.add(hm);
											}
											Log.i("data", szKonf.toString());
											final SimpleAdapter adapterMainList = new SimpleAdapter(getActivity(), list, R.layout.listview_main,
													new String[] { "name" }, new int[] { R.id.name });
											einstellungenList = (ListView) konfigurationView.findViewById(R.id.einstellungenList);
											einstellungenList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
											einstellungenList.setAdapter(adapterMainList);
											einstellungenList.setItemChecked(0, true);

											einstellungenList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
												public void onItemClick(AdapterView<?> arg0, View v, int position, long id) {
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
						});
					}else{
						/*
						android.app.Fragment fragment = getActivity().getFragmentManager().findFragmentById(3);
						FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
						ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
		                ft.commit();
		                getActivity().getSupportFragmentManager().executePendingTransactions();*/
					}
					
				}public void serverErrorHandler(Exception e) {
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

	// int type = getItemViewType(position);
	/*
	 * switch (type) { case TYPE_ITEM: convertView =
	 * mInflater.inflate(R.layout.item1, null); holder.textView =
	 * (TextView)convertView.findViewById(R.id.text); break; case
	 * TYPE_SEPARATOR: convertView = mInflater.inflate(R.layout.item2, null);
	 * holder.textView = (TextView)convertView.findViewById(R.id.textSeparator);
	 * break; }
	 */

}
