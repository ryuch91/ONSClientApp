package kr.ac.kaist.gan;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
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
import android.widget.Toast;

public class MainActivity extends Activity {
	private EditText edtTextAddress;
	private EditText edtTextPort;
	private Button btnConnect;
	private Button btnClear;
	private EditText context;
	private TextView txtResponse;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		edtTextAddress = (EditText) findViewById(R.id.address);
		edtTextPort = (EditText) findViewById(R.id.port);
		btnConnect = (Button) findViewById(R.id.connect);
		btnClear = (Button) findViewById(R.id.clear);
		context = (EditText) findViewById(R.id.editText1);
		txtResponse = (TextView) findViewById(R.id.response);
		
		btnConnect.setOnClickListener(buttonConnectOnClickListener);
		btnClear.setOnClickListener(new OnClickListener(){
			public void onClick(View v){
				txtResponse.setText("Content is cleared");
			}
		});
	}
	
	//connect button listener
	OnClickListener buttonConnectOnClickListener = new OnClickListener(){
		public void onClick(View arg0){
			try{
				NetworkTask myClientTask = new NetworkTask(edtTextAddress.getText().toString(),Integer.parseInt(edtTextPort.getText().toString()));
				myClientTask.execute();
			}catch(Exception e){
				e.printStackTrace();
				Toast.makeText(getApplicationContext(), "Not enough information", Toast.LENGTH_SHORT).show();
			}
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
	class NetworkTask extends AsyncTask<Void,Void,String>{
		private String dstAddress;
		private int dstPort;
		private String response;
		
		NetworkTask(String addr, int port){
			dstAddress = addr;
			dstPort = port;
		}
		
		@Override
		protected String doInBackground(Void... arg0){
			try{
				Socket socket = new Socket(dstAddress, dstPort);
				InputStream in = socket.getInputStream();
				OutputStream out = socket.getOutputStream();
				PrintWriter pout = new PrintWriter(out, true);
				
				pout.println("Hello from client");
				
				BufferedReader br = new BufferedReader(new InputStreamReader(in));
				String readResult = br.readLine();

				socket.close();
				if(readResult!=null){
					response = readResult;
				}else{
					response = "";
				}
			}catch(UnknownHostException e){
				e.printStackTrace();
			}catch(IOException e){
				e.printStackTrace();
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(String result){
			txtResponse.setText(response);
			super.onPostExecute(result);
		}
	}
}
