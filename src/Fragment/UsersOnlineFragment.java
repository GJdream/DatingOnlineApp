package Fragment;

import io.socket.IOAcknowledge;
import io.socket.IOCallback;
import io.socket.SocketIO;
import io.socket.SocketIOException;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.example.datingonline.CustomArrayAdapter;
import com.example.datingonline.R;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.ProfilePictureView;
import com.google.gson.Gson;

import Model.UserInfo;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

public class UsersOnlineFragment extends Fragment {
	private static final int REAUTH_ACTIVITY_CODE = 100;
	
	ListView listview;
	List<UserInfo> usersInfomation;
	SocketIO socket = null;

	private ProfilePictureView profilePictureView;
	private TextView userNameView;

	private UiLifecycleHelper uiHelper;
	private Session.StatusCallback callback = new Session.StatusCallback() {
		@Override
		public void call(final Session session, final SessionState state, final Exception exception) {
			onSessionStateChange(session, state, exception);
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		uiHelper = new UiLifecycleHelper(getActivity(), callback);
		uiHelper.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_users_online, container, false);
		
		listview = (ListView) view.findViewById(R.id.listUsersOnline);

//		// Find the user's profile picture custom view
//		profilePictureView = (ProfilePictureView) view.findViewById(R.id.selection_profile_pic);
//		profilePictureView.setCropped(true);
//
//		// Find the user's name view
//		userNameView = (TextView) view.findViewById(R.id.selection_user_name);

		// Check for an open session
		Session session = Session.getActiveSession();
		if (session != null) {
			// Get the user's data
			makeMeRequest(session);
		}

		return view;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REAUTH_ACTIVITY_CODE) {
			uiHelper.onActivityResult(requestCode, resultCode, data);
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		uiHelper.onResume();
	}

	@Override
	public void onSaveInstanceState(Bundle bundle) {
		super.onSaveInstanceState(bundle);
		uiHelper.onSaveInstanceState(bundle);
	}

	@Override
	public void onPause() {
		super.onPause();
		uiHelper.onPause();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		uiHelper.onDestroy();
	}

	private void makeMeRequest(final Session session) {
		// Make an API call to get user data and define a
		// new callback to handle the response.
		Request request = Request.newMeRequest(session,
				new Request.GraphUserCallback() {
					@Override
					public void onCompleted(GraphUser user, Response response) {
						// If the response is successful
						if (session == Session.getActiveSession()) {
							if (user != null) {
//								// Set the id for the ProfilePictureView
//								// view that in turn displays the profile
//								// picture.
//								profilePictureView.setProfileId(user.getId());
//								// Set the Textview's text to the user's name.
//								userNameView.setText(user.getName());
								
								displayUsersOnlineList(user.getName());
							}
						}
						if (response.getError() != null) {
							// Handle errors, will do so later.
						}
					}

				});
		request.executeAsync();
	}

	private void onSessionStateChange(final Session session, SessionState state, Exception exception) {
		if (session != null && session.isOpened()) {
			// Get the user's data.
			makeMeRequest(session);
		}
	}
	
	private void displayUsersOnlineList(final String username) {
		//listview = (ListView) findViewById(R.id.listUsersOnline);
		usersInfomation = new ArrayList<UserInfo>();
		
		final CustomArrayAdapter adapter = new CustomArrayAdapter(getActivity(), usersInfomation);		 
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
                	
                	getActivity().runOnUiThread(new Runnable() {
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
                	
                	getActivity().runOnUiThread(new Runnable() {
	            	    public void run() {
	            	    	adapter.notifyDataSetChanged();
	            	    }
	            	});     
                	
                }
            }
        });
	}
}
