package com.voice.jarvis;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;

import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.v7.app.ActionBarActivity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ToggleButton;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class JARVISActivity extends ActionBarActivity implements RecognitionListener{

	private TextView returnedText;
	private TextView displayText;
    private ToggleButton toggleButton;
    private ProgressBar progressBar;
    private SpeechRecognizer speech = null;
    private Intent recognizerIntent;
    private String LOG_TAG = "VoiceRecognitionActivity";
    private boolean mResultAvailable;
    private List<String> mResults;
    private CommandProcessorImpl commandProcessorImpl;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_jarvis);
		
		returnedText = (TextView) findViewById(R.id.txtSpeechInput);
		displayText = (TextView) findViewById(R.id.textDisplay);
        progressBar = (ProgressBar) findViewById(R.id.progressBar1);
        toggleButton = (ToggleButton) findViewById(R.id.toggleButton1);
        
        startListening();
 
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.jarvi, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public void startListening(){
		if (SpeechRecognizer.isRecognitionAvailable(this)) {
			if (speech!=null){
				speech.startListening(recognizerIntent);
				mResultAvailable = false;
				mResults = new ArrayList<String>();
			}
			else
				startSR();
		}
	}
	    
	public void startSR(){
		 progressBar.setVisibility(View.INVISIBLE);
	        speech = SpeechRecognizer.createSpeechRecognizer(this);
	        speech.setRecognitionListener(this);
	        recognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
	        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE,
	                "en");
	        recognizerIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,
	                this.getPackageName());
	        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
	                RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH);
	        recognizerIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3);
	        speech.startListening(recognizerIntent);
	}
	
	@Override
    public void onResume() {
        super.onResume();
    }
	
	@Override
    protected void onPause() {
        super.onPause();
        if (speech != null) {
            speech.destroy();
            Log.i(LOG_TAG, "destroy");
        }
 
    }
	
	@Override
	public void onBeginningOfSpeech() {
		Log.i(LOG_TAG, "onBeginningOfSpeech");
        progressBar.setIndeterminate(false);
        progressBar.setMax(10);
	}

	@Override
	public void onBufferReceived(byte[] buffer) {
		Log.i(LOG_TAG, "onBufferReceived: " + buffer);
	}

	@Override
	public void onEndOfSpeech() {
		Log.i(LOG_TAG, "onEndOfSpeech");
        progressBar.setIndeterminate(true);
        toggleButton.setChecked(false);
	}

	@Override
	public void onError(int errorCode) {
		String errorMessage = getErrorText(errorCode);
        Log.d(LOG_TAG, "FAILED " + errorMessage);
        returnedText.setText(errorMessage);
        toggleButton.setChecked(false);
	}

	@Override
	public void onEvent(int arg0, Bundle arg1) {
		Log.i(LOG_TAG, "onEvent");
	}

	@Override
	public void onPartialResults(Bundle arg0) {
		Log.i(LOG_TAG, "onPartialResults");
	}

	@Override
	public void onReadyForSpeech(Bundle arg0) {
		Log.i(LOG_TAG, "onReadyForSpeech");
	}

	@Override
	public void onResults(Bundle results) {
        mResultAvailable = true;
        Log.d(LOG_TAG, "onResults " + results);

        mResults = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        commandProcessorImpl = new CommandProcessorImpl();
        returnedText.setText(mResults.iterator().next());
        try {
			commandProcessorImpl.filterUserInputText(mResults);
		}catch (Exception e) {
			Log.d(LOG_TAG, "error occured onResult : " + e);
		}
        returnedText.setText(mResults.iterator().next());
        startListening();
	}

	@Override
	public void onRmsChanged(float rmsdB) {
		Log.i(LOG_TAG, "onRmsChanged: " + rmsdB);
        progressBar.setProgress((int) rmsdB);
	}
	
	public String getErrorText(int errorCode) {
        String message;
        switch (errorCode) {
        case SpeechRecognizer.ERROR_AUDIO:
            message = "Audio recording error";
            break;
        case SpeechRecognizer.ERROR_CLIENT:
            message = "Client side error";
            startListening();
            break;
        case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
            message = "Insufficient permissions";
            break;
        case SpeechRecognizer.ERROR_NETWORK:
            message = "Network error";
            startListening();
            break;
        case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
            message = "Network timeout";
            startListening();
            break;
        case SpeechRecognizer.ERROR_NO_MATCH:
            message = "No match";
            speech.startListening(recognizerIntent);
            break;
        case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
            message = "RecognitionService busy";
            startListening();
            break;
        case SpeechRecognizer.ERROR_SERVER:
            message = "error from server";
            startListening();
            break;
        case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
            message = "No speech input";
            speech.startListening(recognizerIntent);
            break;
        default:
            message = "Didn't understand, please try again.";
            startListening();
            break;
        }
        return message;
    }
	
	public class CommandProcessorImpl implements CommandProcessor {

		@Override
		public void openObject(List<String> mResult) throws Exception {
			// TODO Auto-generated method stub
		}

		@Override
		public void writeMessage(List<String> mResult) throws Exception {
			// TODO Auto-generated method stub

		}

		@Override
		public void searchText(List<String> mResult) throws Exception {
			// TODO Auto-generated method stub

		}

		@Override
		public void callPhoneNumber(List<String> mResult) throws Exception {
			String phoneNumber ="";
			phoneNumber = getPhoneNumber(mResult.get(1).toString(), getApplicationContext());
			if(!phoneNumber.equalsIgnoreCase("Unsaved")){
				Intent intent = new Intent(Intent.ACTION_CALL);
				intent.setData(Uri.parse("tel:"+phoneNumber));
				//startActivity(getPackageManager().getLaunchIntentForPackage("com.skype.android"));
				startActivity(intent);
			}
			
		}

		@Override
		public void playMusic(List<String> mResult) throws Exception {
			playMusic();
		}
		
		public String getPhoneNumber(String name, Context context) {
		    String ret = null;
		    String selection = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME
		            + " like'%" + name + "%'";
		    String[] projection = new String[] { ContactsContract.CommonDataKinds.Phone.NUMBER };
		    Cursor c = context.getContentResolver().query(
		            ContactsContract.CommonDataKinds.Phone.CONTENT_URI, projection,
		            selection, null, null);
		    if (c.moveToFirst()) {
		        ret = c.getString(0);
		    }
		    c.close();
		    if (ret == null)
		        ret = "Unsaved";
		    return ret;
		}
		
		public void filterUserInputText(List<String> mResult) throws Exception{
			StringTokenizer stringTokenizer = new StringTokenizer(mResult.get(0));
			List<String> mResultList = new ArrayList<String>();
			
			while (stringTokenizer.hasMoreTokens()) {
	            mResultList.add(stringTokenizer.nextToken());
	        }
			
			if(mResultList.contains("open")){
				openObject(mResultList);
			}if(mResultList.contains("write")){
				writeMessage(mResultList);
			}if(mResultList.contains("search")){
				searchText(mResultList);
			}if(mResultList.contains("call")){
				callPhoneNumber(mResultList);
			}if(mResultList.contains("play music")){
				playMusic(mResultList);
			}
			
		}
		
		// Play Music
        protected void playMusic(){
            // Read Mp3 file present under SD card
        	MediaPlayer mPlayer;
            Uri myUri1 = Uri.parse("file:///sdcard/jai_ho.mp3");
            mPlayer  = new MediaPlayer();
            mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            try {
                mPlayer.setDataSource(getApplicationContext(), myUri1);
                mPlayer.prepare();
                // Start playing the Music file
                mPlayer.start();
                    
            } catch (Exception e) {
                
            }
        }

	}
}
