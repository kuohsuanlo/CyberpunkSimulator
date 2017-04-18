package com.mygdx.game;

import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Queue;
import com.mygdx.item.ItemAbstract;
import com.mygdx.item.ItemFood;
import com.mygdx.job.*;

import com.mygdx.need.*;

public class ObjectNPC extends ObjectAbstract{
	
	// Inner id for system to quickly points to the index.
	public int id;
	
	/*
	 * Species : Probably human all the time, sometimes cats :p 
	 */
	public static final int HUMAN = 0;
	private int species;
	
	/*
	 * Body Parts : the parts, which are replaceable for the NPCs themselves. 
	 */
	private static int bpNumber = 6;
	private ObjectBodyPart[] bpTraits;
	
	/*
	 * Personal Ability : based on body part traits, chips, and other add-ons to the body parts.
	 */
    private float speed;
    private float speed2;
    private float handReach;
    private float baseEnergyConsumption;
    
    /*
     * Queues : to simulate the human behavior by creating the needs and daily jobs, or jobs driven by needs
     */
    private Queue<ItemAbstract> itemQueue;
    private Queue<NeedAbstract> needQueue;
    private Queue<JobAbstract> jobQueue;
    private JobAbstract cjob;
    private int maxJobNumber = 10;
   
    /*
     * Font : for npc to print their own dialog bubbles, current goal, etc.
     */
    private BitmapFont font;
	
    private Random random = new Random();
    private ObjectMap inMap;
    

    
    public ObjectNPC(int gid, int tid,int species,ObjectMap im) {
    	super();
    	
    	this.id = gid;
    	this.species = species;
    	
    	gPosition.x = random.nextFloat()*Gdx.graphics.getWidth();
    	gPosition.y = random.nextFloat()*Gdx.graphics.getHeight();
    	
    	texture = new Texture("npc/"+tid+".png");
        xOffset = this.texture.getWidth()*0.5f;
        yOffset = this.texture.getHeight()*0.5f;
    	
        itemQueue = new Queue<ItemAbstract>();
    	jobQueue = new Queue<JobAbstract>();
    	needQueue = new Queue<NeedAbstract>();
    	bpTraits = new ObjectBodyPart[bpNumber];
    	
    	font = new BitmapFont(); 
    	
    	inMap = im;
    	
    	//initializing in-game data
    	initNeed();
    	initBodyPartTraits();
    	initItem();
    	
    	updatePersonalAbilities();
    }
    
    public void doAI(){
    	this.printSelfInfo();
    	
    	//increase need
    	this.increaseNeed();
    	this.checkNeed();
    	
    	//refresh queue, pop-out object such as null item (remove by system, used by others, etc)
    	this.refreshQueue();
    	   	
    	//deciding the daily job(by professional)
    	this.decideJob();
    	
    	//doing the job in queue
    	this.doJob();
    	
    	
    }

	private void initBodyPartTraits(){
		for(int i=0;i<bpNumber;i++){
			this.bpTraits[i] = new ObjectBodyPart(i,random.nextFloat(),random.nextFloat(),random.nextFloat());
		}
	}
	private void initItem(){
		this.itemQueue.addLast(new ItemFood(5, null ,0,"free food",1,NeedAbstract.NEED_HUNGER_ID,NeedAbstract.NEED_THIRST_ID,50,50,null));
	}
	private void updatePersonalAbilities(){
		//speed formula
		float speed_tmp=0;
		float baseEC_tmp=0;
		for(int i=0;i<bpNumber;i++){
			speed_tmp+=this.bpTraits[i].traits.dex;
			baseEC_tmp+=(this.bpTraits[i].traits.str+this.bpTraits[i].traits.vit);
		}
		this.speed = speed_tmp;
    	this.speed2 = speed*speed;
    	this.handReach = speed/2;
    	this.baseEnergyConsumption = baseEC_tmp/120;
    	
    	
	}
	
	
    private void initNeed(){
    	needQueue.addLast( (NeedAbstract)new NeedFatigue("fatigue",0,0,0,100,null,null) );
    	needQueue.addLast( (NeedAbstract)new NeedHunger("hunger",0,0,100,100,null,null) );
    	needQueue.addLast( (NeedAbstract)new NeedThirst("thirst",0,0,100,100,null,null) );
    }
    private void increaseNeed(){
    	if(this.species==ObjectNPC.HUMAN){
        	for(int i=0;i<needQueue.size;i++){
        		needQueue.get(i).tickLevel = baseEnergyConsumption;
        		needQueue.get(i).tickNeed();
        	}
    	}
    }
    
