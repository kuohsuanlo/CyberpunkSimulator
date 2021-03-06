package com.mygdx.mission;

import com.badlogic.gdx.utils.Queue;
import com.mygdx.character.ObjectNPC;
import com.mygdx.item.ItemAbstract;
import com.mygdx.job.JobAbstractBatch;
import com.mygdx.job.JobMove;
import com.mygdx.job.JobTake;

public class MissionCollect extends MissionAbstract{
	public void assign(ObjectNPC npc, ItemAbstract ia){
		JobAbstractBatch jb = new JobAbstractBatch(this);  	
		
		ItemAbstract foundItem = npc.findItem(ia.getId(),npc.game.getItemQueue());
		if(foundItem!=null){
			jb.getBatch().addLast(new JobMove(foundItem.gPosition,-1, -1, null, null,0,0));
			jb.getBatch().addLast(new JobTake(foundItem.gPosition,-1, -1, null, null,0,0,foundItem,null));
		}
		npc.getJobBatchQueue().addLast(jb);
	}

}
