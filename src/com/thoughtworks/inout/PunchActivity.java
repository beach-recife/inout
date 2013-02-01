package com.thoughtworks.inout;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

import com.thoughtworks.inout.clock.Clock;
import com.thoughtworks.inout.db.SQLLiteTimeCardDAO;
import com.thoughtworks.inout.db.TimeCardDAO;
import com.thoughtworks.inout.exception.DataRetrieveException;

public class PunchActivity extends Activity {
	
	private final int CONFIRM_PUNCH_DIALOG = 1;
	private final int ERROR_ALERT_DIALOG_ID = 0;
	
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
				showDialog(CONFIRM_PUNCH_DIALOG);
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
	protected Dialog onCreateDialog(int id, Bundle args) {
		Dialog diag = null;
		if (id == ERROR_ALERT_DIALOG_ID) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("An Error Ocurred");			
			diag = builder.create();
		}
		if (id == CONFIRM_PUNCH_DIALOG) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setCancelable(true);
			builder.setMessage(getString(R.string.confirm_punch_message));
			builder.setNegativeButton(getString(R.string.cancel_button), new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.cancel();
				}
			});
			builder.setPositiveButton(getString(R.string.ok_button), new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					timeCardDAO.insertPunch(new Punch(getCurrentPunchType(), Clock.now()));
					((Button) PunchActivity.this.findViewById(R.id.punch_button)).setText(getNextPunchType().getValue());
				}
			});
			diag = builder.create();
		}
		return diag;
	}
}