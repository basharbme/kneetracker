using UnityEngine;
using System.Collections;

public class PongHome : MonoBehaviour {

	public void GoHome() {
		// Return to home screen
		Application.LoadLevel (0);
	}
}
