package com.mygdx.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Queue;
import com.mygdx.game.MyGdxGame;
import com.mygdx.game.ObjectNPC;
import com.mygdx.item.ItemAbstract;
import com.mygdx.item.ItemFood;
import com.mygdx.job.JobAbstract;
import com.mygdx.job.JobCollect;
import com.mygdx.job.JobConsume;
import com.mygdx.job.JobMove;
import com.mygdx.job.JobRest;
import com.mygdx.job.JobTake;
import com.mygdx.need.NeedAbstract;
import com.mygdx.need.NeedFatigue;
import com.mygdx.need.NeedHunger;
import com.mygdx.need.NeedJob;
import com.mygdx.need.NeedThirst;

public class ThreadNpcAI extends Thread{
	public int requestQueueMax;
	private Queue<ObjectRequest> npcr_queue;
	public ThreadNpcAI(MyGdxGame game){
		this.requestQueueMax = game.getThreadNpcAIQueueMaxNumber();
		npcr_queue = new Queue<ObjectRequest>();
	}
	private void processIncreaseNeed(ObjectRequest oq){

		Queue<NeedAbstract> needQueue = oq.npc.getNeedQueue();
		if(oq.npc.getSpecies()==ObjectNPC.HUMAN){
        	for(int i=0;i<needQueue.size;i++){
        		needQueue.get(i).tickLevel = oq.npc.getBaseEnergyConsumption();
        		needQueue.get(i).tickNeed();
        	}
    	}
	}
	private void processBodyCycle(ObjectRequest oq){

		Queue<NeedAbstract> needQueue = oq.npc.getNeedQueue();
    	boolean allNeedPassed= true;
    	for(int i=0;i<needQueue.size;i++){
    		if(needQueue.get(i).currentLevel>=needQueue.get(i).maxLevel){
    			oq.npc.damageBody(oq.npc.getBaseEnergyConsumption());
    			allNeedPassed = false;
    		}
    	}
    	if(allNeedPassed){
    		oq.npc.recoverBody(oq.npc.getBaseEnergyConsumption()*0.5f);
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
						needQueue.get(i).neededItemQueue.addFirst(onBodyItem);
					}
					//not found
					else{
						//find it on the ground
						onGroundItem = oq.npc.findItemForNeedOnGround(needQueue.get(i));
						
						//found 
						if(onGroundItem!=null){
    	    				if (!needQueue.get(i).handledInQueue){
    	    					goal = onGroundItem;
    	    					JobMove jm = new JobMove(goal.gPosition,-1, -1, 0, 0,0,0);
    	    					JobConsume pendingCJ = new JobConsume(goal.gPosition,m,m-c,0,0,0f,0f,null);
    	    					JobTake jt = new JobTake(goal.gPosition,-1,-1,goal.getDecreasedNeed_id(),0,0f,0f,goal,pendingCJ);

    	    					//Inverse Order;
    	    					jobQueue.addFirst(jt);
    	    					jobQueue.addFirst(pendingCJ);
    	    					jobQueue.addFirst(jm);
    	    					
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
        				jobQueue.addFirst( new JobConsume(goal.gPosition,m,m-c,goal.getDecreasedNeed_id(),0,0f,0f,goal));
        				needQueue.get(i).handledInQueue = true;
    				}
				}
    		}
			else if( needQueue.get(i) instanceof NeedJob){
				if (!needQueue.get(i).handledInQueue){
		    		
					/*
					 * Making the NeedJob into actually JobAbstract such as JobCollect, 
					 * and it will be done in doJob() later. 
					 */
					oq.npc.getJobQueue().addLast( (JobCollect)((NeedJob)needQueue.get(i)).neededJob);
					Gdx.app.log("NJOB","NeededJob to jobQueue");
					needQueue.get(i).handledInQueue = true;
				}
			}
    	}
	}
	private void processUpdatePersonalAbilities(ObjectRequest oq){

		float speed_tmp=0;
		float baseEC_tmp=0;
		for(int i=0;i<oq.npc.getBpNumber();i++){
			speed_tmp+=oq.npc.getBpTraits()[i].traits.dex;
			baseEC_tmp+=(oq.npc.getBpTraits()[i].traits.str+oq.npc.getBpTraits()[i].traits.vit);
		}
		oq.npc.setSpeed(speed_tmp*oq.npc.getSpeedBase());
		oq.npc.setBaseEnergyConsumption(baseEC_tmp*Gdx.graphics.getDeltaTime());
	}
	private void processDecideJob(ObjectRequest oq){
		int jobType = oq.npc.getRandom().nextInt(10);
		if(jobType ==0){
			Queue<JobAbstract> jobQueue = oq.npc.getJobQueue();
	    	if(jobQueue.size==0){
	    		float x_d = oq.npc.getRandom().nextFloat()*Gdx.graphics.getWidth();
	    		float y_d = oq.npc.getRandom().nextFloat()*Gdx.graphics.getHeight();
	        	jobQueue.addLast(new JobMove(new Vector2(x_d,y_d),-1, -1, 0, 0,0,0));
	    	}
		}
		else{
			/*
			 * Dummy job for testing collecting item.
			 * NPC's job makes NPC to collect food. 
			 * 
			 * Dummy job : find 5 food items.
			 */
			Queue<ItemAbstract> ciq = new Queue<ItemAbstract>();
			ciq.addFirst(new ItemAbstract(3,null,0,"",100,0,0,0,0,null));
			JobCollect jc = new JobCollect(oq.npc.gPosition,-1, -1, 0, 0,0,0,ciq);

			/*
			 * If this NPC has a NeedJob in queue. Ignore the mission. 
			 * TODO : This function could be extends as a maximum mission in Queue<> missionQueue
			 */
			for(int i=0;i<oq.npc.getNeedQueue().size;i++){
				if(oq.npc.getNeedQueue().get(i) instanceof NeedJob){
					((NeedJob)oq.npc.getNeedQueue().get(i)).neededJob = jc;
					Gdx.app.log("NJOB","aiq");
					return;
				}
				
			}
			
		}

	}
	public boolean addRequest(ObjectNPC onpc, int type){
		if(this.npcr_queue.size>=requestQueueMax){
			Gdx.app.log("ADDR",""+type);
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
