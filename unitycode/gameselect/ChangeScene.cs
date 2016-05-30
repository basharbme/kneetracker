using UnityEngine;
using System.Collections;

public class ChangeScene : MonoBehaviour {

	public void Start () {
		// Orientation portrait
		Screen.orientation = ScreenOrientation.Portrait;
	}

	public void ChangeToScene(int sceneNum) {
		Application.LoadLevel (sceneNum);

	}
}
