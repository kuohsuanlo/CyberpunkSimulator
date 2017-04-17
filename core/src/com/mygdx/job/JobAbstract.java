package com.mygdx.job;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.need.NeedAbstract;

public abstract class JobAbstract {
	
	public Vector2 position;
	public float maxProgress;
	public float currentProgress;
	public int decreasedNeed_id;
	public int increasedNeed_id;
	public float decreaseNeed_amount;
	public float increaseNeed_amount;

	public JobAbstract(Vector2 position, float maxProgress, float currentProgress, int decreasedNeed_id,
			int increasedNeed_id, float decreaseNeed_amount, float increaseNeed_amount) {
		super();
		this.position = position;
		this.maxProgress = maxProgress;
		this.currentProgress = currentProgress;
		this.decreasedNeed_id = decreasedNeed_id;
		this.increasedNeed_id = increasedNeed_id;
		this.decreaseNeed_amount = decreaseNeed_amount;
		this.increaseNeed_amount = increaseNeed_amount;
	}
}

