package com.mygdx.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;

public class CoorUtility {

	
	public static boolean isNearCursor(Vector2 sPosition){
		return sPosition.dst(new Vector2(Gdx.input.getX(),Gdx.graphics.getHeight()-Gdx.input.getY()))<=50;
	}
	public static Vector2 cursor2Game(Vector2 sPosition){
		return new Vector2(sPosition.x,Gdx.graphics.getHeight()-sPosition.y);
	}
	public static Vector2 screen2Game(Vector2 sPosition){
		return new Vector2(sPosition.x,sPosition.y);
	}
	public static Vector2 game2Screen(Vector2 gPosition){
		return new Vector2(gPosition.x,gPosition.y);
	}
}
