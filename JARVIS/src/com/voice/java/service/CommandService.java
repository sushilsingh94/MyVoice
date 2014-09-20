package com.voice.java.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

public class CommandService extends Service{
	private static Context context;
	
	CommandService(){
		
	}
	CommandService(Context context){
		this.context = context;
	}
	
	private final IBinder mBinder = new MyBinder();

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return mBinder;
	}
	
	public class MyBinder extends Binder {
		CommandService getService() {
	      return CommandService.this;
	    }
	  }

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return super.onStartCommand(intent, flags, startId);
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		new CommandVoiceListner().startListening();
	}
}
