package com.example.datingonline;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;


import com.google.gson.Gson;

import io.socket.IOAcknowledge;
import io.socket.IOCallback;
import io.socket.SocketIO;
import io.socket.SocketIOException;
import Model.UserInfo;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.widget.ListView;

public class MainActivity extends Activity {
	
	ListView listview;
	List<UserInfo> usersInfomation;	 

	SocketIO socket = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		Intent intent = getIntent();
		final String username = intent.getStringExtra(HomeActivity.EXTRA_USERNAME);
		
		listview = (ListView) findViewById(R.id.listUsersOnline);
		usersInfomation = new ArrayList<UserInfo>();
		
		final CustomArrayAdapter adapter = new CustomArrayAdapter(this, usersInfomation);		 
	    listview.setAdapter(adapter);		
		
		try {
			socket = new SocketIO("http://10.0.2.2:8888");
			System.out.println("connect.");
			
		} catch (MalformedURLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        socket.connect(new IOCallback() {
            @Override
            public void onMessage(JSONObject json, IOAcknowledge ack) {
                try {
                    System.out.println("Server said:" + json.toString(2));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onMessage(String data, IOAcknowledge ack) {
                System.out.println("Server said: " + data);
            }

            @Override
            public void onError(SocketIOException socketIOException) {
                System.out.println("an Error occured");
                socketIOException.printStackTrace();
            }

            @Override
            public void onDisconnect() {
                System.out.println("Connection terminated.");
            }

            @Override
            public void onConnect() {
                System.out.println("Connection established");
            }

            @Override
            public void on(String event, IOAcknowledge ack, final Object... args) {            	
                System.out.println("Server triggered event '" + event + "'");
                if ("needInfo".equals(event) ) {    
                	
                	UserInfo userInfo = new UserInfo(username, "");
                	Gson gson = new Gson();
                	String userInfoJson = gson.toJson(userInfo);                	
                	socket.emit("sendInfo", userInfoJson);
                } else if ("displayUsersOnline".equals(event)) {                	
                	
                	String json = (String)args[0];
                	Gson gson = new Gson();
                	UserInfo[] usersInfo = gson.fromJson(json, UserInfo[].class);
                	
                	for (UserInfo userInfo : usersInfo) {
						usersInfomation.add(userInfo);
					}
                	
                	runOnUiThread(new Runnable() {
	            	    public void run() {
	            	    	adapter.notifyDataSetChanged();
	            	    }
	            	});       

                } else if ("hasUserDisconnect".equals(event)) {
                	String socketId = (String)args[0];
                	UserInfo userRemoved = null;
                	for (UserInfo userInfo : usersInfomation) {
						if ((userInfo.socketId).equals(socketId)) {
							userRemoved = userInfo;
						}
					}
                	usersInfomation.remove(userRemoved);
                	
                	runOnUiThread(new Runnable() {
	            	    public void run() {
	            	    	adapter.notifyDataSetChanged();
	            	    }
	            	});     
                	
                } else if ("serverMessage".equals(event)) {
                	
//                	runOnUiThread(new Runnable() {
//                	    public void run() {
//                	    	TextView messagesArea = (TextView) findViewById(R.id.messagesArea);
//                        	String previousMessages = (String) messagesArea.getText();
//                        	String messages = "";
//                        	if (previousMessages == "") {
//                        		messages = (String)args[0];
//                        	} else {
//                        		messages = previousMessages + "\n" + (String)args[0];
//                        	}
//                        	
//                        	messagesArea.setText(messages);
//                	    }
//                	});                	
                }
            }
        });

        // This line is cached until the connection is established.        
       
        
	}
	
	@Override
	protected void onStop () {
		super.onStop();
		socket.disconnect();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
}
