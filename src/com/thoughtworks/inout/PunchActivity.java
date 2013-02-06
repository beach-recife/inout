package com.thoughtworks.inout;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TimePicker;

import com.thoughtworks.inout.clock.Clock;
import com.thoughtworks.inout.db.SQLLiteTimeCardDAO;
import com.thoughtworks.inout.db.TimeCardDAO;
import com.thoughtworks.inout.exception.DataRetrieveException;

public class PunchActivity extends Activity {
	
	public static final int ERROR_ALERT_DIALOG_ID = 0;
	public static final int CONFIRM_PUNCH_DIALOG = 1;
	public static final int DATE_PICKER_ID = 2;
	public static final int TIME_PICKER_ID = 3;
	
	private TimeCardDAO timeCardDAO;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.timeCardDAO = new SQLLiteTimeCardDAO(this);
		setContentView(R.layout.activity_punch);
		Button punchBtn = (Button) findViewById(R.id.punch_button);
		punchBtn.setText(getNextPunchType().getValue());
		punchBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Bundle args = new Bundle();
				args.putSerializable("punch_date", Clock.now());
				showDialog(CONFIRM_PUNCH_DIALOG, args);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_punch, menu);
		return true;
	}
	
	private PunchType getNextPunchType() {
		Punch punch;
		PunchType punchType = null;
		try {
			punch = timeCardDAO.getLastPunch();
			if (punch == null) {
				punchType = PunchType.IN;
			} else {
				punchType = punch.getType() == PunchType.IN ? PunchType.OUT : PunchType.IN;
			}
		} catch (DataRetrieveException e) {
			Bundle args = new Bundle();
			args.putString("message", e.getMessage());
			showDialog(ERROR_ALERT_DIALOG_ID, args);
		}
		return punchType;
	}
	
	private PunchType getCurrentPunchType() {
		return PunchType.getTypeOf(((Button) findViewById(R.id.punch_button)).getText().toString());
	}

	@Override
	protected void onPrepareDialog(int id, Dialog dialog, Bundle args) {
		if (id == ERROR_ALERT_DIALOG_ID) {
			AlertDialog alert = (AlertDialog) dialog;
			alert.setMessage(args.getString("message"));
		}
	}

	@Override
	protected Dialog onCreateDialog(int id, final Bundle args) {
		Dialog diag = null;
		if (id == ERROR_ALERT_DIALOG_ID) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("An Error Ocurred");			
			diag = builder.create();
		}
		if (id == CONFIRM_PUNCH_DIALOG) {
			LinearLayout layout = new LinearLayout(this);
			layout.setOrientation(LinearLayout.VERTICAL);
			
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			Calendar punchDate = GregorianCalendar.getInstance();
			punchDate.setTime((Date)args.getSerializable("punch_date"));
			
			builder.setCancelable(true);
			builder.setTitle(getString(R.string.confirm_punch_message));
			
			DatePicker datePicker = new DatePicker(this);
			datePicker.setId(DATE_PICKER_ID);
			datePicker.updateDate(punchDate.get(Calendar.YEAR), punchDate.get(Calendar.MONTH), punchDate.get(Calendar.DAY_OF_MONTH));
			
			TimePicker timePicker = new TimePicker(this);
			timePicker.setId(TIME_PICKER_ID);
			timePicker.setCurrentHour(punchDate.get(Calendar.HOUR_OF_DAY));
			timePicker.setCurrentMinute(punchDate.get(Calendar.MINUTE));
			timePicker.setIs24HourView(true);
			
			layout.addView(datePicker);
			layout.addView(timePicker);
			
			builder.setView(layout);
			builder.setNegativeButton(getString(R.string.cancel_button), new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.cancel();
				}
			});
			builder.setPositiveButton(getString(R.string.ok_button), new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					timeCardDAO.insertPunch(new Punch(getCurrentPunchType(), (Date)args.getSerializable("punch_date")));
					((Button) PunchActivity.this.findViewById(R.id.punch_button)).setText(getNextPunchType().getValue());
				}
			});
			diag = builder.create();
		}
		return diag;
	}
}