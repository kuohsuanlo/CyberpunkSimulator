package com.mygdx.job;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Queue;
import com.mygdx.item.ItemAbstract;

public class JobProduce extends JobAbstract{

	public Queue<ItemAbstract> usedItemQueue ;
	public Queue<ItemAbstract> producedItemQueue;

	public JobProduce(Vector2 position, float maxProgress, float currentProgress, int decreasedNeed_id,
			int increasedNeed_id, float decreaseNeed_amount, float increaseNeed_amount,Queue<ItemAbstract> usedItemQueue, Queue<ItemAbstract> producedItemQueue) {
		super(position, maxProgress, currentProgress, decreasedNeed_id, increasedNeed_id, decreaseNeed_amount,
				increaseNeed_amount);
		this.usedItemQueue = usedItemQueue;
		this.producedItemQueue = producedItemQueue;
	}


}
