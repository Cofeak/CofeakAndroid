package cofeak.cofeakapp;

import java.io.File;

import android.support.v4.app.NotificationCompat;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.util.Log;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;

public class MainActivity extends Activity implements OnClickListener {
	
	private Button scanBtn, authBtn;
	private TextView formatTxt, contentTxt, name_tv;
	private EditText username_et, pwd_et;
	private String username;
	public static Context context;
	NotificationManager NM;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		context = getApplicationContext();
		
		String cred = Util.getCredentials();
		Log.v("cred", cred);
		
		if ( cred.length() > 1 )
		{
			String[] tokens = cred.split(";");
			username = tokens[0];
			initMainView();
			
		}
		else
			initAuthView();
	}
	
	public void initMainView()
	{
		setContentView(R.layout.activity_main);
		
		scanBtn = (Button)findViewById(R.id.scan_button);
		formatTxt = (TextView)findViewById(R.id.scan_format);
		contentTxt = (TextView)findViewById(R.id.scan_content);
		name_tv = (TextView)findViewById(R.id.name_tv);
		
		scanBtn.setOnClickListener(this);
		name_tv.setText(username);
	}
	
	public void initAuthView()
	{
		setContentView(R.layout.authentification);
		
		username_et = (EditText)findViewById(R.id.username_et);
		pwd_et = (EditText)findViewById(R.id.pwd_et);
		authBtn = (Button)findViewById(R.id.ok_auth_btn);
		
		authBtn.setOnClickListener(this);
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
			Util.flushCredentials();
			//Util.getCredentials();
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
		//let's scan
		if(v.getId() == R.id.scan_button){
			IntentIntegrator scanIntegrator = new IntentIntegrator(this);
			scanIntegrator.initiateScan();
			
			notification(username);
		}
		
		//authentification
		if( v.getId() == R.id.ok_auth_btn )
		{
			Toast.makeText(getApplicationContext(), "Authentification réussie", 
					   Toast.LENGTH_LONG).show();
			
			Editable usernameEdit = username_et.getText();
			Editable pwdEdit = pwd_et.getText();
			Util.setCredentials(usernameEdit.toString(), pwdEdit.toString());
			username = usernameEdit.toString();

			initMainView();
			
		}
	}
	
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		//retrieve scan result
		IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
		if (scanningResult != null) {
			//we have a result
			String scanContent = scanningResult.getContents();
			String scanFormat = scanningResult.getFormatName();
			formatTxt.setText("FORMAT: " + scanFormat);
			contentTxt.setText("CONTENT: " + scanContent);
			
			boolean isUrl = URLUtil.isValidUrl(scanContent);
			//and a correct one
			if( isUrl ) {
				Intent i = new Intent(Intent.ACTION_VIEW);
				i.setData(Uri.parse(scanContent));
				startActivity(i);
			}
			else {
				Util.alertDialog(this, "Le QR code scanné est incorrect", "Désolé");
			}
		}
		else{
		    Toast toast = Toast.makeText(getApplicationContext(), 
		        "No scan data received!", Toast.LENGTH_SHORT);
		    toast.show();
		}
	}
	
	public void notification(String username)
	{
		NotificationCompat.Builder mBuilder =
		        new NotificationCompat.Builder(this)
		        .setSmallIcon(R.drawable.ic_launcher)
		        .setContentTitle("Offre BNPP")
		        .setContentText(username + " , profitez des offres BNPP");
		
			// Creates an explicit intent for an Activity in your app
			Intent resultIntent = new Intent(this, MainActivity.class);
	
			// The stack builder object will contain an artificial back stack for the
			// started Activity.
			// This ensures that navigating backward from the Activity leads out of
			// your application to the Home screen.
			TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
			// Adds the back stack for the Intent (but not the Intent itself)
			stackBuilder.addParentStack(MainActivity.class);
			// Adds the Intent that starts the Activity to the top of the stack
			stackBuilder.addNextIntent(resultIntent);
			PendingIntent resultPendingIntent =
			        stackBuilder.getPendingIntent(
			            0,
			            PendingIntent.FLAG_UPDATE_CURRENT
			        );
			mBuilder.setContentIntent(resultPendingIntent);
			NotificationManager mNotificationManager =
			    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
			// mId allows you to update the notification later on.
			mNotificationManager.notify(0, mBuilder.build());
			
	   }
	
	
}
