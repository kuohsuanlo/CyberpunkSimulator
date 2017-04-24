package com.mygdx.job;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.item.ItemAbstract;
import com.mygdx.mission.MissionAbstract;
import com.mygdx.need.NeedAbstract;

public class JobDrop extends JobAbstract{

	public ItemAbstract droppedItem ;

	
	public JobDrop(Vector2 position, float maxProgress, float currentProgress, NeedAbstract decreasedNeed,
			NeedAbstract increasedNeed, float decreaseNeed_amount, float increaseNeed_amount,ItemAbstract droppedItem) {
		super(position, maxProgress, currentProgress, decreasedNeed, increasedNeed, decreaseNeed_amount, increaseNeed_amount);
		this.droppedItem = droppedItem;
	}

	public JobDrop(Vector2 gPosition, int i, int j, MissionAbstract mission, ItemAbstract droppedItem) {
		super(gPosition,i,j,mission);
		this.droppedItem = droppedItem;
	}


	public boolean compareJobAbstract(JobAbstract ja) {
		if(ja==null) return false;
		
		if(ja instanceof JobDrop){
			return true;
		}
		else{
			return false;
		}
	}
}
