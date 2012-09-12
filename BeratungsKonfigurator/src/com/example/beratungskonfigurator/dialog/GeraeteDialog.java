package com.example.beratungskonfigurator.dialog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.TabHost;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.example.beratungskonfigurator.ListViewButtonAdapter;
import com.example.beratungskonfigurator.ListViewSpinnerAdapter;
import com.example.beratungskonfigurator.NumberPic;
import com.example.beratungskonfigurator.R;
import com.example.beratungskonfigurator.server.ServerInterface;
import com.example.beratungskonfigurator.server.ServerInterfaceListener;

public class GeraeteDialog extends Dialog {

	private ProgressDialog pDialog;
	private ListView raeumeListView;
	private ListView geraeteListView;
	private ListView geraetestandortListView;

	private int raumId;

	private JSONArray jsonGeraeteList = new JSONArray();
	private JSONArray jsonGeraeteIdList = new JSONArray();

	private JSONArray jsonRaumIdArray = new JSONArray();
	private JSONArray jsonAnzGeraeteArray = new JSONArray();
	private JSONArray jsonGeraeteId = new JSONArray();
	private JSONArray jsonGeraeteKundeName = new JSONArray();
	private JSONArray jsonKundeGeraetestandortId = new JSONArray();
	private JSONArray jsonGeraetestandortList = new JSONArray();
	private JSONArray jsonGeraetestandortGeraeteId = new JSONArray();
	private JSONArray jsonGeraetestandortGeraeteName = new JSONArray();

	ListViewSpinnerAdapter listAdapterSpinner;
	ListViewButtonAdapter listAdapter;
	private int mKundeId;
	private int mSzenarioId;
	private Context mContext;

	EditText etNumber;
	List<String> anzList = new ArrayList<String>();
	List<String> gerList = new ArrayList<String>();
	List<String> gerNameList = new ArrayList<String>();
	List<Integer> gerIdList = new ArrayList<Integer>();
	List<String> geraetestandortList = new ArrayList<String>();
	List<Integer> geraetestandortListKundeId = new ArrayList<Integer>();

	SimpleAdapter dataAdapter;
	SimpleAdapter dataAdapterRaeume;
	SimpleAdapter dataAdapterGeraetestandort;

	ArrayList<Integer> selSzenarioList = new ArrayList<Integer>();

	// Constructor
	public GeraeteDialog(final Context context, int kundeId, int szenarioId) {
		super(context);

		mContext = context;
		pDialog = new ProgressDialog(mContext);
		pDialog.setMessage("Lade Daten!");
		pDialog.setIndeterminate(false);
		pDialog.setCancelable(false);

		mKundeId = kundeId;
		mSzenarioId = szenarioId;

		// get this window's layout parameters so we can change the position
		WindowManager.LayoutParams paramsLayout = getWindow().getAttributes();

		// change the position. 0,0 is center
		paramsLayout.x = 0;
		paramsLayout.y = 0;
		paramsLayout.height = WindowManager.LayoutParams.FILL_PARENT;
		paramsLayout.width = WindowManager.LayoutParams.FILL_PARENT;
		this.getWindow().setAttributes(paramsLayout);

		setCancelable(false);

		// no title on this dialog
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.geraete_dialog_layout);

