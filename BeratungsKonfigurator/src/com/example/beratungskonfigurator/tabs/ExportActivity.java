package com.example.beratungskonfigurator.tabs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.example.beratungskonfigurator.R;
import com.example.beratungskonfigurator.ExportPdf;


public class ExportActivity extends Fragment {
	
	
	private static final String KUNDENDATEN = "Kundendaten";
	private static final String WOHNUNGSDATEN = "Wohnungsdaten";
	private static final String PROBLEMDATEN = "gesundheitliche Problemstellung";
	private static final String ANWENDUNGSFALL = "Anwendungsfall";
	private static final String SZENARIO = "persönliche Szenarien";
	
	ListView exportList;
	Button createPdf;

	
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		final View exportView = (View) inflater.inflate(R.layout.tab_export_layout, container, false);
		
		createPdf = (Button) exportView.findViewById(R.id.createPdf);
		
		String[] values = new String[] { KUNDENDATEN, WOHNUNGSDATEN, PROBLEMDATEN, ANWENDUNGSFALL, SZENARIO };
		
		List<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
		for (int i = 0; i < values.length; i++) {
			HashMap<String, String> hm = new HashMap<String, String>();
			hm.put("txt", values[i]);
			list.add(hm);
		}
		SimpleAdapter dataAdapter = new SimpleAdapter(getActivity(), list, R.layout.listview_checkable, new String[] { "txt" }, new int[] { R.id.txt });
		exportList = (ListView) exportView.findViewById(R.id.exportList);
		exportList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		exportList.setAdapter(dataAdapter);

		exportList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View v, int position, long id) {
				exportList.setItemChecked(position, true);
			}
		});
		
		createPdf.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				ExportPdf exportPDF = new ExportPdf();
				exportPDF.main(null);
			}
		});
		
		
		return exportView;
	}

	

}