    private void checkNeed(){
    	for(int i=0;i<needQueue.size;i++){
    		float c = needQueue.get(i).currentLevel;
    		float m = needQueue.get(i).maxLevel;
    		if(c>=m){
    			if( needQueue.get(i) instanceof NeedFatigue) {
    				if (!needQueue.get(i).handledInQueue){
    					this.jobQueue.addFirst( new JobRest(this.gPosition,m,m-c,0,0,0f,0f));
    					needQueue.get(i).handledInQueue = true;
    				}
    			}
    			else if( needQueue.get(i) instanceof NeedHunger  ||  needQueue.get(i) instanceof NeedThirst) {
    				//no candidates item in queue, start to search
    				if(needQueue.get(i).neededItemQueue.size==0 ){
    					ItemAbstract onBodyItem = this.findItemOnBody(needQueue.get(i));
    					ItemAbstract onGroundItem = this.findItemOnGround(needQueue.get(i));
    					if(onBodyItem !=null){
    	    				//just on NPC body
    						needQueue.get(i).neededItemQueue.addLast(onBodyItem);
    	    				if (!needQueue.get(i).handledInQueue){
    	    					ItemAbstract goal = needQueue.get(i).neededItemQueue.first();
    	    					goal.gPosition = this.gPosition;
    	        				this.jobQueue.addFirst( new JobConsume(goal.gPosition,m,m-c,0,0,0f,0f,goal));
    	        				needQueue.get(i).handledInQueue = true;
    	    				}
    					}
    					else if(onGroundItem!=null){
    						//go to candidates item's location.
    						needQueue.get(i).neededItemQueue.addLast(onGroundItem);
    	    				if (!needQueue.get(i).handledInQueue){
    	    					ItemAbstract goal = needQueue.get(i).neededItemQueue.first();
    	    					this.jobQueue.addLast(new JobMove(goal.gPosition,-1, -1, 0, 0,0,0));
    	    					this.jobQueue.addLast(new JobTake(goal.gPosition,-1,-1,0,0,0,0,goal));
    	        				this.jobQueue.addLast( new JobConsume(goal.gPosition,m,m-c,0,0,0f,0f,goal));
    	        				needQueue.get(i).handledInQueue = true;
    	    				}
    					}
    					else{
    						//no item to solve the need. Let's rest (ignore) and see.
    						if (!needQueue.get(i).handledInQueue){
    	    					
    	    				}
    					}
    				}
    				else{
    					
    				}

    				
    			}
    		}
    	}
    }
    private ItemAbstract findItemOnBody(NeedAbstract na){
    	Queue<ItemAbstract> q = this.itemQueue;
    	if(na instanceof NeedHunger){		
			//searching item for hunger need
    		return findItemForNeed(q,NeedAbstract.NEED_HUNGER_ID);
    	}
    	else if(na instanceof NeedThirst){		
			//searching item for hunger need
    		return findItemForNeed(q,NeedAbstract.NEED_THIRST_ID);
    	}		
		return null;	
    }
    private ItemAbstract findItemOnGround(NeedAbstract na){
		Queue<ItemAbstract> q = this.inMap.item_ground;
    	if(na instanceof NeedHunger){		
			//searching item for hunger need
    		return findItemForNeed(q,NeedAbstract.NEED_HUNGER_ID);
    	}
    	else if(na instanceof NeedThirst){		
			//searching item for hunger need
    		return findItemForNeed(q,NeedAbstract.NEED_THIRST_ID);
    	}		
		return null;	

    }
    private ItemAbstract findItemForNeed(Queue<ItemAbstract> q, int NEED_ID){
    	for(int i=0;i<q.size;i++){
    		if(q.get(i).decreasedNeed_id==NEED_ID){
    			return q.get(i);
    		}
    	}
    	return null;
    }
    private void refreshQueue(){
    	Queue<ItemAbstract> tmpQ;
    	for(int i=0;i<needQueue.size;i++){
			if(needQueue.get(i).neededItemQueue.size!=0 ){
				tmpQ = needQueue.get(i).neededItemQueue;
				for(int j=0;j<tmpQ.size;j++){
					if(tmpQ.get(j)==null  ||  tmpQ.get(j).itemNeedDestroy()){
						tmpQ.removeIndex(j);
					}
				}
			}
    	}
    	for(int i=0;i<itemQueue.size;i++){
    		itemQueue.get(i).itemTimePass();
    		if(itemQueue.get(i).itemNeedDestroy()){
    			itemQueue.removeIndex(i);
    		}
    	}

    	
    }
    private void doJob(){   
    	//get first job(current)
    	cjob = jobQueue.first();

    	//do job
    	if(cjob instanceof JobMove){
    		JobMove mj = (JobMove)cjob;
    		this.walkOneTick(mj);
    	}
    	else if(cjob instanceof JobRest){
    		JobRest rj = (JobRest)cjob;
    		this.rest(rj);
    	}
    	else if(cjob instanceof JobConsume){
    		JobConsume cj = (JobConsume)cjob;
    		this.consumeItem(cj);
    	}
    	else if(cjob instanceof JobTake){
    		JobTake tj = (JobTake) cjob;
    		this.takeItem(tj);
    	}
    	
    	//check progress
    	if(checkJobDone(cjob)){
    		this.jobConsequence(cjob);
			this.jobQueue.removeFirst();
    	}
    }
    private boolean checkJobDone(JobAbstract ja){
    	if(ja instanceof JobMove){
    		if(this.gPosition.dst(ja.position)<=this.handReach){
				return true;
    		}
    	}
    	else if(ja instanceof JobRest){
			if(ja.currentProgress>=ja.maxProgress){
				return true;	
			}
    	}
    	else if(ja instanceof JobConsume){
			if(ja.currentProgress>=ja.maxProgress){
				return true;	
			}
    	}
    	else if(ja instanceof JobTake){
			if(ja.currentProgress>=ja.maxProgress){
				return true;	
			}
    	}
    	return false;
    }
    
