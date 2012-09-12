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
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.example.beratungskonfigurator.R;
import com.example.beratungskonfigurator.dialog.SzenarioDialog;
import com.example.beratungskonfigurator.server.ServerInterface;
import com.example.beratungskonfigurator.server.ServerInterfaceListener;

public class AnwendungsfallActivity extends Fragment {

	private ProgressDialog pDialog;
	private ProgressDialog pDialogUpdate;

	private int currentSelected = 0;
	private int kundeId;
	private int anwendungsfallId = 0;
	private String anwendungsfallName;

	private static final String KOMFORT = "Komfort";
	private static final String ENERGIE = "Energie";
	private static final String MOBILITAET = "Mobilität";
	private static final String GESUNDHEIT = "Gesundheit";
	private static final String HAUSHALT = "Haushalt und Versorgung";
	private static final String SICHERHEIT = "Sicherheit im Wohnumfeld";
	private static final String MEDIEN = "Medien und Unterhaltung";
	
	JSONArray anId;
	JSONArray kategorien;
	JSONArray la;

	ListView kategorieList;
	ListView anwendungsfallList;
	

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

		final View anwendungsfallView = (View) inflater.inflate(R.layout.tab_anwendungsfall_layout, container, false);
		final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
		
		//TextView titelAnwendungsfall = (TextView) anwendungsfallView.findViewById(R.id.titelAnwendungsfall);
		//titelAnwendungsfall.setText("Anwendungsfall auswählen:");

		String[] values = new String[] { KOMFORT, ENERGIE, MOBILITAET, GESUNDHEIT, HAUSHALT, SICHERHEIT, MEDIEN };

		SimpleAdapter adapterMainList = new SimpleAdapter(getActivity(), list, R.layout.listview_kategorie, new String[] { "icon", "name" },
				new int[] { R.id.icon, R.id.name });
		kategorieList = (ListView) anwendungsfallView.findViewById(R.id.kategorieList);
		kategorieList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		list.clear();
		HashMap<String, String> temp = new HashMap<String, String>();
		for (int i = 0; i < values.length; i++) {
			temp.put("name", values[i]);
			switch (i) {
			case 0:
				temp.put("icon", String.valueOf(R.drawable.icon_kategorie1));
				break;
			case 1:
				temp.put("icon", String.valueOf(R.drawable.icon_kategorie2));
				break;
			case 2:
				temp.put("icon", String.valueOf(R.drawable.icon_kategorie3));
				break;
			case 3:
				temp.put("icon", String.valueOf(R.drawable.icon_kategorie4));
				break;
			case 4:
				temp.put("icon", String.valueOf(R.drawable.icon_kategorie5));
				break;
			case 5:
				temp.put("icon", String.valueOf(R.drawable.icon_kategorie6));
				break;
			case 6:
				temp.put("icon", String.valueOf(R.drawable.icon_kategorie7));
				break;
			}
			list.add(temp);
			temp = new HashMap<String, String>();
		}
		kategorieList.setAdapter(adapterMainList);

