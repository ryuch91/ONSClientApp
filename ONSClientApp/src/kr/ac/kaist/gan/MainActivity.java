package kr.ac.kaist.gan;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.util.Log;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import kr.ac.kaist.gan.SimpleSocketThread.MessageTypeClass;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		final EditText ed1 = (EditText) findViewById(R.id.editText1);
		Button buttonSend = (Button) findViewById(R.id.button1);
		
		Handler mHandler = new Handler(Looper.getMainLooper()){
			@Override
			public void handleMessage(Message inputMessage){
				switch(inputMessage.what){
				case MessageTypeClass.SIMSOCK_DATA :
					String msg = (String) inputMessage.obj;
					Log.d("OUT",  msg);
					// do something with UI
					break;
				case MessageTypeClass.SIMSOCK_CONNECTED:
					// do something with UI
					break;
				case MessageTypeClass.SIMSOCK_DISCONNECTED:
					// do something with UI
					break;
				}
			}
		};
		
		final SimpleSocketThread ssocket = new SimpleSocketThread("143.248.55.143", 7777, mHandler);
		ssocket.start();
		
		buttonSend.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v){
				// TODO Auto-generated method stub
				ssocket.sendString(ed1.getText());
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
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

	private void AlertDialog(){
		AlertDialog.Builder ad = new AlertDialog.Builder(this);
		ad.setMessage("Connection is failed").setPositiveButton("OK", new DialogInterface.OnClickListener(){ public void onClick(DialogInterface dialog, int id){dialog.cancel();}
		});
		AlertDialog alert = ad.create();
		alert.setTitle("Warning");
		alert.show();
	}
	
}
