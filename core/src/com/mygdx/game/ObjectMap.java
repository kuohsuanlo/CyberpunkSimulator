package com.mygdx.game;

import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Queue;

import com.mygdx.job.*;

public class ObjectMap {

	public int x_MAX = 50;
	public int y_MAX = 50;
	public int [][] type;

	public static final int tr_number = 2;
	public Texture[] tr_texture ;
	
	public ObjectMap(){
		this.init();
		this.loadAsset();
		this.generateLevel();
	}
	public void init(){
		type = new int[x_MAX][y_MAX];
		tr_texture= new Texture[tr_number];
		
	}
	public void loadAsset(){
		for(int i=0;i<tr_number;i++){
			tr_texture[i] = new Texture("terrain/"+i+".png");
		}
	}
	public void generateLevel(){
		for(int i=0;i<x_MAX;i++){
			for(int j=0;j<y_MAX;j++){
				if((i+j)%4 == 0 )
					type[i][j]=1;
				else
					type[i][j]=0;
			}
		
		}
	}
	
}
