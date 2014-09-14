package com.voice.jarvis;

import java.util.List;

public interface CommandProcessor {
	
	public void openObject(List<String> mResult) throws  Exception;
	
	public void writeMessage(List<String> mResult) throws Exception;
	
	public void searchText(List<String> mResult) throws Exception;
	
	public void callPhoneNumber(List<String> mResult) throws Exception;
	
	public void playMusic(List<String> mResult) throws Exception;

}
