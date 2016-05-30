using UnityEngine;
using System.Collections;

public class CopterHome : MonoBehaviour {

	public void GoHome() {
		// Go back to home screen
		Application.LoadLevel (0);
	}
}
