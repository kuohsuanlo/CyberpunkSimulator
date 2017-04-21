package com.mygdx.job;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.mission.MissionAbstract;
import com.mygdx.need.NeedAbstract;

public class JobMove extends JobAbstract{


	public JobMove(Vector2 position, float maxProgress, float currentProgress, NeedAbstract decreasedNeed,
			NeedAbstract increasedNeed, float decreaseNeed_amount, float increaseNeed_amount) {
		super(position, maxProgress, currentProgress, decreasedNeed, increasedNeed, decreaseNeed_amount, increaseNeed_amount);
		// TODO Auto-generated constructor stub
	}

	public JobMove(Vector2 gPosition, int i, int j, MissionAbstract mission) {
		super(gPosition,i,j,mission);
	}

	public boolean compareJobAbstract(JobAbstract ja) {
		if(ja==null) return false;
		
		if(ja instanceof JobMove){
			return true;
		}
		else{
			return false;
		}
	}

}
