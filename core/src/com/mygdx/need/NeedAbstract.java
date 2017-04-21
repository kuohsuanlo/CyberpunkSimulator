package com.mygdx.need;

import com.badlogic.gdx.utils.Queue;
import com.mygdx.item.*;
import com.mygdx.job.*;

public abstract class NeedAbstract {
	public static final int NEED_ABSTRACT_ID = -1;
	public static final int NEED_FATIGUE_ID = 0;
	public static final int NEED_HUNGER_ID = 1;
	public static final int NEED_THIRST_ID = 2;
	public static final int NEED_JOB_ID = 3;
	public int id;
	public float searchRadius;
	
	public float tickLevel;
	public float currentLevel;
	public float maxLevel;
	
	public boolean handledBatchInQueue;
	private String displayName;
	
	/*
	 * NeededItem : It is preferable by this NPC. Or in other words, this item is in this NPC's memory.
	 * So when this NPC meets this need object, it will try to find this item first (whether it is zero-sized or not)
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
		this.handledBatchInQueue = false;
		this.id =NEED_ABSTRACT_ID;
		this.initQueue();
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
		this.handledBatchInQueue = false;
		this.initQueue();
	}
	
	private void initQueue(){
		
		if(this.neededItemQueue==null){
			this.neededItemQueue= new Queue<ItemAbstract>();
		}
		if(this.neededJobQueue==null){
			this.neededJobQueue= new Queue<JobAbstract>();
		}
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
	public String getDisplayName(){
		if(this.displayName==null){
			this.displayName = "not defined";
		}
		return this.displayName;
	}
}
