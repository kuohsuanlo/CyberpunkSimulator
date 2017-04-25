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
import com.mygdx.job.*;
import com.mygdx.need.*;
import com.mygdx.util.CharacterUtility;
import com.mygdx.util.CoorUtility;
import com.mygdx.util.ItemUtility;
import com.mygdx.util.ThreadNpcAI;

public class ObjectNPC extends ObjectAbstract{
	
	/*
	 * AI recalculating time
	 */
	private float currentTimeRC ;
	private float maxTimeRC = 1f;

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
	private int bpNumber = 6;
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
	public int jobType;
	
    private Queue<ItemAbstract> itemQueue;
    private Queue<NeedAbstract> needQueue;
    private Queue<JobAbstractBatch> jobBatchQueue;
    private JobAbstract cjob;
   
    /*
     * Font : for npc to print their own dialog bubbles, current goal, etc.
     */
    private BitmapFont font;
	
    private Random random;
    
    public MyGdxGame game;
    
    public ObjectNPC(int gid, int tid,int species, Random random, MyGdxGame game) {
    	super();
    	
    	this.id = gid;
    	this.setSpecies(species);
    	this.setRandom(random);
    	this.currentTimeRC = random.nextFloat()*this.maxTimeRC;

    	this.game = game;
    	this.jobType = this.id%3;
    	
    	gPosition.x = random.nextFloat()*Gdx.graphics.getWidth();
    	gPosition.y = random.nextFloat()*Gdx.graphics.getHeight();
    	
    	texture = new Texture("npc/"+tid+".png");
        xOffset = this.texture.getWidth()*0.5f;
        yOffset = this.texture.getHeight()*0.5f;
    	
        itemQueue = new Queue<ItemAbstract>();
        jobBatchQueue = new Queue<JobAbstractBatch>();
    	needQueue = new Queue<NeedAbstract>();
    	bpTraits = new ObjectBodyPart[bpNumber];
    	
    	font = new BitmapFont(); 
    	
    	
    	//initializing in-game data
    	initBodyPartTraits();
    	initItem();
    	initNeed();
    	initPersonalAbilities();
    	
    }
    
    public void doAI(){
    	ThreadNpcAI tnpc = this.game.getThreadNpc();
    	
    	//body status changes and takes effect
    
    	
    	//reducing the AI calculating burden
    	
    	this.increaseNeed(tnpc);//1
        this.cycleBody(tnpc);//2
    	
    	
    	
    	this.tickRC();
    	if(this.needRC()){
    		//this.printSelfInfo();
    		//increase need
        	this.checkNeed(tnpc);//3
        	this.updatePersonalAbilities(tnpc);//4
        	//deciding the daily job(by professional)
        	this.decideJob(tnpc);//5
    	}
    	
    	
    	//doing the job in queue
    	this.doJob();
    	//refresh queue, pop-out object such as null item (remove by system, used by others, etc)
    	this.refreshQueue();
    }
    

    
    private void tickRC(){
    	this.currentTimeRC+=Gdx.graphics.getDeltaTime();
    }
    private boolean needRC(){
    	if(this.currentTimeRC>=this.maxTimeRC){
    		this.currentTimeRC-=maxTimeRC;
    		return true;
    	}
    	return false;
    }

	private void initBodyPartTraits(){
		for(int i=0;i<getBpNumber();i++){
			this.getBpTraits()[i] = new ObjectBodyPart(i,getRandom().nextFloat(),getRandom().nextFloat(),getRandom().nextFloat());
		}
	}
	private void initItem(){
		
	}
	private void initPersonalAbilities(){
		this.setSpeed(CharacterUtility.calculateSpeed(this)*this.getSpeedBase());
		this.setBaseEnergyConsumption(CharacterUtility.calculateBaseEC(this));
    	
    	this.maxLifeStatus = getBaseEnergyConsumption()*expectedLifeInSec;
    	this.lifeStatus = getBaseEnergyConsumption()*expectedLifeInSec;
    	
	}
    private void initNeed(){
    	needQueue.addLast( (NeedAbstract)new NeedFatigue("fatigue",0,0,random.nextInt(Math.round(getNeedMax(NeedAbstract.NEED_FATIGUE_ID))),getNeedMax(NeedAbstract.NEED_FATIGUE_ID),null,null) );
    	needQueue.addLast( (NeedAbstract)new NeedHunger("hunger",0,0,getNeedMax(NeedAbstract.NEED_HUNGER_ID),getNeedMax(NeedAbstract.NEED_HUNGER_ID),null,null) );
    	needQueue.addLast( (NeedAbstract)new NeedThirst("thirst",0,0,getNeedMax(NeedAbstract.NEED_THIRST_ID),getNeedMax(NeedAbstract.NEED_THIRST_ID),null,null) );
    }
	private float getNeedMax(int type){
		return CharacterUtility.calculateNeedMax(this, type);
	}
    private void increaseNeed(ThreadNpcAI tnpc){
    	if(tnpc!=null){
    		tnpc.addRequest(this,1);
    	}
    }
    private void cycleBody(ThreadNpcAI tnpc){
    	if(tnpc!=null){
    		tnpc.addRequest(this,2);
    	}
    }
    private void checkNeed(ThreadNpcAI tnpc){
    	if(tnpc!=null){
    		tnpc.addRequest(this,3);
    	}
    }

