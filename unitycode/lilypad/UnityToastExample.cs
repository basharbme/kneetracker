using UnityEngine;
using System.Collections;

public class UnityToastExample : MonoBehaviour {

//	public static void TstFunc() {
//		AndroidJavaObject toastExample = null;
//		AndroidJavaObject activityContext = null;
//
//		if(toastExample == null) {
//			using(AndroidJavaClass activityClass = new AndroidJavaClass("com.unity3d.player.UnityPlayer")) {
//				activityContext = activityClass.GetStatic<AndroidJavaObject>("currentActivity");
//			}
//			
//			using(AndroidJavaClass pluginClass = new AndroidJavaClass("com.henrywarhurst.bletest.ToastExample")) {
//				if(pluginClass != null) {
//					toastExample = pluginClass.CallStatic<AndroidJavaObject>("instance");
//					toastExample.Call("setContext", activityContext);
//					activityContext.Call("runOnUiThread", new AndroidJavaRunnable(() => {
//						toastExample.Call("showMessage", "This is a Toast message");
//					}));
//				}
//			}
//		}
//	}
//
//	public static void ScanDevices() {
//		AndroidJavaObject toastExample = null;
//		AndroidJavaObject activityContext = null;
//		
//		if(toastExample == null) {
//			using(AndroidJavaClass activityClass = new AndroidJavaClass("com.unity3d.player.UnityPlayer")) {
//				activityContext = activityClass.GetStatic<AndroidJavaObject>("currentActivity");
//			}
//			
//			using(AndroidJavaClass pluginClass = new AndroidJavaClass("com.henrywarhurst.bletest.ToastExample")) {
//				if(pluginClass != null) {
//					toastExample = pluginClass.CallStatic<AndroidJavaObject>("instance");
//					toastExample.Call("setContext", activityContext);
//						toastExample.Call("scanDevices");
//				}
//			}
//		}
//	}
}
