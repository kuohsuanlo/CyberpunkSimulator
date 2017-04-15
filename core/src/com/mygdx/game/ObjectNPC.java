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
	public static final int HUMAN = 0;
	
	public int species;
	
	public int id;
    private float speed;
    private float speed2;
    private float handReach;
    
    private ObjectMap inMap;
    
    public Queue<NeedAbstract> needQueue;

    public Queue<JobAbstract> jobQueue;
    public JobAbstract cjob;
    public int maxJobNumber = 10;
    
    public BitmapFont font = new BitmapFont(); 
    
	private Random random = new Random();


    public ObjectNPC(int gid, int tid,int species,ObjectMap im) {
    	super();
    	
    	this.id = gid;
    	this.species = species;
    	this.gPosition.x = random.nextFloat()*Gdx.graphics.getWidth();
    	this.gPosition.y = random.nextFloat()*Gdx.graphics.getHeight();
    	
    	texture = new Texture("npc/"+tid+".png");
        xOffset = this.texture.getWidth()*0.5f;
        yOffset = this.texture.getHeight()*0.5f;
    	
    	speed =  2+(random.nextFloat()*5);
    	speed2 = speed*speed;
    	handReach = speed/2;
    	
    	jobQueue = new Queue<JobAbstract>();
    	needQueue = new Queue<NeedAbstract>();
    	
    	inMap = im;
    	initNeed();
    }

    public void doAI(){
    	//increase need
    	this.increaseNeed();
    	this.checkNeed();
    	
    	
    	//
    	this.decideJob();
    	
    	//do the job in queue
    	this.doJob();
    	
    	
    }
	@Override
	public void render(SpriteBatch batch) {
    	this.c2s();
    	this.renderSelf(batch);
    	this.renderJob(batch);
	}
	
	
    private void initNeed(){
    	needQueue.addLast( (NeedAbstract)new NeedFatigue("fatigue",0,random.nextFloat()*100+100,null,null) );
    	needQueue.addLast( (NeedAbstract)new NeedHunger("hunger",0,null,null) );
    	needQueue.addLast( (NeedAbstract)new NeedThirst("thirst",0,null,null) );
    }
    private void increaseNeed(){
    	if(this.species==ObjectNPC.HUMAN){
        	for(int i=0;i<needQueue.size;i++){
        		needQueue.get(i).addNeed(0.1f);
        	}
    	}
    }
    
    private void checkNeed(){
    	for(int i=0;i<needQueue.size;i++){
    		float c = needQueue.get(i).currentLevel;
    		float m = needQueue.get(i).maxLevel;
    		if(c>=m){
    			if( (needQueue.get(i) instanceof NeedFatigue)  && !(cjob instanceof JobRest)){
    				this.jobQueue.addFirst( new JobRest(this.gPosition,m,m-c,-1,-1,0f,0f));
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
    	return false;
    }
    
    private void jobConsequence(JobAbstract ja){

    	if(ja.increasedNeed_id>=0){
    		this.needQueue.get(ja.increasedNeed_id).addNeed(ja.increaseNeed_amount);
    	}
    	if(ja.decreasedNeed_id>=0){
    		this.needQueue.get(ja.decreasedNeed_id).addNeed(ja.decreaseNeed_amount);
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
    

	private void renderSelf(SpriteBatch batch) {
		batch.draw(new TextureRegion(this.texture), 
    			this.sPosition.x-this.xOffset, this.gPosition.y-this.yOffset, 
    			this.texture.getWidth()/2, this.texture.getHeight()/2, 
    			this.texture.getWidth(), this.texture.getHeight(), 1, 1, this.rotation, true);

	}
	private void renderJob(SpriteBatch batch) {
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