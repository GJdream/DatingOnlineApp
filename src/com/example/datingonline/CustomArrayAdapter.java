package com.example.datingonline;

import java.util.List;

import Model.UserInfo;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class CustomArrayAdapter extends ArrayAdapter<UserInfo> {
	private final Context context;
	//private final UserInfo[] usersInfomatioin;
	private List<UserInfo> usersInformation;

	public CustomArrayAdapter(Context context, List<UserInfo> usersInformation) {
		super(context, R.layout.user_row, usersInformation);
		// TODO Auto-generated constructor stub
		this.context = context;
		this.usersInformation = usersInformation;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
	    LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    View rowView = inflater.inflate(R.layout.user_row, parent, false);
	    TextView txtUsername = (TextView) rowView.findViewById(R.id.username);
	    
	    txtUsername.setText(usersInformation.get(position).username);
	    

	    return rowView;
	}

}
