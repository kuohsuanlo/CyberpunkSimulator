package com.mygdx.job;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.item.ItemAbstract;
import com.mygdx.need.NeedAbstract;
import com.mygdx.need.NeedFatigue;

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

}
