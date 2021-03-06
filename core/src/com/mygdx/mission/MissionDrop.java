package com.mygdx.mission;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.character.ObjectNPC;
import com.mygdx.item.ItemAbstract;
import com.mygdx.job.JobAbstractBatch;
import com.mygdx.job.JobDrop;
import com.mygdx.job.JobMove;
import com.mygdx.job.JobTake;

public class MissionDrop extends MissionAbstract{
	public void assign(ObjectNPC npc, ItemAbstract ia, Vector2 location){
		JobAbstractBatch jb = new JobAbstractBatch(this);  	
		
		ItemAbstract foundItem = npc.findItem(ia.getId(),npc.getItemQueue());
		if(foundItem!=null){
			jb.getBatch().addLast(new JobMove(location, -1, -1, null, null,0,0));
			jb.getBatch().addLast(new JobDrop(location, 100, 0, null, null, 0, 0,foundItem));
		}
		npc.getJobBatchQueue().addLast(jb);
	}

}
