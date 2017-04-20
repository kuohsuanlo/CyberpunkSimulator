package com.mygdx.job;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.item.ItemAbstract;
import com.mygdx.need.NeedAbstract;

public class JobConsume extends JobAbstract{

	public ItemAbstract consumedItem ;
	public JobConsume(Vector2 position, float maxProgress, float currentProgress, NeedAbstract decreasedNeed,
			NeedAbstract increasedNeed, float decreaseNeed_amount, float increaseNeed_amount,ItemAbstract consumedItem) {
		super(position, maxProgress, currentProgress, decreasedNeed, increasedNeed, decreaseNeed_amount, increaseNeed_amount);
		this.consumedItem = consumedItem;
	}
	public boolean compareJobAbstract(JobAbstract ja) {
		if(ja==null) return false;
		
		if(ja instanceof JobConsume){
			return compareItem(this.consumedItem,((JobConsume) ja).consumedItem);
		}
		else{
			return false;
		}
		
	}


}
