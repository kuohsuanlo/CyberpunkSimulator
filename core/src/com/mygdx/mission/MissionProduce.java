package com.mygdx.mission;

import com.badlogic.gdx.utils.Queue;
import com.mygdx.game.ObjectNPC;
import com.mygdx.item.ItemAbstract;
import com.mygdx.item.ItemRecipe;
import com.mygdx.job.JobAbstractBatch;
import com.mygdx.job.JobMove;
import com.mygdx.job.JobProduce;
import com.mygdx.job.JobTake;

public class MissionProduce extends MissionAbstract{
	public static void assign(ObjectNPC npc, ItemRecipe ir){
		JobAbstractBatch jb = new JobAbstractBatch(null); 
		jb.getBatch().addLast(new JobProduce(npc.gPosition, 100, 0, null, null, 0, 0, ir));
		npc.getJobBatchQueue().addLast(jb);
	}
}