	private void updatePersonalAbilities(ThreadNpcAI tnpc){
    	if(tnpc!=null){
    		tnpc.addRequest(this,4);
    	}	
	}
    private void decideJob(ThreadNpcAI tnpc){
    	if(tnpc!=null){
    		tnpc.addRequest(this,5);
    	}	
    }
    private void doJob(){

    	if(jobBatchQueue.size==0){
    		return;
    	}
    	/*
    	 * Sync jobBatchQueue, because the other thread might pop the other batch, making the last line of 
    	 * code popping the wrong job.
    	 */

		
		/*
		 * Do the first not-aborted and undone job. 
		 */
    	synchronized(jobBatchQueue){
    		this.cjob = jobBatchQueue.first().getFirstDoableJob();
        	
        	if(cjob!=null){
        		if(this.cjob instanceof JobMove){
            		JobMove mj = (JobMove)this.cjob;
            		this.walkOneTick(mj);
            	}
            	else if(this.cjob instanceof JobRest){
            		JobRest rj = (JobRest)this.cjob;
            		this.rest(rj);
            	}
            	else if(this.cjob instanceof JobConsume){
            		JobConsume cj = (JobConsume)this.cjob;
            		this.consumeItem(cj);
            	}
            	else if(this.cjob instanceof JobTake){
            		JobTake tj = (JobTake) this.cjob;
            		this.takeItem(tj);
            	}
            	else if(this.cjob instanceof JobDrop){
            		JobDrop tj = (JobDrop) this.cjob;
            		this.dropItem(tj);
            	}
            	else if(this.cjob instanceof JobProduce){
            		JobProduce pj = (JobProduce) this.cjob;
            		this.produceItem(pj);
            	}
            	//check progress
            	if(this.checkJobDone(this.cjob)){
            		this.jobConsequence(this.cjob);
            		this.cjob.setJobDone(true);
            	}
        	}
        	
        	/*
        	 * The consequences of a job batch, probably like the salary earned after picking 5 trash on road.
        	 */
        	if(jobBatchQueue.first().isJobBatchDone()  ||  jobBatchQueue.first().isJobBatchAborted()){
        		this.jobBatchConsequence(jobBatchQueue.first());
        		jobBatchQueue.removeFirst();
        	}
    	
    	
    	}
		
    }
    private void jobBatchConsequence(JobAbstractBatch jb){
    	/*
    	 * Consequences of Job itself (handledInQueue checked by behavior itself)
    	 */
    	if(jb.getdrivenNeed()!=null){
    		if(!jb.isJobBatchAborted()){
        		/*
        		 * Get job batch's consequence 
        		 */
        	}
    		jb.getdrivenNeed().handledBatchInQueue=false;
    	}
    }
    private void jobConsequence(JobAbstract ja){
    	/*
    	 * Consequences of Job itself (handledInQueue checked by behavior itself)
    	 */
    	if(ja.isJobAborted()) return;
    	
    	if(ja.getDecreasedNeed()!=null){
        		ja.getDecreasedNeed().addNeed(ja.getDecreaseNeed_amount()*-1);
    	}
    	/*
    	 * Side-effect of the item (not the purpose of enqueue) 
    	 */
    	if(ja.getIncreasedNeed()!=null){
        		ja.getIncreasedNeed().addNeed(ja.getIncreaseNeed_amount());
    	}
    	/*
    	 * Reducing the number of used, consumed items.  (handledInQueue checked by item itself)
    	 * And get the destroyed item, such as water into empty bottle.
    	 */
    	if(ja instanceof JobConsume){
		
    		ItemAbstract Itmp = null;
    		ItemAbstract IDtmp =null;
    		Itmp = ((JobConsume) ja).consumedItem;
    		if(Itmp!=null  &&  Itmp.hasDestroyedItem()){
    			IDtmp = Itmp.getOneDestroyedItem();
    		}
			if(IDtmp!=null){
    			this.obtainItem(IDtmp);
			}
			Itmp.setStack_number(Itmp.getStack_number() - 1);
		
    	}
    	/*
    	 * Taking item is an instant behavior, the number of items are reduced when splitting the item stack.
    	 * It is not the NPC's conseqence. So we don't reduce the number here. 
    	 */
    	else if(ja instanceof JobTake){
    		JobTake tj = (JobTake) ja;
    		int valid_number = tj.takenItem.getValidNumber(this.determineItemNumber()) ;;
        	this.obtainItem(tj.takenItem.getDup(valid_number));
        	tj.takenItem.setStack_number(tj.takenItem.getStack_number()-valid_number);
    	}
    	else if(ja instanceof JobDrop){
    		JobDrop dj = (JobDrop) ja;
    		int valid_number = dj.droppedItem.getValidNumber(this.determineItemNumber()) ;
    		this.loseItem(dj.droppedItem.getDup(valid_number));
    		this.game.addItem(dj.droppedItem.getDup(valid_number),this.gPosition);
    	}
    	else if(ja instanceof JobProduce){
    		JobProduce pj = (JobProduce) ja;
        	//Producing process done, generate items.
        	if(pj.getCurrentProgress()>=pj.getMaxProgress()){
        		for(int i=0;i<pj.recipe.producedItemQueue.size;i++){
            		this.obtainItem(pj.recipe.producedItemQueue.get(i));
            	}
        		for(int i=0;i<pj.recipe.usedItemQueue.size;i++){
            		this.loseItem(pj.recipe.usedItemQueue.get(i));
            	}
        	}
    	}
    }
    
