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

import com.example.beratungskonfigurator.FolgeberatungActivity;
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

	private String wohnsituationId = "";

	ListView wohnsituationList;
	ListView wohnformList;
	ListView wohnumfeldList;
	ListView wohnraeumeList;
	ListView wohnbarrierenList;
	ListView lv;
	

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

		layWohnsituation.setVisibility(View.VISIBLE);
		layWohnform.setVisibility(View.GONE);
		layWohninformation.setVisibility(View.GONE);
		layWohnumfeld.setVisibility(View.GONE);
		layWohnraeume.setVisibility(View.GONE);
		layWohnbarrieren.setVisibility(View.GONE);

		String[] values = new String[] { WOHNSITUATION, WOHNFORM, WOHNINFORMATION, WOHNUMFELD, WOHNRAEUME, WOHNBARRIEREN };

		/*ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), R.layout.wohnung_listview, values);
		final ListView lv = (ListView) wohnungView.findViewById(R.id.wohnungList);*/
		
		SimpleAdapter adapterMainList = new SimpleAdapter(getActivity(), list, R.layout.listview_main, new String[] { "name" }, new int[] { R.id.name });
		lv = (ListView) wohnungView.findViewById(R.id.wohnungList);
		lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		list.clear();
		HashMap<String, String> temp = new HashMap<String, String>();
		for (int i = 0; i < values.length; i++) {
			temp.put("name", values[i]);
			list.add(temp);
			temp = new HashMap<String, String>();
		}
		lv.setAdapter(adapterMainList);
		lv.setItemChecked(0, true);

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
					SimpleAdapter dataAdapter = new SimpleAdapter(getActivity(), list, R.layout.listview_checkable, from, to);
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
					SimpleAdapter dataAdapter = new SimpleAdapter(getActivity(), list, R.layout.listview_checkable, from, to);
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
					SimpleAdapter dataAdapter = new SimpleAdapter(getActivity(), list, R.layout.listview_checkable, from, to);
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
					SimpleAdapter dataAdapter = new SimpleAdapter(getActivity(), list, R.layout.listview_checkable, from, to);
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
					SimpleAdapter dataAdapter = new SimpleAdapter(getActivity(), list, R.layout.listview_checkable, from, to);
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

			// ----------------------------------------------------------------------------------//
			// gibKundeWohnsituation
			// ----------------------------------------------------------------------------------//

			params = new JSONObject();
			pDialog.show();

			params.put("kundeId", kundeId);

			si = new ServerInterface();
			si.addListener(new ServerInterfaceListener() {

				public void serverSuccessHandler(JSONObject result) throws JSONException {

					pDialog.dismiss();

					JSONArray wd = result.getJSONArray("data");

					for (int i = 0; i < wd.length(); i++) {
						wohnsituationList.setItemChecked((wd.getInt(i)) - 1, true);
					}
				}

				public void serverErrorHandler(Exception e) {
					// z.B. Fehler Dialog aufploppen lassen
					Log.e("error", "called");
				}
			});
			si.call("gibKundeWohnsituation", params);

			// ----------------------------------------------------------------------------------//
			// gibKundeWohnform
			// ----------------------------------------------------------------------------------//

			params = new JSONObject();
			pDialog.show();

			params.put("kundeId", kundeId);

			si = new ServerInterface();
			si.addListener(new ServerInterfaceListener() {

				public void serverSuccessHandler(JSONObject result) throws JSONException {

					pDialog.dismiss();

					JSONArray wd = result.getJSONArray("data");

					for (int i = 0; i < wd.length(); i++) {
						wohnformList.setItemChecked((wd.getInt(i)) - 1, true);
					}
				}

				public void serverErrorHandler(Exception e) {
					// z.B. Fehler Dialog aufploppen lassen
					Log.e("error", "called");
				}
			});
			si.call("gibKundeWohnform", params);

			// ----------------------------------------------------------------------------------//
			// gibKundeWohnumfeld
			// ----------------------------------------------------------------------------------//

			params = new JSONObject();
			pDialog.show();

			params.put("kundeId", kundeId);

			si = new ServerInterface();
			si.addListener(new ServerInterfaceListener() {

				public void serverSuccessHandler(JSONObject result) throws JSONException {

					pDialog.dismiss();

					JSONArray wd = result.getJSONArray("data");

					for (int i = 0; i < wd.length(); i++) {
						wohnumfeldList.setItemChecked((wd.getInt(i)) - 1, true);
					}
				}

				public void serverErrorHandler(Exception e) {
					// z.B. Fehler Dialog aufploppen lassen
					Log.e("error", "called");
				}
			});
			si.call("gibKundeWohnumfeld", params);

			// ----------------------------------------------------------------------------------//
			// gibKundeWohnraeume
			// ----------------------------------------------------------------------------------//

			params = new JSONObject();
			pDialog.show();

			params.put("kundeId", kundeId);

			si = new ServerInterface();
			si.addListener(new ServerInterfaceListener() {

				public void serverSuccessHandler(JSONObject result) throws JSONException {

					pDialog.dismiss();

					JSONArray wd = result.getJSONArray("data");

					for (int i = 0; i < wd.length(); i++) {
						wohnraeumeList.setItemChecked((wd.getInt(i)) - 1, true);
					}
				}

				public void serverErrorHandler(Exception e) {
					// z.B. Fehler Dialog aufploppen lassen
					Log.e("error", "called");
				}
			});
			si.call("gibKundeWohnraeume", params);

			// ----------------------------------------------------------------------------------//
			// gibKundeWohnbarrieren
			// ----------------------------------------------------------------------------------//

			params = new JSONObject();
			pDialog.show();

			params.put("kundeId", kundeId);

			si = new ServerInterface();
			si.addListener(new ServerInterfaceListener() {

				public void serverSuccessHandler(JSONObject result) throws JSONException {

					pDialog.dismiss();

					JSONArray wd = result.getJSONArray("data");

					for (int i = 0; i < wd.length(); i++) {
						wohnbarrierenList.setItemChecked((wd.getInt(i)) - 1, true);
					}
				}

				public void serverErrorHandler(Exception e) {
					// z.B. Fehler Dialog aufploppen lassen
					Log.e("error", "called");
				}
			});
			si.call("gibKundeWohnbarrieren", params);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return wohnungView;
	}

	
	@Override
	public void onPause() {
		
		lv.setItemChecked(0, true);

		// ----------------------------------------------------------------------------------//
		// insertWohnsituation
		// ----------------------------------------------------------------------------------//

		try {
			String selected = "";
			int count = 0;
			int cntChoice = wohnsituationList.getCount();

			SparseBooleanArray sparseBooleanArray = wohnsituationList.getCheckedItemPositions();

			for (int i = 0; i < cntChoice; i++) {
				if (sparseBooleanArray.get(i)) {
					selected += (wohnsituationList.getItemIdAtPosition(i) + 1) + ".";
					count++;
				}
			}
			if (count != 0) {
				selected = selected.substring(0, selected.length() - 1);
				Log.i("onPause: ", "Selected nach substring: " + selected + "  Count != 0: " + count);
			}

			JSONObject updateParams = new JSONObject();
			updateParams.put("wohnsituation", selected);
			updateParams.put("kundeId", kundeId);
			updateParams.put("countAnz", count);

			ServerInterface si = new ServerInterface();
			si.addListener(new ServerInterfaceListener() {
				public void serverSuccessHandler(JSONObject result) throws JSONException {
					Log.i("INSERT Wohnsituation: ", result.getString("msg"));
				}

				public void serverErrorHandler(Exception e) {
					// TODO Auto-generated method
					// stub
				}
			});
			si.call("insertWohnsituation", updateParams);
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		// }

		// ----------------------------------------------------------------------------------//
		// insertWohnform
		// ----------------------------------------------------------------------------------//

		try {
			String selected = "";
			int count = 0;
			int cntChoice = wohnformList.getCount();
			SparseBooleanArray sparseBooleanArray = wohnformList.getCheckedItemPositions();

			for (int i = 0; i < cntChoice; i++) {
				if (sparseBooleanArray.get(i)) {
					selected += (wohnformList.getItemIdAtPosition(i) + 1) + ".";
					count++;
				}
			}
			if (count != 0) {
				selected = selected.substring(0, selected.length() - 1);
				Log.i("onPause: ", "Selected nach substring: " + selected + "  Count != 0: " + count);
			}

			JSONObject updateParams = new JSONObject();
			updateParams.put("wohnform", selected);
			updateParams.put("kundeId", kundeId);
			updateParams.put("countAnz", count);

			ServerInterface si = new ServerInterface();
			si.addListener(new ServerInterfaceListener() {
				public void serverSuccessHandler(JSONObject result) throws JSONException {
					Log.i("INSERT Wohnform: ", result.getString("msg"));
				}

				public void serverErrorHandler(Exception e) {
					// TODO Auto-generated method
					// stub
				}
			});
			si.call("insertWohnform", updateParams);
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		// ----------------------------------------------------------------------------------//
		// insertWohnumfeld
		// ----------------------------------------------------------------------------------//

		try {
			String selected = "";
			int count = 0;
			int cntChoice = wohnumfeldList.getCount();
			SparseBooleanArray sparseBooleanArray = wohnumfeldList.getCheckedItemPositions();

			for (int i = 0; i < cntChoice; i++) {
				if (sparseBooleanArray.get(i)) {
					selected += (wohnumfeldList.getItemIdAtPosition(i) + 1) + ".";
					count++;
				}
			}
			if (count != 0) {
				selected = selected.substring(0, selected.length() - 1);
				Log.i("onPause: ", "Selected nach substring: " + selected + "  Count != 0: " + count);
			}

			JSONObject updateParams = new JSONObject();
			updateParams.put("wohnumfeld", selected);
			updateParams.put("kundeId", kundeId);
			updateParams.put("countAnz", count);

			ServerInterface si = new ServerInterface();
			si.addListener(new ServerInterfaceListener() {
				public void serverSuccessHandler(JSONObject result) throws JSONException {
					Log.i("INSERT Wohnumfeld: ", result.getString("msg"));
				}

				public void serverErrorHandler(Exception e) {
					// TODO Auto-generated method
					// stub
				}
			});
			si.call("insertWohnumfeld", updateParams);
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		// ----------------------------------------------------------------------------------//
		// insertWohnraeume
		// ----------------------------------------------------------------------------------//

		try {
			String selected = "";
			int count = 0;
			int cntChoice = wohnraeumeList.getCount();
			SparseBooleanArray sparseBooleanArray = wohnraeumeList.getCheckedItemPositions();

			for (int i = 0; i < cntChoice; i++) {
				if (sparseBooleanArray.get(i)) {
					selected += (wohnraeumeList.getItemIdAtPosition(i) + 1) + ".";
					count++;
				}
			}
			if (count != 0) {
				selected = selected.substring(0, selected.length() - 1);
				Log.i("onPause: ", "Selected nach substring: " + selected + "  Count != 0: " + count);
			}

			JSONObject updateParams = new JSONObject();
			updateParams.put("wohnraeume", selected);
			updateParams.put("kundeId", kundeId);
			updateParams.put("countAnz", count);

			ServerInterface si = new ServerInterface();
			si.addListener(new ServerInterfaceListener() {
				public void serverSuccessHandler(JSONObject result) throws JSONException {
					Log.i("INSERT Wohnräume: ", result.getString("msg"));
				}

				public void serverErrorHandler(Exception e) {
					// TODO Auto-generated method
					// stub
				}
			});
			si.call("insertWohnraeume", updateParams);
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		// ----------------------------------------------------------------------------------//
		// insertWohnbarrieren
		// ----------------------------------------------------------------------------------//

		try {
			String selected = "";
			int count = 0;
			int cntChoice = wohnbarrierenList.getCount();
			SparseBooleanArray sparseBooleanArray = wohnbarrierenList.getCheckedItemPositions();

			for (int i = 0; i < cntChoice; i++) {
				if (sparseBooleanArray.get(i)) {
					selected += (wohnbarrierenList.getItemIdAtPosition(i) + 1) + ".";
					count++;
				}
			}
			if (count != 0) {
				selected = selected.substring(0, selected.length() - 1);
				Log.i("onPause: ", "Selected nach substring: " + selected + "  Count != 0: " + count);
			}

			JSONObject updateParams = new JSONObject();
			updateParams.put("wohnbarrieren", selected);
			updateParams.put("kundeId", kundeId);
			updateParams.put("countAnz", count);

			ServerInterface si = new ServerInterface();
			si.addListener(new ServerInterfaceListener() {
				public void serverSuccessHandler(JSONObject result) throws JSONException {
					Log.i("INSERT Wohnbarrieren: ", result.getString("msg"));
				}

				public void serverErrorHandler(Exception e) {
					// TODO Auto-generated method
					// stub
				}
			});
			si.call("insertWohnbarrieren", updateParams);
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		super.onPause();
	}
	
	static final ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();

}
