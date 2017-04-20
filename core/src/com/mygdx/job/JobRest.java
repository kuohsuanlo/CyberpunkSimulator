package com.mygdx.job;

import com.badlogic.gdx.math.Vector2;

public class JobRest extends JobAbstract{

	
	public JobRest(Vector2 position, float maxProgress, float currentProgress, int decreasedNeed_id,
			int increasedNeed_id, float decreaseNeed_amount, float increaseNeed_amount) {
		super(position, maxProgress, currentProgress, decreasedNeed_id, increasedNeed_id, decreaseNeed_amount,
				increaseNeed_amount);
	}

	public boolean compareJobAbstract(JobAbstract ja) {
		if(ja==null) return false;
		
		if(ja instanceof JobRest){
			return true;
		}
		else{
			return false;
		}
	}
}
