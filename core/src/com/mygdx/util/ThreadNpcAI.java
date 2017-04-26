package com.mygdx.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Queue;
import com.mygdx.character.ObjectNPC;
import com.mygdx.game.MyGdxGame;
import com.mygdx.item.ItemAbstract;
import com.mygdx.item.ItemRecipe;
import com.mygdx.job.JobAbstract;
import com.mygdx.job.JobAbstractBatch;
import com.mygdx.job.JobConsume;
import com.mygdx.job.JobDrop;
import com.mygdx.job.JobMove;
import com.mygdx.job.JobProduce;
import com.mygdx.job.JobRest;
import com.mygdx.job.JobTake;
import com.mygdx.mission.MissionCollect;
import com.mygdx.mission.MissionDrop;
import com.mygdx.mission.MissionProduce;
import com.mygdx.need.NeedAbstract;
import com.mygdx.need.NeedFatigue;
import com.mygdx.need.NeedHunger;
import com.mygdx.need.NeedThirst;
import com.mygdx.profession.ProfessionAbstract;

public class ThreadNpcAI extends Thread{
	public int requestQueueMax;
	private Queue<ObjectRequest> npcr_queue;
	public ThreadNpcAI(MyGdxGame game){
		this.requestQueueMax = game.getThreadNpcAIQueueMaxNumber();
		npcr_queue = new Queue<ObjectRequest>();
	}
	
	/*
	 * getLastTimeElapsed() will return the real time in seconds.
	 */
	private void processIncreaseNeed(ObjectRequest oq){
		Queue<NeedAbstract> needQueue = oq.npc.getNeedQueue();
		if(oq.npc.getSpecies()==ObjectNPC.HUMAN){
        	for(int i=0;i<needQueue.size;i++){
        		needQueue.get(i).tickLevel = oq.npc.getBaseEnergyConsumption()*oq.npc.game.getLastTimeElapsed();
        		needQueue.get(i).tickNeed();
        	}
    	}
	}
	private void processBodyCycle(ObjectRequest oq){

		Queue<NeedAbstract> needQueue = oq.npc.getNeedQueue();
    	for(int i=0;i<needQueue.size;i++){
    		if(needQueue.get(i).currentLevel>=needQueue.get(i).maxLevel){
    			oq.npc.damageBody(oq.npc.getBaseEnergyConsumption()*oq.npc.game.getLastTimeElapsed());
    		}
    		else{
    			oq.npc.recoverBody(oq.npc.getBaseEnergyConsumption()*oq.npc.game.getLastTimeElapsed()*0.5f);
    		}
    	}
	}
	
