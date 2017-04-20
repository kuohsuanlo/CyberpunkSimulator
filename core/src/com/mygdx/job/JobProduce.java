package com.mygdx.job;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Queue;
import com.mygdx.item.ItemAbstract;
import com.mygdx.need.NeedAbstract;

public class JobProduce extends JobAbstract{

	public Queue<ItemAbstract> usedItemQueue ;
	public Queue<ItemAbstract> producedItemQueue;

	public JobProduce(Vector2 position, float maxProgress, float currentProgress, NeedAbstract decreasedNeed,
			NeedAbstract increasedNeed, float decreaseNeed_amount, float increaseNeed_amount,Queue<ItemAbstract> usedItemQueue,Queue<ItemAbstract> producedItemQueue) {
		super(position, maxProgress, currentProgress, decreasedNeed, increasedNeed, decreaseNeed_amount, increaseNeed_amount);

		this.usedItemQueue = usedItemQueue;
		this.producedItemQueue = producedItemQueue;
	}
	public boolean compareJobAbstract(JobAbstract ja) {
		if(ja==null) return false;
		
		if(ja instanceof JobProduce){
			return compareItemQueue(this.usedItemQueue,((JobProduce) ja).usedItemQueue)  &&
					compareItemQueue(this.producedItemQueue,((JobProduce) ja).producedItemQueue) ;
		}
		else{
			return false;
		}
		
	}
}
