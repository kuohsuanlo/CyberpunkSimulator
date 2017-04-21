package com.mygdx.job;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Queue;
import com.mygdx.item.ItemAbstract;
import com.mygdx.mission.MissionAbstract;
import com.mygdx.need.NeedAbstract;

public abstract class JobAbstract {
	
	private Vector2 position;
	private float maxProgress;
	private float currentProgress;
	private NeedAbstract decreasedNeed;
	private NeedAbstract increasedNeed;
	private float decreaseNeed_amount;
	private float increaseNeed_amount;
	
	private MissionAbstract mission;

	private boolean jobAborted;
	private boolean jobDone;
	
	public JobAbstract(Vector2 position, float maxProgress, float currentProgress, MissionAbstract mission) {
		super();
		this.setPosition(position);
		this.setMaxProgress(maxProgress);
		this.setCurrentProgress(currentProgress);
		this.setMission(mission); 
	}
	public JobAbstract(Vector2 position, float maxProgress, float currentProgress, NeedAbstract decreasedNeed,
			NeedAbstract increasedNeed, float decreaseNeed_amount, float increaseNeed_amount) {
		super();
		this.setPosition(position);
		this.setMaxProgress(maxProgress);
		this.setCurrentProgress(currentProgress);
		this.setDecreasedNeed(decreasedNeed);
		this.setIncreasedNeed(increasedNeed);
		this.setDecreaseNeed_amount(decreaseNeed_amount);
		this.setIncreaseNeed_amount(increaseNeed_amount);
		this.setJobAborted(false);
		this.setJobDone(false);
	
	}
	public abstract boolean compareJobAbstract(JobAbstract ja);
	
	

	public boolean compareItem(ItemAbstract ia1, ItemAbstract ia2){
		return (ia1.getId()==ia2.getId());
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
		if(currentProgress>this.maxProgress){
			this.currentProgress = this.maxProgress;
		}
		else{
			this.currentProgress = currentProgress;
		}
	}
	public NeedAbstract getDecreasedNeed() {
		return decreasedNeed;
	}
	public void setDecreasedNeed(NeedAbstract decreasedNeed) {
		this.decreasedNeed = decreasedNeed;
	}
	public NeedAbstract getIncreasedNeed() {
		return increasedNeed;
	}
	public void setIncreasedNeed(NeedAbstract increasedNeed) {
		this.increasedNeed = increasedNeed;
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

	public boolean isJobDone() {
		return jobDone;
	}
	public void setJobDone(boolean jobDone) {
		this.jobDone = jobDone;
	}
	public MissionAbstract getMission() {
		return mission;
	}
	public void setMission(MissionAbstract mission) {
		this.mission = mission;
	}
}

