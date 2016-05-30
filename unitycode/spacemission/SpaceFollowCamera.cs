using UnityEngine;
using System.Collections;

public class SpaceFollowCamera : MonoBehaviour {
	
	public float interpVelocity;
	public float minDistance;
	public float followDistance;
	public GameObject target;
	public Vector3 offset;
	Vector3 targetPos;
	// Use this for initialization
	void Start () {
		offset = new Vector3 (1.9f, 2.0f, 0.0f);
		targetPos = transform.position;
	}
	
	// Update is called once per frame
	void FixedUpdate () {
		if (target)
		{
			Vector3 posNoZ = transform.position;
			posNoZ.z = target.transform.position.z;
			
			Vector3 targetDirection = (target.transform.position - posNoZ);
			
			interpVelocity = targetDirection.magnitude * 50f;
			
			targetPos = transform.position + (targetDirection.normalized * interpVelocity * Time.deltaTime); 

			if (targetPos.y < 4.5f) targetPos.y = 4.5f;
			
			transform.position = Vector3.Lerp( transform.position, targetPos + offset, 0.25f);
			
		}
	}
}
