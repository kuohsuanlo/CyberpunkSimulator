package com.mygdx.game.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.mygdx.game.MyGdxGame;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		

		//config.width = 1440;
		//config.height = 900;
		
	    // TODO: ori2prime (resizing issue)
	    // TODO: prime2ori (resizing issue)
		// TODO: progress based on realtime
		
		
		config.width = 400;
		config.height = 200;
		
		new LwjglApplication(new MyGdxGame(), config);
	}
}
