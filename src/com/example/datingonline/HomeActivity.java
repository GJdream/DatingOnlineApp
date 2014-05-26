package com.example.datingonline;

import com.facebook.Session;
import com.facebook.Session.StatusCallback;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class HomeActivity extends FragmentActivity implements OnClickListener {
	public final static String EXTRA_USERNAME = "com.example.datingonline.USERNAME";
	private static final int WELCOME = 0;
	private static final int USERS_ONLINE = 1;
	private static final int FRAGMENT_COUNT = USERS_ONLINE + 1;
	private Fragment[] fragments = new Fragment[FRAGMENT_COUNT];
	private boolean isResumed = false;
	private UiLifecycleHelper uiHelper; 
	private StatusCallback callback = new StatusCallback() {		
		@Override
		public void call(Session session, SessionState state, Exception exception) {
			onSessionStateChange(session, state, exception);
			
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		uiHelper = new UiLifecycleHelper(this, callback);
		uiHelper.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_home);
		

		// hide all fragment on Home activity
		FragmentManager fm = getSupportFragmentManager();
		fragments[WELCOME] = fm.findFragmentById(R.id.welcomeFragment);
		fragments[USERS_ONLINE] = fm.findFragmentById(R.id.usersOnlineFragment);
		FragmentTransaction transaction = fm.beginTransaction();
		for (int i = 0; i < fragments.length; i++) {
			transaction.hide(fragments[i]);
		}
		transaction.commit();
	}

	@Override
	public void onResume() {
		super.onResume();
		uiHelper.onResume();
		isResumed = true;
	}

	@Override
	public void onPause() {
		super.onPause();
		uiHelper.onPause();
		isResumed = false;
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	    super.onActivityResult(requestCode, resultCode, data);
	    uiHelper.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onDestroy() {
	    super.onDestroy();
	    uiHelper.onDestroy();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
	    super.onSaveInstanceState(outState);
	    uiHelper.onSaveInstanceState(outState);
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
		// EditText editText = (EditText) findViewById(R.id.txtUsername);
		// Intent intent = new Intent(this, MainActivity.class);
		// intent.putExtra(EXTRA_USERNAME, editText.getText().toString());
		// startActivity(intent);

	}
	
	@Override
	public void onResumeFragments() {
		Session session = Session.getActiveSession();
		if (session != null && session.isOpened()) {
			showFragment(USERS_ONLINE, false);
		} else {
			showFragment(WELCOME, false);
		}
	}

	private void showFragment(int fragmentIndex, boolean addToBackStack) {
		FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction transaction = fm.beginTransaction();
		for (int i = 0; i < fragments.length; i++) {
			if (fragmentIndex == i) {
				transaction.show(fragments[i]);
			} else {
				transaction.hide(fragments[i]);
			}
		}
		if (addToBackStack) {
			transaction.addToBackStack(null);
		}
		transaction.commit();
	}

	private void onSessionStateChange(Session session, SessionState state, Exception exception) {
		// only make changes when home activity is visible
		if (isResumed) {
			FragmentManager fm = getSupportFragmentManager();
			int backStackSize = fm.getBackStackEntryCount();

			// clear the back stack
			for (int i = 0; i < backStackSize; i++) {
				fm.popBackStack();
			}

			if (state.isOpened()) {
				showFragment(USERS_ONLINE, false);
			} else if (state.isClosed()) {
				showFragment(WELCOME, false);
			}
		}
	}
}
