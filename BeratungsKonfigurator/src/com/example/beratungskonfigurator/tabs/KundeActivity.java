package com.example.beratungskonfigurator.tabs;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.widget.Spinner;

import com.example.beratungskonfigurator.R;
import com.example.beratungskonfigurator.ServerInterface;
import com.example.beratungskonfigurator.ServerInterfaceListener;

public class KundeActivity extends Fragment implements View.OnKeyListener {

	// Progress Dialog
	private ProgressDialog pDialog;

	private int currentSelected = 0;

	private static final String ADRESSE = "Adresse";
	private static final String KONTAKT = "Kontakt";
	private static final String INFORMATION = "Information";
	private static final String VERSORGUNG = "Versorgung";
	private static final String ANGEHOERIGER = "Angehöriger";

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		final View fragView = (View) inflater.inflate(R.layout.tab_kunde_layout, container, false);
		final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
		currentSelected = 0;
		
		final LinearLayout layAdresse = (LinearLayout) fragView.findViewById(R.id.layAdresse);
		final LinearLayout layKontakt = (LinearLayout) fragView.findViewById(R.id.layKontakt);
		final LinearLayout layInformation = (LinearLayout) fragView.findViewById(R.id.layInformation);
		final LinearLayout layVersorgung = (LinearLayout) fragView.findViewById(R.id.layVersorgung);
		final LinearLayout layAngehoeriger = (LinearLayout) fragView.findViewById(R.id.layAngehoeriger);
		
		layKontakt.setVisibility(View.GONE);
		layInformation.setVisibility(View.GONE);
		layVersorgung.setVisibility(View.GONE);
		layAngehoeriger.setVisibility(View.GONE);

		final ToggleButton toggleEditAdresse = (ToggleButton) fragView.findViewById(R.id.toggleEditAdresse);
		final ToggleButton toggleEditKontakt = (ToggleButton) fragView.findViewById(R.id.toggleEditKontakt);
		final ToggleButton toggleEditInformation = (ToggleButton) fragView.findViewById(R.id.toggleEditInformation);
		final ToggleButton toggleEditAngehoeriger = (ToggleButton) fragView.findViewById(R.id.toggleEditAngehoeriger);

		final TextView titelAdresse = (TextView) fragView.findViewById(R.id.titelAdresse);
		titelAdresse.setText(ADRESSE);
		final EditText kName = (EditText) fragView.findViewById(R.id.kName);
		final EditText kVorname = (EditText) fragView.findViewById(R.id.kVorname);
		final EditText kStrasse = (EditText) fragView.findViewById(R.id.kStrasse);
		final EditText kHausnummer = (EditText) fragView.findViewById(R.id.kHausnummer);
		final EditText kPLZ = (EditText) fragView.findViewById(R.id.kPLZ);
		final EditText kOrt = (EditText) fragView.findViewById(R.id.kOrt);

		final TextView titelKontakt = (TextView) fragView.findViewById(R.id.titelKontakt);
		titelKontakt.setText(KONTAKT);
		final EditText kTelefon = (EditText) fragView.findViewById(R.id.kTelefon);
		final EditText kMobil = (EditText) fragView.findViewById(R.id.kMobil);
		final EditText kFax = (EditText) fragView.findViewById(R.id.kFax);
		final EditText kMail = (EditText) fragView.findViewById(R.id.kMail);

		final TextView titelInformation = (TextView) fragView.findViewById(R.id.titelInformation);
		titelInformation.setText(INFORMATION);
		final EditText kGeburtsdatum = (EditText) fragView.findViewById(R.id.kGeburtsdatum);
		final Spinner kFamilienstand = (Spinner) fragView.findViewById(R.id.kFamilienstand);
		final Spinner kKonfession = (Spinner) fragView.findViewById(R.id.kKonfession);
		final Spinner kPflegestufe = (Spinner) fragView.findViewById(R.id.kPflegestufe);
		
