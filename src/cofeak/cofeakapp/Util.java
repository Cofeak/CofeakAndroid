package cofeak.cofeakapp;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;
import android.widget.Toast;

public class Util 
{
	public static String authFileName = "auth.txt";
	
	public static void alertDialog(Activity activity, String msg, String title)
	{
		// 1. Instantiate an AlertDialog.Builder with its constructor
		AlertDialog.Builder builder = new AlertDialog.Builder(activity);

		// 2. Chain together various setter methods to set the dialog characteristics
		builder.setMessage(msg)
		       .setTitle(title);

		// 3. Get the AlertDialog from create()
		AlertDialog dialog = builder.create();
		
		dialog.show();
	}
	
	public static String getCredentials() 
	{
		FileInputStream fis = null;
		
		StringBuffer fileContent = null;
		try {
			fis = MainActivity.context.openFileInput(authFileName);
			fileContent = new StringBuffer("");
			byte[] buffer = new byte[1024];
			int n;
			while ((n = fis.read(buffer)) != -1) 
			{ 
				fileContent.append(new String(buffer, 0, n)); 
			}
			
			fis.close();
		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		if(fileContent != null){
			//Log.d("file",fileContent.toString() );
			return fileContent.toString();
		}
		else
			return "";
	}
	
	public static void setCredentials(String username, String code)
	{
		FileOutputStream outputStream;

		try {
		  outputStream = MainActivity.context.openFileOutput(authFileName, Context.MODE_PRIVATE);
		  outputStream.write(username.getBytes());
		  outputStream.write(';');
		  outputStream.write(code.getBytes());
		  outputStream.close();
		  
		} catch (Exception e) {
		  e.printStackTrace();
		}
	}
	
	public static void flushCredentials()
	{
		File file = new File(authFileName);
		MainActivity.context.deleteFile(authFileName);
	}

}
