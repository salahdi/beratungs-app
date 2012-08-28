package com.example.beratungskonfigurator.tabs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.example.beratungskonfigurator.R;
import com.example.beratungskonfigurator.ServerInterface;
import com.example.beratungskonfigurator.ServerInterfaceListener;

public class GesundheitActivity extends Fragment {

	private ProgressDialog pDialog;
	private ProgressDialog pDialogUpdate;

	private int currentSelected = 0;
	private int kundeId;

	private static final String ERKRANKUNGEN = "Erkrankungen";
	private static final String PFLEGE = "Pflege erfolgt durch";
	private static final String ALLTAGSKOMPETENZEN = "Alltagskompetenzen";
	private static final String BEOBACHTUNGEN = "Beobachtungen";

	ListView erkrankungenList;
	ListView pflegeList;
	ListView alltagskompetenzenList;
	ListView beobachtungenList;
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

		final View gesundheitView = (View) inflater.inflate(R.layout.tab_gesundheit_layout, container, false);
		final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

		currentSelected = 0;

		final LinearLayout layErkrankungen = (LinearLayout) gesundheitView.findViewById(R.id.layErkrankungen);
		final LinearLayout layPflege = (LinearLayout) gesundheitView.findViewById(R.id.layPflege);
		final LinearLayout layAlltagskompetenzen = (LinearLayout) gesundheitView.findViewById(R.id.layAlltagskompetenzen);
		final LinearLayout layBeobachtungen = (LinearLayout) gesundheitView.findViewById(R.id.layBeobachtungen);

		layErkrankungen.setVisibility(View.VISIBLE);
		layPflege.setVisibility(View.GONE);
		layAlltagskompetenzen.setVisibility(View.GONE);
		layBeobachtungen.setVisibility(View.GONE);

		String[] values = new String[] { ERKRANKUNGEN, PFLEGE, ALLTAGSKOMPETENZEN, BEOBACHTUNGEN };

