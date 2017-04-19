package com.mygdx.job;

import com.badlogic.gdx.math.Vector2;

public abstract class JobAbstract {
	
	private Vector2 position;
	private float maxProgress;
	private float currentProgress;
	private int decreasedNeed_id;
	private int increasedNeed_id;
	private float decreaseNeed_amount;
	private float increaseNeed_amount;
	private boolean jobAborted;
	public JobAbstract(Vector2 position, float maxProgress, float currentProgress, int decreasedNeed_id,
			int increasedNeed_id, float decreaseNeed_amount, float increaseNeed_amount) {
		super();
		this.setPosition(position);
		this.setMaxProgress(maxProgress);
		this.setCurrentProgress(currentProgress);
		this.setDecreasedNeed_id(decreasedNeed_id);
		this.setIncreasedNeed_id(increasedNeed_id);
		this.setDecreaseNeed_amount(decreaseNeed_amount);
		this.setIncreaseNeed_amount(increaseNeed_amount);
		this.setJobAborted(false);
	}
	public Vector2 getPosition() {
		return position;
	}
	public void setPosition(Vector2 position) {
		this.position = position;
	}
	public float getMaxProgress() {
		return maxProgress;
	}
	public void setMaxProgress(float maxProgress) {
		this.maxProgress = maxProgress;
	}
	public float getCurrentProgress() {
		return currentProgress;
	}
	public void setCurrentProgress(float currentProgress) {
		this.currentProgress = currentProgress;
	}
	public int getDecreasedNeed_id() {
		return decreasedNeed_id;
	}
	public void setDecreasedNeed_id(int decreasedNeed_id) {
		this.decreasedNeed_id = decreasedNeed_id;
	}
	public int getIncreasedNeed_id() {
		return increasedNeed_id;
	}
	public void setIncreasedNeed_id(int increasedNeed_id) {
		this.increasedNeed_id = increasedNeed_id;
	}
	public float getDecreaseNeed_amount() {
		return decreaseNeed_amount;
	}
	public void setDecreaseNeed_amount(float decreaseNeed_amount) {
		this.decreaseNeed_amount = decreaseNeed_amount;
	}
	public float getIncreaseNeed_amount() {
		return increaseNeed_amount;
	}
	public void setIncreaseNeed_amount(float increaseNeed_amount) {
		this.increaseNeed_amount = increaseNeed_amount;
	}
	public boolean isJobAborted() {
		return jobAborted;
	}
	public void setJobAborted(boolean jobAborted) {
		this.jobAborted = jobAborted;
	}
}

