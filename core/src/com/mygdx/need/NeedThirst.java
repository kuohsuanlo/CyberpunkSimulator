package com.mygdx.need;

import com.mygdx.item.ItemAbstract;
import com.mygdx.job.JobAbstract;

public class NeedThirst extends NeedAbstract {
	public static int id =2;

	public NeedThirst(String displayName, float currentLevel, ItemAbstract neededItem, JobAbstract neededJob) {
		super(displayName, currentLevel, neededItem, neededJob);
	}
	
	public NeedThirst(String displayName, float currentLevel, float maxLevel, ItemAbstract neededItem, JobAbstract neededJob) {
		super(displayName, currentLevel,maxLevel, neededItem, neededJob);
	}

}
