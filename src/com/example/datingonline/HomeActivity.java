package com.example.datingonline;

import Fragment.HomeFragment;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class HomeActivity extends FragmentActivity implements OnClickListener  {
	public final static String 	EXTRA_USERNAME = "com.example.datingonline.USERNAME";
	private HomeFragment homeFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		
//		if (savedInstanceState == null) {
//	        // Add the fragment on initial activity setup
//			homeFragment = new HomeFragment();
//	        getSupportFragmentManager()
//	        .beginTransaction()
//	        .add(R., homeFragment)
//	        .commit();
//	    } else {
//	        // Or set the fragment from restored state info
//	    	homeFragment = (HomeFragment) getSupportFragmentManager().findFragmentById(android.R.id.content);
//	    }
		
		Button loginBtn = (Button) findViewById(R.id.loginButton);
		loginBtn.setOnClickListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.home, menu);
		return true;
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		EditText editText = (EditText) findViewById(R.id.txtUsername);
		Intent intent = new Intent(this, MainActivity.class);
		intent.putExtra(EXTRA_USERNAME, editText.getText().toString());
		startActivity(intent);	
		
	}

}
