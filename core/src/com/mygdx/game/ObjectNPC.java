package com.mygdx.game;

import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Queue;

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
	public Texture texture;
    public float xOffset;
    public float yOffset;

    

    
    
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
    	
    	this.giveNeed();
    }
    public void giveNeed(){
    	this.needQueue.addLast( (NeedAbstract)new NeedHunger("hunger",5,0,1,0,null,null) );
    	this.needQueue.addLast( (NeedAbstract)new NeedFatigue("fatigue",5,0,1,0,null,null) );
    	this.needQueue.addLast( (NeedAbstract)new NeedThirst("thirst",5,0,1,0,null,null) );
    }
    public void increaseNeed(){
    	this.needQueue.get(NeedFatigue.id).addNeed(1.0f);
    	this.needQueue.get(NeedHunger.id).addNeed(1.0f);
    	this.needQueue.get(NeedThirst.id).addNeed(1.0f);
    }
    public void doAI(){
    	//increase need
    	if(this.species==ObjectNPC.HUMAN){
    		this.increaseNeed();
    	}
    	
    	//add professional job
    	if(this.jobQueue.size<maxJobNumber){
        	this.jobQueue.addLast( this.decideJob());
    		
    	}
    	
    	//do the job in queue
    	this.doJob();
    	
    	
    }
    public void doJob(){
    	cjob = jobQueue.first();
    	if(cjob instanceof JobMove){
    		JobMove mj = (JobMove)cjob;

    		this.walkOneTick(mj);
    		
    		if(this.gPosition.dst(mj.position)<=this.handReach){
    			this.jobQueue.removeFirst();
    		}
    	}
    }
    
    public JobAbstract decideJob(){
    	//if walk to goal
    	float x_d = random.nextFloat()*Gdx.graphics.getWidth();
    	float y_d = random.nextFloat()*Gdx.graphics.getHeight();
    	
    	return new JobMove(x_d,y_d,1);
    }
    public void walkOneTick(JobMove mj){
    	
    	Vector2 vtmp = new Vector2(mj.position.x - this.gPosition.x, mj.position.y - this.gPosition.y);
    	
    	if(vtmp.len2()>speed2){
        	vtmp.setLength(this.speed);
    	}
    	this.rotation = vtmp.angle();
    	this.gPosition.add(vtmp);
    }
    public void c2s(){
    	this.sPosition.x =   this.gPosition.x;
    	this.sPosition.y =   this.gPosition.y;
    }
	@Override
	public void render(SpriteBatch batch) {
    	this.c2s();
    	batch.draw(new TextureRegion(this.texture), 
    			this.sPosition.x-this.xOffset, this.gPosition.y-this.yOffset, 
    			this.texture.getWidth()/2, this.texture.getHeight()/2, 
    			this.texture.getWidth(), this.texture.getHeight(), 1, 1, this.rotation, true);

		//batch.draw(this.texture,this.sPosition.x-this.xOffset,this.sPosition.y-this.yOffset);
		font.draw(batch, this.id+"", this.cjob.position.x, this.cjob.position.y);

	}
	
	
	
	

	
   
}