		final TextView titelVersorgung = (TextView) fragView.findViewById(R.id.titelVersorgung);
		titelVersorgung.setText(VERSORGUNG);
		final Spinner kVersicherungsart = (Spinner) fragView.findViewById(R.id.kVersicherungsart);
		final Spinner kLeistungsart = (Spinner) fragView.findViewById(R.id.kLeistungsart);
		final Spinner kKrankenkasse = (Spinner) fragView.findViewById(R.id.kKrankenkasse);
		final Spinner kKostentraeger = (Spinner) fragView.findViewById(R.id.kKostentraeger);
		
		final TextView titelAngehoeriger = (TextView) fragView.findViewById(R.id.titelAngehoeriger);
		titelAngehoeriger.setText(ANGEHOERIGER);
		final Spinner aArt = (Spinner) fragView.findViewById(R.id.aArt);
		final EditText aName = (EditText) fragView.findViewById(R.id.aName);
		final EditText aVorname = (EditText) fragView.findViewById(R.id.aVorname);
		final EditText aStrasse = (EditText) fragView.findViewById(R.id.aStrasse);
		final EditText aHausnummer = (EditText) fragView.findViewById(R.id.aHausnummer);
		final EditText aPLZ = (EditText) fragView.findViewById(R.id.aPLZ);
		final EditText aOrt = (EditText) fragView.findViewById(R.id.aOrt);
		final EditText aTelefon = (EditText) fragView.findViewById(R.id.aTelefon);
		final EditText aMobil = (EditText) fragView.findViewById(R.id.aMobil);
		final EditText aFax = (EditText) fragView.findViewById(R.id.aFax);
		final EditText aMail = (EditText) fragView.findViewById(R.id.aMail);

		try {

			//----------------------------------------------------------------------------------//
			//gibFamilienstaende
			//----------------------------------------------------------------------------------//
			
			JSONObject params = new JSONObject();

			pDialog = new ProgressDialog(getActivity());
			pDialog.setMessage("Lade Daten!");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();

			ServerInterface si = new ServerInterface();
			// si.setVerbose( true );
			si.addListener(new ServerInterfaceListener() {

				public void serverSuccessHandler(JSONObject result) throws JSONException {

					// z.B. Swirl jetzt verstecken und Grid mit Daten füllen
					pDialog.dismiss();

					List<String> list = new ArrayList<String>();

					JSONArray fs = result.getJSONArray("data");

					for (int i = 0; i < fs.length(); i++) {
						list.add(fs.getString(i));
					}
					Log.i("data", fs.toString());
					Log.i("msg", result.getString("msg"));

					ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, list);
					dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
					kFamilienstand.setAdapter(dataAdapter);

				}

				public void serverErrorHandler(Exception e) {

					// z.B. Fehler Dialog aufploppen lassen
					Log.e("error", "called");
				}
			});
			params = new JSONObject();
			si.call("gibFamilienstaende", params);

			
			//----------------------------------------------------------------------------------//
			//gibKonfession
			//----------------------------------------------------------------------------------//
			
			params = new JSONObject();
			pDialog.show();

