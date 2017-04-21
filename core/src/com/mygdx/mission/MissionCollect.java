package com.mygdx.mission;

import com.badlogic.gdx.utils.Queue;
import com.mygdx.item.ItemAbstract;
import com.mygdx.job.JobAbstractBatch;
import com.mygdx.job.JobMove;
import com.mygdx.job.JobTake;

public class MissionCollect extends MissionAbstract{
	/*
	if(cj.getMission()!=null){
    		if(!cj.getMission().handledBatchInQueue){
    			Queue<ItemAbstract> iaq = this.findItemOnGround(cj.collectedItem);
    			
    			for(int i=0;i<iaq.size;i++){
    				//JobAbstract(Vector2 position, float maxProgress, float currentProgress, MissionAbstract mission) {
    				JobMove jm = new JobMove(iaq.get(i).gPosition,-1, -1, cj.getMission());
					JobTake jt = new JobTake(iaq.get(i).gPosition,-1,-1, cj.getMission(),iaq.get(i),null);

					//Inverse Order;

					JobAbstractBatch jb = new JobAbstractBatch(needQueue.get(i));
					jb.getBatch().addFirst(jt);
					jb.getBatch().addFirst(jm);
					jobBatchQueue.addFirst(jb);
    			}
    			
    			cj.getMission().handledBatchInQueue =true;
    		}
    	}
	*/

}
