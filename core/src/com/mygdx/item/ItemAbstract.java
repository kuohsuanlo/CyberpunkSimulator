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
	private float texture_default_size = 16;
	private int id;
	private int ageTick;
	private int maxAgeTick;
	private float price;
	private String name;
	private int stack_number ;
	private ObjectNPC owner;
	private int decreasedNeed_id;
	private int increasedNeed_id;
	private float decreasedNeed_amount;
	private float increasedNeed_amount;
	
	private ItemAbstract destroyedItem;
	
    private BitmapFont font;

	public ItemAbstract(int id,Vector2 gp, float price, String name,int stack_number, int decreasedNeed_id, int increasedNeed_id,float decreasedNeed_amount, float increasedNeed_amount, ObjectNPC owner, ItemAbstract destroyedItem) {
		super();
		this.setId(id);
		this.gPosition = gp;
		this.price = price;
		this.name = name;
		this.setStack_number(stack_number);
		this.setDecreasedNeed_id(decreasedNeed_id);
		this.setIncreasedNeed_id(increasedNeed_id);
		this.setDecreasedNeed_amount(decreasedNeed_amount);
		this.setIncreasedNeed_amount(increasedNeed_amount);
		this.owner = owner;
		this.maxAgeTick = 100;
		this.destroyedItem = destroyedItem;
		 
		
        xOffset = this.texture_default_size*0.5f;
        yOffset = this.texture_default_size*0.5f;
		
    	//texture = new Texture("item/"+id+".png");
        //xOffset = this.texture.getWidth()*0.5f;
        //yOffset = this.texture.getHeight()*0.5f;
	}
	public ItemAbstract(int id,Vector2 gp, float price, String name,int stack_number, int decreasedNeed_id, int increasedNeed_id,float decreasedNeed_amount, float increasedNeed_amount, ObjectNPC owner) {
		super();
		this.setId(id);
		this.gPosition = gp;
		this.price = price;
		this.name = name;
		this.setStack_number(stack_number);
		this.setDecreasedNeed_id(decreasedNeed_id);
		this.setIncreasedNeed_id(increasedNeed_id);
		this.setDecreasedNeed_amount(decreasedNeed_amount);
		this.setIncreasedNeed_amount(increasedNeed_amount);
		this.owner = owner;
		this.maxAgeTick = 100;
		this.destroyedItem = null;
		
		this.font = new BitmapFont(); 
		
    	texture = new Texture("item/"+id+".png");
        xOffset = this.texture.getWidth()*0.5f;
        yOffset = this.texture.getHeight()*0.5f;
	}
	public String getDisplayName(){
		return this.name+" x "+this.getStack_number();
	}
	public ItemAbstract getTaken(int number){
		int tkNumber = this.getStack_number();
		if(tkNumber>=number){
			tkNumber = number;
		}
		if(this.getStack_number()>=tkNumber){
			this.setStack_number(this.getStack_number() - tkNumber);
			return new ItemAbstract(this.getId(),this.gPosition,this.price,this.name,tkNumber,this.getDecreasedNeed_id(),this.getIncreasedNeed_id(),this.getDecreasedNeed_amount(),this.getIncreasedNeed_amount(),null,this.destroyedItem);

		}
		return null;
	}
	public boolean compareItemAbstract(ItemAbstract ia){
		return  this.getId()==ia.getId()  && 
				this.name==ia.name  &&  
				this.getDecreasedNeed_id() == ia.getDecreasedNeed_id()  &&
				this.getIncreasedNeed_id() == ia.getIncreasedNeed_id()  &&
				this.getDecreasedNeed_amount() == ia.getDecreasedNeed_amount()  &&
				this.getIncreasedNeed_amount() == ia.getIncreasedNeed_amount()  &&
				this.maxAgeTick == ia.maxAgeTick  &&  
				this.owner  == ia.owner;
				
	}
	public void itemTimePass(){
		this.rotation=(this.rotation+5)%360;
		//ageTick+=1;
	}
	public boolean itemNeedDestroy(){
		return ageTick>maxAgeTick  ||  getStack_number()<=0;
	}
	public boolean hasDestroyedItem(){
		return this.destroyedItem!=null;
	}
	public ItemAbstract getOneDestroyedItem(){
		return new ItemAbstract(this.destroyedItem.getId(),this.destroyedItem.gPosition,this.destroyedItem.price,this.destroyedItem.name,1,this.destroyedItem.getDecreasedNeed_id(),this.destroyedItem.getIncreasedNeed_id(),this.destroyedItem.getDecreasedNeed_amount(),this.destroyedItem.getIncreasedNeed_amount(),this.destroyedItem.owner,this.destroyedItem.destroyedItem);

	}
	public void renderSelf(SpriteBatch batch) {
		if(texture==null){
			texture = new Texture("item/"+getId()+".png");
	        xOffset = this.texture.getWidth()*0.5f;
	        yOffset = this.texture.getHeight()*0.5f;
		}
    	batch.draw(new TextureRegion(this.texture), 
    			this.sPosition.x-this.xOffset, this.gPosition.y-this.yOffset, 
    			this.texture.getWidth()/2, this.texture.getHeight()/2, 
    			this.texture.getWidth(), this.texture.getHeight(), 1, 1, this.rotation, true);

	}
	public void renderFont(SpriteBatch batch) {	
		if(font ==null){
			this.font = new BitmapFont();
		}
		if(nearCursor()){
			font.draw(batch, this.getDisplayName(), this.sPosition.x, this.sPosition.y+2*this.texture.getHeight());
		}
	}
	public int getDecreasedNeed_id() {
		return decreasedNeed_id;
	}
	public void setDecreasedNeed_id(int decreasedNeed_id) {
		this.decreasedNeed_id = decreasedNeed_id;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getStack_number() {
		return stack_number;
	}
	public void setStack_number(int stack_number) {
		this.stack_number = stack_number;
	}
	public float getDecreasedNeed_amount() {
		return decreasedNeed_amount;
	}
	public void setDecreasedNeed_amount(float decreasedNeed_amount) {
		this.decreasedNeed_amount = decreasedNeed_amount;
	}
	public float getIncreasedNeed_amount() {
		return increasedNeed_amount;
	}
	public void setIncreasedNeed_amount(float increasedNeed_amount) {
		this.increasedNeed_amount = increasedNeed_amount;
	}
	public int getIncreasedNeed_id() {
		return increasedNeed_id;
	}
	public void setIncreasedNeed_id(int increasedNeed_id) {
		this.increasedNeed_id = increasedNeed_id;
	}

}
