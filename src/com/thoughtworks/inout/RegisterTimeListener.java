package com.thoughtworks.inout;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.view.View.OnClickListener;


public class RegisterTimeListener implements OnClickListener {

	private Confirmable confirmable;
	private Context context;

	public RegisterTimeListener(Context context, Confirmable confirmable) {
		this.confirmable = confirmable;
		this.context = context;
	}

	@Override
	public void onClick(final View button) {

		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

		
		alertDialogBuilder.setMessage("Do you want to confirm it?");
		alertDialogBuilder.setCancelable(false);
		
		alertDialogBuilder.setNegativeButton("No",new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog,int id) {
				dialog.cancel();
			}
		});

		alertDialogBuilder.setPositiveButton("Yes",new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog,int id) {
				confirmable.onConfirm(button);
			}
		});
		
		AlertDialog alertDialog = alertDialogBuilder.create();

		alertDialog.show();
	}
}


