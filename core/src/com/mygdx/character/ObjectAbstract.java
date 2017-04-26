package com.mygdx.character;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.MyGdxGame;
import com.mygdx.util.CoorUtility;





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

    public void updateScreenCoor(MyGdxGame game){
    	/*
    	 * We don't transform here because we might have done it on batch. 
    	 */
    	this.sPosition = CoorUtility.game2Screen(gPosition);
    }
    
    public abstract String getDisplayName();

	public abstract void renderSelf(SpriteBatch batch);
	public abstract void renderFont(SpriteBatch batch, BitmapFont font);

}