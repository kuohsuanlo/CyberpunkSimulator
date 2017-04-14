package com.mygdx.need;

import com.mygdx.item.ItemAbstract;
import com.mygdx.job.JobAbstract;

public class NeedHunger extends NeedAbstract {

	public NeedHunger(String displayName, float thresholdPerAccumulation, float currentAccumulation,
			float searchRadius, float totalLevel, ItemAbstract neededItem, JobAbstract neededJob) {
		super(displayName, thresholdPerAccumulation, currentAccumulation, searchRadius, totalLevel, neededItem, neededJob);
		id=1;
	}

}
