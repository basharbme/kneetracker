using UnityEngine;
using System;
using System.Collections;

public class BleReceiver
{

	AndroidJavaObject currentActivity;
	AndroidJavaObject androidPlugin;
	AndroidJavaClass javaUnityPlayer;
 
	public BleReceiver ()
	{

	}

	// Call this first
	public void bindToService ()
	{
		javaUnityPlayer = new AndroidJavaClass ("com.unity3d.player.UnityPlayer");
		currentActivity = javaUnityPlayer.GetStatic<AndroidJavaObject> ("currentActivity");
		androidPlugin = new AndroidJavaObject ("com.henrywarhurst.bletest.BleReceiver", currentActivity);

		androidPlugin.Call ("bindToService");
	}
	
//	public string getData ()
//	{	
//		return androidPlugin.Call<string> ("getData");
//	}

	public int getData ()
	{	
		return androidPlugin.Call<int> ("getData");
	}

	// Sends an email to user with their data
	public void sendEmail () {
		androidPlugin.Call ("sendEmail");
	}

	public void sendForceData() {
		androidPlugin.Call ("sendForceData");
	}

	public void sendAccelerometerData() {
		androidPlugin.Call ("sendAccelerometerData");
	}

	public void disconnect() {
		androidPlugin.Call ("disconnect");
	}
}
