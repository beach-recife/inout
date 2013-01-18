package com.thoughtworks.inout;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

import com.thoughtworks.inout.db.SQLLiteTimeCardDAO;

public class InOutActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_in_out);
        buildButtons();
    }

    private void buildButtons() {
    	Button buttonIn = (Button) findViewById(R.id.button_in);
    	Button buttonOut = (Button) findViewById(R.id.button_out);
    	
    	buttonIn.setOnClickListener(new RegisterTimeListener(this));
    	buttonOut.setOnClickListener(new RegisterTimeListener(this));
    }

	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_in_out, menu);
        return true;
    }
    
    public void registerTime(View view) {
    	TimeCard timeCard = new TimeCard(new SQLLiteTimeCardDAO(this));
    	PunchType punchType = view.getId() == R.id.button_in ? PunchType.IN : PunchType.OUT;
    	timeCard.punch(punchType);
    }
    
   
    
}
