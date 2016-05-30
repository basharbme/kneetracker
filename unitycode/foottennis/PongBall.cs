using UnityEngine;
using System.Collections;
using UnityEngine.UI;

public class PongBall : MonoBehaviour {
	public float speed = 10;

	private int score = 0;

	// Current score text box
	public Text currentScore;
	public Text highestScore;

	void Start () {
		// Keep orientation landscape
		Screen.orientation = ScreenOrientation.LandscapeLeft;
		// Initial velocity
		GetComponent<Rigidbody2D> ().velocity = (new Vector2(0.9f, 0.1f)) * speed;
	}

	void Update() {
		float xCoordinate = GetComponent<Rigidbody2D> ().position.x;
		if (xCoordinate < -40 || xCoordinate > 40) {
			// Game over, update ball position
			transform.position = new Vector2(0,0);
			// Initial velocity
			GetComponent<Rigidbody2D> ().velocity = (new Vector2(0.9f, 0.1f)) * speed;
			// Reset score
			score = 0;
		}

		// Always display current score
		currentScore.text = "CURRENT: " + score.ToString ();

		// Check if a new high score has been set
		int highScore = PlayerPrefs.GetInt ("Pedi High Score");
		if (score > highScore) {
			PlayerPrefs.SetInt("Pedi High Score", score);
		}
		// Display high score at all times
		highestScore.text = "HIGH: " + highScore.ToString();
	}

	float hitFactor(Vector2 ballPos, Vector2 racketPos,
	                float racketHeight) {
		// ascii art:
		// ||  1 <- at the top of the racket
		// ||
		// ||  0 <- at the middle of the racket
		// ||
		// || -1 <- at the bottom of the racket
		return (ballPos.y - racketPos.y) / racketHeight;
	}

	void OnCollisionEnter2D (Collision2D col) {
		// Hit the left Racket?
		if (col.gameObject.name == "RacketLeft") {
			// Calculate hit Factor
			float y = hitFactor(transform.position,
			                    col.transform.position,
			                    col.collider.bounds.size.y);
			
			// Calculate direction, make length=1 via .normalized
			Vector2 dir = new Vector2(1, y).normalized;
			
			// Set Velocity with dir * speed
			GetComponent<Rigidbody2D>().velocity = dir * speed;
			// Increase the score
			score++;
		}
		
		// Hit the right Racket?
		if (col.gameObject.name == "RacketRight") {
			// Calculate hit Factor
			float y = hitFactor(transform.position,
			                    col.transform.position,
			                    col.collider.bounds.size.y);
			
			// Calculate direction, make length=1 via .normalized
			Vector2 dir = new Vector2(-1, y).normalized;
			
			// Set Velocity with dir * speed
			GetComponent<Rigidbody2D>().velocity = dir * speed;
			// Increaase the score
			score++;
		}
	}
}
