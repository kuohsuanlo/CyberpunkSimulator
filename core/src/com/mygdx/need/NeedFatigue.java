package com.mygdx.need;

import com.mygdx.item.ItemAbstract;
import com.mygdx.job.JobAbstract;

public class NeedFatigue extends NeedAbstract {


	public static int id =0;
	
	public NeedFatigue(String displayName, float currentLevel, float maxLevel, ItemAbstract neededItem,
			JobAbstract neededJob) {
		super(displayName, currentLevel, maxLevel, neededItem, neededJob);
	}
	public NeedFatigue(String displayName, float currentLevel, ItemAbstract neededItem, JobAbstract neededJob) {
		super(displayName, currentLevel, neededItem, neededJob);
	}




}
