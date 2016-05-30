using UnityEngine;
using System.Collections;

public class PediPlayer : MonoBehaviour {
	public Vector2 jumpForce = new Vector2(0, 300);
	private Rigidbody2D rb2d;

	// Receiver object
	private BleReceiver bleReceiver;
	// Integer value received from Bluetooth
	private int bleVal = 0;

	// Current position on the screen (low = -1, middle = 0, high = 1)
	int currentPos = 0;

	// Offset from centre
	float OFFSET_VAL = 2.5f;

	// Use this for initialization
	void Start () {
		// Keep screen horizontal
		Screen.orientation = ScreenOrientation.LandscapeLeft;
		rb2d = gameObject.GetComponent<Rigidbody2D> ();

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
	
	// Update is called once per frame
	void Update () {

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

		//************ OLD METHOD ******************
//		//rb2d.position = new Vector2 (rb2d.position.x, (float) bleReceiver.getData ()/50.0f);
//		float data = (float)bleReceiver.getData ();
//		// If force too low, don't shift
//		if (Mathf.Abs (data) < 8.0f)
//			data = 0.0f;
//		rb2d.AddForce ((new Vector2 (0.0f, data)) * 0.01f, ForceMode2D.Impulse);
//
//		// Die by being off screen
//		Vector2 screenPosition = Camera.main.WorldToScreenPoint (transform.position);
//		if (screenPosition.y > Screen.height || screenPosition.y < 0) {
//			Die ();
//		}
	}

	// Die by running into an object
	void OnCollisionEnter2D(Collision2D other) {
		Die ();
	}

	// Sends email data to user
	public void sendDataViaEmail() {
		bleReceiver.sendEmail ();
	}

	void Die() {
		Application.LoadLevel (3);
	}
}
