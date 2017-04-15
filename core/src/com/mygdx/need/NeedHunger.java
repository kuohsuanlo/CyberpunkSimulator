package com.mygdx.need;

import com.mygdx.item.ItemAbstract;
import com.mygdx.job.JobAbstract;

public class NeedHunger extends NeedAbstract {
	public static int id =1;

	public NeedHunger(String displayName, float currentLevel, ItemAbstract neededItem, JobAbstract neededJob) {
		super(displayName, currentLevel, neededItem, neededJob);
	}

	public NeedHunger(String displayName, float currentLevel, float maxLevel, ItemAbstract neededItem, JobAbstract neededJob) {
		super(displayName, currentLevel,maxLevel, neededItem, neededJob);
	}

}
