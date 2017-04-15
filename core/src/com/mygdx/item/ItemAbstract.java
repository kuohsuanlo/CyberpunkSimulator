package com.mygdx.item;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.ObjectAbstract;
import com.mygdx.game.ObjectNPC;
import com.mygdx.need.NeedAbstract;

public abstract class ItemAbstract extends ObjectAbstract {
	public int id;
	public int ageTick;
	public int maxAgeTick;
	public float price;
	public String name;
	public ObjectNPC owner;
	public int decreasedNeed_id;
	public int increasedNeed_id;
	
	public ItemAbstract(int id,Vector2 gp, float price, String name, int decreasedNeed_id, int increasedNeed_id,ObjectNPC owner) {
		super();
		this.id = id;
		this.gPosition = gp;
		this.price = price;
		this.name = name;
		this.decreasedNeed_id = decreasedNeed_id;
		this.increasedNeed_id = increasedNeed_id;
		this.owner = owner;
		this.maxAgeTick = 100;
		
    	texture = new Texture("item/"+id+".png");
        xOffset = this.texture.getWidth()*0.5f;
        yOffset = this.texture.getHeight()*0.5f;
	}
	
	public void itemTimePass(){
		this.rotation=(this.rotation+3)%360;
		ageTick+=1;
	}
	public boolean itemDestroy(){
		return ageTick>maxAgeTick;
	}
	@Override
	public void render(SpriteBatch batch) {
    	this.c2s();
    	batch.draw(new TextureRegion(this.texture), 
    			this.sPosition.x-this.xOffset, this.gPosition.y-this.yOffset, 
    			this.texture.getWidth()/2, this.texture.getHeight()/2, 
    			this.texture.getWidth(), this.texture.getHeight(), 1, 1, this.rotation, true);

	}

}
