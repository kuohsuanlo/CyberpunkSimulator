package com.mygdx.game;

import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Queue;
import com.mygdx.item.ItemAbstract;
import com.mygdx.job.JobAbstract;
import com.mygdx.need.NeedAbstract;

public class ObjectBuilding extends ObjectAbstract {

    private BitmapFont font;
	private String displayName;
    private Random random;
    private ObjectMap inMap;
    
	public ObjectBuilding(){
		super();
	}
	
	 public ObjectBuilding(int gid, int tid,int species,ObjectMap im, Random random) {
	    	super();
	    	
	    	this.id = gid;
	    	
	    	gPosition.x = random.nextFloat()*Gdx.graphics.getWidth();
	    	gPosition.y = random.nextFloat()*Gdx.graphics.getHeight();
	    	
	    	texture = new Texture("npc/"+tid+".png");
	        xOffset = this.texture.getWidth()*0.5f;
	        yOffset = this.texture.getHeight()*0.5f;

	    	font = new BitmapFont(); 
	    	inMap = im;
	    	displayName = "Building "+this.id;
	    }
	@Override
	public String getDisplayName() {
		return displayName;
	}

	@Override
	public void renderSelf(SpriteBatch batch) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void renderFont(SpriteBatch batch) {
		// TODO Auto-generated method stub
		
	}

}
