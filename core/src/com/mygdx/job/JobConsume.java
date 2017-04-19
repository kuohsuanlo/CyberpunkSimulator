package com.mygdx.job;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.item.ItemAbstract;

public class JobConsume extends JobAbstract{

	public ItemAbstract consumedItem ;
	public JobConsume(Vector2 position, float maxProgress, float currentProgress, int decreasedNeed_id,
			int increasedNeed_id, float decreaseNeed_amount, float increaseNeed_amount,ItemAbstract consumedItem) {
		super(position, maxProgress, currentProgress, decreasedNeed_id, increasedNeed_id, decreaseNeed_amount,
				increaseNeed_amount);
		this.consumedItem = consumedItem;
	}


}