    private void jobConsequence(JobAbstract ja){
    	/*
    	 * Consequences of Job itself (handledInQueue checked by behavior itself)
    	 */
		if(ja.decreasedNeed_id>=0  &&  !ja.jobAborted){
    		this.needQueue.get(ja.decreasedNeed_id).addNeed(ja.decreaseNeed_amount*-1);
    		this.needQueue.get(ja.decreasedNeed_id).handledInQueue=false;
    	}
    	
    	if(ja.increasedNeed_id>=0  &&  !ja.jobAborted){
    		this.needQueue.get(ja.increasedNeed_id).addNeed(ja.increaseNeed_amount);
    	}
    	
    	/*
    	 * Reducing the number of used, consumed items.  (handledInQueue checked by item itself)
    	 */
    	if(ja instanceof JobConsume){
    		ItemAbstract Itmp = ((JobConsume) ja).consumedItem;

        	
    		
    		if(!ja.jobAborted){
    			Itmp.stack_number-=1;
    		}
    		this.needQueue.get(Itmp.decreasedNeed_id).handledInQueue=false; 
    	}
    	else if(ja instanceof JobTake){
    		ItemAbstract Itmp = ((JobTake) ja).takenItem;
    		if(!ja.jobAborted){
    			Itmp.stack_number-=1;
    		} 
    		this.produceItem();
    	}
    }
    private void decideJob(){
    	if(this.jobQueue.size==0){
    		float x_d = random.nextFloat()*Gdx.graphics.getWidth();
    		float y_d = random.nextFloat()*Gdx.graphics.getHeight();
        	this.jobQueue.addLast(new JobMove(new Vector2(x_d,y_d),-1, -1, 0, 0,0,0));
    	}
    }
    private void walkOneTick(JobMove mj){
    	this.needQueue.get(NeedAbstract.NEED_FATIGUE_ID).addNeed((this.speed/this.bpTraits.length)*this.baseEnergyConsumption);
    	Vector2 vtmp = new Vector2(mj.position.x - this.gPosition.x, mj.position.y - this.gPosition.y);
    	
    	if(vtmp.len2()>speed2){
        	vtmp.setLength(this.speed);
    	}
    	this.rotation = vtmp.angle();
    	this.gPosition.add(vtmp);
    }

