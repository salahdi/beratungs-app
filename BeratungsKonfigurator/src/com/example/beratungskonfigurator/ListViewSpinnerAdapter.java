package com.example.beratungskonfigurator;

import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class ListViewSpinnerAdapter extends BaseAdapter {
	
	private Context context;
	private List<String> items;
	private List<Integer> geraeteId;
	private List<String> geraetestandortList;
	private List<Integer> geraetestandortListKundeId;
	private List<Integer> geraetestandortListRueckgabe;
	private List<Integer> geraetestandortListRueckgabeGeraeteId;
	private LayoutInflater mInflater;
	private Integer geraetestandortItemPosition;
	private String geraetestandortItemText;


	public ListViewSpinnerAdapter(Context context, List<String> items, List<Integer> geraeteId, List<String> geraetestandortList, List<Integer> geraetestandortListKundeId) {
		mInflater = LayoutInflater.from(context);
		this.context = context;
		this.items = items;
		this.geraeteId = geraeteId;
		this.geraetestandortList = geraetestandortList;
		this.geraetestandortListKundeId = geraetestandortListKundeId;
		geraetestandortListRueckgabe = this.geraetestandortListKundeId;
		geraetestandortListRueckgabeGeraeteId = this.geraeteId;
	}

	public int getCount() {
		return items.size();
	}

	public Object getItem(int position) {
		return items.get(position);
	}

	public long getItemId(int position) {
		return position;
	}
	
	public List<Integer> getSpinnerItemId() {
		return geraetestandortListRueckgabe;
	}
	public List<Integer> getSpinnerGeraeteId() {
		return geraetestandortListRueckgabeGeraeteId;
	}
	
	public String getSpinnerItemText() {
		return geraetestandortItemText;
	}
	

	public View getView(final int position, View convertView, ViewGroup parent) {
		
		String nameGeraetString = items.get(position);
		String geraetestandortString = geraetestandortList.get(position);
		Log.i("zu Beginn", "Rückgabe Standort: "+geraetestandortListRueckgabe);
		final ViewHolder holder;
		
		if (convertView == null) {
	        convertView = mInflater.inflate(R.layout.listview_spinner, parent, false);
	        holder = new ViewHolder();

	        holder.nameGeraet = (TextView) convertView.findViewById(R.id.nameGeraet);
			holder.geraetestandortSpinner = (Spinner) convertView.findViewById(R.id.geraetestandortSpinner);

	        convertView.setTag(holder);
	    } else {
	    	holder = (ViewHolder) convertView.getTag();
	    }
		holder.nameGeraet.setText(nameGeraetString);
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, geraetestandortList);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		holder.geraetestandortSpinner.setEnabled(true);
		holder.geraetestandortSpinner.setAdapter(dataAdapter);
		if(geraetestandortListKundeId.isEmpty() || geraetestandortListKundeId.get(position) == 0){
			holder.geraetestandortSpinner.setSelection(47);
		}else{
			holder.geraetestandortSpinner.setSelection((geraetestandortListKundeId.get(position))-1);
		}
		
		holder.geraetestandortSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			 public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
				// TODO Auto-generated method stub
				geraetestandortItemPosition = (holder.geraetestandortSpinner.getSelectedItemPosition())+1;
				geraetestandortListRueckgabe.set(position, geraetestandortItemPosition);
				geraetestandortListRueckgabeGeraeteId.set(position, geraeteId.get(position));
				//geraetestandortItemText = holder.geraetestandortSpinner.getSelectedItem().toString();
			}

			@Override
			public void onNothingSelected(AdapterView<?> parentView) {
				// TODO Auto-generated method stub
				
			}

			 
		});

		
		return convertView;
	}
	
	private static class ViewHolder {
	    public TextView nameGeraet;
	    public Spinner geraetestandortSpinner;
	}
}