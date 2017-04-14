package com.mygdx.need;

import com.mygdx.item.*;
import com.mygdx.job.*;

public abstract class NeedAbstract {
	public static int id;
	public String displayName;
	public float thresholdPerAccumulation;
	public float currentAccumulation;
	public float searchRadius;
	public float totalLevel;
	public ItemAbstract neededItem;
	public JobAbstract neededJob;
	
	public NeedAbstract(String displayName, float thresholdPerAccumulation, float currentAccumulation,
			float searchRadius, float totalLevel, ItemAbstract neededItem, JobAbstract neededJob) {
		super();
		this.displayName = displayName;
		this.thresholdPerAccumulation = thresholdPerAccumulation;
		this.currentAccumulation = currentAccumulation;
		this.searchRadius = searchRadius;
		this.totalLevel = totalLevel;
		this.neededItem = neededItem;
		this.neededJob = neededJob;
	}
	
	public void addNeed(float amount){
		currentAccumulation+=amount;
		totalLevel +=amount;
		
		if(currentAccumulation>=thresholdPerAccumulation){
			searchRadius+=1;
			currentAccumulation=0;
		}
	}
}
