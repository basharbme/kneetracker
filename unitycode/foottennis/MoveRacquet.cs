using UnityEngine;
using System.Collections;

public class MoveRacquet : MonoBehaviour {
	public float speed = 30;
	public string axis = "Vertical";

	// Receiver object
	private BleReceiver bleReceiver;
	// Integer value received from Bluetooth
	private int bleVal = 0;
	// Rigidbody
	public Rigidbody2D rb2d;
	// Offset from centre
	float OFFSET_VAL = 21.0f;

	// Current position on the screen (low = -1, middle = 0, high = 1)
	int currentPos = 0;

	// For displaying ble data on screen
	private GUIStyle guiStyle;

	void Start() {
		rb2d = GetComponent<Rigidbody2D> ();
		bleReceiver = new BleReceiver ();
		bleReceiver.bindToService ();

		// Request accelerometer data
		StartCoroutine (askForAccelData ());
	}

	IEnumerator askForAccelData() {
		// Delay for 1 sec
		yield return new WaitForSeconds (1);
		// Tell bluefruit to send acccelerometer values
		bleReceiver.sendAccelerometerData ();
	}

	void FixedUpdate() {
		//************ POSITION METHOD *************
		int newData = bleReceiver.getData ();
		// If requested movement is up and we aren't at the top of the screen
		if (newData == 1 && currentPos != 1) {
			rb2d.position = rb2d.position + new Vector2 (0, OFFSET_VAL);
			currentPos++;
		// If requested movement is down and we aren't at the bottom of the screen
		} else if (newData == -1 && currentPos != -1) {
			rb2d.position = rb2d.position + new Vector2 (0, -OFFSET_VAL);
			currentPos--;
		}
	
		//************ FORCE METHOD ****************
//		float data = (float) bleReceiver.getData ();
//		// If force too low, don't shift
//		if (Mathf.Abs (data) < 4.0f)
//			data = 0.0f;
//		Rigidbody2D rb2d = GetComponent<Rigidbody2D> ();
//		rb2d.AddForce ((new Vector2 (0.0f, data)) * 0.01f, ForceMode2D.Impulse);
	}

	// Sends email data to user
	public void sendDataViaEmail() {
		bleReceiver.sendEmail ();
	}
	
//	void OnGUI() {
//		GUI.color = Color.black;
//		//guiStyle.fontSize = 20;
//		GUILayout.Label ("Data: " + bleReceiver.getData ());
//	}
}
