package com.mygdx.item;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.ObjectAbstract;
import com.mygdx.game.ObjectNPC;
import com.mygdx.job.JobAbstract;
import com.mygdx.job.JobMove;
import com.mygdx.job.JobRest;
import com.mygdx.need.NeedAbstract;

public class ItemAbstract extends ObjectAbstract {
	public int id;
	public int ageTick;
	public int maxAgeTick;
	public float price;
	public String name;
	public int stack_number ;
	public ObjectNPC owner;
	public int decreasedNeed_id;
	public int increasedNeed_id;
	public float decreasedNeed_amount;
	public float increasedNeed_amount;
	
	private ItemAbstract destroyedItem;
	
    private BitmapFont font;

	public ItemAbstract(int id,Vector2 gp, float price, String name,int stack_number, int decreasedNeed_id, int increasedNeed_id,float decreasedNeed_amount, float increasedNeed_amount, ObjectNPC owner, ItemAbstract destroyedItem) {
		super();
		this.id = id;
		this.gPosition = gp;
		this.price = price;
		this.name = name;
		this.stack_number = stack_number;
		this.decreasedNeed_id = decreasedNeed_id;
		this.increasedNeed_id = increasedNeed_id;
		this.decreasedNeed_amount = decreasedNeed_amount;
		this.increasedNeed_amount = increasedNeed_amount;
		this.owner = owner;
		this.maxAgeTick = 100;
		this.destroyedItem = destroyedItem;
		
		this.font = new BitmapFont(); 
		
    	texture = new Texture("item/"+id+".png");
        xOffset = this.texture.getWidth()*0.5f;
        yOffset = this.texture.getHeight()*0.5f;
	}
	public ItemAbstract(int id,Vector2 gp, float price, String name,int stack_number, int decreasedNeed_id, int increasedNeed_id,float decreasedNeed_amount, float increasedNeed_amount, ObjectNPC owner) {
		super();
		this.id = id;
		this.gPosition = gp;
		this.price = price;
		this.name = name;
		this.stack_number = stack_number;
		this.decreasedNeed_id = decreasedNeed_id;
		this.increasedNeed_id = increasedNeed_id;
		this.decreasedNeed_amount = decreasedNeed_amount;
		this.increasedNeed_amount = increasedNeed_amount;
		this.owner = owner;
		this.maxAgeTick = 100;
		this.destroyedItem = null;
		
		this.font = new BitmapFont(); 
		
    	texture = new Texture("item/"+id+".png");
        xOffset = this.texture.getWidth()*0.5f;
        yOffset = this.texture.getHeight()*0.5f;
	}
	public String getDisplayName(){
		return this.name+" x "+this.stack_number;
	}
	public ItemAbstract getTaken(int number){
		int tkNumber = this.stack_number;
		if(tkNumber>=number){
			tkNumber = number;
		}
		if(this.stack_number>=tkNumber){
			this.stack_number-=tkNumber;
			return new ItemAbstract(this.id,this.gPosition,this.price,this.name,tkNumber,this.decreasedNeed_id,this.increasedNeed_id,this.decreasedNeed_amount,this.increasedNeed_amount,null,this.destroyedItem);

		}
		return null;
	}
	public boolean compareItemAbstract(ItemAbstract ia){
		return  this.id==ia.id  && 
				this.name==ia.name  &&  
				this.decreasedNeed_id == ia.decreasedNeed_id  &&
				this.increasedNeed_id == ia.increasedNeed_id  &&
				this.decreasedNeed_amount == ia.decreasedNeed_amount  &&
				this.increasedNeed_amount == ia.increasedNeed_amount  &&
				this.maxAgeTick == ia.maxAgeTick  &&  
				this.owner  == ia.owner;
				
	}
	public void itemTimePass(){
		this.rotation=(this.rotation+3)%360;
		//ageTick+=1;
	}
	public boolean itemNeedDestroy(){
		return ageTick>maxAgeTick  ||  stack_number<=0;
	}
	public boolean hasDestroyedItem(){
		return this.destroyedItem!=null;
	}
	public ItemAbstract getOneDestroyedItem(){
		return new ItemAbstract(this.destroyedItem.id,this.destroyedItem.gPosition,this.destroyedItem.price,this.destroyedItem.name,1,this.destroyedItem.decreasedNeed_id,this.destroyedItem.increasedNeed_id,this.destroyedItem.decreasedNeed_amount,this.destroyedItem.increasedNeed_amount,this.destroyedItem.owner,this.destroyedItem.destroyedItem);

	}
	public void renderSelf(SpriteBatch batch) {
    	batch.draw(new TextureRegion(this.texture), 
    			this.sPosition.x-this.xOffset, this.gPosition.y-this.yOffset, 
    			this.texture.getWidth()/2, this.texture.getHeight()/2, 
    			this.texture.getWidth(), this.texture.getHeight(), 1, 1, this.rotation, true);

	}
	public void renderFont(SpriteBatch batch) {	
		if(nearCursor()){
			font.draw(batch, this.getDisplayName(), this.sPosition.x, this.sPosition.y+2*this.texture.getHeight());
		}
	}

}
