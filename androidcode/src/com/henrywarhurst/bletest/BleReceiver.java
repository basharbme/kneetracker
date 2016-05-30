package com.henrywarhurst.bletest;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

public class BleReceiver {
	private final static String TAG = "BleReceiver";
	
	public int DELETE_THIS_VARIABLE = 0;

	public static final String DEVICE_NAME = "Bluefruit LE Micro";
	// Adafruit 1 address
	//public static final String DEVICE_ADDRESS = "DE:39:A2:8E:55:D1";
	// Adafruit 2 address
	public static final String DEVICE_ADDRESS = "C8:CE:09:8C:30:45";

	private static final int UART_PROFILE_CONNECTED = 20;
	private static final int UART_PROFILE_DISCONNECTED = 21;

	private Context context;

	private BluetoothLeService mBluetoothLeService;
	
	// Expected accelerometer data by default
	private boolean isReceivingForceData = false;
	
	// The raw data from the micro controller
	private int receivedVal = -1;
	
//	// For cleaning up the accelerometer data
//	private Filter filter;
	
	// For cleaning up the accelerometer data
	private AdaptiveFilter filter2;
	
	// History of user data from the current session
	private ArrayList<Integer> historyArray;

	public BleReceiver(Context context) {
		this.context = context;
		filter2 = new AdaptiveFilter(context);
		historyArray = new ArrayList<Integer>();
	}

	// Code to manage Service lifecycle.
	private final ServiceConnection mServiceConnection = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName componentName,
				IBinder service) {
			mBluetoothLeService = ((BluetoothLeService.LocalBinder) service)
					.getService();
			if (!mBluetoothLeService.initialize()) {
				Log.e(TAG, "Unable to initialize Bluetooth");
			}
			// Automatically connects to the device upon successful start-up
			// initialization.
			mBluetoothLeService.connect(DEVICE_ADDRESS);
		}

		@Override
		public void onServiceDisconnected(ComponentName componentName) {
			mBluetoothLeService = null;
		}
	};

	private final BroadcastReceiver UARTStatusChangeReceiver = new BroadcastReceiver() {

		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			final Intent mIntent = intent;
			// *********************//
			if (action.equals(BluetoothLeService.ACTION_GATT_CONNECTED)) {

			}

			// *********************//
			if (action.equals(BluetoothLeService.ACTION_GATT_DISCONNECTED)) {
				mBluetoothLeService.close();
			}

			// *********************//
			if (action.equals(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED)) {
				mBluetoothLeService.enableTXNotification();
			}
			// *********************//
			if (action.equals(BluetoothLeService.ACTION_DATA_AVAILABLE)) {	
              final byte[] txValue = intent.getByteArrayExtra(BluetoothLeService.EXTRA_DATA);
                      try {
                      	Log.d(TAG, "Inside data available");
                      	String text = new String(txValue, "UTF-8");
                      	// If we are receiving accelerometer data, filter it
                      	if (!isReceivingForceData) {
                      		//receivedVal = (int) filter2.process((double) receivedVal);
                      		if (text.equals("Forward")) {
                      			receivedVal = 1;
                      		} else if (text.equals("Backwards")) {
                      			receivedVal = -1;
                      		}
                      	} else {
                      		// This is force data
                          	receivedVal = Integer.parseInt(text);
                      	}
                      	// Keep a record of data
                      	historyArray.add(receivedVal);
                      } catch (Exception e) {
                          Log.e(TAG, e.toString());
              }
			}
			// *********************//
			if (action.equals(BluetoothLeService.DEVICE_DOES_NOT_SUPPORT_UART)) {
				mBluetoothLeService.disconnect();
			}
		}
	};

	public void bindToService() {
		Intent bindIntent = new Intent(context, BluetoothLeService.class);
		context.bindService(bindIntent, mServiceConnection,
				Context.BIND_AUTO_CREATE);
      context.registerReceiver(UARTStatusChangeReceiver, makeGattUpdateIntentFilter());
	}
	
  private static IntentFilter makeGattUpdateIntentFilter() {
      final IntentFilter intentFilter = new IntentFilter();
      intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
      intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
      intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
      intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
      intentFilter.addAction(BluetoothLeService.DEVICE_DOES_NOT_SUPPORT_UART);
      return intentFilter;
  }
  
  public int getData() {
	  if (isReceivingForceData) {
  		return receivedVal;
	  } else {
		  int returnVal = receivedVal;
		  // The movement has been made, 0 represents no movement next time
		  receivedVal = 0;
		  return returnVal;
	  }
  }
  
  public void sendForceData() {
  	String message = "f";
  	byte [] forceCommand;
  	try {
			forceCommand = message.getBytes("UTF-8");
			mBluetoothLeService.writeRXCharacteristic(forceCommand);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
  	isReceivingForceData = true;
  }
  
  public void sendAccelerometerData() {
  	String message = "a";
  	byte [] accelCommand;
  	try {
			accelCommand = message.getBytes("UTF-8");
			mBluetoothLeService.writeRXCharacteristic(accelCommand);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
  	isReceivingForceData = false;
  }
  
  public void sendEmail() {
  	// Create temporary text file containing data
  	File outputDir = context.getExternalCacheDir();
  	File outputFile = null;
  	try {
  		outputFile = File.createTempFile("data", ".txt", outputDir);
			if (outputFile.exists()) {
				OutputStream fo = new FileOutputStream(outputFile);
				OutputStreamWriter osw = new OutputStreamWriter(fo);
				// Get data from string
				String csvData = android.text.TextUtils.join(",", historyArray);
				// Clear data storage arraylist
				historyArray.clear();
				osw.write(csvData);
				osw.flush();
				osw.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
  	
  	Uri path = Uri.fromFile(outputFile);
  	
  	Intent emailIntent = new Intent(Intent.ACTION_SEND);
  	emailIntent.putExtra(Intent.EXTRA_EMAIL, "henrywarhurst@gmail.com");
  	emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Patient Data");
  	emailIntent.putExtra(Intent.EXTRA_TEXT, 
  			"Dear User,\n\nHere is your KneeTracker data.\n\nKind Regards,\nKneeTracker Team");
  	emailIntent.putExtra(Intent.EXTRA_STREAM, path);
  	// RFC822 is an email format standard
  	emailIntent.setType("message/rfc822");
  	context.startActivity(Intent.createChooser(emailIntent, "Choose email client..."));	
  }
  
  public void disconnect() {
  	mBluetoothLeService.disconnect();
  }
}
