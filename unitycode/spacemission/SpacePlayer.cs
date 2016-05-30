using UnityEngine;
using System.Collections;
using UnityEngine.UI;

public class SpacePlayer : MonoBehaviour {
	public Vector2 jumpForce = new Vector2(0, 300);
	public int maxHeight = 45;

	// For displaying encouragements
	public Text encouragement;

	private Rigidbody2D rb2d;

	private BleReceiver bleReceiver;
	private int bleVal;
	// History of last value, note that this value is inverted (300 - val).
	private int lastProcessedVal = 0;
	// Gradient thresholds
	private int GRADIENT_THRESH = 50;
	private int MAX_GRADIENT = 200;
	// Thrust coefficient
	float THRUST_CONSTANT = 0.1f;

	// List of different encouragements
	private string [] encouragementList = new string[]{"NICE!", "GREAT!", "AMAZING!", "IMPRESSIVE!", "SWEET!", "WOW!"};

	// Encouragement index
	int encouragementIdx = 0;

	// For use with encouragements, tracks if we have just come from high to low
	bool isHigh = false;

	// For displaying ble data on screen
	private GUIStyle guiStyle;

	private string dat;

	private float yCoordinate;

	// Use this for initialization
	void Start () {
		rb2d = gameObject.GetComponent<Rigidbody2D> ();

		bleReceiver = new BleReceiver ();
		bleReceiver.bindToService ();
		// Ask for force data with a delay. If a delay isn't used, the 
		// request is ignored.
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
	void Update () {
		// Get the bluetooth data
		bleVal = bleReceiver.getData ();
		// Cap weight to 300
		int capVal = 300;
		if (bleVal > capVal) bleVal = capVal;
		// ********* POSITIONAL METHOD **************************************
		// Transform the data into a position coordinate y = (-7/50)x + 40
		// assuming max weight is the value 300 and min weight is zero, 
		// max position is 40, min position is -2
		yCoordinate = (-7.0f / 50.0f) * bleVal + 40.0f;
		if (yCoordinate > 40.0f) {
			yCoordinate = 40.0f;
		}
		// Give encouragement
		if (yCoordinate > 25.0f) {
			encouragement.text = encouragementList[encouragementIdx];
			isHigh = true;
		} else {
			if (isHigh) {
				isHigh = false;
				if (++encouragementIdx == encouragementList.Length)
					encouragementIdx = 0;
			}
			encouragement.text = "";
		}
		transform.position = Vector2.Lerp (rb2d.position, new Vector2 (rb2d.position.x, yCoordinate), Time.deltaTime*5.0f);
		// Stop the rocket from shooting out of the top of the screen
//		if (rb2d.position.y > 42.0f) {
//			rb2d.position = new Vector2(-1.86f, 40.0f);
//		}
		// ********* END POSITIONAL METHOD **********************************


//		// ********* THRUST METHOD ******************************************
//		int inverseBleVal = capVal - bleVal;
//		// Check gradient
//		int gradient = inverseBleVal - lastProcessedVal;
//		// If small gradient / no data / rocket too high
//		if (gradient < GRADIENT_THRESH || bleVal == -1 || rb2d.position.y > 25) {
//			// apply no thrust
//			gradient = 0;
//		}
//		// Stop rocket from going off screen
//		if (rb2d.position.y > 42.0f) {
//			rb2d.position = new Vector2(-1.86f, 40.0f);
//		}
//		// Cap gradient if it is too extreme
//		if (gradient > MAX_GRADIENT)
//			gradient = MAX_GRADIENT;
//
//		Vector2 thrustForce = new Vector2 (0.0f, gradient*THRUST_CONSTANT);
//		rb2d.AddForce (thrustForce, ForceMode2D.Impulse);
//		lastProcessedVal = inverseBleVal;
//		// ********* END THRUST METHOD **************************************
	}

	// Sends email data to user
	public void sendDataViaEmail() {
		bleReceiver.sendEmail ();
	}


//	void OnGUI() {
//		GUI.color = Color.white;
//		GUILayout.Label ("Position: " + bleVal);
//	}
}