			si = new ServerInterface();
			si.addListener(new ServerInterfaceListener() {

				public void serverSuccessHandler(JSONObject result) throws JSONException {

					// z.B. Swirl jetzt verstecken und Grid mit Daten füllen
					pDialog.dismiss();

					List<String> list = new ArrayList<String>();

					JSONArray kf = result.getJSONArray("data");

					for (int i = 0; i < kf.length(); i++) {
						list.add(kf.getString(i));
					}
					Log.i("data", kf.toString());
					Log.i("msg", result.getString("msg"));

					ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, list);
					dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
					kKonfession.setAdapter(dataAdapter);
					kKonfession.setSelection(2);

				}

				public void serverErrorHandler(Exception e) {

					// z.B. Fehler Dialog aufploppen lassen
					Log.e("error", "called");
				}
			});
			params = new JSONObject();
			si.call("gibKonfession", params);
			
			
			//----------------------------------------------------------------------------------//
			//gibPflegestufe
			//----------------------------------------------------------------------------------//
			
			params = new JSONObject();
			pDialog.show();

			si = new ServerInterface();
			si.addListener(new ServerInterfaceListener() {

				public void serverSuccessHandler(JSONObject result) throws JSONException {

					// z.B. Swirl jetzt verstecken und Grid mit Daten füllen
					pDialog.dismiss();

					List<String> list = new ArrayList<String>();

					JSONArray pfs = result.getJSONArray("data");

					for (int i = 0; i < pfs.length(); i++) {
						list.add(pfs.getString(i));
					}
					Log.i("data", pfs.toString());
					Log.i("msg", result.getString("msg"));

					ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, list);
					dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
					kPflegestufe.setAdapter(dataAdapter);
					kPflegestufe.setSelection(-1);

				}

				public void serverErrorHandler(Exception e) {

					// z.B. Fehler Dialog aufploppen lassen
					Log.e("error", "called");
				}
			});
			params = new JSONObject();
			si.call("gibPflegestufe", params);
			
			
			//----------------------------------------------------------------------------------//
			//gibKostentraeger
			//----------------------------------------------------------------------------------//
			
			params = new JSONObject();
			pDialog.show();

			si = new ServerInterface();
			si.addListener(new ServerInterfaceListener() {

				public void serverSuccessHandler(JSONObject result) throws JSONException {

					// z.B. Swirl jetzt verstecken und Grid mit Daten füllen
					pDialog.dismiss();

					List<String> list = new ArrayList<String>();

					JSONArray kt = result.getJSONArray("data");

					for (int i = 0; i < kt.length(); i++) {
						list.add(kt.getString(i));
					}
					Log.i("data", kt.toString());
					Log.i("msg", result.getString("msg"));

					ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, list);
					dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
					kKostentraeger.setAdapter(dataAdapter);

				}

				public void serverErrorHandler(Exception e) {

					// z.B. Fehler Dialog aufploppen lassen
					Log.e("error", "called");
				}
			});
			params = new JSONObject();
			si.call("gibKostentraeger", params);

			
			//----------------------------------------------------------------------------------//
			//gibVersicherungsart
			//----------------------------------------------------------------------------------//
			
			params = new JSONObject();
			pDialog.show();

			si = new ServerInterface();
			si.addListener(new ServerInterfaceListener() {

				public void serverSuccessHandler(JSONObject result) throws JSONException {

					// z.B. Swirl jetzt verstecken und Grid mit Daten füllen
					pDialog.dismiss();

					List<String> list = new ArrayList<String>();

					JSONArray va = result.getJSONArray("data");

					for (int i = 0; i < va.length(); i++) {
						list.add(va.getString(i));
					}
					Log.i("data", va.toString());
					Log.i("msg", result.getString("msg"));

					ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, list);
					dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
					kVersicherungsart.setAdapter(dataAdapter);

				}

				public void serverErrorHandler(Exception e) {

					// z.B. Fehler Dialog aufploppen lassen
					Log.e("error", "called");
				}
			});
			params = new JSONObject();
			si.call("gibVersicherungsart", params);

			
			//----------------------------------------------------------------------------------//
			//gibKrankenkasse
			//----------------------------------------------------------------------------------//
			
			params = new JSONObject();
			pDialog.show();

			si = new ServerInterface();
			si.addListener(new ServerInterfaceListener() {

				public void serverSuccessHandler(JSONObject result) throws JSONException {

					// z.B. Swirl jetzt verstecken und Grid mit Daten füllen
					pDialog.dismiss();

					List<String> list = new ArrayList<String>();

					JSONArray kk = result.getJSONArray("data");

					for (int i = 0; i < kk.length(); i++) {
						list.add(kk.getString(i));
					}
					Log.i("data", kk.toString());
					Log.i("msg", result.getString("msg"));

					ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, list);
					dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
					kKrankenkasse.setAdapter(dataAdapter);

				}

				public void serverErrorHandler(Exception e) {

					// z.B. Fehler Dialog aufploppen lassen
					Log.e("error", "called");
				}
			});
			params = new JSONObject();
			si.call("gibKrankenkasse", params);

			
			//----------------------------------------------------------------------------------//
			//gibLeistungsart
			//----------------------------------------------------------------------------------//
			
			params = new JSONObject();
			pDialog.show();

			si = new ServerInterface();
			si.addListener(new ServerInterfaceListener() {

				public void serverSuccessHandler(JSONObject result) throws JSONException {

					// z.B. Swirl jetzt verstecken und Grid mit Daten füllen
					pDialog.dismiss();

					List<String> list = new ArrayList<String>();

					JSONArray la = result.getJSONArray("data");

					for (int i = 0; i < la.length(); i++) {
						list.add(la.getString(i));
					}
					Log.i("data", la.toString());
					Log.i("msg", result.getString("msg"));

					ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, list);
					dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
					kLeistungsart.setAdapter(dataAdapter);

				}

				public void serverErrorHandler(Exception e) {

					// z.B. Fehler Dialog aufploppen lassen
					Log.e("error", "called");
				}
			});
			params = new JSONObject();
			si.call("gibLeistungsart", params);
			
			
			//----------------------------------------------------------------------------------//
			//gibAngehoerigerArt
			//----------------------------------------------------------------------------------//
			
			params = new JSONObject();
			pDialog.show();

			si = new ServerInterface();
			si.addListener(new ServerInterfaceListener() {

				public void serverSuccessHandler(JSONObject result) throws JSONException {

					// z.B. Swirl jetzt verstecken und Grid mit Daten füllen
					pDialog.dismiss();

					List<String> list = new ArrayList<String>();

					JSONArray aa = result.getJSONArray("data");

					for (int i = 0; i < aa.length(); i++) {
						list.add(aa.getString(i));
					}
					Log.i("data", aa.toString());
					Log.i("msg", result.getString("msg"));

					ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, list);
					dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
					aArt.setAdapter(dataAdapter);

				}

				public void serverErrorHandler(Exception e) {

					// z.B. Fehler Dialog aufploppen lassen
					Log.e("error", "called");
				}
			});
			params = new JSONObject();
			si.call("gibAngehoerigerArt", params);
			
			
			//----------------------------------------------------------------------------------//
			//gibAngehoeriger
			//----------------------------------------------------------------------------------//
			
			params = new JSONObject();
			params.put("angehoerigerId", 1);
			pDialog.show();

			si = new ServerInterface();
			si.addListener(new ServerInterfaceListener() {

				public void serverSuccessHandler(JSONObject result) throws JSONException {

					// z.B. Swirl jetzt verstecken und Grid mit Daten füllen
					pDialog.dismiss();

					aName.setText(result.getJSONObject("data").getString("name"));
					aVorname.setText(result.getJSONObject("data").getString("vorname"));
					aStrasse.setText(result.getJSONObject("data").getString("strasse"));
					aHausnummer.setText(result.getJSONObject("data").getString("hausnummer"));
					aPLZ.setText(result.getJSONObject("data").getString("plz"));
					aOrt.setText(result.getJSONObject("data").getString("ort"));

					aTelefon.setText(result.getJSONObject("data").getString("telefon"));
					aMobil.setText(result.getJSONObject("data").getString("mobil"));
					aFax.setText(result.getJSONObject("data").getString("fax"));
					aMail.setText(result.getJSONObject("data").getString("email"));

				}

				public void serverErrorHandler(Exception e) {

					// z.B. Fehler Dialog aufploppen lassen
					Log.e("error", "called");
				}
			});
			si.call("gibAngehoeriger", params);
			

			//----------------------------------------------------------------------------------//
			//gibKundendaten
			//----------------------------------------------------------------------------------//
			
			params = new JSONObject();
			params.put("kundeId", 1);

			// z.B. Lass Swirl jetzt rotieren
			pDialog.show();

			si = new ServerInterface();
			si.addListener(new ServerInterfaceListener() {

				public void serverSuccessHandler(JSONObject result) throws JSONException {

					pDialog.dismiss();

					String[] values = new String[] { ADRESSE, KONTAKT, INFORMATION, VERSORGUNG, ANGEHOERIGER };

					kName.setText(result.getJSONObject("data").getString("name"));
					kVorname.setText(result.getJSONObject("data").getString("vorname"));
					kStrasse.setText(result.getJSONObject("data").getString("strasse"));
					kHausnummer.setText(result.getJSONObject("data").getString("hausnummer"));
					kPLZ.setText(result.getJSONObject("data").getString("plz"));
					kOrt.setText(result.getJSONObject("data").getString("ort"));

					kTelefon.setText(result.getJSONObject("data").getString("telefon"));
					kMobil.setText(result.getJSONObject("data").getString("mobil"));
					kFax.setText(result.getJSONObject("data").getString("fax"));
					kMail.setText(result.getJSONObject("data").getString("email"));

					kGeburtsdatum.setText(result.getJSONObject("data").getString("geburtsdatum"));

					/*
					 * JSONArray fs = result.getJSONArray("data");
					 * 
					 * for (int i = 0; i < fs.length(); i++) { fs.getString(i);
					 * } Log.i("data", fs.toString()); Log.i("msg",
					 * result.getString("msg"));
					 */

					ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, values);
					ListView lv = (ListView) fragView.findViewById(R.id.listView1);
					lv.setClickable(true);
					lv.setAdapter(adapter);

					lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
						public void onItemClick(AdapterView<?> arg0, View v, int position, long id) {

							switch (position) {
							case 0:
								Log.i("CASE", "IN Case: 0");
								switch (currentSelected) {
								case 0:
									Log.i("Current-CASE", "IN Case: 0");
									layAdresse.setVisibility(View.GONE);
									break;
								case 1:
									Log.i("Current-CASE", "IN Case: 1");
									layKontakt.setVisibility(View.GONE);
									break;
								case 2:
									Log.i("Current-CASE", "IN Case: 2");
									layInformation.setVisibility(View.GONE);
									break;
								case 3:
									Log.i("Current-CASE", "IN Case: 3");
									layVersorgung.setVisibility(View.GONE);
									break;
								case 4:
									Log.i("Current-CASE", "IN Case: 4");
									layAngehoeriger.setVisibility(View.GONE);
									break;
								}
								Log.i("CASE-0", "Out of Case 0!");
								layAdresse.setVisibility(View.VISIBLE);
								currentSelected = position;
								break;
							case 1:
								Log.i("CASE", "IN Case: 1");
								switch (currentSelected) {
								case 0:
									Log.i("Current-CASE", "IN Case: 0");
									layAdresse.setVisibility(View.GONE);
									break;
								case 1:
									Log.i("Current-CASE", "IN Case: 1");
									layKontakt.setVisibility(View.GONE);
									break;
								case 2:
									Log.i("Current-CASE", "IN Case: 2");
									layInformation.setVisibility(View.GONE);
									break;
								case 3:
									Log.i("Current-CASE", "IN Case: 3");
									layVersorgung.setVisibility(View.GONE);
									break;
								case 4:
									Log.i("Current-CASE", "IN Case: 4");
									layAngehoeriger.setVisibility(View.GONE);
									break;
								}
								Log.i("CASE-1", "Out of Case 1!");
								layKontakt.setVisibility(View.VISIBLE);
								currentSelected = position;
								break;
							case 2:
								Log.i("CASE", "IN Case: 2");
								switch (currentSelected) {
								case 0:
									Log.i("Current-CASE", "IN Case: 0");
									layAdresse.setVisibility(View.GONE);
									break;
								case 1:
									Log.i("Current-CASE", "IN Case: 1");
									layKontakt.setVisibility(View.GONE);
									break;
								case 2:
									Log.i("Current-CASE", "IN Case: 2");
									layInformation.setVisibility(View.GONE);
									break;
								case 3:
									Log.i("Current-CASE", "IN Case: 3");
									layVersorgung.setVisibility(View.GONE);
									break;
								case 4:
									Log.i("Current-CASE", "IN Case: 4");
									layAngehoeriger.setVisibility(View.GONE);
									break;
								}
								Log.i("CASE-2", "Out of Case 2!");
								layInformation.setVisibility(View.VISIBLE);
								currentSelected = position;
								break;
							case 3:
								Log.i("CASE", "IN Case: 3");
								switch (currentSelected) {
								case 0:
									Log.i("Current-CASE", "IN Case: 0");
									layAdresse.setVisibility(View.GONE);
									break;
								case 1:
									Log.i("Current-CASE", "IN Case: 1");
									layKontakt.setVisibility(View.GONE);
									break;
								case 2:
									Log.i("Current-CASE", "IN Case: 2");
									layInformation.setVisibility(View.GONE);
									break;
								case 3:
									Log.i("Current-CASE", "IN Case: 3");
									layVersorgung.setVisibility(View.GONE);
									break;
								case 4:
									Log.i("Current-CASE", "IN Case: 4");
									layAngehoeriger.setVisibility(View.GONE);
									break;
								}
								Log.i("CASE-3", "Out of Case 3!");
								layVersorgung.setVisibility(View.VISIBLE);
								currentSelected = position;
								break;
							case 4:
								Log.i("CASE", "IN Case: 4");
								switch (currentSelected) {
								case 0:
									Log.i("Current-CASE", "IN Case: 0");
									layAdresse.setVisibility(View.GONE);
									break;
								case 1:
									Log.i("Current-CASE", "IN Case: 1");
									layKontakt.setVisibility(View.GONE);
									break;
								case 2:
									Log.i("Current-CASE", "IN Case: 2");
									layInformation.setVisibility(View.GONE);
									break;
								case 3:
									Log.i("Current-CASE", "IN Case: 3");
									layVersorgung.setVisibility(View.GONE);
									break;
								case 4:
									Log.i("Current-CASE", "IN Case: 4");
									layAngehoeriger.setVisibility(View.GONE);
									break;
								}
								Log.i("CASE-4", "Out of Case 4!");
								layAngehoeriger.setVisibility(View.VISIBLE);
								currentSelected = position;
								break;
							}
						}
					});

					toggleEditAdresse.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
						public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
							if (isChecked) {
								// The toggle is enabled
								kName.setFocusable(true);
								kName.setClickable(true);
								kName.setCursorVisible(true);
								kName.setFocusableInTouchMode(true);
								kName.requestFocus();

								imm.showSoftInput(kName, InputMethodManager.SHOW_IMPLICIT);

								kVorname.setFocusable(true);
								kVorname.setClickable(true);
								kVorname.setCursorVisible(true);
								kVorname.setFocusableInTouchMode(true);
								
								kStrasse.setFocusable(true);
								kStrasse.setClickable(true);
								kStrasse.setCursorVisible(true);
								kStrasse.setFocusableInTouchMode(true);
								
								kPLZ.setFocusable(true);
								kPLZ.setClickable(true);
								kPLZ.setCursorVisible(true);
								kPLZ.setFocusableInTouchMode(true);
								
								kHausnummer.setFocusable(true);
								kHausnummer.setClickable(true);
								kHausnummer.setCursorVisible(true);
								kHausnummer.setFocusableInTouchMode(true);
								
								kOrt.setFocusable(true);
								kOrt.setClickable(true);
								kOrt.setCursorVisible(true);
								kOrt.setFocusableInTouchMode(true);
								
								//toggleEdit1.setBackgroundColor(Color.rgb(51, 182, 234));
							} else {
								// The toggle is disabled
								kName.setFocusable(false);
								kName.setClickable(false);
								kName.setCursorVisible(false);
								kName.setFocusableInTouchMode(false);
								
								kVorname.setFocusable(false);
								kVorname.setClickable(false);
								kVorname.setCursorVisible(false);
								kVorname.setFocusableInTouchMode(false);
								
								kStrasse.setFocusable(false);
								kStrasse.setClickable(false);
								kStrasse.setCursorVisible(false);
								kStrasse.setFocusableInTouchMode(false);
								
								kPLZ.setFocusable(false);
								kPLZ.setClickable(false);
								kPLZ.setCursorVisible(false);
								kPLZ.setFocusableInTouchMode(false);
								
								kHausnummer.setFocusable(false);
								kHausnummer.setClickable(false);
								kHausnummer.setCursorVisible(false);
								kHausnummer.setFocusableInTouchMode(false);
								
								kOrt.setFocusable(false);
								kOrt.setClickable(false);
								kOrt.setCursorVisible(false);
								kOrt.setFocusableInTouchMode(false);
								
								imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
								//toggleEdit1.setBackgroundColor(Color.LTGRAY);
							}
						}
					});
					
					toggleEditKontakt.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
						public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
							if (isChecked) {
								// The toggle is enabled
								kTelefon.setFocusable(true);
								kTelefon.setClickable(true);
								kTelefon.setCursorVisible(true);
								kTelefon.setFocusableInTouchMode(true);
								kTelefon.requestFocus();

								imm.showSoftInput(kTelefon, InputMethodManager.SHOW_IMPLICIT);

								kMobil.setFocusable(true);
								kMobil.setClickable(true);
								kMobil.setCursorVisible(true);
								kMobil.setFocusableInTouchMode(true);
								
								kFax.setFocusable(true);
								kFax.setClickable(true);
								kFax.setCursorVisible(true);
								kFax.setFocusableInTouchMode(true);
								
								kMail.setFocusable(true);
								kMail.setClickable(true);
								kMail.setCursorVisible(true);
								kMail.setFocusableInTouchMode(true);
								
							} else {
								// The toggle is disabled
								kTelefon.setFocusable(false);
								kTelefon.setClickable(false);
								kTelefon.setCursorVisible(false);
								kTelefon.setFocusableInTouchMode(false);
								kTelefon.requestFocus();

								kMobil.setFocusable(false);
								kMobil.setClickable(false);
								kMobil.setCursorVisible(false);
								kMobil.setFocusableInTouchMode(false);
								
								kFax.setFocusable(false);
								kFax.setClickable(false);
								kFax.setCursorVisible(false);
								kFax.setFocusableInTouchMode(false);
								
								kMail.setFocusable(false);
								kMail.setClickable(false);
								kMail.setCursorVisible(false);
								kMail.setFocusableInTouchMode(false);
								
								imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
							}
						}
					});
					
					toggleEditInformation.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
						public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
							if (isChecked) {
								// The toggle is enabled
								kGeburtsdatum.setFocusable(true);
								kGeburtsdatum.setClickable(true);
								kGeburtsdatum.setCursorVisible(true);
								kGeburtsdatum.setFocusableInTouchMode(true);
								kGeburtsdatum.requestFocus();

								imm.showSoftInput(kGeburtsdatum, InputMethodManager.SHOW_IMPLICIT);

							} else {
								// The toggle is disabled
								kGeburtsdatum.setFocusable(false);
								kGeburtsdatum.setClickable(false);
								kGeburtsdatum.setCursorVisible(false);
								kGeburtsdatum.setFocusableInTouchMode(false);
								
								imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
							}
						}
					});
					
					toggleEditAngehoeriger.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
						public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
							if (isChecked) {
								// The toggle is enabled
								aName.setFocusable(true);
								aName.setClickable(true);
								aName.setCursorVisible(true);
								aName.setFocusableInTouchMode(true);
								aName.requestFocus();

								imm.showSoftInput(aName , InputMethodManager.SHOW_IMPLICIT);

								aVorname.setFocusable(true);
								aVorname.setClickable(true);
								aVorname.setCursorVisible(true);
								aVorname.setFocusableInTouchMode(true);
								
								aStrasse.setFocusable(true);
								aStrasse.setClickable(true);
								aStrasse.setCursorVisible(true);
								aStrasse.setFocusableInTouchMode(true);
								
								aPLZ.setFocusable(true);
								aPLZ.setClickable(true);
								aPLZ.setCursorVisible(true);
								aPLZ.setFocusableInTouchMode(true);
								
								aHausnummer.setFocusable(true);
								aHausnummer.setClickable(true);
								aHausnummer.setCursorVisible(true);
								aHausnummer.setFocusableInTouchMode(true);
								
								aOrt.setFocusable(true);
								aOrt.setClickable(true);
								aOrt.setCursorVisible(true);
								aOrt.setFocusableInTouchMode(true);
								
								aTelefon.setFocusable(true);
								aTelefon.setClickable(true);
								aTelefon.setCursorVisible(true);
								aTelefon.setFocusableInTouchMode(true);

								aMobil.setFocusable(true);
								aMobil.setClickable(true);
								aMobil.setCursorVisible(true);
								aMobil.setFocusableInTouchMode(true);
								
								aFax.setFocusable(true);
								aFax.setClickable(true);
								aFax.setCursorVisible(true);
								aFax.setFocusableInTouchMode(true);
								
								aMail.setFocusable(true);
								aMail.setClickable(true);
								aMail.setCursorVisible(true);
								aMail.setFocusableInTouchMode(true);
								
							} else {
								// The toggle is disabled
								aName.setFocusable(false);
								aName.setClickable(false);
								aName.setCursorVisible(false);
								aName.setFocusableInTouchMode(false);

								aVorname.setFocusable(false);
								aVorname.setClickable(false);
								aVorname.setCursorVisible(false);
								aVorname.setFocusableInTouchMode(false);
								
								aStrasse.setFocusable(false);
								aStrasse.setClickable(false);
								aStrasse.setCursorVisible(false);
								aStrasse.setFocusableInTouchMode(false);
								
								aPLZ.setFocusable(false);
								aPLZ.setClickable(false);
								aPLZ.setCursorVisible(false);
								aPLZ.setFocusableInTouchMode(false);
								
								aHausnummer.setFocusable(false);
								aHausnummer.setClickable(false);
								aHausnummer.setCursorVisible(false);
								aHausnummer.setFocusableInTouchMode(false);
								
								aOrt.setFocusable(false);
								aOrt.setClickable(false);
								aOrt.setCursorVisible(false);
								aOrt.setFocusableInTouchMode(false);
								
								aTelefon.setFocusable(false);
								aTelefon.setClickable(false);
								aTelefon.setCursorVisible(false);
								aTelefon.setFocusableInTouchMode(false);

								aMobil.setFocusable(false);
								aMobil.setClickable(false);
								aMobil.setCursorVisible(false);
								aMobil.setFocusableInTouchMode(false);
								
								aFax.setFocusable(false);
								aFax.setClickable(false);
								aFax.setCursorVisible(false);
								aFax.setFocusableInTouchMode(false);
								
								aMail.setFocusable(false);
								aMail.setClickable(false);
								aMail.setCursorVisible(false);
								aMail.setFocusableInTouchMode(false);
								
								imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
							}
						}
					});

				}

				public void serverErrorHandler(Exception e) {
					// TODO Auto-generated method stub
				}
			});
			si.call("gibKundendaten", params);

		} catch (JSONException e) {
		}
		return fragView;
	}

	/****************************************************************************************************
	 * postData Methode
	 *****************************************************************************************************/

	public JSONObject postData(String method, JSONObject params) {
		JSONObject result = null;
		String jsonString = "";

		// Create a new HttpClient and Post Header
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost("http://jd.mazebert.com/dbService.php");
		// HttpPost httppost = new
		// HttpPost("http://jaydee85.ja.funpic.de/app/dbService.php");
		// HttpPost httppost = new
		// HttpPost("http://jaydee.bplaced.net/app/dbService.php");
		// HttpPost httppost = new
		// HttpPost("http://jaydee.site11.com/app/dbService.php");

		try {
			params.put("method", method);

			// Create a NameValuePair out of the JSONObject + a name
			List<NameValuePair> nVP = new ArrayList<NameValuePair>();
			nVP.add(new BasicNameValuePair("jsonPost", params.toString()));

			// Hand the NVP to the POST
			httppost.setEntity(new UrlEncodedFormEntity(nVP));
			Log.i("main", "TestPOST - nVP = " + nVP.toString());

			// Execute HTTP Post Request
			HttpResponse response = httpclient.execute(httppost);
			Log.e("HTTPresponse", response.toString());

			// for JSON:
			if (response != null) {
				Log.e("RESPONSE", "RESPONSE not NULL");
				InputStream is = response.getEntity().getContent();
				Log.e("InputSTREAM is", is.toString());

				jsonString = convertStreamToString(is);
			}
			Log.e("ausgabe", jsonString);
			// tv.setText(text);
			Log.e("TEXT View", "ENDE");

		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
		} catch (IOException e) {
			// TODO Auto-generated catch block
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// try parse the string to a JSON object
		try {
			result = new JSONObject(jsonString);
		} catch (JSONException e) {
			Log.e("JSON Parser", "Error parsing data " + e.toString());
		}

		return result;
	}

	/****************************************************************************************************
	 * convertStreamToString Methode
	 *****************************************************************************************************/

	public static String convertStreamToString(InputStream is) {
		/*
		 * To convert the InputStream to String we use the
		 * BufferedReader.readLine() method. We iterate until the BufferedReader
		 * return null which means there's no more data to read. Each line will
		 * appended to a StringBuilder and returned as String.
		 */

		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();

		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
	}

	@Override
	public boolean onKey(View v, int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		return false;
	}
}