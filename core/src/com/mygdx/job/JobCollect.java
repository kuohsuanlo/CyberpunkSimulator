package com.mygdx.job;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Queue;
import com.mygdx.item.ItemAbstract;

public class JobCollect extends JobAbstract{

	
	/*
	 * JobCollect is for collecting the items in the recipe.
	 * The NPC would keep checking all the items are collected.
	 * 
	 * This job generates a series of (JobMove + JobTake) of the specified types of items scattered around the map. 
	 * 
	 */
	public Queue<ItemAbstract> collectItemQueue ;
	public JobCollect(Vector2 position, float maxProgress, float currentProgress, int decreasedNeed_id,
			int increasedNeed_id, float decreaseNeed_amount, float increaseNeed_amount,Queue<ItemAbstract> collectItemQueue) {
		super(position, maxProgress, currentProgress, decreasedNeed_id, increasedNeed_id, decreaseNeed_amount,
				increaseNeed_amount);
		this.collectItemQueue = collectItemQueue;
	}

	public boolean compareJobAbstract(JobAbstract ja) {
		if(ja==null) return false;
		
		if(ja instanceof JobCollect){
			return compareItemQueue(this.collectItemQueue,((JobCollect) ja).collectItemQueue);
		}
		else{
			return false;
		}
	}
}
