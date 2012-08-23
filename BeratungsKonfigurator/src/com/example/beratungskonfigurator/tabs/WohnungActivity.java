package com.example.beratungskonfigurator.tabs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
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
import com.example.beratungskonfigurator.R.color;

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

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
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
		
		String[] values = new String[] { WOHNSITUATION, WOHNFORM, WOHNINFORMATION, WOHNUMFELD, WOHNRAEUME, WOHNBARRIEREN };
		
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, values);
		final ListView lv = (ListView) wohnungView.findViewById(R.id.wohnungList);
		lv.setClickable(true);
		lv.setAdapter(adapter);
		
		lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View v, int position, long id) {
			}
		});
		
        List<HashMap<String,String>> aList = new ArrayList<HashMap<String,String>>();
 
        for(int i=0;i<10;i++){
            HashMap<String, String> hm = new HashMap<String,String>();
            hm.put("txt", "Country: "+i);
            aList.add(hm);
        }
 
        // Keys used in Hashmap
       String[] from = {"txt"};
 
       // Ids of views in listview_layout
       int[] to = { R.id.txt };
 
       // Instantiating an adapter to store each items
       // R.layout.listview_layout defines the layout of each item
       SimpleAdapter wohnsituationAdapter = new SimpleAdapter(getActivity(), aList, R.layout.wohnung_listview, from, to);
       final ListView wohnsituationList = ( ListView ) wohnungView.findViewById(R.id.wohnsituationList);
       
       wohnsituationList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
       wohnsituationList.setAdapter(wohnsituationAdapter);
       
       wohnsituationList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View v, int position, long id) {
				Toast.makeText(getActivity(), "Multiple List: "+wohnsituationList.getItemAtPosition(position).toString()+" Position: "+position, Toast.LENGTH_SHORT).show();
			}
		});
		
		
		return wohnungView;
	}

	

}
