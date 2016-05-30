using UnityEngine;
using System.Collections;

public class RocketHome : MonoBehaviour {

	public void GoHome() {
		// Go back to the home screen
		Application.LoadLevel (0);
	}
}