	/*
	 * QJA is in 0,1,2,3 order.
	 * addJobBundleFirst : added by addFirst(3,2,1,0). Inverse
	 * addJobBundleLast  : added by addLast (0,1,2,3)
	 * 
	private void addJobBundleFirst(Queue<JobAbstract> npc_qja, Queue<JobAbstract> qja){
		for(int i=qja.size-1;i>=0;i--){
			npc_qja.addFirst(qja.get(i));
		}
		
	}
	private void addJobBundleLast(Queue<JobAbstract> npc_qja, Queue<JobAbstract> qja){
		for(int i=0;i<qja.size;i++){
			npc_qja.addLast(qja.get(i));
		}
	}
	*/
	private void processCheckNeed(ObjectRequest oq){
		/*
		 * Because jobs driven by need should be placed before profession. (addFirst instead of addLast)
		 * TODO : It actually should be driven by NPC's characteristics, but now let's make it this way.
		 */
		
		Queue<NeedAbstract> needQueue = oq.npc.getNeedQueue();
		Queue<JobAbstractBatch> jobBatchQueue = oq.npc.getJobBatchQueue();
		synchronized(jobBatchQueue){
			for(int i=0;i<needQueue.size;i++){
	    		float c = needQueue.get(i).currentLevel;
	    		float m = needQueue.get(i).maxLevel;
	    		
	    		if(c<m) continue; 

				if( needQueue.get(i) instanceof NeedFatigue) {
					if (!needQueue.get(i).handledBatchInQueue){
						JobAbstractBatch jb = new JobAbstractBatch(needQueue.get(i));
						jb.getBatch().addFirst(new JobRest(oq.npc.gPosition,m,m-c,needQueue.get(i),null,0f,0f));
						jobBatchQueue.addFirst(jb);
						needQueue.get(i).handledBatchInQueue = true;
					}
				}
				else if( needQueue.get(i) instanceof NeedHunger  ||  needQueue.get(i) instanceof NeedThirst) {
					//Gdx.app.log("NEED",needQueue.get(i).getClass()+"");
					//no candidates item in queue, start to search
					ItemAbstract onBodyItem ;
					ItemAbstract onGroundItem ;
					ItemAbstract goal;
					
					

					//NPC has no idea where the item is
					if(needQueue.get(i).neededItemQueue.size==0 ){
						//find it on NPC body
						onBodyItem = oq.npc.findItem(needQueue.get(i),oq.npc.getItemQueue());
						
						//found
						if(onBodyItem!=null){
							needQueue.get(i).neededItemQueue.addFirst(onBodyItem);
						}
						//not found
						else{
							//find it on the ground
							onGroundItem = oq.npc.findItem(needQueue.get(i),oq.npc.game.getItemQueue());
							
							//found 
							if(onGroundItem!=null){
	    	    				if (!needQueue.get(i).handledBatchInQueue){
	    	    					goal = onGroundItem;
	    	    					JobMove jm = new JobMove(goal.gPosition,-1, -1, null, null,0,0);
	    	    					//JobConsume pendingCJ = new JobConsume(goal.gPosition,100,0,needQueue.get(i),null,0f,0f,null);
	    	    					//JobTake jt = new JobTake(goal.gPosition,-1,-1,null,null,0f,0f,goal,pendingCJ);

	    	    					JobTake jt = new JobTake(goal.gPosition,-1,-1,null,null,0f,0f,goal,null);
	    	    					//Inverse Order;

	    	    					JobAbstractBatch jb = new JobAbstractBatch(needQueue.get(i));
	    	    					//jb.getBatch().addFirst(pendingCJ);
	    	    					jb.getBatch().addFirst(jt);
	    	    					jb.getBatch().addFirst(jm);
	    	    					jobBatchQueue.addFirst(jb);
	    	    					
	    	        				needQueue.get(i).handledBatchInQueue = true;
	    	    				}
							}
							//not found, screw the NPC. Ignore the need.
							else{
								//no item to solve the need. Let's rest (ignore) and see.
								if (!needQueue.get(i).handledBatchInQueue){
									
			    				}
							}
						}
					}
					//NPC know it's on its body
					else{
						onBodyItem = needQueue.get(i).neededItemQueue.first();
	    				//just on NPC body
	    				if (!needQueue.get(i).handledBatchInQueue){
	    					goal = needQueue.get(i).neededItemQueue.first();
	    					goal.gPosition = oq.npc.gPosition;
	        				
	        				JobAbstractBatch jb = new JobAbstractBatch(needQueue.get(i));
	    					jb.getBatch().addFirst( new JobConsume(goal.gPosition,100,0,needQueue.get(i),null,0f,0f,goal));
	    					jobBatchQueue.addFirst(jb);
	        				needQueue.get(i).handledBatchInQueue = true;
	    				}
					}
	    		}
			}
		}
		
    	
	}
	private void processUpdatePersonalAbilities(ObjectRequest oq){
		oq.npc.setSpeed(CharacterUtility.calculateSpeed(oq.npc)*oq.npc.getSpeedBase());
		oq.npc.setBaseEnergyConsumption(CharacterUtility.calculateBaseEC(oq.npc));
	}
	private void processDecideJob(ObjectRequest oq){
		Queue<JobAbstractBatch> jobBatchQueue = oq.npc.getJobBatchQueue();
		
		synchronized(jobBatchQueue){
			oq.npc.getProfession().checkMissions();
		}
	}
	public boolean addRequest(ObjectNPC onpc, int type){
		if(this.npcr_queue.size>=requestQueueMax){
			//Gdx.app.log("ADDR",""+type);
			return false;
		}
		else{
			this.npcr_queue.addLast(new ObjectRequest(onpc,type));
			return true;
		}
	}
	public int getCurrentRequestNumber(){
		return this.npcr_queue.size;
	}
	public void run() { // override Thread's run()
		while (!Thread.currentThread().isInterrupted()) {
			if(this.npcr_queue.size!=0) {
				ObjectRequest oq = this.npcr_queue.removeFirst();
				if(oq!=null){
					switch(oq.requestType){
					case 1:
						processIncreaseNeed(oq);
						break;
					case 2:
						processBodyCycle(oq);
						break;
					case 3:
						processCheckNeed(oq);
						break;
					case 4:
						processUpdatePersonalAbilities(oq);
						break;
					case 5:
						processDecideJob(oq);
						break;
					default:
						break;
					}
				}
			}
		}
		
		
    }
}

class ObjectRequest{
	public ObjectNPC npc;
	public int requestType;
	public ObjectRequest(ObjectNPC npc, int requestType){
		this.requestType = requestType;
		this.npc = npc;
	}
	
}
