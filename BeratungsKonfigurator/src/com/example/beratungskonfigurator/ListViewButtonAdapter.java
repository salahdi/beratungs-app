package com.example.beratungskonfigurator;

import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.TextView;

public class ListViewButtonAdapter extends BaseAdapter {
	
	private Context context;
	private List<String> items;
	private List<String> anzList;
	private LayoutInflater mInflater;

	public ListViewButtonAdapter(Context context, List<String> items, List<String> anzList) {
		mInflater = LayoutInflater.from(context);
		this.context = context;
		this.items = items;
		this.anzList = anzList;
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
	
	public List<String> getAnzGeraeteListeAll() {
		List<String> alleGeraete = anzList;
		return alleGeraete;
	}
	

	public View getView(final int position, View convertView, ViewGroup parent) {
		String item = items.get(position);
		String anzahl = anzList.get(position);
		
		final ViewHolder holder;
		
		if (convertView == null) {
	        convertView = mInflater.inflate(R.layout.listview_anzahl_checkable, parent, false);

	        holder = new ViewHolder();

	        holder.itemTV = (CheckedTextView) convertView.findViewById(R.id.txtGeraeteNameChecked);
			holder.tvAnzGeraete = (TextView) convertView.findViewById(R.id.anzGeraete);
			holder.btnMinusGeraete = (Button) convertView.findViewById(R.id.btnMinusGeraete);
			holder.btnPlusGeraete = (Button) convertView.findViewById(R.id.btnPlusGeraete);

	        convertView.setTag(holder);
	    } else {
	    	holder = (ViewHolder) convertView.getTag();
	    }
		holder.tvAnzGeraete.setText(anzahl);
		holder.itemTV.setText(item);
		final NumberPic numberPic = new NumberPic();
		holder.btnMinusGeraete.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				int tfNumber = Integer.valueOf(holder.tvAnzGeraete.getText().toString());
				int number = numberPic.decrement(tfNumber);
				if (number > 0) {
					holder.itemTV.setChecked(true);
				} else {
					holder.itemTV.setChecked(false);
				}
				holder.tvAnzGeraete.setText(Integer.toString(number));
				anzList.set(position, Integer.toString(number));
			}
		});
		holder.btnPlusGeraete.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				int tfNumber = Integer.valueOf(holder.tvAnzGeraete.getText().toString());
				int number = numberPic.increment(tfNumber);
				if (number > 0) {
					holder.itemTV.setChecked(true);
				} else {
					holder.itemTV.setChecked(false);
				}
				holder.tvAnzGeraete.setText(Integer.toString(number));
				anzList.set(position, Integer.toString(number));
			}
		});
		return convertView;
	}
	
	private static class ViewHolder {
	    public CheckedTextView itemTV;
	    public TextView tvAnzGeraete;
	    public Button btnMinusGeraete;
	    public Button btnPlusGeraete;
	}
}