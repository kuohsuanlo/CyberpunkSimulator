package com.mygdx.job;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.item.ItemAbstract;
import com.mygdx.need.NeedAbstract;

public class JobTake extends JobAbstract{

	public ItemAbstract takenItem ;
	public JobAbstract nextPendingJob;


	public JobTake(Vector2 position, float maxProgress, float currentProgress, NeedAbstract decreasedNeed,
			NeedAbstract increasedNeed, float decreaseNeed_amount, float increaseNeed_amount,ItemAbstract takenItem, JobAbstract nextPendingJob) {
		super(position, maxProgress, currentProgress, decreasedNeed, increasedNeed, decreaseNeed_amount, increaseNeed_amount);
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
