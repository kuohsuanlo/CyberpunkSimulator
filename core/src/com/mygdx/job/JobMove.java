package com.mygdx.job;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.need.NeedAbstract;

public class JobMove extends JobAbstract{
	NeedAbstract decreasedNeed;
	NeedAbstract increasedNeed;
	public JobMove(float x,float y){
		super(x,y);
	}
	
	public JobMove(float x,float y,int maxPgs){
		super(x,y,maxPgs);
	}
	
	public JobMove(float x,float y, int maxPgs, int currentAttempt, NeedAbstract decreasedNeed,
			NeedAbstract increasedNeed) {
		super( x, y, maxPgs, currentAttempt, decreasedNeed, increasedNeed);
	}
}
