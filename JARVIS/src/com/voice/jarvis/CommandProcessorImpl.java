package com.voice.jarvis;

import java.util.List;

import android.content.pm.PackageManager;

public class CommandProcessorImpl implements CommandProcessor {

	@Override
	public void openObject(List<String> mResult) throws Exception {
		  //startActivity(getPackageManager().getLaunchIntentForPackage("com.skype.android"));
	}

	@Override
	public void writeMessage(List<String> mResult) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void searchText(List<String> mResult) throws Exception {
		// TODO Auto-generated method stub

	}
	
	public String filterUserInputText(List<String> mResult) throws Exception{
		
		if(mResult.contains("open")){
			openObject(mResult);
		}if(mResult.contains("write")){
			writeMessage(mResult);
		}if(mResult.contains("search")){
			searchText(mResult);
		}
		
		return "";
	}

	@Override
	public void callPhoneNumber(List<String> mResult) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void playMusic(List<String> mResult) throws Exception {
		// TODO Auto-generated method stub
		
	}

}
