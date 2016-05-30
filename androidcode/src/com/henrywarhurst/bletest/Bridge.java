package com.henrywarhurst.bletest;

import android.os.Environment;
import android.os.BatteryManager;
import android.util.Log;
import android.widget.Toast;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Context;

public class Bridge {
	// Needed to get the battery level.
	private Context context;
	private Activity activity;

	private BluetoothAdapter mBluetoothAdapter;
	private BroadcastReceiver mReceiver;
	private final static int REQUEST_ENABLE_BT = 1;

	@SuppressLint("NewApi")
	public Bridge(Context context) {
		this.context = context;
		this.activity = (Activity) context;
	}

	// Return the battery level as a float between 0 and 1 (1 being fully
	// charged, 0 fulled discharged)
	public float GetBatteryPct() {

		Intent batteryStatus = GetBatteryStatusIntent();

		int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
		int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

		float batteryPct = level / (float) scale;
		return batteryPct;
	}

	// Return whether or not we're currently on charge
	public boolean IsBatteryCharging() {
		Intent batteryStatus = GetBatteryStatusIntent();

		int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
		return status == BatteryManager.BATTERY_STATUS_CHARGING
				|| status == BatteryManager.BATTERY_STATUS_FULL;
	}

	@SuppressLint({ "InlinedApi", "NewApi" }) public String GetBleDeviceName() {
		// Initialises bluetooth manager
		final BluetoothManager bluetoothManager =
		        (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
		mBluetoothAdapter = bluetoothManager.getAdapter();
    	final String [] returnStr = new String[1];
    	mBluetoothAdapter.startDiscovery(); 
    	mReceiver = new BroadcastReceiver() {
    	public void onReceive(Context context, Intent intent) {
    	    String action = intent.getAction();

    	    //Finding devices                 
    	    if (BluetoothDevice.ACTION_FOUND.equals(action)) 
    	    {
    	        // Get the BluetoothDevice object from the Intent
    	        BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
    	        // Add the name and address to an array adapter to show in a ListView
    	       //mArrayAdapter.add(device.getName() + "\n" + device.getAddress());
    	       Log.w("BRIDGE", "Device named: " + device.getAddress());
    	       returnStr[0] = device.getAddress();
    	    }
    	  }
    	};
    	
    	IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND); 
    	context.registerReceiver(mReceiver, filter);
    	
    	return returnStr[0];
    }

	private Intent GetBatteryStatusIntent() {
		IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
		return context.registerReceiver(null, ifilter);
	}
}