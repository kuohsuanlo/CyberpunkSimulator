package com.mygdx.need;

import com.badlogic.gdx.utils.Queue;
import com.mygdx.item.ItemAbstract;
import com.mygdx.job.JobAbstract;

public class NeedThirst extends NeedAbstract {

	public NeedThirst(String displayName, float searchRadius, float tickLevel, float currentLevel,
			Queue<ItemAbstract> neededItemQueue, Queue<JobAbstract> neededJobQueue) {
		super(displayName, searchRadius, tickLevel, currentLevel, neededItemQueue, neededJobQueue);
		id =NEED_THIRST_ID;
	}
	public NeedThirst(String displayName, float searchRadius, float tickLevel, float currentLevel, float maxLevel,
			Queue<ItemAbstract> neededItemQueue, Queue<JobAbstract> neededJobQueue) {
		super(displayName, searchRadius, tickLevel, currentLevel, maxLevel, neededItemQueue, neededJobQueue);
		id =NEED_THIRST_ID;
	}
}
