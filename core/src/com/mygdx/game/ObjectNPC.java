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
import com.mygdx.util.ThreadNpc;

public class ObjectNPC extends ObjectAbstract{
	
	/*
	 * AI recalculating time
	 */
	private float currentTimeRC ;
	private float maxTimeRC = 1.0f;
	private float speedBase = 60f;
	private int expectedLifeInSec = 3600;
	
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
	private float lifeStatus;
	private float maxLifeStatus;
    private float speed;
    private float baseEnergyConsumption;
    
    /*
     * Queues : to simulate the human behavior by creating the needs and daily jobs, or jobs driven by needs
     */
    private Queue<ItemAbstract> itemQueue;
    private Queue<NeedAbstract> needQueue;
    private Queue<JobAbstract> jobQueue;
    private JobAbstract cjob;
   
    /*
     * Font : for npc to print their own dialog bubbles, current goal, etc.
     */
    private BitmapFont font;
	
    private Random random;
    private ObjectMap inMap; 
    
    private MyGdxGame game;
    
    public ObjectNPC(int gid, int tid,int species,ObjectMap im, Random random, MyGdxGame game) {
    	super();
    	
    	this.id = gid;
    	this.species = species;
    	this.random = random;
    	this.currentTimeRC = random.nextFloat()*this.maxTimeRC;
    	this.game = game;
    	
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
    	initPersonalAbilities();
    	
    }
    
    public void doAI(){
    	
    	
    	//body status changes and takes effect
    	this.increaseNeed();
    	this.cycleBody();
    	
    	//reducing the AI calculating burden
    	this.addRC();
    	if(this.needRC()){
    		//this.printSelfInfo();
    		
    		//increase need
        	this.checkNeed();
        	this.updatePersonalAbilities();
        	//deciding the daily job(by professional)
        	this.decideJob();
    	}
    	this.recoverRC();
    	
    	//doing the job in queue
    	this.doJob();
    	//refresh queue, pop-out object such as null item (remove by system, used by others, etc)
    	this.refreshQueue();  
    }
    private void addRC(){
    	this.currentTimeRC+=Gdx.graphics.getDeltaTime();
    }
    private void recoverRC(){
    	if(this.needRC()){
        	this.currentTimeRC=0;
    	}
    }
    private boolean needRC(){
    	//return true;;
    	return this.currentTimeRC>=this.maxTimeRC;
    }

