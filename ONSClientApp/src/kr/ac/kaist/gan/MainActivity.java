package kr.ac.kaist.gan;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.util.Log;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends Activity {
	private EditText edtTextAddress;
	private EditText edtTextPort;
	private Button btnConnect;
	private Button btnClear;
	private TextView txtResponse;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		edtTextAddress = (EditText) findViewById(R.id.address);
		edtTextPort = (EditText) findViewById(R.id.port);
		btnConnect = (Button) findViewById(R.id.connect);
		btnClear = (Button) findViewById(R.id.clear);
		txtResponse = (TextView) findViewById(R.id.response);
		
		btnConnect.setOnClickListener(buttonConnectOnClickListener);
		btnClear.setOnClickListener(new OnClickListener(){
			public void onClick(View v){
				txtResponse.setText("");
			}
		});
	}
	
	//connect button listener
	OnClickListener buttonConnectOnClickListener = new OnClickListener(){
		public void onClick(View arg0){
			NetworkTask myClientTask = new NetworkTask(edtTextAddress.getText().toString(),Integer.parseInt(edtTextPort.getText().toString()));
			myClientTask.execute();
		}
	};

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
	
	//AsyncTask for socket connection
	class NetworkTask extends AsyncTask<Void,Void,Void>{
		private String dstAddress;
		private int dstPort;
		private String response;
		
		NetworkTask(String addr, int port){
			dstAddress = addr;
			dstPort = port;
		}
		
		@Override
		protected Void doInBackground(Void... arg0){
			try{
				Socket socket = new Socket(dstAddress, dstPort);
				InputStream in = socket.getInputStream();
				ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(1024);
				byte[] buffer = new byte[1024];
				
				int bytesRead;
				while((bytesRead = in.read(buffer)) != -1){
					byteArrayOutputStream.write(buffer, 0, bytesRead);
				}
				
				socket.close();
				response = byteArrayOutputStream.toString("UTF-8");
			}catch(UnknownHostException e){
				e.printStackTrace();
			}catch(IOException e){
				e.printStackTrace();
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result){
			txtResponse.setText(response);
			super.onPostExecute(result);
		}
	}
}
