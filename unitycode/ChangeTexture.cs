using UnityEngine;
using System.Collections;

public class ChangeTexture : MonoBehaviour {

	public GameObject[] pics = new GameObject[4];
	int curImage = 0;
	// Are we on the up or down cycle?
	bool onUpCycle = true;

	// Use this for initialization
	void Start () {
		InvokeRepeating ("changeImage", 0, 0.5f);
	}
	
	// Update is called once per frame
	void Update () {

	}

	private void changeImage() {
		setActiveImage (curImage);
		if (onUpCycle) {
			if (curImage < pics.Length - 1) {
				curImage++;
			} else {
				curImage--;
				onUpCycle = false;
			}
		} else {
			if (curImage > 0) {
				curImage--;
			} else {
				curImage++;
				onUpCycle = true;
			}
		}
	}

	// Sets image k to be active and all others inactive
	private void setActiveImage(int k) {
		for (int i=0; i< pics.Length; ++i) {
			if (i != k) {
				pics[i].SetActive(false);
			} else {
				pics[i].SetActive(true);
			}
		}
	}
}
