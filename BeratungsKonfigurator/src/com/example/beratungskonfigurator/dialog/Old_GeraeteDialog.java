package com.example.beratungskonfigurator.dialog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
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
import com.example.beratungskonfigurator.NumberPic;
import com.example.beratungskonfigurator.R;
import com.example.beratungskonfigurator.server.ServerInterface;
import com.example.beratungskonfigurator.server.ServerInterfaceListener;

public class Old_GeraeteDialog extends Dialog {

	private ProgressDialog pDialog;
	private ListView raeumeList;
	private ListView geraeteList;
	private ListView ausloeserList;
	private ListView beenderList;

	private int raumId;

	private JSONArray geraeteListe = new JSONArray();
	private JSONArray geraeteIdListe = new JSONArray();

	private JSONArray raumIdArray = new JSONArray();
	private JSONArray anzGeraeteArray = new JSONArray();
	private JSONArray geraeteId = new JSONArray();
	
	private JSONArray beenderGeraeteId = new JSONArray();
	private JSONArray ausloeserGeraeteId = new JSONArray();

	ListViewButtonAdapter listAdapter;
	private int mKundeId;
	private int mSzenarioId;
	private Context mContext;

	EditText etNumber;
	List<String> anzList = new ArrayList<String>();
	List<String> gerList = new ArrayList<String>();

	SimpleAdapter dataAdapter;
	SimpleAdapter dataAdapterRaeume;
	SimpleAdapter dataAdapterAusloeser;
	SimpleAdapter dataAdapterBeender;

	ArrayList<Integer> selSzenarioList = new ArrayList<Integer>();