		TextView titelGeraetestandort = (TextView) findViewById(R.id.titelGeraetestandort);
		titelGeraetestandort.setText("Standort für das ausgewählte Gerät:");
		Button bCloseGeraete = (Button) findViewById(R.id.bCloseGeraete);
		bCloseGeraete.setBackgroundResource(R.drawable.button_close);
		bCloseGeraete.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {

				insertKundeGeraete();
				dismiss();
			}
		});

		try {

			// ----------------------------------------------------------------------------------//
			// gibKundeRaumauswahlName
			// ----------------------------------------------------------------------------------//

			pDialog.show();

			JSONObject params = new JSONObject();
			params.put("kundeId", mKundeId);
			params.put("szenarioId", mSzenarioId);

			ServerInterface si = new ServerInterface();
			si.addListener(new ServerInterfaceListener() {

				public void serverSuccessHandler(JSONObject result) throws JSONException {

					List<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
					JSONArray la = result.getJSONArray("data");
					jsonRaumIdArray = result.getJSONArray("raumId");

					if (la.isNull(0)) {
						AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
						builder.setTitle("Kein Räume ausgewählt!");
						builder.setIcon(R.drawable.icon_kategorie1);
						builder.setMessage(
								"Sie haben noch keine Räume für Ihre persönliche Geräteeinstellungen ausgewählt! Wählen Sie zuerst unter dem Rubrik Raumauswahl Ihre Räume für dieses Szenario aus!")
								.setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog, int id) {
									}
								});
						AlertDialog alert = builder.create();
						alert.show();
						pDialog.dismiss();
						dismiss();
					} else {

						for (int i = 0; i < la.length(); i++) {
							HashMap<String, String> hm = new HashMap<String, String>();
							hm.put("name", la.getString(i));
							hm.put("icon", String.valueOf(R.drawable.listitem_refresh));
							list.add(hm);
						}
						Log.i("data", la.toString());
						Log.i("msg", result.getString("msg"));

						dataAdapterRaeume = new SimpleAdapter(mContext, list, R.layout.listview_raeume_einstellungen,
								new String[] { "name", "icon" }, new int[] { R.id.name, R.id.icon });
						raeumeListView = (ListView) findViewById(R.id.raeumeList);
						raeumeListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
						raeumeListView.setAdapter(dataAdapterRaeume);
						raeumeListView.setItemChecked(0, true);

						// ----------------------------------------------------------------------------------//
						// gibGeraete
						// ----------------------------------------------------------------------------------//

						ServerInterface si = new ServerInterface();
						si.addListener(new ServerInterfaceListener() {

							public void serverSuccessHandler(JSONObject result) throws JSONException {

								jsonGeraeteList = result.getJSONArray("data");
								jsonGeraeteIdList = result.getJSONArray("geraeteId");
								anzList.clear();

								for (int i = 0; i < jsonGeraeteList.length(); i++) {
									gerList.add(jsonGeraeteList.getString(i));
									anzList.add("0");
								}

								listAdapter = new ListViewButtonAdapter(mContext, gerList, anzList);
								geraeteListView = (ListView) findViewById(R.id.geraeteList);
								geraeteListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
								geraeteListView.setAdapter(listAdapter);
								geraeteListView.setOnFocusChangeListener(new OnFocusChangeListener() {
									public void onFocusChange(View v, boolean hasFocus) {
										geraeteListView.clearChoices();
										for (int i = 0; i < listAdapter.getAnzGeraeteListeAll().size(); i++) {
											if (!listAdapter.getAnzGeraeteListeAll().get(i).equals("0")) {
												geraeteListView.setItemChecked(i, true);
											} else {
												geraeteListView.setItemChecked(i, false);
											}
										}

									}
								});
								geraeteListView.setOnScrollListener(new OnScrollListener() {
									public void onScrollStateChanged(AbsListView view, int scrollState) {
									}

									public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
										// TODO Auto-generated method stub
									}
								});

							}

							public void serverErrorHandler(Exception e) {
								// z.B. Fehler Dialog aufploppen lassen
								Log.e("error", "called");
							}
						});
						JSONObject params = new JSONObject();
						si.call("gibGeraete", params);

						raeumeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
							public void onItemClick(AdapterView<?> arg0, View v, int position, long id) {

								insertKundeGeraete();

								gibKundeGeraete(position);
								gibKundeGeraetestandort(position);
								gibGeraetestandortGeraete(position);

							}
						});

						gibKundeGeraete(0);
						gibGeraetestandort();
						gibKundeGeraetestandort(0);
						gibGeraetestandortGeraete(0);
						pDialog.dismiss();
					}

				}

				public void serverErrorHandler(Exception e) {
					// z.B. Fehler Dialog aufploppen lassen
					Log.e("error", "called");
				}
			});
			si.call("gibKundeRaumauswahlName", params);

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// get our tabHost from the xml
		final TabHost tabs = (TabHost) findViewById(R.id.TabHost01);
		tabs.setup();

		// create tab 1
		TabHost.TabSpec tab1 = tabs.newTabSpec("Geräte im Raum");
		tab1.setContent(R.id.layGeraete);
		tab1.setIndicator("Geräte im Raum");
		tabs.addTab(tab1);

		// create tab 2
		TabHost.TabSpec tab2 = tabs.newTabSpec("Gerätestandort");
		tab2.setContent(R.id.layGeraetestandort);
		tab2.setIndicator("Gerätestandort");
		tabs.addTab(tab2);

	}

	static final ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();

	public boolean isNullList(List<String> liste) {

		if (liste.contains("0")) {
			for (Object o : liste) {
				if (!o.equals("0"))
					return false;
			}
		}
		return true;
	}

	public void gibGeraetestandortGeraete(int position) {

		// ----------------------------------------------------------------------------------//
		// gibGeraetestandortGeraete
		// ----------------------------------------------------------------------------------//

		try {
			raumId = jsonRaumIdArray.getInt(position);

			JSONObject params = new JSONObject();
			params.put("kundeId", mKundeId);
			params.put("wohnraeumeId", raumId);
			params.put("szenarioId", mSzenarioId);
			Log.i("Gerätestandort ADAPTER", "Kunde ID: " + mKundeId);
			Log.i("Gerätestandort ADAPTER", "Raum ID: " + raumId);
			Log.i("Gerätestandort ADAPTER", "Szenario ID: " + mSzenarioId);

			pDialog.show();
			ServerInterface si = new ServerInterface();
			si.addListener(new ServerInterfaceListener() {

				public void serverSuccessHandler(JSONObject result) throws JSONException {

					gerNameList.clear();
					gerIdList.clear();
					jsonGeraetestandortGeraeteName = result.getJSONArray("data");
					jsonGeraetestandortGeraeteId = result.getJSONArray("geraeteId");

					for (int i = 0; i < jsonGeraetestandortGeraeteName.length(); i++) {
						gerNameList.add(jsonGeraetestandortGeraeteName.getString(i));
						gerIdList.add(jsonGeraetestandortGeraeteId.getInt(i));
					}
					Log.i("Gerätestandort ADAPTER", "GERAETE JSONARRAY: " + jsonGeraetestandortGeraeteName);
					Log.i("Gerätestandort ADAPTER", "GERAETE Namensliste: " + gerNameList);
					Log.i("Gerätestandort ADAPTER", "GERAETE ID: " + gerIdList);
					Log.i("Gerätestandort ADAPTER", "GERAETE Standortliste: " + geraetestandortList);
					Log.i("Gerätestandort ADAPTER", "GERAETE Standortliste Kunde: " + geraetestandortListKundeId);

					listAdapterSpinner = new ListViewSpinnerAdapter(mContext, gerNameList, gerIdList, geraetestandortList, geraetestandortListKundeId);
					geraetestandortListView = (ListView) findViewById(R.id.geraetestandortList);
					geraetestandortListView.setEnabled(false);
					geraetestandortListView.setAdapter(listAdapterSpinner);

					pDialog.dismiss();
				}

				public void serverErrorHandler(Exception e) {
					// z.B. Fehler Dialog aufploppen lassen
					Log.e("error", "called");
				}
			});
			si.call("gibGeraetestandortGeraete", params);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void gibGeraetestandort() {

		// ----------------------------------------------------------------------------------//
		// gibGeraetestandort
		// ----------------------------------------------------------------------------------//

		pDialog.show();

		ServerInterface si = new ServerInterface();
		si.addListener(new ServerInterfaceListener() {

			public void serverSuccessHandler(JSONObject result) throws JSONException {

				geraetestandortList = new ArrayList<String>();

				jsonGeraetestandortList = result.getJSONArray("data");

				for (int i = 0; i < jsonGeraetestandortList.length(); i++) {
					geraetestandortList.add(jsonGeraetestandortList.getString(i));
				}
				Log.i("data Gerätestandort", jsonGeraetestandortList.toString());

				pDialog.dismiss();
			}

			public void serverErrorHandler(Exception e) {
				// z.B. Fehler Dialog aufploppen lassen
				Log.e("error", "called");
			}
		});
		JSONObject params = new JSONObject();
		si.call("gibGeraetestandort", params);
	}

	public void gibKundeGeraete(int position) {

		// ----------------------------------------------------------------------------------//
		// gibKundeGeraete
		// ----------------------------------------------------------------------------------//

		try {
			raumId = jsonRaumIdArray.getInt(position);
			JSONObject params = new JSONObject();
			pDialog.show();

			params.put("kundeId", mKundeId);
			params.put("wohnraeumeId", raumId);
			params.put("szenarioId", mSzenarioId);

			ServerInterface si = new ServerInterface();
			si.addListener(new ServerInterfaceListener() {

				public void serverSuccessHandler(JSONObject result) throws JSONException {

					jsonGeraeteKundeName = result.getJSONArray("geraeteKunde");
					jsonGeraeteId = result.getJSONArray("data");
					jsonAnzGeraeteArray = result.getJSONArray("anzGeraete");

					anzList.clear();
					geraeteListView.clearChoices();
					Log.i("geräte ID", "Geräte ID: " + jsonGeraeteId);
					Log.i("geräte ID", "Geräte Kunde NAME: " + jsonGeraeteKundeName);
					Log.i("geräte ID", "Anzahl Geräte ID: " + jsonAnzGeraeteArray);

					if (!jsonGeraeteId.isNull(0)) {
						boolean treffer = false;
						for (int i = 0; i < jsonGeraeteIdList.length(); i++) {
							for (int k = 0; k < jsonGeraeteId.length(); k++) {
								if (jsonGeraeteIdList.getString(i).equals(jsonGeraeteId.getString(k)) && jsonAnzGeraeteArray.getInt(k) > 0) {
									anzList.add(jsonAnzGeraeteArray.getString(k));
									geraeteListView.setItemChecked((jsonGeraeteId.getInt(k)) - 1, true);
									treffer = true;
									break;
								} else {
									treffer = false;
								}
							}
							if (!treffer) {
								anzList.add("0");
							}
						}
					} else {
						Log.i("ELSE", "ELSE: " + jsonGeraeteId.isNull(0));
						geraeteListView.clearChoices();
						for (int i = 0; i < jsonGeraeteIdList.length(); i++) {
							anzList.add("0");
						}
					}
					Log.i("anzList", "anzList: " + anzList);
					geraeteListView.invalidateViews();

					pDialog.dismiss();
				}

				public void serverErrorHandler(Exception e) {
					// z.B. Fehler Dialog aufploppen lassen
					Log.e("error", "called");
				}
			});
			si.call("gibKundeGeraete", params);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void gibKundeGeraetestandort(int position) {

		// ----------------------------------------------------------------------------------//
		// gibKundeGeraetestandort
		// ----------------------------------------------------------------------------------//

		try {
			raumId = jsonRaumIdArray.getInt(position);
			JSONObject params = new JSONObject();
			pDialog.show();

			params.put("kundeId", mKundeId);
			params.put("wohnraeumeId", raumId);
			params.put("szenarioId", mSzenarioId);

			ServerInterface si = new ServerInterface();
			si.addListener(new ServerInterfaceListener() {

				public void serverSuccessHandler(JSONObject result) throws JSONException {

					geraetestandortListKundeId.clear();
					jsonKundeGeraetestandortId = result.getJSONArray("kundeGeraetestandortId");

					Log.i("geräte ID", "Gerätestandort ID: " + jsonKundeGeraetestandortId);

					for (int i = 0; i < jsonKundeGeraetestandortId.length(); i++) {
						geraetestandortListKundeId.add(jsonKundeGeraetestandortId.getInt(i));
					}
					pDialog.dismiss();
				}

				public void serverErrorHandler(Exception e) {
					// z.B. Fehler Dialog aufploppen lassen
					Log.e("error", "called");
				}
			});
			si.call("gibKundeGeraetestandort", params);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void insertKundeGeraete() {

		// ----------------------------------------------------------------------------------//
		// BERECHNUNG insertKundeGeraete
		// ----------------------------------------------------------------------------------//

		List<String> mAnzList = new ArrayList<String>();
		List<String> mSelectedAnzahl = new ArrayList<String>();
		List<Integer> mGeraete = new ArrayList<Integer>();
		List<Integer> mGeraetestandort = new ArrayList<Integer>();

		mSelectedAnzahl.clear();
		mAnzList = listAdapter.getAnzGeraeteListeAll();
		mSelectedAnzahl = listAdapter.getAnzGeraeteListeAll();

		try {
			int count = 0;
			String selectedGeraet = "";
			String selectedAnzahl = "";
			String selectedGeraetestandort = "";

			if (isNullList(mSelectedAnzahl)) {
				Log.i("List is EMPTY", "mSelectedAnzahl: " + mSelectedAnzahl);
				selectedGeraet = "0";
				selectedAnzahl = "0";
			} else {
				Log.i("List is NOT EMPTY", "mAnzList: " + mAnzList);
				for (int i = 0; i < mAnzList.size(); i++) {
					if (!mAnzList.get(i).equals("0")) {
						mGeraete.add(jsonGeraeteIdList.getInt(i));
						selectedGeraet += (jsonGeraeteIdList.getString(i)) + ".";
					}
				}
				if (!selectedGeraet.equals("0")) {
					selectedGeraet = selectedGeraet.substring(0, selectedGeraet.length() - 1);
				}
				boolean removeAllNull = mSelectedAnzahl.removeAll(Arrays.asList("0"));
				selectedAnzahl = TextUtils.join(".", mSelectedAnzahl);
			}

			/*
			 * for (int i = 0; i < jsonGeraetestandortGeraeteId.length(); i++) {
			 * selectedGeraetestandort +=
			 * (listAdapterSpinner.getSpinnerItemId()) + "."; count++; } if
			 * (count != 0) { selectedGeraetestandort =
			 * selectedGeraetestandort.substring(0,
			 * selectedGeraetestandort.length() - 1); } else if(count == 0){
			 * selectedGeraetestandort = "0"; }
			 */

			// selectedGeraetestandort = TextUtils.join(".",
			// listAdapterSpinner.getSpinnerItemId());
			// Log.i("INSERT GERÄTE",
			// "selectedGeraetestandort: "+selectedGeraetestandort);

			/*
			 * for (int i = 0; i < listAdapterSpinner.getSpinnerItemId().size();
			 * i++) {
			 * mGeraetestandort.add(jsonGeraetestandortGeraeteId.getInt(i)); }
			 */

			loop: for (int i = 0; i < mGeraete.size(); i++) {
				for (int g = 0; g < listAdapterSpinner.getSpinnerItemId().size(); g++) {
					if (mGeraete.get(i).equals(listAdapterSpinner.getSpinnerGeraeteId().get(g))) {
						int bla = listAdapterSpinner.getSpinnerItemId().get(g);
						selectedGeraetestandort += bla + ".";
						count++;
						continue loop;
					}
				}
				selectedGeraetestandort += 0 + ".";
			}
			if (count != 0) {
				selectedGeraetestandort = selectedGeraetestandort.substring(0, selectedGeraetestandort.length() - 1);
			} else if (count == 0) {
				selectedGeraetestandort = "0";
			}

			Log.i("INSERT GERÄTE", "selectedGeraetestandort: " + selectedGeraetestandort);
			// ----------------------------------------------------------------------------------//
			// insertKundeGeraete
			// ----------------------------------------------------------------------------------//

			JSONObject updateParams = new JSONObject();
			updateParams.put("kundeId", mKundeId);
			updateParams.put("szenarioId", mSzenarioId);
			updateParams.put("wohnraeumeId", raumId);
			updateParams.put("anzGeraete", selectedAnzahl);
			updateParams.put("geraeteId", selectedGeraet);
			updateParams.put("geraetestandortId", selectedGeraetestandort);

			ServerInterface si = new ServerInterface();
			si.addListener(new ServerInterfaceListener() {
				public void serverSuccessHandler(JSONObject result) throws JSONException {
					Log.i("INSERT KundeGeraete: ", result.getString("msg"));
				}

				public void serverErrorHandler(Exception e) {
					// TODO Auto-generated method
					// stub
				}
			});
			si.call("insertKundeGeraete", updateParams);
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

	}

	/*
	 * public void insertKundeGeraetestandort() {
	 * 
	 * //
	 * ------------------------------------------------------------------------
	 * ----------// // BERECHNUNG insertKundeGeraetestandort //
	 * ------------------
	 * ----------------------------------------------------------------//
	 * 
	 * int count = 0; String selectedGeraetestandort = ""; for (int i = 0; i <
	 * jsonGeraetestandortGeraeteId.length(); i++) { selectedGeraetestandort +=
	 * (listAdapterSpinner.getSpinnerItemId()) + "."; count++; } if (count != 0)
	 * { selectedGeraetestandort = selectedGeraetestandort.substring(0,
	 * selectedGeraetestandort.length() - 1); } else if(count == 0){
	 * selectedGeraetestandort = "0"; }
	 * 
	 * Log.i("INSERT STANDORT", "getSpinnerItem ID: " +
	 * listAdapterSpinner.getSpinnerItemId()); Log.i("INSERT STANDORT",
	 * "getSpinnerItem TEXT: " + listAdapterSpinner.getSpinnerItemText());
	 * Log.i("INSERT STANDORT", "selectedGeraetestandort: " +
	 * selectedGeraetestandort);
	 * 
	 * try { //
	 * ------------------------------------------------------------------
	 * ----------------// // insertKundeGeraetestandort //
	 * ----------------------
	 * ------------------------------------------------------------//
	 * 
	 * JSONObject updateParams = new JSONObject(); updateParams.put("kundeId",
	 * mKundeId); updateParams.put("szenarioId", mSzenarioId);
	 * updateParams.put("wohnraeumeId", raumId);
	 * updateParams.put("geraetestandortId", selectedGeraetestandort);
	 * 
	 * ServerInterface si = new ServerInterface(); si.addListener(new
	 * ServerInterfaceListener() { public void serverSuccessHandler(JSONObject
	 * result) throws JSONException { Log.i("INSERT KundeGeraetestandort: ",
	 * result.getString("msg")); }
	 * 
	 * public void serverErrorHandler(Exception e) { // TODO Auto-generated
	 * method // stub } }); si.call("insertKundeGeraetestandort", updateParams);
	 * } catch (JSONException e) { // TODO Auto-generated catch block
	 * e.printStackTrace(); } }
	 */

}