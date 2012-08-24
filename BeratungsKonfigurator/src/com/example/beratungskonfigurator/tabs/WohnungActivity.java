package com.example.beratungskonfigurator.tabs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.beratungskonfigurator.R;
import com.example.beratungskonfigurator.ServerInterface;
import com.example.beratungskonfigurator.ServerInterfaceListener;

public class WohnungActivity extends Fragment {

	private ProgressDialog pDialog;
	private ProgressDialog pDialogUpdate;

	private int currentSelected = 0;
	private int kundeId;

	private static final String WOHNSITUATION = "Wohnsituation";
	private static final String WOHNFORM = "Wohnform";
	private static final String WOHNINFORMATION = "Wohninformation";
	private static final String WOHNUMFELD = "Wohnumfeld";
	private static final String WOHNRAEUME = "Wohnräume";
	private static final String WOHNBARRIEREN = "Wohnbarrieren";

	ListView wohnsituationList;
	ListView wohnformList;
	ListView wohnumfeldList;
	ListView wohnraeumeList;
	ListView wohnbarrierenList;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		kundeId = this.getArguments().getInt("sendKundeId");

		pDialog = new ProgressDialog(getActivity());
		pDialog.setMessage("Lade Daten!");
		pDialog.setIndeterminate(false);
		pDialog.setCancelable(false);

		pDialogUpdate = new ProgressDialog(getActivity());
		pDialogUpdate.setMessage("Update Daten!");
		pDialogUpdate.setIndeterminate(false);
		pDialogUpdate.setCancelable(false);

		final View wohnungView = (View) inflater.inflate(R.layout.tab_wohnung_layout, container, false);
		final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

		currentSelected = 0;

		final LinearLayout layWohnsituation = (LinearLayout) wohnungView.findViewById(R.id.layWohnsituation);
		final LinearLayout layWohnform = (LinearLayout) wohnungView.findViewById(R.id.layWohnform);
		final LinearLayout layWohninformation = (LinearLayout) wohnungView.findViewById(R.id.layWohninformation);
		final LinearLayout layWohnumfeld = (LinearLayout) wohnungView.findViewById(R.id.layWohnumfeld);
		final LinearLayout layWohnraeume = (LinearLayout) wohnungView.findViewById(R.id.layWohnraeume);
		final LinearLayout layWohnbarrieren = (LinearLayout) wohnungView.findViewById(R.id.layWohnbarrieren);

