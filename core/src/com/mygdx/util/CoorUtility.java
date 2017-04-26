package com.mygdx.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;

public class CoorUtility {

	
	public static boolean isNearCursor(Vector2 sPosition){
		return sPosition.dst(new Vector2(Gdx.input.getX(),Gdx.graphics.getHeight()-Gdx.input.getY()))<=50;
	}
	public static Vector2 cursor2Game(Vector2 cursor){
		//Cusor2Screen
		Vector2 sPosition = new Vector2(cursor.x,Gdx.graphics.getHeight()-cursor.y);
		//Screen2Game
		return screen2Game(sPosition);
	}
	public static Vector2 screen2Game(Vector2 sPosition){
		//return new Vector2(sPosition.x,0);
		return new Vector2(sPosition.x,sPosition.y);
	}
	public static Vector2 game2Screen(Vector2 gPosition){
		//return new Vector2(gPosition.x,0);
		return new Vector2(gPosition.x,gPosition.y);
	}
}
