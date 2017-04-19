package com.mygdx.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Queue;
import com.mygdx.game.ObjectNPC;
import com.mygdx.item.ItemAbstract;
import com.mygdx.job.JobAbstract;
import com.mygdx.job.JobConsume;
import com.mygdx.job.JobMove;
import com.mygdx.job.JobRest;
import com.mygdx.job.JobTake;
import com.mygdx.need.NeedAbstract;
import com.mygdx.need.NeedFatigue;
import com.mygdx.need.NeedHunger;
import com.mygdx.need.NeedThirst;

public class ThreadNpc extends Thread{
	public static final int requestQueueMax = 500;
	private Queue<ObjectRequest> npcr_queue;
	public ThreadNpc(){
		npcr_queue = new Queue<ObjectRequest>();
	}
	
	public void processCheckNeed(){
		if(this.npcr_queue.size==0) {
			return;
		}
		
		ObjectRequest oq = this.npcr_queue.removeFirst();
		Queue<NeedAbstract> needQueue = oq.npc.getNeedQueue();
		Queue<JobAbstract> jobQueue = oq.npc.getJobQueue();
		for(int i=0;i<needQueue.size;i++){
    		float c = needQueue.get(i).currentLevel;
    		float m = needQueue.get(i).maxLevel;
    		
    		if(c<m) continue; 

			if( needQueue.get(i) instanceof NeedFatigue) {
				if (!needQueue.get(i).handledInQueue){
					jobQueue.addFirst( new JobRest(oq.npc.gPosition,m,m-c,0,0,0f,0f));
					needQueue.get(i).handledInQueue = true;
				}
			}
			else if( needQueue.get(i) instanceof NeedHunger  ||  needQueue.get(i) instanceof NeedThirst) {
				//no candidates item in queue, start to search
				ItemAbstract onBodyItem ;
				ItemAbstract onGroundItem ;
				ItemAbstract goal;
				
				//NPC has no idea where the item is
				if(needQueue.get(i).neededItemQueue.size==0 ){
					//find it on NPC body
					onBodyItem = oq.npc.findItemOnBody(needQueue.get(i));
					
					//found
					if(onBodyItem!=null){
						needQueue.get(i).neededItemQueue.addLast(onBodyItem);
					}
					//not found
					else{
						//find it on the ground
						onGroundItem = oq.npc.findItemOnGround(needQueue.get(i));
						
						//found 
						if(onGroundItem!=null){
    	    				if (!needQueue.get(i).handledInQueue){
    	    					goal = onGroundItem;
    	    					jobQueue.addLast(new JobMove(goal.gPosition,-1, -1, 0, 0,0,0));
    	    					JobConsume pendingCJ = new JobConsume(goal.gPosition,m,m-c,0,0,0f,0f,null);
    	    					
    	    					jobQueue.addLast(new JobTake(goal.gPosition,-1,-1,goal.decreasedNeed_id,0,0f,0f,goal,pendingCJ));
    	        				jobQueue.addLast(pendingCJ);
    	        				needQueue.get(i).handledInQueue = true;
    	    				}
						}
						//not found, screw the NPC. Ignore the need.
						else{
							//no item to solve the need. Let's rest (ignore) and see.
							if (!needQueue.get(i).handledInQueue){
								
		    				}
						}
					}
				}
				//NPC know it's on its body
				else{
					onBodyItem = needQueue.get(i).neededItemQueue.first();
    				//just on NPC body
    				if (!needQueue.get(i).handledInQueue){
    					goal = needQueue.get(i).neededItemQueue.first();
    					goal.gPosition = oq.npc.gPosition;
        				jobQueue.addFirst( new JobConsume(goal.gPosition,m,m-c,goal.decreasedNeed_id,0,0f,0f,goal));
        				needQueue.get(i).handledInQueue = true;
    				}
				}
    		}
    	}
	}
	

	public boolean addRequest(ObjectNPC onpc){
		if(this.npcr_queue.size>=requestQueueMax){
			return false;
		}
		else{
			this.npcr_queue.addLast(new ObjectRequest(onpc));
			return true;
		}
	}
	public int getCurrentRequestNumber(){
		return this.npcr_queue.size;
	}
	public void run() { // override Thread's run()
		while (!Thread.currentThread().isInterrupted()) {
		    processCheckNeed();
		    try {
				sleep(20);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
    }
}

class ObjectRequest{
	public ObjectNPC npc;
	public ObjectRequest(ObjectNPC npc){
		this.npc = npc;
	}
	
}