	// Constructor
	public Old_GeraeteDialog(final Context context, int kundeId, int szenarioId) {
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

		TextView titelStartSzenario = (TextView) findViewById(R.id.titelStartSzenario);
		titelStartSzenario.setText("Szenario starten");
		TextView titelEndSzenario = (TextView) findViewById(R.id.titelEndSzenario);
		titelEndSzenario.setText("Szenario beenden");
		Button bCloseGeraete = (Button) findViewById(R.id.bCloseGeraete);
		bCloseGeraete.setBackgroundResource(R.drawable.button_close);
		bCloseGeraete.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				
				// ----------------------------------------------------------------------------------//
				// BERECHNUNG insertKundeGeraete
				// ----------------------------------------------------------------------------------//

				List<String> mAnzList = new ArrayList<String>();
				List<String> mSelectedAnzahl = new ArrayList<String>();
				List<Integer> mGeraete = new ArrayList<Integer>();
				List<Integer> mAusloeser = new ArrayList<Integer>();
				List<Integer> mBeender = new ArrayList<Integer>();
				
				mSelectedAnzahl.clear();
				mAnzList = listAdapter.getAnzGeraeteListeAll();
				mSelectedAnzahl = listAdapter.getAnzGeraeteListeAll();

				try {
					String selectedGeraet = "";
					String selectedAnzahl = "";
					String selectedAusloeser = "";
					String selectedBeender = "";

					int count = 0;
					int cntChoiceAusloeser = ausloeserList.getCount();
					int cntChoiceBeender = beenderList.getCount();
					SparseBooleanArray sparseBooleanArrayAusloeser = ausloeserList.getCheckedItemPositions();
					SparseBooleanArray sparseBooleanArrayBeender = beenderList.getCheckedItemPositions();

					if (isNullList(mSelectedAnzahl)) {
						Log.i("List is EMPTY", "mSelectedAnzahl: " + mSelectedAnzahl);
						selectedGeraet = "0";
						selectedAnzahl = "0";
					} else {
						Log.i("List is NOT EMPTY", "mAnzList: " + mAnzList);
						for (int i = 0; i < mAnzList.size(); i++) {
							if (!mAnzList.get(i).equals("0")) {
								mGeraete.add(geraeteIdListe.getInt(i));
								selectedGeraet += (geraeteIdListe.getString(i)) + ".";
							}
						}
						if (!selectedGeraet.equals("0")) {
							selectedGeraet = selectedGeraet.substring(0, selectedGeraet.length() - 1);
						}
						boolean removeAllNull = mSelectedAnzahl.removeAll(Arrays.asList("0"));
						selectedAnzahl = TextUtils.join(".", mSelectedAnzahl);
					}
					
					for (int i = 0; i < cntChoiceAusloeser; i++) {
						if (sparseBooleanArrayAusloeser.get(i)) {
							mAusloeser.add(ausloeserGeraeteId.getInt(i));
						}else if (!sparseBooleanArrayAusloeser.get(i)){
							mAusloeser.add(0);
						}
					}
					for (int i = 0; i < cntChoiceBeender; i++) {
						if (sparseBooleanArrayBeender.get(i)) {
							mBeender.add(beenderGeraeteId.getInt(i));
						}else if (!sparseBooleanArrayBeender.get(i)){
							mBeender.add(0);
						}
					}
					
					loop: for(int i = 0; i < mGeraete.size(); i++){
						for(int g = 0; g < cntChoiceAusloeser; g++ ){
							if(mGeraete.get(i).equals(mAusloeser.get(g))){
								int bla = mAusloeser.get(g);
								selectedAusloeser += bla + ".";
								count++;
								continue loop;
							}							
						}
						selectedAusloeser += 0 + ".";
					}
					if (count != 0) {
						selectedAusloeser = selectedAusloeser.substring(0, selectedAusloeser.length() - 1);
					} else if(count == 0){
						selectedAusloeser = "0";
					}
					
					count=0;
					
					loop: for(int i = 0; i < mGeraete.size(); i++){
						for(int g = 0; g < cntChoiceBeender; g++ ){
							if(mGeraete.get(i).equals(mBeender.get(g))){
								int bla = mBeender.get(g);
								selectedBeender += bla + ".";
								count++;
								continue loop;
							}							
						}
						selectedBeender += 0 + ".";
					}
					if (count != 0) {
						selectedBeender = selectedBeender.substring(0, selectedBeender.length() - 1);
					} else if(count == 0){
						selectedBeender = "0";
					}
					

					// ----------------------------------------------------------------------------------//
					// insertKundeGeraete
					// ----------------------------------------------------------------------------------//

					Log.i("INSERT Close", "Ausloeser List: " + mAusloeser.toString());
					Log.i("INSERT Close", "Beender List: " + mBeender.toString());
					Log.i("INSERT Close", "ausloeser: " + selectedAusloeser);
					Log.i("INSERT Close", "beender: " + selectedBeender);
					Log.i("INSERT Close", "Geräte: " + mGeraete.toString());

					JSONObject updateParams = new JSONObject();
					updateParams.put("kundeId", mKundeId);
					updateParams.put("szenarioId", mSzenarioId);
					updateParams.put("wohnraeumeId", raumId);
					updateParams.put("anzGeraete", selectedAnzahl);
					updateParams.put("geraeteId", selectedGeraet);
					updateParams.put("ausloeser", selectedAusloeser);
					updateParams.put("beender", selectedBeender);

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
					raumIdArray = result.getJSONArray("raumId");
					Log.i("raumId Array", "raumId Array: " + raumIdArray);
					Log.i("raumId Array", "raumId Array an Pos 0: " + raumIdArray.getInt(0));

					for (int i = 0; i < la.length(); i++) {
						HashMap<String, String> hm = new HashMap<String, String>();
						hm.put("name", la.getString(i));
						hm.put("icon", String.valueOf(R.drawable.listitem_refresh));
						list.add(hm);
					}
					Log.i("data", la.toString());
					Log.i("msg", result.getString("msg"));

					dataAdapterRaeume = new SimpleAdapter(mContext, list, R.layout.listview_raeume_einstellungen, new String[] { "name", "icon" },
							new int[] { R.id.name, R.id.icon });
					raeumeList = (ListView) findViewById(R.id.raeumeList);
					raeumeList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
					raeumeList.setAdapter(dataAdapterRaeume);
					raeumeList.setItemChecked(0, true);

					// ----------------------------------------------------------------------------------//
					// gibGeraete
					// ----------------------------------------------------------------------------------//

					ServerInterface si = new ServerInterface();
					si.addListener(new ServerInterfaceListener() {

						public void serverSuccessHandler(JSONObject result) throws JSONException {

							pDialog.dismiss();

							geraeteListe = result.getJSONArray("data");
							geraeteIdListe = result.getJSONArray("geraeteId");
							anzList.clear();

							for (int i = 0; i < geraeteListe.length(); i++) {
								gerList.add(geraeteListe.getString(i));
								anzList.add("0");
							}

							listAdapter = new ListViewButtonAdapter(mContext, gerList, anzList);
							geraeteList = (ListView) findViewById(R.id.geraeteList);
							geraeteList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
							geraeteList.setAdapter(listAdapter);
							geraeteList.setOnFocusChangeListener(new OnFocusChangeListener() {
								public void onFocusChange(View v, boolean hasFocus) {

									Log.i("FOCUS CHANGED", "alle anzListe: " + listAdapter.getAnzGeraeteListeAll().toString());
									geraeteList.clearChoices();
									for (int i = 0; i < listAdapter.getAnzGeraeteListeAll().size(); i++) {
										if (!listAdapter.getAnzGeraeteListeAll().get(i).equals("0")) {
											Log.i("FOCUS CHANGED", "i: " + i + " Anzahl: " + listAdapter.getAnzGeraeteListeAll().get(i));
											geraeteList.setItemChecked(i, true);
										} else {
											geraeteList.setItemChecked(i, false);
										}
									}

								}
							});
							geraeteList.setOnScrollListener(new OnScrollListener() {
								public void onScrollStateChanged(AbsListView view, int scrollState) {
									/*
									 * Log.i("SCROLL", "changed");
									 * geraeteList.clearChoices(); for (int i =
									 * 0; i <
									 * listAdapter.getAnzGeraeteListeAll()
									 * .size(); i++) { if
									 * (!listAdapter.getAnzGeraeteListeAll
									 * ().get(i).equals("0")) {
									 * Log.i("FOCUS CHANGED", "i: " + i +
									 * " Anzahl: " +
									 * listAdapter.getAnzGeraeteListeAll
									 * ().get(i)); geraeteList.setItemChecked(i,
									 * true); } else {
									 * geraeteList.setItemChecked(i, false); } }
									 */
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

					// ----------------------------------------------------------------------------------//
					// gibKundeGeraete
					// ----------------------------------------------------------------------------------//

					raumId = raumIdArray.getInt(0);
					params = new JSONObject();

					params.put("kundeId", mKundeId);
					params.put("wohnraeumeId", raumId);
					params.put("szenarioId", mSzenarioId);

					si = new ServerInterface();
					si.addListener(new ServerInterfaceListener() {

						public void serverSuccessHandler(JSONObject result) throws JSONException {

							geraeteId = result.getJSONArray("data");
							anzGeraeteArray = result.getJSONArray("anzGeraete");

							anzList.clear();
							geraeteList.clearChoices();
							Log.i("geräte ID", "Geräte ID: " + geraeteId);
							Log.i("geräte ID", "Anzahl Geräte ID: " + anzGeraeteArray);

							if (!geraeteId.isNull(0)) {
								boolean treffer = false;
								for (int i = 0; i < geraeteIdListe.length(); i++) {
									for (int k = 0; k < geraeteId.length(); k++) {
										if (geraeteIdListe.getString(i).equals(geraeteId.getString(k)) && anzGeraeteArray.getInt(k) > 0) {
											anzList.add(anzGeraeteArray.getString(k));
											geraeteList.setItemChecked((geraeteId.getInt(k)) - 1, true);
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
								Log.i("ELSE", "ELSE: " + geraeteId.isNull(0));
								geraeteList.clearChoices();
								for (int i = 0; i < geraeteIdListe.length(); i++) {
									anzList.add("0");
								}
							}
							Log.i("anzList", "anzList: " + anzList);
							geraeteList.invalidateViews();
						}

						public void serverErrorHandler(Exception e) {
							// z.B. Fehler Dialog aufploppen lassen
							Log.e("error", "called");
						}
					});
					si.call("gibKundeGeraete", params);

					raeumeList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
						public void onItemClick(AdapterView<?> arg0, View v, int position, long id) {

							// ----------------------------------------------------------------------------------//
							// BERECHNUNG insertKundeGeraete
							// ----------------------------------------------------------------------------------//

							List<String> mAnzList = new ArrayList<String>();
							List<String> mSelectedAnzahl = new ArrayList<String>();
							List<Integer> mGeraete = new ArrayList<Integer>();
							List<Integer> mAusloeser = new ArrayList<Integer>();
							List<Integer> mBeender = new ArrayList<Integer>();
							
							mSelectedAnzahl.clear();
							mAnzList = listAdapter.getAnzGeraeteListeAll();
							mSelectedAnzahl = listAdapter.getAnzGeraeteListeAll();

							try {
								String selectedGeraet = "";
								String selectedAnzahl = "";
								String selectedAusloeser = "";
								String selectedBeender = "";

								int count = 0;
								int cntChoiceAusloeser = ausloeserList.getCount();
								int cntChoiceBeender = beenderList.getCount();
								SparseBooleanArray sparseBooleanArrayAusloeser = ausloeserList.getCheckedItemPositions();
								SparseBooleanArray sparseBooleanArrayBeender = beenderList.getCheckedItemPositions();

								if (isNullList(mSelectedAnzahl)) {
									Log.i("List is EMPTY", "mSelectedAnzahl: " + mSelectedAnzahl);
									selectedGeraet = "0";
									selectedAnzahl = "0";
								} else {
									Log.i("List is NOT EMPTY", "mAnzList: " + mAnzList);
									for (int i = 0; i < mAnzList.size(); i++) {
										if (!mAnzList.get(i).equals("0")) {
											mGeraete.add(geraeteIdListe.getInt(i));
											selectedGeraet += (geraeteIdListe.getString(i)) + ".";
										}
									}
									if (!selectedGeraet.equals("0")) {
										selectedGeraet = selectedGeraet.substring(0, selectedGeraet.length() - 1);
									}
									boolean removeAllNull = mSelectedAnzahl.removeAll(Arrays.asList("0"));
									selectedAnzahl = TextUtils.join(".", mSelectedAnzahl);
								}
								
								for (int i = 0; i < cntChoiceAusloeser; i++) {
									if (sparseBooleanArrayAusloeser.get(i)) {
										mAusloeser.add(ausloeserGeraeteId.getInt(i));
									}else if (!sparseBooleanArrayAusloeser.get(i)){
										mAusloeser.add(0);
									}
								}
								for (int i = 0; i < cntChoiceBeender; i++) {
									if (sparseBooleanArrayBeender.get(i)) {
										mBeender.add(beenderGeraeteId.getInt(i));
									}else if (!sparseBooleanArrayBeender.get(i)){
										mBeender.add(0);
									}
								}
								
								loop: for(int i = 0; i < mGeraete.size(); i++){
									for(int g = 0; g < cntChoiceAusloeser; g++ ){
										if(mGeraete.get(i).equals(mAusloeser.get(g))){
											int bla = mAusloeser.get(g);
											selectedAusloeser += bla + ".";
											count++;
											continue loop;
										}							
									}
									selectedAusloeser += 0 + ".";
								}
								if (count != 0) {
									selectedAusloeser = selectedAusloeser.substring(0, selectedAusloeser.length() - 1);
								} else if(count == 0){
									selectedAusloeser = "0";
								}
								
								count=0;
								
								loop: for(int i = 0; i < mGeraete.size(); i++){
									for(int g = 0; g < cntChoiceBeender; g++ ){
										if(mGeraete.get(i).equals(mBeender.get(g))){
											int bla = mBeender.get(g);
											selectedBeender += bla + ".";
											count++;
											continue loop;
										}							
									}
									selectedBeender += 0 + ".";
								}
								if (count != 0) {
									selectedBeender = selectedBeender.substring(0, selectedBeender.length() - 1);
								} else if(count == 0){
									selectedBeender = "0";
								}
								

								// ----------------------------------------------------------------------------------//
								// insertKundeGeraete
								// ----------------------------------------------------------------------------------//

								Log.i("INSERT Close", "Ausloeser List: " + mAusloeser.toString());
								Log.i("INSERT Close", "Beender List: " + mBeender.toString());
								Log.i("INSERT Close", "ausloeser: " + selectedAusloeser);
								Log.i("INSERT Close", "beender: " + selectedBeender);
								Log.i("INSERT Close", "Geräte: " + mGeraete.toString());

								JSONObject updateParams = new JSONObject();
								updateParams.put("kundeId", mKundeId);
								updateParams.put("szenarioId", mSzenarioId);
								updateParams.put("wohnraeumeId", raumId);
								updateParams.put("anzGeraete", selectedAnzahl);
								updateParams.put("geraeteId", selectedGeraet);
								updateParams.put("ausloeser", selectedAusloeser);
								updateParams.put("beender", selectedBeender);
								
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

							// ----------------------------------------------------------------------------------//
							// gibKundeGeraete
							// ----------------------------------------------------------------------------------//

							try {
								raumId = raumIdArray.getInt(position);
								JSONObject params = new JSONObject();

								params.put("kundeId", mKundeId);
								params.put("szenarioId", mSzenarioId);
								params.put("wohnraeumeId", raumId);

								ServerInterface si = new ServerInterface();
								si.addListener(new ServerInterfaceListener() {

									public void serverSuccessHandler(JSONObject result) throws JSONException {

										geraeteId = result.getJSONArray("data");
										anzGeraeteArray = result.getJSONArray("anzGeraete");

										anzList.clear();
										geraeteList.clearChoices();
										Log.i("geräte ID", "Geräte ID: " + geraeteId);
										Log.i("geräte ID", "Anzahl Geräte ID: " + anzGeraeteArray);

										if (!geraeteId.isNull(0)) {
											boolean treffer = false;
											for (int i = 0; i < geraeteIdListe.length(); i++) {
												for (int k = 0; k < geraeteId.length(); k++) {
													if (geraeteIdListe.getString(i).equals(geraeteId.getString(k)) && anzGeraeteArray.getInt(k) > 0) {
														anzList.add(anzGeraeteArray.getString(k));
														geraeteList.setItemChecked((geraeteId.getInt(k)) - 1, true);
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
											Log.i("ELSE", "ELSE: " + geraeteId.isNull(0));
											geraeteList.clearChoices();
											for (int i = 0; i < geraeteIdListe.length(); i++) {
												anzList.add("0");
											}
										}
										Log.i("anzList", "anzList: " + anzList);
										// geraeteList.deferNotifyDataSetChanged();
										listAdapter.notifyDataSetChanged();
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
							gibAusloeser(position);
							gibKundeAusloeser(position);
							gibBeender(position);
							gibKundeBeender(position);

						}
					});

					gibAusloeser(0);
					gibKundeAusloeser(0);
					gibBeender(0);
					gibKundeBeender(0);

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
		TabHost.TabSpec tab2 = tabs.newTabSpec("Auslöser");
		tab2.setContent(R.id.layAusloeser);
		tab2.setIndicator("Auslöser");
		tabs.addTab(tab2);

		// create tab 3
		TabHost.TabSpec tab3 = tabs.newTabSpec("Beender");
		tab3.setContent(R.id.layBeender);
		tab3.setIndicator("Beender");
		tabs.addTab(tab3);

		/*
		 * tabs.setOnTabChangedListener(new OnTabChangeListener() {
		 * 
		 * @Override public void onTabChanged(String tabId) { int tabInt =
		 * tabs.getCurrentTab(); switch (tabInt) { case 0:
		 * 
		 * break; case 1: gibAusloeser(0); gibKundeAusloeser(); break; case 2:
		 * gibBeender(0); gibKundeBeender(); break; default: break; } }
		 * 
		 * });
		 */

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

	public void gibAusloeser(int position) {

		// ----------------------------------------------------------------------------------//
		// gibAusloeser
		// ----------------------------------------------------------------------------------//

		try {
			raumId = raumIdArray.getInt(position);
			Log.i("AUSLOESER", "Raum ID: " + raumId);
			Log.i("AUSLOESER", "Kunde ID: " + mKundeId);
			JSONObject params = new JSONObject();
			params.put("kundeId", mKundeId);
			params.put("wohnraeumeId", raumId);
			params.put("szenarioId", mSzenarioId);

			pDialog.show();
			ServerInterface si = new ServerInterface();
			si.addListener(new ServerInterfaceListener() {

				public void serverSuccessHandler(JSONObject result) throws JSONException {

					List<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
					JSONArray la = result.getJSONArray("data");
					ausloeserGeraeteId = result.getJSONArray("geraeteId");

					for (int i = 0; i < la.length(); i++) {
						HashMap<String, String> hm = new HashMap<String, String>();
						hm.put("txt", la.getString(i));
						list.add(hm);
					}
					Log.i("data Ausloeser", la.toString());
					Log.i("msg Ausloeser", result.getString("msg"));

					String[] from = { "txt" };
					int[] to = { R.id.txt };

					dataAdapterAusloeser = new SimpleAdapter(mContext, list, R.layout.listview_checkable, from, to);
					ausloeserList = (ListView) findViewById(R.id.ausloeserList);

					ausloeserList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
					ausloeserList.setAdapter(dataAdapterAusloeser);

					pDialog.dismiss();
					ausloeserList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
						public void onItemClick(AdapterView<?> arg0, View v, int position, long id) {

						}
					});
				}

				public void serverErrorHandler(Exception e) {
					// z.B. Fehler Dialog aufploppen lassen
					Log.e("error", "called");
				}
			});
			si.call("gibAusloeser", params);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void gibKundeAusloeser(int position) {

		// ----------------------------------------------------------------------------------//
		// gibKundeAusloeser
		// ----------------------------------------------------------------------------------//

		try {
			raumId = raumIdArray.getInt(position);
			JSONObject params = new JSONObject();
			params.put("kundeId", mKundeId);
			params.put("wohnraeumeId", raumId);
			params.put("szenarioId", mSzenarioId);

			ServerInterface si = new ServerInterface();
			si.addListener(new ServerInterfaceListener() {

				public void serverSuccessHandler(JSONObject result) throws JSONException {

					JSONArray wd = result.getJSONArray("data");
					Log.i("Gib Kunde Ausloeser", "Kunde Ausloeser: "+wd.toString());
					for (int i = 0; i < wd.length(); i++) {
						if (Integer.valueOf(wd.get(i).toString()) == ausloeserGeraeteId.getInt(i)) {
							ausloeserList.setItemChecked(i, true);
						}
					}
				}

				public void serverErrorHandler(Exception e) {
					// z.B. Fehler Dialog aufploppen lassen
					Log.e("error", "called");
				}
			});
			si.call("gibKundeAusloeser", params);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void gibBeender(int position) {

		// ----------------------------------------------------------------------------------//
		// gibBeender
		// ----------------------------------------------------------------------------------//

		try {
			raumId = raumIdArray.getInt(position);
			Log.i("BEENDER", "Raum ID: " + raumId);
			Log.i("BEENDER", "Kunde ID: " + mKundeId);
			JSONObject params = new JSONObject();
			params.put("kundeId", mKundeId);
			params.put("wohnraeumeId", raumId);
			params.put("szenarioId", mSzenarioId);

			pDialog.show();

			ServerInterface si = new ServerInterface();
			si.addListener(new ServerInterfaceListener() {

				public void serverSuccessHandler(JSONObject result) throws JSONException {

					List<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
					JSONArray la = result.getJSONArray("data");
					beenderGeraeteId = result.getJSONArray("geraeteId");

					for (int i = 0; i < la.length(); i++) {
						HashMap<String, String> hm = new HashMap<String, String>();
						hm.put("txt", la.getString(i));
						list.add(hm);
					}
					Log.i("data Beender", la.toString());
					Log.i("msg Beender", result.getString("msg"));

					String[] from = { "txt" };
					int[] to = { R.id.txt };

					dataAdapterBeender = new SimpleAdapter(mContext, list, R.layout.listview_checkable, from, to);
					beenderList = (ListView) findViewById(R.id.beenderList);

					beenderList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
					beenderList.setAdapter(dataAdapterBeender);
					pDialog.dismiss();
					beenderList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
						public void onItemClick(AdapterView<?> arg0, View v, int position, long id) {

						}
					});
				}

				public void serverErrorHandler(Exception e) {
					// z.B. Fehler Dialog aufploppen lassen
					Log.e("error", "called");
				}
			});
			si.call("gibBeender", params);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void gibKundeBeender(int position) {

		// ----------------------------------------------------------------------------------//
		// gibKundeBeender
		// ----------------------------------------------------------------------------------//

		try {
			raumId = raumIdArray.getInt(position);
			JSONObject params = new JSONObject();

			params.put("kundeId", mKundeId);
			params.put("wohnraeumeId", raumId);
			params.put("szenarioId", mSzenarioId);

			ServerInterface si = new ServerInterface();
			si.addListener(new ServerInterfaceListener() {

				public void serverSuccessHandler(JSONObject result) throws JSONException {

					JSONArray wd = result.getJSONArray("data");
					Log.i("Gib Kunde Beender", "Kunde Beender: "+wd.toString());
					for (int i = 0; i < wd.length(); i++) {
						if (Integer.valueOf(wd.get(i).toString()) == beenderGeraeteId.getInt(i)) {
							beenderList.setItemChecked(i, true);
						}
					}
				}

				public void serverErrorHandler(Exception e) {
					// z.B. Fehler Dialog aufploppen lassen
					Log.e("error", "called");
				}
			});
			si.call("gibKundeBeender", params);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}