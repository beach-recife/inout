package com.thoughtworks.inout;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

import com.thoughtworks.inout.db.TimeCardDAO;

public class InOutActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_in_out);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_in_out, menu);
        return true;
    }
    
    public void registerTime(View view) {
    	TimeCard timeCard = new TimeCard(new TimeCardDAO(this));
    	TextView console = (TextView) findViewById(R.id.console);
    	console.setText(timeCard.punch(PunchType.IN).toString());
    }
}