		String[] values = new String[] { WOHNSITUATION, WOHNFORM, WOHNINFORMATION, WOHNUMFELD, WOHNRAEUME, WOHNBARRIEREN };

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, values);
		final ListView lv = (ListView) wohnungView.findViewById(R.id.wohnungList);
		lv.setClickable(true);
		lv.setAdapter(adapter);

		lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View v, int position, long id) {

				switch (position) {
				case 0:
					switch (currentSelected) {
					case 0:
						layWohnsituation.setVisibility(View.GONE);
						break;
					case 1:
						layWohnform.setVisibility(View.GONE);
						break;
					case 2:
						layWohninformation.setVisibility(View.GONE);
						break;
					case 3:
						layWohnumfeld.setVisibility(View.GONE);
						break;
					case 4:
						layWohnraeume.setVisibility(View.GONE);
						break;
					case 5:
						layWohnbarrieren.setVisibility(View.GONE);
						break;
					}
					layWohnsituation.setVisibility(View.VISIBLE);
					currentSelected = position;
					break;
				case 1:
					switch (currentSelected) {
					case 0:
						layWohnsituation.setVisibility(View.GONE);
						break;
					case 1:
						layWohnform.setVisibility(View.GONE);
						break;
					case 2:
						layWohninformation.setVisibility(View.GONE);
						break;
					case 3:
						layWohnumfeld.setVisibility(View.GONE);
						break;
					case 4:
						layWohnraeume.setVisibility(View.GONE);
						break;
					case 5:
						layWohnbarrieren.setVisibility(View.GONE);
						break;
					}
					layWohnform.setVisibility(View.VISIBLE);
					currentSelected = position;
					break;
				case 2:
					switch (currentSelected) {
					case 0:
						layWohnsituation.setVisibility(View.GONE);
						break;
					case 1:
						layWohnform.setVisibility(View.GONE);
						break;
					case 2:
						layWohninformation.setVisibility(View.GONE);
						break;
					case 3:
						layWohnumfeld.setVisibility(View.GONE);
						break;
					case 4:
						layWohnraeume.setVisibility(View.GONE);
						break;
					case 5:
						layWohnbarrieren.setVisibility(View.GONE);
						break;
					}
					layWohninformation.setVisibility(View.VISIBLE);
					currentSelected = position;
					break;
				case 3:
					switch (currentSelected) {
					case 0:
						layWohnsituation.setVisibility(View.GONE);
						break;
					case 1:
						layWohnform.setVisibility(View.GONE);
						break;
					case 2:
						layWohninformation.setVisibility(View.GONE);
						break;
					case 3:
						layWohnumfeld.setVisibility(View.GONE);
						break;
					case 4:
						layWohnraeume.setVisibility(View.GONE);
						break;
					case 5:
						layWohnbarrieren.setVisibility(View.GONE);
						break;
					}
					layWohnumfeld.setVisibility(View.VISIBLE);
					currentSelected = position;
					break;
				case 4:
					switch (currentSelected) {
					case 0:
						layWohnsituation.setVisibility(View.GONE);
						break;
					case 1:
						layWohnform.setVisibility(View.GONE);
						break;
					case 2:
						layWohninformation.setVisibility(View.GONE);
						break;
					case 3:
						layWohnumfeld.setVisibility(View.GONE);
						break;
					case 4:
						layWohnraeume.setVisibility(View.GONE);
						break;
					case 5:
						layWohnbarrieren.setVisibility(View.GONE);
						break;
					}
					layWohnraeume.setVisibility(View.VISIBLE);
					currentSelected = position;
					break;
				case 5:
					switch (currentSelected) {
					case 0:
						layWohnsituation.setVisibility(View.GONE);
						break;
					case 1:
						layWohnform.setVisibility(View.GONE);
						break;
					case 2:
						layWohninformation.setVisibility(View.GONE);
						break;
					case 3:
						layWohnumfeld.setVisibility(View.GONE);
						break;
					case 4:
						layWohnraeume.setVisibility(View.GONE);
						break;
					case 5:
						layWohnbarrieren.setVisibility(View.GONE);
						break;
					}
					layWohnbarrieren.setVisibility(View.VISIBLE);
					currentSelected = position;
					break;
				}
			}
		});

		try {

			// ----------------------------------------------------------------------------------//
			// gibWohnsituation
			// ----------------------------------------------------------------------------------//

			JSONObject params = new JSONObject();
			ServerInterface si;
			pDialog.show();

			si = new ServerInterface();
			si.addListener(new ServerInterfaceListener() {

				public void serverSuccessHandler(JSONObject result) throws JSONException {

					pDialog.dismiss();

					List<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
					JSONArray la = result.getJSONArray("data");

					for (int i = 0; i < la.length(); i++) {
						HashMap<String, String> hm = new HashMap<String, String>();
						hm.put("txt", la.getString(i));
						list.add(hm);
					}
					Log.i("data", la.toString());
					Log.i("msg", result.getString("msg"));

					String[] from = { "txt" };
					int[] to = { R.id.txt };

					// Instantiating an adapter to store each items
					// R.layout.wohnung_listview defines the layout of each item
					SimpleAdapter dataAdapter = new SimpleAdapter(getActivity(), list, R.layout.wohnung_listview, from, to);
					wohnsituationList = (ListView) wohnungView.findViewById(R.id.wohnsituationList);

					// wohnsituationList.setSelection(kLeistungsartId - 1);
					wohnsituationList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
					wohnsituationList.setAdapter(dataAdapter);

					wohnsituationList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
						public void onItemClick(AdapterView<?> arg0, View v, int position, long id) {
							Toast.makeText(getActivity(),
									"Multiple List: " + wohnsituationList.getItemAtPosition(position).toString() + " Position: " + position,
									Toast.LENGTH_SHORT).show();
						}
					});
				}

				public void serverErrorHandler(Exception e) {
					// z.B. Fehler Dialog aufploppen lassen
					Log.e("error", "called");
				}
			});
			params = new JSONObject();
			si.call("gibWohnsituation", params);

			// ----------------------------------------------------------------------------------//
			// gibWohnform
			// ----------------------------------------------------------------------------------//

			pDialog.show();

			si = new ServerInterface();
			si.addListener(new ServerInterfaceListener() {

				public void serverSuccessHandler(JSONObject result) throws JSONException {

					pDialog.dismiss();

					List<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
					JSONArray la = result.getJSONArray("data");

					for (int i = 0; i < la.length(); i++) {
						HashMap<String, String> hm = new HashMap<String, String>();
						hm.put("txt", la.getString(i));
						list.add(hm);
					}
					Log.i("data", la.toString());
					Log.i("msg", result.getString("msg"));

					String[] from = { "txt" };
					int[] to = { R.id.txt };

					// Instantiating an adapter to store each items
					// R.layout.wohnung_listview defines the layout of each item
					SimpleAdapter dataAdapter = new SimpleAdapter(getActivity(), list, R.layout.wohnung_listview, from, to);
					wohnformList = (ListView) wohnungView.findViewById(R.id.wohnformList);

					// wohnsituationList.setSelection(kLeistungsartId - 1);
					wohnformList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
					wohnformList.setAdapter(dataAdapter);

					wohnformList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
						public void onItemClick(AdapterView<?> arg0, View v, int position, long id) {
							Toast.makeText(getActivity(),
									"Multiple List: " + wohnformList.getItemAtPosition(position).toString() + " Position: " + position,
									Toast.LENGTH_SHORT).show();
						}
					});
				}

				public void serverErrorHandler(Exception e) {
					// z.B. Fehler Dialog aufploppen lassen
					Log.e("error", "called");
				}
			});
			params = new JSONObject();
			si.call("gibWohnform", params);

			// ----------------------------------------------------------------------------------//
			// gibWohnumfeld
			// ----------------------------------------------------------------------------------//

			pDialog.show();

			si = new ServerInterface();
			si.addListener(new ServerInterfaceListener() {

				public void serverSuccessHandler(JSONObject result) throws JSONException {

					pDialog.dismiss();

					List<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
					JSONArray la = result.getJSONArray("data");

					for (int i = 0; i < la.length(); i++) {
						HashMap<String, String> hm = new HashMap<String, String>();
						hm.put("txt", la.getString(i));
						list.add(hm);
					}
					Log.i("data", la.toString());
					Log.i("msg", result.getString("msg"));

					String[] from = { "txt" };
					int[] to = { R.id.txt };

					// Instantiating an adapter to store each items
					// R.layout.wohnung_listview defines the layout of each item
					SimpleAdapter dataAdapter = new SimpleAdapter(getActivity(), list, R.layout.wohnung_listview, from, to);
					wohnumfeldList = (ListView) wohnungView.findViewById(R.id.wohnumfeldList);

					// wohnsituationList.setSelection(kLeistungsartId - 1);
					wohnumfeldList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
					wohnumfeldList.setAdapter(dataAdapter);

					wohnumfeldList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
						public void onItemClick(AdapterView<?> arg0, View v, int position, long id) {
							Toast.makeText(getActivity(),
									"Multiple List: " + wohnumfeldList.getItemAtPosition(position).toString() + " Position: " + position,
									Toast.LENGTH_SHORT).show();
						}
					});
				}

				public void serverErrorHandler(Exception e) {
					// z.B. Fehler Dialog aufploppen lassen
					Log.e("error", "called");
				}
			});
			params = new JSONObject();
			si.call("gibWohnumfeld", params);

			// ----------------------------------------------------------------------------------//
			// gibWohnraeume
			// ----------------------------------------------------------------------------------//

			pDialog.show();

			si = new ServerInterface();
			si.addListener(new ServerInterfaceListener() {

				public void serverSuccessHandler(JSONObject result) throws JSONException {

					pDialog.dismiss();

					List<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
					JSONArray la = result.getJSONArray("data");

					for (int i = 0; i < la.length(); i++) {
						HashMap<String, String> hm = new HashMap<String, String>();
						hm.put("txt", la.getString(i));
						list.add(hm);
					}
					Log.i("data", la.toString());
					Log.i("msg", result.getString("msg"));

					String[] from = { "txt" };
					int[] to = { R.id.txt };

					// Instantiating an adapter to store each items
					// R.layout.wohnung_listview defines the layout of each item
					SimpleAdapter dataAdapter = new SimpleAdapter(getActivity(), list, R.layout.wohnung_listview, from, to);
					wohnraeumeList = (ListView) wohnungView.findViewById(R.id.wohnraeumeList);

					// wohnsituationList.setSelection(kLeistungsartId - 1);
					wohnraeumeList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
					wohnraeumeList.setAdapter(dataAdapter);

					wohnraeumeList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
						public void onItemClick(AdapterView<?> arg0, View v, int position, long id) {
							Toast.makeText(getActivity(),
									"Multiple List: " + wohnraeumeList.getItemAtPosition(position).toString() + " Position: " + position,
									Toast.LENGTH_SHORT).show();
						}
					});
				}

				public void serverErrorHandler(Exception e) {
					// z.B. Fehler Dialog aufploppen lassen
					Log.e("error", "called");
				}
			});
			params = new JSONObject();
			si.call("gibWohnraeume", params);

			// ----------------------------------------------------------------------------------//
			// gibWohnbarrieren
			// ----------------------------------------------------------------------------------//

			pDialog.show();

			si = new ServerInterface();
			si.addListener(new ServerInterfaceListener() {

				public void serverSuccessHandler(JSONObject result) throws JSONException {

					pDialog.dismiss();

					List<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
					JSONArray la = result.getJSONArray("data");

					for (int i = 0; i < la.length(); i++) {
						HashMap<String, String> hm = new HashMap<String, String>();
						hm.put("txt", la.getString(i));
						list.add(hm);
					}
					Log.i("data", la.toString());
					Log.i("msg", result.getString("msg"));

					String[] from = { "txt" };
					int[] to = { R.id.txt };

					// Instantiating an adapter to store each items
					// R.layout.wohnung_listview defines the layout of each item
					SimpleAdapter dataAdapter = new SimpleAdapter(getActivity(), list, R.layout.wohnung_listview, from, to);
					wohnbarrierenList = (ListView) wohnungView.findViewById(R.id.wohnbarrierenList);

					// wohnsituationList.setSelection(kLeistungsartId - 1);
					wohnbarrierenList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
					wohnbarrierenList.setAdapter(dataAdapter);

					wohnbarrierenList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
						public void onItemClick(AdapterView<?> arg0, View v, int position, long id) {
							Toast.makeText(getActivity(),
									"Multiple List: " + wohnbarrierenList.getItemAtPosition(position).toString() + " Position: " + position,
									Toast.LENGTH_SHORT).show();
						}
					});
				}

				public void serverErrorHandler(Exception e) {
					// z.B. Fehler Dialog aufploppen lassen
					Log.e("error", "called");
				}
			});
			params = new JSONObject();
			si.call("gibWohnbarrieren", params);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return wohnungView;
	}

	@Override
	public void onPause() {

		int wohnsituationArray[] = new int [wohnsituationList.getCheckedItemCount()];
		Log.i("onPause: ", "CheckedItemCount: " + wohnsituationList.getCheckedItemCount());
		Log.i("onPause: ", "Count: " + wohnsituationList.getCount());
		Log.i("onPause: ", "sparseBooleanArray: " + wohnsituationList.getCheckedItemPositions());
		int cntChoice = wohnsituationList.getCount();
		SparseBooleanArray sparseBooleanArray = wohnsituationList.getCheckedItemPositions();
		for (int i = 0; i < cntChoice; i++) {
			if (sparseBooleanArray.get(i)) {
				wohnsituationArray[i] = (int) wohnsituationList.getItemIdAtPosition(i);
				Log.i("onPause: ", "WohnungsActivity: " + wohnsituationList.getItemAtPosition(i).toString() + "  Position: " + wohnsituationList.getItemIdAtPosition(i) + "\n");
			}
		}

		// ----------------------------------------------------------------------------------//
		// updateWohnungsdaten
		// ----------------------------------------------------------------------------------//

		try {
			JSONArray dataArray = new JSONArray();
			JSONObject obj1 = new JSONObject();
			JSONObject updateParams = new JSONObject();

			for (int i = 0; i < wohnsituationArray.length; i++) {
				obj1.put("1", wohnsituationArray[i]);
				dataArray.put(obj1);
			}
			
			updateParams.put("wohnsituation", dataArray);
			updateParams.put("kundeId", kundeId);

			ServerInterface si = new ServerInterface();
			si.addListener(new ServerInterfaceListener() {

				public void serverSuccessHandler(JSONObject result) throws JSONException {
					Log.i("UPDATE Wohnsituation: ", result.getString("msg"));
				}

				public void serverErrorHandler(Exception e) {
					// TODO Auto-generated method
					// stub
				}
			});
			si.call("updateWohnsituation", updateParams);
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		super.onPause();
	}

}
