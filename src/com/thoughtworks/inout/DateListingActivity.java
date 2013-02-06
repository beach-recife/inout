package com.thoughtworks.inout;

import java.text.ParseException;
import java.util.Date;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.Context;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.thoughtworks.inout.db.SQLLiteTimeCardDAO;
import com.thoughtworks.inout.exception.DataRetrieveException;

public class DateListingActivity extends ListActivity {
	// This is the Adapter being used to display the list's data
    ArrayAdapter<String> mAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
		setContentView(R.layout.activity_date_listing);
		
		// Create a progress bar to display while the list loads
        ProgressBar progressBar = new ProgressBar(this);
        progressBar.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT));
        progressBar.setIndeterminate(true);
        getListView().setEmptyView(progressBar);
        
        // Must add the progress bar to the root of the layout
        ViewGroup root = (ViewGroup) findViewById(android.R.id.content);
        root.addView(progressBar);
        
        // For the cursor adapter, specify which columns go into which views
        Date[] punchDates;
		try {
			punchDates = new SQLLiteTimeCardDAO(this).getAllRegisterDates();
			String[] formattedPunches = new String[punchDates.length];
	        java.text.DateFormat df = DateFormat.getLongDateFormat(this);
	        for (int i = 0;i < formattedPunches.length; i++) {
	        	formattedPunches[i] = df.format(punchDates[i]);
	        }

	        // Create an empty adapter we will use to display the loaded data.
	        // We pass null for the cursor, then update it in onLoadFinished()
	        mAdapter = new ArrayAdapter<String>(this, 
	                android.R.layout.simple_list_item_1, formattedPunches);
	        setListAdapter(mAdapter);
		} catch (DataRetrieveException e1) {		
			e1.printStackTrace();
		}

        this.getListView().setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int pos,
					long id) {
				String punchDateText = ((TextView) view).getText().toString();
				Context context = view.getContext();
				java.text.DateFormat df = DateFormat.getLongDateFormat(context);
				Bundle bundle = new Bundle();
				try {
					bundle.putLong("punchDate", df.parse(punchDateText).getTime());
				} catch (ParseException e) {
					e.printStackTrace();
				}
				ListActivity activity = DateListingActivity.this;
				activity.showDialog(0, bundle);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_date_listing, menu);
		return true;
	}

	@Override
	protected void onPrepareDialog(int id, Dialog dialog, Bundle args) {
		ListView list = ((AlertDialog) dialog).getListView();
		Date punchDate = new Date(args.getLong("punchDate"));
		Punch[] punches;
		try {
			punches = new SQLLiteTimeCardDAO(this).getAllPunchesFor(punchDate);
			String[] formattedPunches = new String[punches.length];
			for (int i = 0; i < punches.length; i++) {
				formattedPunches[i] = punches[i].toString();
			}
	        list.setAdapter(new ArrayAdapter<String>(this, 
	            android.R.layout.simple_list_item_1, formattedPunches));
		} catch (DataRetrieveException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		// TODO Auto-generated method stub
		return super.onMenuItemSelected(featureId, item);
	}

	@Override
	protected Dialog onCreateDialog(int id, Bundle args) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setCancelable(true).setItems(new String[]{""}, null);
        return builder.create();		
	}
}