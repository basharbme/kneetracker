package com.henrywarhurst.bletest;

//**************DELETE*********************************

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;

@SuppressLint("NewApi")
public class ToastExample {

	private Context context;
	private static ToastExample instance;
	private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;
	private BluetoothAdapter mBluetoothAdapter;
	private BroadcastReceiver mReceiver;

	public ToastExample() {
		this.instance = this;
	}

	public static ToastExample instance() {
		if (instance == null) {
			instance = new ToastExample();
		}
		return instance;
	}

	public void setContext(Context context) {
		this.context = context;
	}

	@SuppressLint("NewApi")
	public void showMessage(String message) {
		// Toast.makeText(this.context, message, Toast.LENGTH_SHORT).show();
		
//		// Here, thisActivity is the current activity
//		if (ContextCompat.checkSelfPermission((Activity) context,
//		                Manifest.permission.ACCESS_COARSE_LOCATION)
//		        != PackageManager.PERMISSION_GRANTED) {
//
//		    // Should we show an explanation?
//		    if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context,
//		            Manifest.permission.ACCESS_COARSE_LOCATION)) {
//
//		        // Show an expanation to the user *asynchronously* -- don't block
//		        // this thread waiting for the user's response! After the user
//		        // sees the explanation, try again to request the permission.
//
//		    } else {
//
//		        // No explanation needed, we can request the permission.
//
//		        ActivityCompat.requestPermissions((Activity) context,
//		                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
//		                PERMISSION_REQUEST_COARSE_LOCATION);
//
//		        // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
//		        // app-defined int constant. The callback method gets the
//		        // result of the request.
//		    }
//		}

//		if (((Activity) context)
//				.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//			final AlertDialog.Builder builder = new AlertDialog.Builder(context);
//
//			builder.setTitle("This app needs location access");
//			builder.setMessage("Please grant location access");
//			builder.setPositiveButton(android.R.string.ok, null);
//
//			builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
//				@SuppressLint("NewApi")
//				@Override
//				public void onDismiss(DialogInterface dialog) {
//					((Activity) context)
//							.requestPermissions(
//									new String[] { Manifest.permission.ACCESS_COARSE_LOCATION },
//									PERMISSION_REQUEST_COARSE_LOCATION);
//				}
//			});
//			builder.show();
//		}
	}
	
//	public void scanDevices() {
//		// Initialises bluetooth manager
//		final BluetoothManager bluetoothManager = (BluetoothManager) context
//				.getSystemService(Context.BLUETOOTH_SERVICE);
//		mBluetoothAdapter = bluetoothManager.getAdapter();
//		mBluetoothAdapter.startDiscovery();
//		mReceiver = new BroadcastReceiver() {
//			public void onReceive(Context context, Intent intent) {
//				String action = intent.getAction();
//
//				// Finding devices
//				if (BluetoothDevice.ACTION_FOUND.equals(action)) {
//					// Get the BluetoothDevice object from the Intent
//					BluetoothDevice device = intent
//							.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
//					// Add the name and address to an array adapter to show in a
//					// ListView
//					// mArrayAdapter.add(device.getName() + "\n" +
//					// device.getAddress());
//					Log.w("ToastExample", "Device named: " + device.getName());
//				}
//			}
//		};
//
//		IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
//		context.registerReceiver(mReceiver, filter);
//		Log.w("ToastExample", "Here");
//	}
}

// ********************************************************************
