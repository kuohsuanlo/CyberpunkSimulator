package com.mygdx.job;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.need.NeedAbstract;

public abstract class JobAbstract {


	public Vector2 position;
	public int maxProgress;
	public int currentProgress;
	NeedAbstract decreasedNeed;
	NeedAbstract increasedNeed;

	public JobAbstract(float x,float y){
		super();
		position = new Vector2(x,y);
	}
	
	public JobAbstract(float x,float y,int maxApt){
		super();
		position = new Vector2(x,y);
		maxProgress = maxApt;
	}
	
	public JobAbstract(float x,float y, int maxProgress, int currentProgress, NeedAbstract decreasedNeed,
			NeedAbstract increasedNeed) {
		super();
		this.position = new Vector2(x,y);
		this.maxProgress = maxProgress;
		this.currentProgress = currentProgress;
		this.decreasedNeed = decreasedNeed;
		this.increasedNeed = increasedNeed;
	}

}

