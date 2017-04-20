package com.mygdx.need;

import com.badlogic.gdx.utils.Queue;
import com.mygdx.item.ItemAbstract;
import com.mygdx.job.JobAbstract;

public class NeedJob extends NeedAbstract {
	public JobAbstract neededJob;
	public NeedJob(String displayName, float searchRadius, float tickLevel, float currentLevel,
			Queue<ItemAbstract> neededItemQueue, Queue<JobAbstract> neededJobQueue, JobAbstract neededJob) {
		super(displayName, searchRadius, tickLevel, currentLevel, neededItemQueue, neededJobQueue);
		this.neededJob = neededJob;
		id =NEED_JOB_ID;
	}
	public NeedJob(String displayName, float searchRadius, float tickLevel, float currentLevel, float maxLevel,
			Queue<ItemAbstract> neededItemQueue, Queue<JobAbstract> neededJobQueue, JobAbstract neededJob) {
		super(displayName, searchRadius, tickLevel, currentLevel, maxLevel, neededItemQueue, neededJobQueue);
		this.neededJob = neededJob;
		id =NEED_JOB_ID;
	}
}
