package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;





public abstract class ObjectAbstract {
	public int id;
    public Vector2 gPosition;
    public Vector2 sPosition;
    public Vector2 dimension;
    public Vector2 origin;
    public Vector2 scale;
    public float rotation;
	public Texture texture;
    public float xOffset;
    public float yOffset;

    
	public ObjectAbstract () {
	    gPosition = new Vector2();
	    sPosition = new Vector2();
	    dimension = new Vector2(1, 1);
	    origin = new Vector2();
	    scale = new Vector2(1, 1);
	    rotation = 0;
	}

    public void c2s(){
    	this.sPosition =   this.gPosition;
    }

    public abstract String getDisplayName();

	public abstract void renderSelf(SpriteBatch batch);
	public abstract void renderFont(SpriteBatch batch,MyGdxGame game);

}