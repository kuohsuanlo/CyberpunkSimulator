package com.mygdx.need;

import com.badlogic.gdx.utils.Queue;
import com.mygdx.item.*;
import com.mygdx.job.*;

public abstract class NeedAbstract {
	public static int id =-1;
	public String displayName;
	public float searchRadius;
	
	public float tickLevel;
	public float currentLevel;
	public float maxLevel;
	
	/*
	 * NeededItem : It is preferable by this NPC. Or in other words, this item is in this NPC's memory.
	 * So when this NPC meets this need object, it will try to find this item first (whether it is null or not)
	 * If it is null, it will make another linear search. In all, this could prevent unintended search.
	 */
	public Queue<ItemAbstract> neededItemQueue;
	public Queue<JobAbstract> neededJobQueue;
	
	public NeedAbstract(String displayName, float searchRadius, float tickLevel, float currentLevel, float maxLevel,
			Queue<ItemAbstract> neededItemQueue, Queue<JobAbstract> neededJobQueue) {
		super();
		this.displayName = displayName;
		this.searchRadius = searchRadius;
		this.tickLevel = tickLevel;
		this.currentLevel = currentLevel;
		this.maxLevel = maxLevel;
		this.neededItemQueue = neededItemQueue;
		this.neededJobQueue = neededJobQueue;
	}
	
	public NeedAbstract(String displayName, float searchRadius, float tickLevel, float currentLevel,
			Queue<ItemAbstract> neededItemQueue, Queue<JobAbstract> neededJobQueue) {
		super();
		this.displayName = displayName;
		this.searchRadius = searchRadius;
		this.tickLevel = tickLevel;
		this.currentLevel = currentLevel;
		this.maxLevel = 100;
		this.neededItemQueue = neededItemQueue;
		this.neededJobQueue = neededJobQueue;
	}
	

	public void tickNeed(){
		addNeed(tickLevel);
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
