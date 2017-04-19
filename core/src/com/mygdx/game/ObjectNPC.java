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
import com.mygdx.util.ThreadNpcAI;

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
    	this.setSpecies(species);
    	this.setRandom(random);
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
    	initBodyPartTraits();
    	initItem();
    	initNeed();
    	initPersonalAbilities();
    	
    }
    
    public void doAI(){
    	
    	ThreadNpcAI tnpc = this.game.getThreadNpc();
    	
    	//body status changes and takes effect
    	this.increaseNeed(tnpc);//1
    	this.cycleBody(tnpc);//2
    	
    	//reducing the AI calculating burden
    	this.addRC();
    	if(this.needRC()){
    		//increase need
        	this.checkNeed(tnpc);//3
        	this.updatePersonalAbilities(tnpc);//4
        	//deciding the daily job(by professional)
        	this.decideJob(tnpc);//5
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
		for(int i=0;i<getBpNumber();i++){
			this.getBpTraits()[i] = new ObjectBodyPart(i,getRandom().nextFloat(),getRandom().nextFloat(),getRandom().nextFloat());
		}
	}
	private void initItem(){
		
	}
	private void initPersonalAbilities(){
		//speed formula
		float speed_tmp=0;
		float baseEC_tmp=0;
		for(int i=0;i<getBpNumber();i++){
			speed_tmp+=this.getBpTraits()[i].traits.dex;
			baseEC_tmp+=(this.getBpTraits()[i].traits.str+this.getBpTraits()[i].traits.vit);
		}
		this.setSpeed(speed_tmp*speedBase);
    	this.setBaseEnergyConsumption((baseEC_tmp)*(1f/60));
    	
    	this.maxLifeStatus = getBaseEnergyConsumption()*60*expectedLifeInSec;
    	this.lifeStatus = getBaseEnergyConsumption()*60*expectedLifeInSec;
    	
	}
    private void initNeed(){
    	needQueue.addLast( (NeedAbstract)new NeedFatigue("fatigue",0,0,random.nextInt(100),getNeedMax(NeedAbstract.NEED_FATIGUE_ID),null,null) );
    	needQueue.addLast( (NeedAbstract)new NeedHunger("hunger",0,0,random.nextInt(100),getNeedMax(NeedAbstract.NEED_HUNGER_ID),null,null) );
    	needQueue.addLast( (NeedAbstract)new NeedThirst("thirst",0,0,random.nextInt(100),getNeedMax(NeedAbstract.NEED_THIRST_ID),null,null) );
    }
	private float getNeedMax(int type){
		float base_tmp=0;
		switch(type){
			case(NeedAbstract.NEED_FATIGUE_ID):
				for(int i=0;i<getBpNumber();i++){
					base_tmp+=this.getBpTraits()[i].traits.vit; //0-6
				}
				return base_tmp*100f*(0.5f+0.5f*random.nextFloat());
			case(NeedAbstract.NEED_HUNGER_ID):
				for(int i=0;i<getBpNumber();i++){
					base_tmp+=this.getBpTraits()[i].traits.vit; //0-6
				}
				return base_tmp*100f*(0.5f+0.5f*random.nextFloat());
			case(NeedAbstract.NEED_THIRST_ID):
				for(int i=0;i<getBpNumber();i++){
					base_tmp+=this.getBpTraits()[i].traits.vit; //0-6
				}
				return base_tmp*100f*(0.5f+0.5f*random.nextFloat());
			default:
				return 300;
		}
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
    	//get first job(current)
    	if(jobQueue.size==0){
    		return;
    	}
    	
    	//do job
    	this.cjob = jobQueue.first();
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
    	
    	//check progress
    	if(this.checkJobDone(this.cjob)){
    		this.jobConsequence(this.cjob);
    		jobQueue.removeFirst();
    		this.cjob=null;
    	}
    	
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
    		if(q.get(i).getDecreasedNeed_id()==NEED_ID){
    			candidates.addFirst(q.get(i));
    		}
    	}
    	if(candidates.size>0){
    		return candidates.get(getRandom().nextInt(candidates.size));
    	}
    	return null;
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
    public boolean checkJobDone(JobAbstract ja){

    	if(ja instanceof JobMove){
    		if(this.gPosition.dst(ja.getPosition())<=1){
				return true;
    		}
    	}
    	else if(ja instanceof JobRest){
			if(ja.getCurrentProgress()>=ja.getMaxProgress()){
				return true;	
			}
    	}
    	else if(ja instanceof JobConsume){
			if(ja.getCurrentProgress()>=ja.getMaxProgress()){
				return true;	
			}
    	}
    	else if(ja instanceof JobTake){
			if(ja.getCurrentProgress()>=ja.getMaxProgress()){
				return true;	
			}
    	}
    	
    	return false;
    }
    
    public void jobConsequence(JobAbstract ja){

		this.needQueue.get(ja.getDecreasedNeed_id()).handledInQueue=false;
		
    	/*
    	 * Consequences of Job itself (handledInQueue checked by behavior itself)
    	 */
		if(ja.getDecreasedNeed_id()>=0  &&  !ja.isJobAborted()){
    		this.needQueue.get(ja.getDecreasedNeed_id()).addNeed(ja.getDecreaseNeed_amount()*-1);
    	}
    	
    	if(ja.getIncreasedNeed_id()>=0  &&  !ja.isJobAborted()){
    		this.needQueue.get(ja.getIncreasedNeed_id()).addNeed(ja.getIncreaseNeed_amount());
    	}
    	
    	/*
    	 * Reducing the number of used, consumed items.  (handledInQueue checked by item itself)
    	 * And get the destroyed item, such as water into empty bottle.
    	 */
    	if(ja instanceof JobConsume){
    		if(!ja.isJobAborted()){
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
    	}

    	/*
    	 * Taking item is an instant behavior, the number of items are reduced when splitting the item stack.
    	 * It is not the NPC's conseqence. So we don't reduce the number here. 
    	 */
    	else if(ja instanceof JobTake){
    		
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
    	rj.setCurrentProgress(rj.getCurrentProgress() + (rj.getMaxProgress()/100)) ;
    	this.needQueue.get(NeedAbstract.NEED_FATIGUE_ID).addNeed((this.getBaseEnergyConsumption())*-20);
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
    	this.needQueue.get(Itmp.getDecreasedNeed_id()).addNeed((Itmp.getDecreasedNeed_amount()/cj.getMaxProgress())*-1);
    	this.needQueue.get(Itmp.getIncreasedNeed_id()).addNeed(Itmp.getIncreasedNeed_amount()/cj.getMaxProgress());
    	
    	cj.setCurrentProgress(cj.getCurrentProgress() + cj.getMaxProgress()/100);
	
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
    	
		ItemAbstract tkItem = tj.takenItem.getTaken(this.determineTakingItemNumber()) ;
    	this.obtainItem(tkItem);
    	
    	//Filling information for the taken item
    	if(tj.nextPendingJob!=null  && tj.nextPendingJob instanceof JobConsume){
    		((JobConsume)tj.nextPendingJob).consumedItem=tkItem;
    		((JobConsume)tj.nextPendingJob).setPosition(this.gPosition);
    	}
		tj.setCurrentProgress(tj.getMaxProgress());
    }
    private int determineTakingItemNumber(){
    	return getRandom().nextInt(3)+1;
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
			font.draw(batch, "zzz", ja.getPosition().x, ja.getPosition().y+this.texture.getHeight()*0.5f);
		}
		else if(ja instanceof JobConsume){
			font.draw(batch, "csm", ja.getPosition().x,ja.getPosition().y+this.texture.getHeight()*0.5f);
		}	
		else if(ja instanceof JobTake){
			font.draw(batch, "jbt", ja.getPosition().x, ja.getPosition().y+this.texture.getHeight()*0.5f);
		}
		else if(ja instanceof JobMove){
			font.draw(batch, this.id+"", ja.getPosition().x, ja.getPosition().y);
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

	public Queue<JobAbstract> getJobQueue(){
		return this.jobQueue;
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