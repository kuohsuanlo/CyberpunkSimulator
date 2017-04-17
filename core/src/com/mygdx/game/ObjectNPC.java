package com.mygdx.game;

import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Queue;
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
    
    /*
     * Queues : to simulate the human behavior by creating the needs and daily jobs, or jobs driven by needs
     */
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
    	
    	jobQueue = new Queue<JobAbstract>();
    	needQueue = new Queue<NeedAbstract>();
    	bpTraits = new ObjectBodyPart[bpNumber];
    	
    	font = new BitmapFont(); 
    	
    	inMap = im;
    	
    	//initializing in-game data
    	initNeed();
    	initBodyPartTraits();
    	updatePersonalAbilities();
    }
    
    public void doAI(){
    	//increase need
    	this.increaseNeed();
    	this.checkNeed();
    	
    	
    	//deciding the daily job(by professional)
    	this.decideJob();
    	
    	//doing the job in queue
    	this.doJob();
    	
    	
    }
	@Override
	public void render(SpriteBatch batch) {
    	this.c2s();
    	this.renderSelf(batch);
    	this.renderFont(batch);
	}
	
	private void initBodyPartTraits(){
		for(int i=0;i<bpNumber;i++){
			this.bpTraits[i] = new ObjectBodyPart(i,random.nextInt(10),random.nextInt(10),random.nextInt(10));
		}
	}
	private void updatePersonalAbilities(){
		//speed formula
		float speed_tmp=0;
		for(int i=0;i<bpNumber;i++){
			speed_tmp+=this.bpTraits[i].traits.dex/10;
		}
		speed = speed_tmp;
    	speed2 = speed*speed;
    	handReach = speed/2;
	}
	
	
    private void initNeed(){
    	needQueue.addLast( (NeedAbstract)new NeedFatigue("fatigue",0,0.05f,0,random.nextFloat()*100+100,null,null) );
    	needQueue.addLast( (NeedAbstract)new NeedHunger("hunger",0,0.05f,0,random.nextFloat()*100+100,null,null) );
    	needQueue.addLast( (NeedAbstract)new NeedThirst("thirst",0,0.05f,0,random.nextFloat()*100+100,null,null) );
    }
    private void increaseNeed(){
    	if(this.species==ObjectNPC.HUMAN){
        	for(int i=0;i<needQueue.size;i++){
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
    				if (!(cjob instanceof JobRest)){
    					this.jobQueue.addFirst( new JobRest(this.gPosition,m,m-c,-1,-1,0f,0f));
    				}
    			}
    			else if( needQueue.get(i) instanceof NeedHunger) {
    				
    			
    			}
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
    	return false;
    }
    
    private void jobConsequence(JobAbstract ja){

    	if(ja.increasedNeed_id>=0){
    		this.needQueue.get(ja.increasedNeed_id).addNeed(ja.increaseNeed_amount);
    	}
    	if(ja.decreasedNeed_id>=0){
    		this.needQueue.get(ja.decreasedNeed_id).addNeed(ja.decreaseNeed_amount);
    	}
    	
    	if(ja instanceof JobConsume){
			((JobConsume) ja).consumedItem.stack_number-=1;
    	}
    }
    
    private void decideJob(){
    	if(this.jobQueue.size==0){float x_d = random.nextFloat()*Gdx.graphics.getWidth();
    		float y_d = random.nextFloat()*Gdx.graphics.getHeight();
        	this.jobQueue.addLast(new JobMove(new Vector2(x_d,y_d),-1, -1, -1, -1,0,0));
    	}
    }
    private void walkOneTick(JobMove mj){
    	this.needQueue.get(NeedFatigue.id).addNeed(this.speed*0.1f);
    	Vector2 vtmp = new Vector2(mj.position.x - this.gPosition.x, mj.position.y - this.gPosition.y);
    	
    	if(vtmp.len2()>speed2){
        	vtmp.setLength(this.speed);
    	}
    	this.rotation = vtmp.angle();
    	this.gPosition.add(vtmp);
    }

    private void rest(JobRest rj){
    	rj.currentProgress+=1;
    	this.needQueue.get(NeedFatigue.id).addNeed(-1);
    }
    private void consumeItem(JobConsume cj){
    	cj.currentProgress +=1 ;
    }
  
	private void renderSelf(SpriteBatch batch) {
		batch.draw(new TextureRegion(this.texture), 
    			this.sPosition.x-this.xOffset, this.gPosition.y-this.yOffset, 
    			this.texture.getWidth()/2, this.texture.getHeight()/2, 
    			this.texture.getWidth(), this.texture.getHeight(), 1, 1, this.rotation, true);

	}
	private void renderFont(SpriteBatch batch) {
		
		//rendering Job
		for(int i=0;i<this.jobQueue.size;i++){
			JobAbstract ja = this.jobQueue.get(i); 
			if(ja instanceof JobRest){
				font.draw(batch, "zzz", ja.position.x, ja.position.y+this.texture.getHeight()*0.5f);
			}
			else if(ja instanceof JobMove){
				font.draw(batch, this.id+"", ja.position.x, ja.position.y);
			}	
		}
	}
	
	private void printSelfInfo(){
    	Gdx.app.log("NPC"+this.id+"_JOB ", this.jobQueue.size+"/"+this.cjob);
    	Gdx.app.log("NPC"+this.id+"_NEED"+this.needQueue.get(0).id, this.needQueue.get(0).displayName+" : "+this.needQueue.get(0).currentLevel+"");
    	Gdx.app.log("NPC"+this.id+"_NEED"+this.needQueue.get(1).id, this.needQueue.get(1).displayName+" : "+this.needQueue.get(1).currentLevel+"");
    	Gdx.app.log("NPC"+this.id+"_NEED"+this.needQueue.get(2).id, this.needQueue.get(2).displayName+" : "+this.needQueue.get(2).currentLevel+"");
	}

	
   
}