		SimpleAdapter adapterMainList = new SimpleAdapter(getActivity(), list, R.layout.listview_main, new String[] { "name" },
				new int[] { R.id.name });
		lv = (ListView) gesundheitView.findViewById(R.id.gesundheitList);
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
						layErkrankungen.setVisibility(View.GONE);
						break;
					case 1:
						layPflege.setVisibility(View.GONE);
						break;
					case 2:
						layAlltagskompetenzen.setVisibility(View.GONE);
						break;
					case 3:
						layBeobachtungen.setVisibility(View.GONE);
						break;
					}
					layErkrankungen.setVisibility(View.VISIBLE);
					currentSelected = position;
					break;
				case 1:
					switch (currentSelected) {
					case 0:
						layErkrankungen.setVisibility(View.GONE);
						break;
					case 1:
						layPflege.setVisibility(View.GONE);
						break;
					case 2:
						layAlltagskompetenzen.setVisibility(View.GONE);
						break;
					case 3:
						layBeobachtungen.setVisibility(View.GONE);
						break;
					}
					layPflege.setVisibility(View.VISIBLE);
					currentSelected = position;
					break;
				case 2:
					switch (currentSelected) {
					case 0:
						layErkrankungen.setVisibility(View.GONE);
						break;
					case 1:
						layPflege.setVisibility(View.GONE);
						break;
					case 2:
						layAlltagskompetenzen.setVisibility(View.GONE);
						break;
					case 3:
						layBeobachtungen.setVisibility(View.GONE);
						break;
					}
					layAlltagskompetenzen.setVisibility(View.VISIBLE);
					currentSelected = position;
					break;
				case 3:
					switch (currentSelected) {
					case 0:
						layErkrankungen.setVisibility(View.GONE);
						break;
					case 1:
						layPflege.setVisibility(View.GONE);
						break;
					case 2:
						layAlltagskompetenzen.setVisibility(View.GONE);
						break;
					case 3:
						layBeobachtungen.setVisibility(View.GONE);
						break;
					}
					layBeobachtungen.setVisibility(View.VISIBLE);
					currentSelected = position;
					break;
				}
			}
		});

		try {

			// ----------------------------------------------------------------------------------//
			// gibErkrankungen
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
					erkrankungenList = (ListView) gesundheitView.findViewById(R.id.erkrankungenList);

					// wohnsituationList.setSelection(kLeistungsartId - 1);
					erkrankungenList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
					erkrankungenList.setAdapter(dataAdapter);

					erkrankungenList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
						public void onItemClick(AdapterView<?> arg0, View v, int position, long id) {
						}
					});
				}

				public void serverErrorHandler(Exception e) {
					// z.B. Fehler Dialog aufploppen lassen
					Log.e("error", "called");
				}
			});
			params = new JSONObject();
			si.call("gibErkrankungen", params);

			// ----------------------------------------------------------------------------------//
			// gibPflege
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
					pflegeList = (ListView) gesundheitView.findViewById(R.id.pflegeList);

					// wohnsituationList.setSelection(kLeistungsartId - 1);
					pflegeList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
					pflegeList.setAdapter(dataAdapter);

					pflegeList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
						public void onItemClick(AdapterView<?> arg0, View v, int position, long id) {
						}
					});
				}

				public void serverErrorHandler(Exception e) {
					// z.B. Fehler Dialog aufploppen lassen
					Log.e("error", "called");
				}
			});
			params = new JSONObject();
			si.call("gibPflege", params);

			// ----------------------------------------------------------------------------------//
			// gibAlltagskompetenzen
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
					alltagskompetenzenList = (ListView) gesundheitView.findViewById(R.id.alltagskompetenzenList);

					// wohnsituationList.setSelection(kLeistungsartId - 1);
					alltagskompetenzenList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
					alltagskompetenzenList.setAdapter(dataAdapter);

					alltagskompetenzenList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
						public void onItemClick(AdapterView<?> arg0, View v, int position, long id) {
						}
					});
				}

				public void serverErrorHandler(Exception e) {
					// z.B. Fehler Dialog aufploppen lassen
					Log.e("error", "called");
				}
			});
			params = new JSONObject();
			si.call("gibAlltagskompetenzen", params);

			// ----------------------------------------------------------------------------------//
			// gibBeobachtungen
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
					beobachtungenList = (ListView) gesundheitView.findViewById(R.id.beobachtungenList);

					// wohnsituationList.setSelection(kLeistungsartId - 1);
					beobachtungenList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
					beobachtungenList.setAdapter(dataAdapter);

					beobachtungenList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
						public void onItemClick(AdapterView<?> arg0, View v, int position, long id) {
						}
					});
				}

				public void serverErrorHandler(Exception e) {
					// z.B. Fehler Dialog aufploppen lassen
					Log.e("error", "called");
				}
			});
			params = new JSONObject();
			si.call("gibBeobachtungen", params);

			// ----------------------------------------------------------------------------------//
			// gibKundeErkrankungen
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
						erkrankungenList.setItemChecked((wd.getInt(i)) - 1, true);
					}
				}

				public void serverErrorHandler(Exception e) {
					// z.B. Fehler Dialog aufploppen lassen
					Log.e("error", "called");
				}
			});
			si.call("gibKundeErkrankungen", params);

			// ----------------------------------------------------------------------------------//
			// gibKundePflege
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
						pflegeList.setItemChecked((wd.getInt(i)) - 1, true);
					}
				}

				public void serverErrorHandler(Exception e) {
					// z.B. Fehler Dialog aufploppen lassen
					Log.e("error", "called");
				}
			});
			si.call("gibKundePflege", params);

			// ----------------------------------------------------------------------------------//
			// gibKundeAlltagskompetenzen
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
						alltagskompetenzenList.setItemChecked((wd.getInt(i)) - 1, true);
					}
				}

				public void serverErrorHandler(Exception e) {
					// z.B. Fehler Dialog aufploppen lassen
					Log.e("error", "called");
				}
			});
			si.call("gibKundeAlltagskompetenzen", params);

			// ----------------------------------------------------------------------------------//
			// gibKundeBeobachtungen
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
						beobachtungenList.setItemChecked((wd.getInt(i)) - 1, true);
					}
				}

				public void serverErrorHandler(Exception e) {
					// z.B. Fehler Dialog aufploppen lassen
					Log.e("error", "called");
				}
			});
			si.call("gibKundeBeobachtungen", params);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return gesundheitView;
	}

	static final ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();

	@Override
	public void onPause() {
		lv.setItemChecked(0, true);

		// ----------------------------------------------------------------------------------//
		// insertErkrankungen
		// ----------------------------------------------------------------------------------//
		try {
			String selected = "";
			int count = 0;
			int cntChoice = erkrankungenList.getCount();

			SparseBooleanArray sparseBooleanArray = erkrankungenList.getCheckedItemPositions();

			for (int i = 0; i < cntChoice; i++) {
				if (sparseBooleanArray.get(i)) {
					selected += (erkrankungenList.getItemIdAtPosition(i) + 1) + ".";
					count++;
				}
			}
			if (count != 0) {
				selected = selected.substring(0, selected.length() - 1);
				Log.i("onPause: ", "Selected nach substring: " + selected + "  Count != 0: " + count);
			}

			JSONObject updateParams = new JSONObject();
			updateParams.put("erkrankungen", selected);
			updateParams.put("kundeId", kundeId);
			updateParams.put("countAnz", count);

			ServerInterface si = new ServerInterface();
			si.addListener(new ServerInterfaceListener() {
				public void serverSuccessHandler(JSONObject result) throws JSONException {
					Log.i("INSERT Erkrankungen: ", result.getString("msg"));
				}

				public void serverErrorHandler(Exception e) {
					// TODO Auto-generated method
					// stub
				}
			});
			si.call("insertErkrankungen", updateParams);
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		// ----------------------------------------------------------------------------------//
		// insertPflege
		// ----------------------------------------------------------------------------------//
		try {
			String selected = "";
			int count = 0;
			int cntChoice = pflegeList.getCount();

			SparseBooleanArray sparseBooleanArray = pflegeList.getCheckedItemPositions();

			for (int i = 0; i < cntChoice; i++) {
				if (sparseBooleanArray.get(i)) {
					selected += (pflegeList.getItemIdAtPosition(i) + 1) + ".";
					count++;
				}
			}
			if (count != 0) {
				selected = selected.substring(0, selected.length() - 1);
				Log.i("onPause: ", "Selected nach substring: " + selected + "  Count != 0: " + count);
			}

			JSONObject updateParams = new JSONObject();
			updateParams.put("pflege", selected);
			updateParams.put("kundeId", kundeId);
			updateParams.put("countAnz", count);

			ServerInterface si = new ServerInterface();
			si.addListener(new ServerInterfaceListener() {
				public void serverSuccessHandler(JSONObject result) throws JSONException {
					Log.i("INSERT Pflege: ", result.getString("msg"));
				}

				public void serverErrorHandler(Exception e) {
					// TODO Auto-generated method
					// stub
				}
			});
			si.call("insertPflege", updateParams);
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		// ----------------------------------------------------------------------------------//
		// insertAlltagskompetenzen
		// ----------------------------------------------------------------------------------//
		try {
			String selected = "";
			int count = 0;
			int cntChoice = alltagskompetenzenList.getCount();

			SparseBooleanArray sparseBooleanArray = alltagskompetenzenList.getCheckedItemPositions();

			for (int i = 0; i < cntChoice; i++) {
				if (sparseBooleanArray.get(i)) {
					selected += (alltagskompetenzenList.getItemIdAtPosition(i) + 1) + ".";
					count++;
				}
			}
			if (count != 0) {
				selected = selected.substring(0, selected.length() - 1);
				Log.i("onPause: ", "Selected nach substring: " + selected + "  Count != 0: " + count);
			}

			JSONObject updateParams = new JSONObject();
			updateParams.put("alltagskompetenzen", selected);
			updateParams.put("kundeId", kundeId);
			updateParams.put("countAnz", count);

			ServerInterface si = new ServerInterface();
			si.addListener(new ServerInterfaceListener() {
				public void serverSuccessHandler(JSONObject result) throws JSONException {
					Log.i("INSERT Alltagskompetenzen: ", result.getString("msg"));
				}

				public void serverErrorHandler(Exception e) {
					// TODO Auto-generated method
					// stub
				}
			});
			si.call("insertAlltagskompetenzen", updateParams);
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		// ----------------------------------------------------------------------------------//
		// insertBeobachtungen
		// ----------------------------------------------------------------------------------//
		try {
			String selected = "";
			int count = 0;
			int cntChoice = beobachtungenList.getCount();

			SparseBooleanArray sparseBooleanArray = beobachtungenList.getCheckedItemPositions();

			for (int i = 0; i < cntChoice; i++) {
				if (sparseBooleanArray.get(i)) {
					selected += (beobachtungenList.getItemIdAtPosition(i) + 1) + ".";
					count++;
				}
			}
			if (count != 0) {
				selected = selected.substring(0, selected.length() - 1);
				Log.i("onPause: ", "Selected nach substring: " + selected + "  Count != 0: " + count);
			}

			JSONObject updateParams = new JSONObject();
			updateParams.put("beobachtungen", selected);
			updateParams.put("kundeId", kundeId);
			updateParams.put("countAnz", count);

			ServerInterface si = new ServerInterface();
			si.addListener(new ServerInterfaceListener() {
				public void serverSuccessHandler(JSONObject result) throws JSONException {
					Log.i("INSERT Beobachtungen: ", result.getString("msg"));
				}

				public void serverErrorHandler(Exception e) {
					// TODO Auto-generated method
					// stub
				}
			});
			si.call("insertBeobachtungen", updateParams);
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		super.onPause();
	}

}