    public boolean checkJobDone(JobAbstract ja){
    	if(ja instanceof JobMove){
    		if(this.gPosition.dst(ja.getPosition())<=1){
				return true;
    		}
    	}
    	else{
			if(ja.getCurrentProgress()>=ja.getMaxProgress()){
				return true;	
			}
    	}
    	
    	return false;
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
    public void recoverBody(float amount){
    	if(this.lifeStatus-amount>this.maxLifeStatus){
    		this.lifeStatus=this.maxLifeStatus;
    	}
    	else{
    		this.lifeStatus+=amount;
    	}
    }
    public void damageBody(float amount){
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

    
	/*
	 * Linear search, need to be more efficient, might could be done by stochastic search. 
	 * TODO : random selection, needed to be planted with best choice search
	 */
    public ItemAbstract findItemOnGround(NeedAbstract na){
    	Queue<ItemAbstract> q = this.game.getItem_queue();
    	Queue<ItemAbstract> tmpQ;
    	//if(q.size==0) return null;
    	synchronized(q){
    	 	tmpQ = ItemUtility.findItemWithNeed(q,na.id);
        	if(tmpQ!=null  &&  tmpQ.size>0){
        		return tmpQ.get(random.nextInt(tmpQ.size));
        	}
    		return null;
    	}
    }
    public ItemAbstract findItemOnBody(NeedAbstract na){
    	Queue<ItemAbstract> q = this.itemQueue;
    	Queue<ItemAbstract> tmpQ;
    	//if(q.size==0) return null;
    	synchronized(q){
    		tmpQ = ItemUtility.findItemWithNeed(q,na.id);
        	if(tmpQ!=null  &&  tmpQ.size>0){
        		return tmpQ.get(random.nextInt(tmpQ.size));
        	}
    		return null;
    	}
    		
    }
    public ItemAbstract findItemOnGround(int iid){
    	Queue<ItemAbstract> q = this.game.getItem_queue();
    	ItemAbstract ans;
    	//if(q.size==0) return null;
    	synchronized(q){
    		Queue<ItemAbstract> qtmp = ItemUtility.findItemWithID(q,iid);
    		if(qtmp.size>0){
    			ans = qtmp.get(random.nextInt(qtmp.size));
    			return ans;
    		}
        	return null;
    	}
    	
    }
    public ItemAbstract findItemOnBody(int iid){
    	Queue<ItemAbstract> q = this.getItemQueue();
    	ItemAbstract ans;
    	//if(q.size==0) return null;
    	synchronized(q){
    		Queue<ItemAbstract> qtmp = ItemUtility.findItemWithID(q,iid);
    		if(qtmp.size>0){
    			ans = qtmp.get(random.nextInt(qtmp.size));
        		//TODO : random selection, needed to be planted with best choice search
    			return ans;
    		}
        	return null;
    	}
    }
    

    public boolean hasItem(ItemAbstract ia){
    	for(int i=0;i<this.itemQueue.size;i++){
    		if( itemQueue.get(i).compareItemAbstract(ia) ){
    			if(this.itemQueue.get(i).getStack_number()>=ia.getStack_number()){
        			return true;
        		}
    		}
    		
    	}
    	return false;
    }
    private void obtainItem(ItemAbstract ia1){
    	for(int i=0;i<itemQueue.size;i++){
    		if(itemQueue.get(i).getId() ==  ia1.getId()){
    			//stackable
    			if( itemQueue.get(i).compareItemAbstract(ia1) ){
    				itemQueue.get(i).setStack_number(itemQueue.get(i).getStack_number() + ia1.getStack_number());
    				return;
    			}
    		}
    	}
    	this.itemQueue.addLast(ia1);
    }
    private void loseItem(ItemAbstract ia1){
    	for(int i=0;i<itemQueue.size;i++){
    		if(itemQueue.get(i).getId() ==  ia1.getId()){
    			//stackable
    			if( itemQueue.get(i).compareItemAbstract(ia1) ){
    				itemQueue.get(i).setStack_number(itemQueue.get(i).getStack_number() - ia1.getStack_number());

    		    	if(itemQueue.get(i).getStack_number()<=0){
    		    		itemQueue.removeIndex(i);
    		    	}
    				return;
    			}
    		}
    	}
    }
    

    public void walkOneTick(JobMove mj){
    	this.needQueue.get(NeedAbstract.NEED_FATIGUE_ID).addNeed(this.getBaseEnergyConsumption());
    	Vector2 vtmp = new Vector2(mj.getPosition().x - this.gPosition.x, mj.getPosition().y - this.gPosition.y);
    	
    	if(vtmp.len()>this.getSpeed()*Gdx.graphics.getDeltaTime()){
        	vtmp.setLength(this.getSpeed()*Gdx.graphics.getDeltaTime());
    	}
    	this.rotation = vtmp.angle();
    	this.gPosition.add(vtmp);
    }

    public void rest(JobRest rj){
    	rj.setCurrentProgress(rj.getCurrentProgress() + this.game.getLastTimeElapsed()*100) ;
    	this.needQueue.get(NeedAbstract.NEED_FATIGUE_ID).addNeed((this.getBaseEnergyConsumption())*this.game.getLastTimeElapsed()*-100);
    }
    public void consumeItem(JobConsume cj){
    	if(cj.consumedItem==null){
    		cj.setJobAborted(true);
    		cj.setCurrentProgress(cj.getMaxProgress());

    		return;
    	}
    	else if(cj.consumedItem.getStack_number()<=0){
    		cj.setJobAborted(true);
    		cj.setCurrentProgress(cj.getMaxProgress());

    		return;
    	}
    	
    	ItemAbstract Itmp = cj.consumedItem;
    	if(cj.getDecreasedNeed()!=null){
    		cj.getDecreasedNeed().addNeed((Itmp.getDecreasedNeed_amount()/cj.getMaxProgress())*this.game.getLastTimeElapsed()*-100);
    	}
    	if(cj.getIncreasedNeed()!=null){
    		cj.getIncreasedNeed().addNeed(Itmp.getIncreasedNeed_amount()/cj.getMaxProgress()*this.game.getLastTimeElapsed()*100);
    	}
    	cj.setCurrentProgress(cj.getCurrentProgress() + this.game.getLastTimeElapsed()*100);
	
    }
    public void produceItem(JobProduce pj){
    	if(pj.recipe.usedItemQueue==null  ||  pj.recipe.producedItemQueue==null){
    		pj.setJobAborted(true);
    		pj.setCurrentProgress(pj.getMaxProgress());
    		return;
    	}
    	/*
    	 * Start producing item. If one of process failed. Abort.
    	 * It use two queue because the product and material might be multiple items.
    	 * 
    	 * We also need recipe here.
    	 * If recipe ==null, abort, because it might mean that NPC has no knowledge of making it.
    	 * This fits the game. NPC.getItemRecipe() == null;
    	 */
    	
    	/*Let's assume that the NPC always has the recipe's used items.
    	 * TODO : Check the npc's itemQueue contains all the usedItems
    	 */
    	
    	//Producing Items
    	Queue<ItemAbstract> iaq = pj.recipe.usedItemQueue;
    	for(int i=0;i<iaq.size;i++){
    		if(!this.hasItem(iaq.get(i))){
    			pj.setJobAborted(true);
        		pj.setCurrentProgress(pj.getMaxProgress());
        		return;
    		}
    	}
    	pj.setCurrentProgress(pj.getCurrentProgress() + this.game.getLastTimeElapsed()*100);
    	

    	
    }
    public void takeItem(JobTake tj){
    	if(tj.takenItem==null){
    		tj.setJobAborted(true);
    		tj.setCurrentProgress(tj.getMaxProgress());
    		return;
    	}
    	else if(tj.takenItem.getStack_number()<=0){
    		tj.setJobAborted(true);
    		tj.setCurrentProgress(tj.getMaxProgress());
    		return;
    	}
    	

		tj.setCurrentProgress(tj.getMaxProgress());
    }
    public void dropItem(JobDrop dj){
    	if(dj.droppedItem==null){
    		dj.setJobAborted(true);
    		dj.setCurrentProgress(dj.getMaxProgress());
    		return;
    	}
    	else if(dj.droppedItem.getStack_number()<=0){
    		dj.setJobAborted(true);
    		dj.setCurrentProgress(dj.getMaxProgress());
    		return;
    	}
    	else if(this.hasItem(dj.droppedItem)){
    		dj.setCurrentProgress(dj.getMaxProgress());
    		return;
    	}
    	else{
    		dj.setJobAborted(true);
    		dj.setCurrentProgress(dj.getMaxProgress());
    		return;
    	}
		
    }
 
    private int determineItemNumber(){
    	return getRandom().nextInt(5)+1;
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
		String prog ="";
		if(ja!=null){
			prog = Math.round(ja.getCurrentProgress())+"/"+Math.round(ja.getMaxProgress());
			if(ja instanceof JobRest){
				font.draw(batch, "zzz"+prog, ja.getPosition().x, ja.getPosition().y+this.texture.getHeight()*0.5f);
			}
			else if(ja instanceof JobConsume){
				font.draw(batch, "csm"+prog, ja.getPosition().x,ja.getPosition().y+this.texture.getHeight()*0.5f);
			}	
			else if(ja instanceof JobTake){
				font.draw(batch, "jbt"+prog, ja.getPosition().x, ja.getPosition().y+this.texture.getHeight()*0.5f);
			}
			else if(ja instanceof JobTake){
				font.draw(batch, "jbd"+prog, ja.getPosition().x, ja.getPosition().y+this.texture.getHeight()*0.5f);
			}
			else if(ja instanceof JobProduce){
				font.draw(batch, "prd"+prog, ja.getPosition().x, ja.getPosition().y+this.texture.getHeight()*0.5f);
			}
			else if(ja instanceof JobMove){
				font.draw(batch, this.id+"", ja.getPosition().x, ja.getPosition().y);
			}
		}
		else{
			font.draw(batch, "---", this.gPosition.x, this.gPosition.y+this.texture.getHeight()*0.5f);
		}
		

	
	
		
		if(CoorUtility.isNearCursor(this.sPosition)){
			String Stmp = "";
			int line =0;
			for(int i=0;i<this.needQueue.size;i++){
				if(this.needQueue.get(i).handledBatchInQueue)
					Stmp+="+";
				else
					Stmp+="-";
				Stmp+= this.needQueue.get(i).getDisplayName()+" : "+Math.round(this.needQueue.get(i).currentLevel)+"/"+Math.round(this.needQueue.get(i).maxLevel);
				Stmp+="\n";
				line+=1;
			}	

			Stmp+="------------\n";
			line+=1;
			
			for(int i=0;i<this.itemQueue.size;i++){
				Stmp+=this.itemQueue.get(i).getDisplayName();
				Stmp+="\n";
				line+=1;
			}
			Stmp+="------------\n";
			line+=1;
			
			for(int i=0;i<this.jobBatchQueue.size;i++){
				for(int j=0;j<jobBatchQueue.get(i).getBatch().size;j++){
					Stmp+=(i+" : "+this.jobBatchQueue.get(i).getBatch().get(j).getClass().getSimpleName());
					
					Stmp+="\n";
					line+=1;
				}
				
			}
			Stmp+="------------\n";
			line+=1;
			
			Stmp+=Math.round(this.lifeStatus)+"/"+Math.round(this.maxLifeStatus);
			line+=1;
			font.draw(batch, Stmp,this.gPosition.x, this.gPosition.y+this.texture.getHeight()*0.5f*line);

		}
		

	}
	private void printSelfInfo(){
    	Gdx.app.log("NPC"+this.id+"_JOB ","---------------");
    	Gdx.app.log("NPC"+this.id+"_JOB ", this.jobBatchQueue.size+"/"+this.cjob);
    	for(int q=0;q<this.jobBatchQueue.size;q++){
    		JobAbstractBatch jb = this.jobBatchQueue.get(q);
        	for(int i=0;i<jb.getBatch().size;i++){
        		if(jb.getBatch().get(i) instanceof JobConsume){
        			JobConsume jc = (JobConsume)jb.getBatch().get(i);
        			if(jc.consumedItem==null)
        				Gdx.app.log("NPC"+this.id+"_JOB:"+i, "CONSUME NULL");
        			else
        				Gdx.app.log("NPC"+this.id+"_JOB:"+i, "CONSUME "+jc.consumedItem.getDisplayName()+""+jb.getBatch().get(i));
        		}
        		else{
        			Gdx.app.log("NPC"+this.id+"_JOB:"+i, jb.getBatch().get(i).getClass()+""+jb.getBatch().get(i));
        		}
        	}
    	}

    	/*
    	for(int i=0;i<this.needQueue.size;i++){
    		Gdx.app.log("NPC"+this.id+"_NEED"+this.needQueue.get(i).id, this.needQueue.get(i).getDisplayName()+" : "+this.needQueue.get(i).currentLevel+"/"+this.needQueue.get(i).maxLevel+" InQueue : "+this.needQueue.get(i).handledBatchInQueue+"nitem qsize : "+this.needQueue.get(i).neededItemQueue.size);
    	}
    	*/
    	for(int i=0;i<this.itemQueue.size;i++){
    		Gdx.app.log("NPC"+this.id+"_ITEM"+this.itemQueue.get(i).getId(),"snum : "+this.itemQueue.get(i).getStack_number());
    	}
    	

    	Gdx.app.log("NPC"+this.id+"_BASE ", this.getBaseEnergyConsumption()+"");
    	
	}

	public int getBpNumber() {
		return bpNumber;
	}

	public void setBpNumber(int bpNumber) {
		this.bpNumber = bpNumber;
	}

	public ObjectBodyPart[] getBpTraits() {
		return bpTraits;
	}

	public void setBpTraits(ObjectBodyPart[] bpTraits) {
		this.bpTraits = bpTraits;
	}

	public float getSpeed() {
		return speed;
	}

	public void setSpeed(float speed) {
		this.speed = speed;
	}

	public float getBaseEnergyConsumption() {
		return baseEnergyConsumption;
	}

	public void setBaseEnergyConsumption(float baseEnergyConsumption) {
		this.baseEnergyConsumption = baseEnergyConsumption;
	}
	

	public Random getRandom() {
		return random;
	}

	public void setRandom(Random random) {
		this.random = random;
	}

	public String getDisplayName() {
		return "NPC : "+this.id;
	}
	public Queue<NeedAbstract> getNeedQueue(){
		return this.needQueue;
	}

	public Queue<JobAbstractBatch> getJobBatchQueue(){
		return this.jobBatchQueue;
	}

	public Queue<ItemAbstract> getItemQueue(){
		return this.itemQueue;
	}
	public float getSpeedBase(){
		return this.speedBase;
	}

	public int getSpecies() {
		return species;
	}

	public void setSpecies(int species) {
		this.species = species;
	}
	
   
}