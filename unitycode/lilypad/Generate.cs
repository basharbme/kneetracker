using UnityEngine;
using System.Collections;
using System.Collections.Generic;

public class Generate : MonoBehaviour {
	public GameObject pad;
	public int instanceCount = 4;

	public Dictionary<int, Vector2> pads;


	// Use this for initialization
	void Start () {
		pads = new Dictionary<int, Vector2> ();
		CreateLilyPads ();
	}
	
	// Update is called once per frame
	void Update () {
	
	}

	void CreateLilyPads () {
		// Starting lilypad, always in same spot
		Instantiate(pad, new Vector2(0.649f, -0.79f), Quaternion.identity); 
		for (int i=0; i<instanceCount; ++i) {
			Vector2 tempVec = new Vector2(7.0f + (float) i * 7.0f, 
			                              -0.79f + (Random.value-0.5f) * 4.0f);
			Instantiate(pad, tempVec, Quaternion.identity); 
			// Store where we want the frog to land
			tempVec += new Vector2(0.0f, 0.8f);
			pads.Add(i, tempVec);
		}
	}

}
