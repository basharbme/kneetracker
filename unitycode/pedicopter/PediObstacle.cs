using UnityEngine;
using System.Collections;

public class PediObstacle : MonoBehaviour {
	public Vector2 velocity = new Vector2(-4, 0);
	public float range = 4;
	private Rigidbody2D rb2d;

	// Use this for initialization
	void Start () {
		rb2d = gameObject.GetComponent<Rigidbody2D> ();
		rb2d.velocity = velocity;
		transform.position = new Vector3 (
			transform.position.x, transform.position.y - range * Random.value, 
			transform.position.z);
	}
	
	// Update is called once per frame
	void Update () {
	
	}
}