	private void initBodyPartTraits(){
		for(int i=0;i<bpNumber;i++){
			this.bpTraits[i] = new ObjectBodyPart(i,random.nextFloat(),random.nextFloat(),random.nextFloat());
		}
	}
	private void initItem(){
		
	}
	private void initPersonalAbilities(){
		//speed formula
		float speed_tmp=0;
		float baseEC_tmp=0;
		for(int i=0;i<bpNumber;i++){
			speed_tmp+=this.bpTraits[i].traits.dex;
			baseEC_tmp+=(this.bpTraits[i].traits.str+this.bpTraits[i].traits.vit);
		}
		this.speed = speed_tmp*speedBase;
    	this.baseEnergyConsumption = (baseEC_tmp)*(1f/60);
    	
    	this.maxLifeStatus = baseEnergyConsumption*60*expectedLifeInSec;
    	this.lifeStatus = baseEnergyConsumption*60*expectedLifeInSec;
    	
	}
	private void updatePersonalAbilities(){
		float speed_tmp=0;
		float baseEC_tmp=0;
		for(int i=0;i<bpNumber;i++){
			speed_tmp+=this.bpTraits[i].traits.dex;
			baseEC_tmp+=(this.bpTraits[i].traits.str+this.bpTraits[i].traits.vit);
		}
		this.speed = speed_tmp*speedBase;
    	this.baseEnergyConsumption = baseEC_tmp*Gdx.graphics.getDeltaTime();
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
    private void cycleBody(){
    	boolean allNeedPassed= true;
    	for(int i=0;i<this.needQueue.size;i++){
    		if(this.needQueue.get(i).currentLevel>=this.needQueue.get(i).maxLevel){
    			this.damageBody(this.baseEnergyConsumption);
    			allNeedPassed = false;
    		}
    	}
    	if(allNeedPassed){
    		this.recoverBody(this.baseEnergyConsumption*0.5f);
    	}
    }
    private void recoverBody(float amount){
    	if(this.lifeStatus-amount>this.maxLifeStatus){
    		this.lifeStatus=this.maxLifeStatus;
    	}
    	else{
    		this.lifeStatus+=amount;
    	}
    }
    private void damageBody(float amount){
    	if(this.lifeStatus-amount<=0){
    		this.lifeStatus=0;
    	}
    	else{
    		this.lifeStatus-=amount;
    	}
    }
    public boolean isNpcDead(){
    	//return false;
    	return this.lifeStatus<=0;
    }
    private void checkNeed(){
    	boolean useThread = true;
    	if(useThread){
	    	ThreadNpc tnpc = this.game.getThreadNpc();
	    	if(tnpc!=null){
	    		tnpc.addRequest(this);
	    	}
    	}
    	else{
    	
    	for(int i=0;i<needQueue.size;i++){
    		float c = needQueue.get(i).currentLevel;
    		float m = needQueue.get(i).maxLevel;
    		
    		if(c<m) continue; 

			if( needQueue.get(i) instanceof NeedFatigue) {
				if (!needQueue.get(i).handledInQueue){
					jobQueue.addFirst( new JobRest(this.gPosition,m,m-c,0,0,0f,0f));
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
					onBodyItem = this.findItemOnBody(needQueue.get(i));
					
					//found
					if(onBodyItem!=null){
						needQueue.get(i).neededItemQueue.addLast(onBodyItem);
					}
					//not found
					else{
						//find it on the ground
						onGroundItem = this.findItemOnGround(needQueue.get(i));
						
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
    					goal.gPosition = this.gPosition;
        				jobQueue.addFirst( new JobConsume(goal.gPosition,m,m-c,goal.decreasedNeed_id,0,0f,0f,goal));
        				needQueue.get(i).handledInQueue = true;
    				}
				}
    		
			}
		}
    	
    	}
    	
    }
    public ItemAbstract findItemOnBody(NeedAbstract na){
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
    public ItemAbstract findItemOnGround(NeedAbstract na){
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
    	Queue<ItemAbstract> candidates = new Queue<ItemAbstract>();
    	/*
    	 * Linear search, need to be more efficient, might could be done by stochastic search. 
    	 */
    	for(int i=0;i<q.size;i++){
    		if(q.get(i).decreasedNeed_id==NEED_ID){
    			candidates.addFirst(q.get(i));
    		}
    	}
    	if(candidates.size>0){
    		return candidates.get(random.nextInt(candidates.size));
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
    private void getItem(ItemAbstract ia1){
    	for(int i=0;i<itemQueue.size;i++){
    		if(itemQueue.get(i).id ==  ia1.id){
    			//stackable
    			if( itemQueue.get(i).compareItemAbstract(ia1) ){
    				itemQueue.get(i).stack_number+=ia1.stack_number;
    				return;
    			}
    		}
    	}
    	this.itemQueue.addLast(ia1);
    }
    private void doJob(){   
    	//get first job(current)
    	if(jobQueue.size==0){
    		return;
    	}
    	
    	
    	//do job
    	cjob = jobQueue.first();
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
	    	cjob=null;
    	}
    }
    private boolean checkJobDone(JobAbstract ja){

    	if(ja instanceof JobMove){
    		if(this.gPosition.dst(ja.position)<=1){
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

		this.needQueue.get(ja.decreasedNeed_id).handledInQueue=false;
		
    	/*
    	 * Consequences of Job itself (handledInQueue checked by behavior itself)
    	 */
		if(ja.decreasedNeed_id>=0  &&  !ja.jobAborted){
    		this.needQueue.get(ja.decreasedNeed_id).addNeed(ja.decreaseNeed_amount*-1);
    	}
    	
    	if(ja.increasedNeed_id>=0  &&  !ja.jobAborted){
    		this.needQueue.get(ja.increasedNeed_id).addNeed(ja.increaseNeed_amount);
    	}
    	
    	/*
    	 * Reducing the number of used, consumed items.  (handledInQueue checked by item itself)
    	 * And get the destroyed item, such as water into empty bottle.
    	 */
    	if(ja instanceof JobConsume){
    		if(!ja.jobAborted){
        		ItemAbstract Itmp = null;
        		ItemAbstract IDtmp =null;
        		
        		Itmp = ((JobConsume) ja).consumedItem;
        		if(Itmp!=null  &&  Itmp.hasDestroyedItem()){
        			IDtmp = Itmp.getOneDestroyedItem();
        		}
        		
    			if(IDtmp!=null){
        			this.getItem(IDtmp);
    			}
    			Itmp.stack_number-=1;
    		}
    	}

    	/*
    	 * Taking item is an instant behavior, the number of items are reduced when splitting the item stack.
    	 * It is not the NPC's conseqence. So we don't reduce the number here. 
    	 */
    	else if(ja instanceof JobTake){
    		
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
    	this.needQueue.get(NeedAbstract.NEED_FATIGUE_ID).addNeed(this.baseEnergyConsumption);
    	Vector2 vtmp = new Vector2(mj.position.x - this.gPosition.x, mj.position.y - this.gPosition.y);
    	
    	if(vtmp.len()>this.speed*Gdx.graphics.getDeltaTime()){
        	vtmp.setLength(this.speed*Gdx.graphics.getDeltaTime());
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
    	
		ItemAbstract tkItem = tj.takenItem.getTaken(this.determineTakingItemNumber()) ;
    	this.getItem(tkItem);
    	
    	//Filling information for the taken item
    	if(tj.nextPendingJob!=null  && tj.nextPendingJob instanceof JobConsume){
    		((JobConsume)tj.nextPendingJob).consumedItem=tkItem;
    		((JobConsume)tj.nextPendingJob).position=this.gPosition;
    	}
		tj.currentProgress = tj.maxProgress;
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
			String Stmp = "";
			int line =0;
			for(int i=0;i<this.needQueue.size;i++){
				if(this.needQueue.get(i).handledInQueue)
					Stmp+="+";
				else
					Stmp+="-";
				Stmp+= this.needQueue.get(i).displayName+" : "+Math.round(this.needQueue.get(i).currentLevel)+"/"+Math.round(this.needQueue.get(i).maxLevel);
				Stmp+="\n";
				line+=1;
			}	
			for(int i=0;i<this.itemQueue.size;i++){
				Stmp+=this.itemQueue.get(i).getDisplayName();
				Stmp+="\n";
				line+=1;
			}
			Stmp+=Math.round(this.lifeStatus)+"/"+Math.round(this.maxLifeStatus);
			line+=1;
			font.draw(batch, Stmp,this.gPosition.x, this.gPosition.y+this.texture.getHeight()*0.5f*line);

		}
		

	}
	
	public String getDisplayName() {
		// TODO Auto-generated method stub
		return "NPC : "+this.id;
	}
	public Queue<NeedAbstract> getNeedQueue(){
		return this.needQueue;
	}

	public Queue<JobAbstract> getJobQueue(){
		return this.jobQueue;
	}

	private Vector2 getRandomLoc(){
		float x = random.nextFloat()*Gdx.graphics.getWidth();
    	float y = random.nextFloat()*Gdx.graphics.getHeight();
    	return new Vector2(x,y);
	}
	private void printSelfInfo(){
    	Gdx.app.log("NPC"+this.id+"_JOB ", this.jobQueue.size+"/"+this.cjob);
    	for(int i=0;i<this.jobQueue.size;i++){
    		if(this.jobQueue.get(i) instanceof JobConsume){
    			JobConsume jc = (JobConsume)this.jobQueue.get(i);
    			if(jc.consumedItem==null)
    				Gdx.app.log("NPC"+this.id+"_JOB:"+i, "CONSUME NULL");
    			else
    				Gdx.app.log("NPC"+this.id+"_JOB:"+i, "CONSUME "+jc.consumedItem.getDisplayName()+""+this.jobQueue.get(i));
    		}
    		else{
    			Gdx.app.log("NPC"+this.id+"_JOB:"+i, this.jobQueue.get(i).getClass()+""+this.jobQueue.get(i));
    		}
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