    private void rest(JobRest rj){
    	rj.currentProgress +=(rj.maxProgress/100) ;
    	this.needQueue.get(NeedAbstract.NEED_FATIGUE_ID).addNeed((this.baseEnergyConsumption)*-20);
    }
    private void consumeItem(JobConsume cj){
    	if(cj.consumedItem==null){
    		cj.jobAborted = true;
    		cj.currentProgress = cj.maxProgress;
    		return;
    	}
    	else if(cj.consumedItem.stack_number<=0){
    		cj.jobAborted = true;
    		cj.currentProgress = cj.maxProgress;
    		return;
    	}
    	
    	ItemAbstract Itmp = cj.consumedItem;
    	this.needQueue.get(Itmp.decreasedNeed_id).addNeed((Itmp.decreasedNeed_amount/cj.maxProgress)*-1);
    	this.needQueue.get(Itmp.increasedNeed_id).addNeed(Itmp.increasedNeed_amount/cj.maxProgress);
    	
    	cj.currentProgress+=cj.maxProgress/100;
	
    }
    private void takeItem(JobTake tj){
    	if(tj.takenItem==null){
    		tj.jobAborted = true;
    		tj.currentProgress = tj.maxProgress;
    		return;
    	}
    	else if(tj.takenItem.stack_number<=0){
    		tj.jobAborted = true;
    		tj.currentProgress = tj.maxProgress;
    		return;
    	}

		tj.currentProgress = tj.maxProgress;
    	this.itemQueue.addFirst(tj.takenItem.getTaken(this.determineTakingItemNumber()) );
    	
    }
    private int determineTakingItemNumber(){
    	return random.nextInt(3)+1;
    }
    private void produceItem(){
    	//produce dummy food
		//this.inMap.item_ground.addFirst(new ItemFood(5, this.getRandomLoc(),0,"free food by "+this.id,1,NeedAbstract.NEED_HUNGER_ID,NeedAbstract.NEED_THIRST_ID,50,50,null));
		//this.inMap.item_ground.addFirst(new ItemFood(5, this.getRandomLoc(),0,"free water by "+this.id,1,NeedAbstract.NEED_THIRST_ID,NeedAbstract.NEED_FATIGUE_ID,50,0,null));
    }
	public void renderSelf(SpriteBatch batch) {
		batch.draw(new TextureRegion(this.texture), 
    			this.sPosition.x-this.xOffset, this.gPosition.y-this.yOffset, 
    			this.texture.getWidth()/2, this.texture.getHeight()/2, 
    			this.texture.getWidth(), this.texture.getHeight(), 1, 1, this.rotation, true);
	}
	public void renderFont(SpriteBatch batch) {
		
		//rendering Job
		JobAbstract ja = this.cjob;
		if(ja instanceof JobRest){
			font.draw(batch, "zzz", ja.position.x, ja.position.y+this.texture.getHeight()*0.5f);
		}
		else if(ja instanceof JobConsume){
			font.draw(batch, "csm", ja.position.x,ja.position.y+this.texture.getHeight()*0.5f);
		}	
		else if(ja instanceof JobTake){
			font.draw(batch, "jbt", ja.position.x, ja.position.y+this.texture.getHeight()*0.5f);
		}
		else if(ja instanceof JobMove){
			font.draw(batch, this.id+"", ja.position.x, ja.position.y);
		}
		
		
		if(nearCursor()){
			for(int i=0;i<this.needQueue.size;i++){
				String Stmp = this.needQueue.get(i).displayName+" : "+Math.round(this.needQueue.get(i).currentLevel)+"/"+Math.round(this.needQueue.get(i).maxLevel);
				font.draw(batch, Stmp,this.gPosition.x, this.gPosition.y+this.texture.getHeight()*0.5f*i);
			}	
			float yOffsetByNeed = this.texture.getHeight()*0.5f*(this.needQueue.size);
			font.draw(batch, "--------",this.gPosition.x, this.gPosition.y+yOffsetByNeed);
			yOffsetByNeed = this.texture.getHeight()*0.5f*(this.needQueue.size+1);

			for(int i=0;i<this.itemQueue.size;i++){
				String Stmp = this.itemQueue.get(i).getDisplayName();
				font.draw(batch, Stmp,this.gPosition.x, this.gPosition.y+this.texture.getHeight()*0.5f*i+yOffsetByNeed);
			}	
		}
		

	}
	
	@Override
	public String getDisplayName() {
		// TODO Auto-generated method stub
		return "NPC : "+this.id;
	}

	private Vector2 getRandomLoc(){
		float x = random.nextFloat()*Gdx.graphics.getWidth();
    	float y = random.nextFloat()*Gdx.graphics.getHeight();
    	return new Vector2(x,y);
	}
	private void printSelfInfo(){
    	Gdx.app.log("NPC"+this.id+"_JOB ", this.jobQueue.size+"/"+this.cjob);
    	for(int i=0;i<this.jobQueue.size;i++){
        	Gdx.app.log("NPC"+this.id+"_JOB:"+i, this.jobQueue.get(i).getClass()+"");
    	}
    	for(int i=0;i<this.needQueue.size;i++){
    		Gdx.app.log("NPC"+this.id+"_NEED"+this.needQueue.get(i).id, this.needQueue.get(i).displayName+" : "+this.needQueue.get(i).currentLevel+"/"+this.needQueue.get(i).maxLevel+" InQueue : "+this.needQueue.get(i).handledInQueue+"niq : "+this.needQueue.get(i).neededItemQueue.size);
    	}
    	for(int i=0;i<this.itemQueue.size;i++){
    		Gdx.app.log("NPC"+this.id+"_ITEM"+this.itemQueue.get(i).id,"snum : "+this.itemQueue.get(i).stack_number);
    	}
    	

    	Gdx.app.log("NPC"+this.id+"_BASE ", this.baseEnergyConsumption+"");
	}


	
   
}