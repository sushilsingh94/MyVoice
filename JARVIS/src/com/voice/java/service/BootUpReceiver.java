package com.voice.java.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootUpReceiver extends BroadcastReceiver{

	@Override
	public void onReceive(Context context, Intent intent) {
		new CommandService(context);
		Intent service = new Intent(context, CommandService.class);
	    context.startService(service);
	}

}
