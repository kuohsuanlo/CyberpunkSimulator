package com.mygdx.need;

import com.mygdx.item.*;
import com.mygdx.job.*;

public abstract class NeedAbstract {
	public static int id =-1;
	public String displayName;
	public float searchRadius;
	public float currentLevel;
	public float maxLevel;
	public ItemAbstract neededItem;
	public JobAbstract neededJob;
	
	public NeedAbstract(String displayName, float currentLevel, ItemAbstract neededItem, JobAbstract neededJob) {
		super();
		this.displayName = displayName;
		this.currentLevel = currentLevel;
		this.maxLevel = 100;
		this.neededItem = neededItem;
		this.neededJob = neededJob;
	}
	public NeedAbstract(String displayName, float currentLevel,float maxLevel, ItemAbstract neededItem, JobAbstract neededJob) {
		super();
		this.displayName = displayName;
		this.currentLevel = currentLevel;
		this.maxLevel = maxLevel;
		this.neededItem = neededItem;
		this.neededJob = neededJob;
	}
	
	public void addNeed(float amount){
		if(currentLevel+amount<maxLevel){
			currentLevel +=amount;
		}
		else{
			currentLevel = maxLevel;
		}
		
		if(currentLevel<0){
			currentLevel=0;
		}
		
		searchRadius= 1000*(currentLevel / maxLevel);;

	}
}
