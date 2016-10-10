package com.leontran.themobilevn.layouts;

import android.app.Dialog;
import android.content.Context;
import android.view.View;

import com.leontran.themobilevn.myinterface.EventDialog;


public class MyCustomDialogParent extends Dialog implements View.OnClickListener {

	public EventDialog myEvent = null;

	public MyCustomDialogParent(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	
	public MyCustomDialogParent(Context context, int themedialogcustom) {
		super(context, themedialogcustom);
		// TODO Auto-generated constructor stub
	}

	public void setEvendialog(EventDialog event) {
		myEvent = event;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
	}

}
