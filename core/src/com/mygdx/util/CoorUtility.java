package com.mygdx.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;

public class CoorUtility {

	
	public static boolean isNearCursor(Vector2 sPosition){
		return sPosition.dst(new Vector2(Gdx.input.getX(),Gdx.graphics.getHeight()-Gdx.input.getY()))<=50;
	}
}
