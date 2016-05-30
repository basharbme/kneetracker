using UnityEngine;
using System.Collections;
using UnityEngine.UI;

public class PediGenerate : MonoBehaviour {
	public GameObject rocks;
	public int score = 0;

	public Text bigScore;
	public Text currentScore;
	public Text highestScore;

	// Use this for initialization
	void Start () {
		InvokeRepeating ("CreateObstacle", 1f, 1.5f);
	}

//	void OnGUI() {
//		GUI.color = Color.black;
//		GUILayout.Label ("Score: " + score.ToString());
//	}

	void CreateObstacle() {
		Instantiate (rocks);
		score++;
	}	
	
	// Update is called once per frame
	void Update () {
		// Always display current score
		currentScore.text = "Current Score: " + score.ToString ();
		// Display big score if it is a multiple of 10
		if (score % 10 == 0 && score != 0) {
			// Display score for 3 seconds
			StartCoroutine("displayScore");
		}
		// Check if a new high score has been set
		int highScore = PlayerPrefs.GetInt ("High Score");
		if (score > highScore) {
			PlayerPrefs.SetInt("High Score", score);
		}
		// Display high score at all times
		highestScore.text = "Highest Score: " + highScore.ToString();
	}

	// Displays score for 3 seconds
	private IEnumerator displayScore() {
		bigScore.text = score.ToString() + " points!";
		yield return new WaitForSeconds (3);
		bigScore.text = "";
	}
}
