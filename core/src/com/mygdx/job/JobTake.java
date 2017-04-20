package com.mygdx.job;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.item.ItemAbstract;

public class JobTake extends JobAbstract{

	public ItemAbstract takenItem ;
	public JobAbstract nextPendingJob;
	public JobTake(Vector2 position, float maxProgress, float currentProgress, int decreasedNeed_id,
			int increasedNeed_id, float decreaseNeed_amount, float increaseNeed_amount,ItemAbstract takenItem) {
		super(position, maxProgress, currentProgress, decreasedNeed_id, increasedNeed_id, decreaseNeed_amount,
				increaseNeed_amount);
		this.takenItem = takenItem;
	}

	public JobTake(Vector2 position, float maxProgress, float currentProgress, int decreasedNeed_id,
			int increasedNeed_id, float decreaseNeed_amount, float increaseNeed_amount,ItemAbstract takenItem, JobAbstract nextPendingJob) {
		super(position, maxProgress, currentProgress, decreasedNeed_id, increasedNeed_id, decreaseNeed_amount,
				increaseNeed_amount);
		this.takenItem = takenItem;
		this.nextPendingJob = nextPendingJob;
	}

	public boolean compareJobAbstract(JobAbstract ja) {
		if(ja==null) return false;
		
		if(ja instanceof JobTake){
			return true;
		}
		else{
			return false;
		}
	}
}