		kategorieList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View v, int position, long id) {

				// ----------------------------------------------------------------------------------//
				// gibAnwendungsfall
				// ----------------------------------------------------------------------------------//

				try {
					JSONObject params = new JSONObject();
					ServerInterface si;
					pDialog.show();

					String selectedKategorie = "";
					final ArrayList<Integer> arrayList = new ArrayList<Integer>();
					Log.i("COUNT CHECKED: ", "COUNT CHECKED: " + kategorieList.getCheckedItemCount());
					int count = 0;
					int cntChoice = kategorieList.getCount();
					SparseBooleanArray sparseBooleanArray = kategorieList.getCheckedItemPositions();

					for (int i = 0; i < cntChoice; i++) {
						if (sparseBooleanArray.get(i)) {
							int item = (int)(kategorieList.getItemIdAtPosition(i)+1);
							selectedKategorie += (kategorieList.getItemIdAtPosition(i) + 1) + ",";
							arrayList.add(item);
							count++;
						}
					}
					Log.i("COUNT: ", "COUNT: " + count);
					if (count != 0) {
						selectedKategorie = selectedKategorie.substring(0, selectedKategorie.length() - 1);
						Log.i("selectedKategorie: ", "Selected nach substring: " + selectedKategorie + "  Count != 0: " + count);

						params.put("kategorieId", selectedKategorie);

						si = new ServerInterface();
						si.addListener(new ServerInterfaceListener() {

							public void serverSuccessHandler(JSONObject result) throws JSONException {

								pDialog.dismiss();

								List<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
								
								la = result.getJSONArray("data");
								kategorien = result.getJSONArray("kategorie");
								anId = result.getJSONArray("anwendungsfallId");
								
								Log.i("anwendungsfallId:", anId.toString());
								
								Log.i("Kategorie:", kategorien.toString());

								SimpleAdapter adapterMainList = new SimpleAdapter(getActivity(), list, R.layout.listview_anwendungsfall,
										new String[] { "name", "iKategorie1", "iKategorie2", "iKategorie3", "iKategorie4", "iKategorie5",
												"iKategorie6", "iKategorie7" }, new int[] { R.id.name, R.id.iKategorie1, R.id.iKategorie2,
												R.id.iKategorie3, R.id.iKategorie4, R.id.iKategorie5, R.id.iKategorie6, R.id.iKategorie7 });
								
								anwendungsfallList = (ListView) anwendungsfallView.findViewById(R.id.anwendungsfallList);
								list.clear();
								
								for (int i = 0; i < la.length(); i++) {
									HashMap<String, String> temp = new HashMap<String, String>();
									temp.put("name", la.getString(i));
									temp.put("iKategorie1", String.valueOf(R.drawable.icon_kategorie1_in));
									temp.put("iKategorie2", String.valueOf(R.drawable.icon_kategorie2_in));
									temp.put("iKategorie3", String.valueOf(R.drawable.icon_kategorie3_in));
									temp.put("iKategorie4", String.valueOf(R.drawable.icon_kategorie4_in));
									temp.put("iKategorie5", String.valueOf(R.drawable.icon_kategorie5_in));
									temp.put("iKategorie6", String.valueOf(R.drawable.icon_kategorie6_in));
									temp.put("iKategorie7", String.valueOf(R.drawable.icon_kategorie7_in));
									
									String[] strings = kategorien.getString(i).split("\\-");
							        for(int k = 0; k < strings.length; k++){
							            int iOut = Integer.valueOf(strings[k]);
										switch (iOut) {
										case 1:
											temp.put("iKategorie1", String.valueOf(R.drawable.icon_kategorie1));
											break;
										case 2:
											temp.put("iKategorie2", String.valueOf(R.drawable.icon_kategorie2));
											break;
										case 3:
											temp.put("iKategorie3", String.valueOf(R.drawable.icon_kategorie3));
											break;
										case 4:
											temp.put("iKategorie4", String.valueOf(R.drawable.icon_kategorie4));
											break;
										case 5:
											temp.put("iKategorie5", String.valueOf(R.drawable.icon_kategorie5));
											break;
										case 6:
											temp.put("iKategorie6", String.valueOf(R.drawable.icon_kategorie6));
											break;
										case 7:
											temp.put("iKategorie7", String.valueOf(R.drawable.icon_kategorie7));
											break;
										}
									}
									list.add(temp);
								}
								Log.i("data", la.toString());
								anwendungsfallList.setAdapter(adapterMainList);

								anwendungsfallList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
									public void onItemClick(AdapterView<?> arg0, View v, int position, long id) {
										
										
										try {
											anwendungsfallName = la.getString(position);
											anwendungsfallId = anId.getInt(position);
											Log.i("anwendungsfallLIST:", "anwendungsfallId: "+anwendungsfallId + " Position: "+position);
										} catch (JSONException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}
										
										SzenarioDialog customDialog = new SzenarioDialog(getActivity(), anwendungsfallId, kundeId, anwendungsfallName);
										customDialog.show();
									}
								});
							}

							public void serverErrorHandler(Exception e) {
								// z.B. Fehler Dialog aufploppen lassen
								Log.e("error", "called");
							}
						});
						si.call("gibAnwendungsfall", params);
					}else {
						pDialog.dismiss();
						if(count==0){
							anwendungsfallList.setAdapter(null);
						}
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		});

		return anwendungsfallView;
	}
	
	@Override
	public void onPause() {
		kategorieList.clearChoices();
		super.onPause();
	}

	static final ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();

}
