using UnityEngine;
using System.Collections;
using System.Collections.Generic;
using UnityEngine.UI;

public class Player : MonoBehaviour
{
	public Rigidbody2D rb2d;
	public Vector2 jumpForce;

	// Is a jump in progress?
	private bool jumpInProgress;

	// Starting point of a jump
	Vector2 startPoint;

	// Was the previous ble value low or high
	private bool isPreviousHigh = true;

	// How many jumps have we done?
	private int jumpNum;

	// Locations of lilypads
	Dictionary<int, Vector2> padLocations;
	//AndroidJavaClass jc;

	// Integer value received from Bluetooth
	private int bleVal = 0;

	// The value that constitutes a 'lifted leg'
	private int THRESHOLD = 30;

	// Used for jumping interpolation
	float t = 0.0f;

	private BleReceiver bleReceiver;

	// For displaying encouragements
	public Text encouragement;

	// List of different encouragements
	private string [] encouragementList = new string[]{"NICE!", "GREAT!", "AMAZING!", "IMPRESSIVE!", "SWEET!", "WOW!"};
	
	// Encouragement index
	int encouragementIdx = 0;

	// For displaying ble data on screen
	private GUIStyle guiStyle;
	
	// Use this for initialization
	void Start ()
	{
		// Keep screen horizontal
		Screen.orientation = ScreenOrientation.LandscapeLeft;
		rb2d = GetComponent<Rigidbody2D> ();
		jumpForce = new Vector2 (100.0f, 100.0f);
		jumpInProgress = false;

		jumpNum = 0;

		guiStyle = new GUIStyle ();

		bleReceiver = new BleReceiver ();
		bleReceiver.bindToService ();
		// Ask for force data with a delay. If a delay isn't used,
		// the request is ignored.
		StartCoroutine (askForForceData());
	}

	// We must ask for force data because accelerometer data is sent by default
	IEnumerator askForForceData() {
		// Delay for 1 sec
		yield return new WaitForSeconds (1);
		// Tell the Adafruit to send force values.
		bleReceiver.sendForceData ();
	}
	
	// Update is called once per frame
	void Update ()
	{
		// Get lilypad positions
		GameObject g = GameObject.Find ("ExtraPads");
		Generate generator = g.GetComponent<Generate> ();
		padLocations = generator.pads;

//		// Get the bluetooth data
//		string strData = bleReceiver.getData ();
//		bleVal = int.Parse(strData);

		// Get the bluetooth data
		bleVal = bleReceiver.getData ();

		if (bleVal < THRESHOLD && !isPreviousHigh && !jumpInProgress) {
			// Initiate a jump
			jumpInProgress = true;
			jumpNum++;
			isPreviousHigh = true;
			if (jumpNum == 1) {
				// Position of the first lilypad
				startPoint = new Vector2(0.64f, 0.1f);
			} else {
				// Position of a lilypad that is not the first lilypad (first one is
				// hard coded in Unity and is not in the dictionary).
				startPoint = padLocations[jumpNum - 2];
			}
		} else if (bleVal > THRESHOLD) {
			isPreviousHigh = false;
		}

		// If jump button pressed and we aren't already jumping
//		if (Input.GetKeyUp ("space") && !jumpInProgress) {
//			// Initiate a jump
//			jumpInProgress = true;
//			jumpNum++;
//		}

		if (jumpNum > 4) {
			// Give encouragement
			StartCoroutine("displayEncouragement");
			// Restart game
			transform.position = new Vector2(0.45f, 0.1f);
			jumpNum = 0;
			jumpInProgress = false;
		}

		if (jumpInProgress) {
			Debug.Log (padLocations [jumpNum - 1]);
			transform.position = CalculateCubicBezierPoint(t, 
			                                               startPoint, (Vector2) startPoint + new Vector2(1.5f, 2.0f), 
			                                               (Vector2) startPoint + new Vector2(3.0f, 2.0f), 
			                                               padLocations[jumpNum-1]);
			// Update time
			t += 0.02f;
			if (Mathf.Abs(1.0f - t) < 0.001f) {
				jumpInProgress = false;
				t = 0.0f;
			}
		}

		//Debug.Log ("Battery level is " + BatteryLevelPlugin.GetBatteryLevel());
		//Debug.Log ("Bluetooth Device found named: " + BatteryLevelPlugin.GetBleDeviceName ());
		//UnityToastExample.ScanDevices ();

		// We get the text property of our receiver
//		javaMessage = jc.GetStatic<string> ("text");
//		Debug.Log ("RECEIVED MESSAGE is: " + javaMessage);

		// Get the data from the ble module
		//Debug.Log ("Data value is: " + blerec.getData ());
	}
	
	// For Bezier interpolation
	Vector2 CalculateCubicBezierPoint(float t, Vector2 p0, Vector2 p1, Vector2 p2, Vector2 p3) {
		float u = 1 - t;
		float tt = t * t;
		float uu = u * u;
		float uuu = uu * u;
		float ttt = tt * t;
		Vector2 p = uuu * p0;
		p += 3 * uu * t * p1;
		p += 3 * u * tt * p2;
		p += ttt * p3;
		return p;
	}

	// Sends email data to user
	public void sendDataViaEmail() {
		bleReceiver.sendEmail ();
	}

	// Displays encouragement for 3 seconds
	private IEnumerator displayEncouragement() {
		encouragement.text = encouragementList[encouragementIdx++];
		if (encouragementIdx == encouragementList.Length - 1)
			encouragementIdx = 0;
		yield return new WaitForSeconds (3);
		encouragement.text = "";
	}

//	void OnGUI() {
//		GUI.color = Color.black;
//		guiStyle.fontSize = 20;
//		GUILayout.Label ("Data: " + bleReceiver.getData ());
//	}
}
