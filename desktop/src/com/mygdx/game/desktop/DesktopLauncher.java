package com.mygdx.game.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.mygdx.game.MyGdxGame;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		

		config.width = 1440;
		config.height = 900;
		//config.fullscreen = true;
	    // TODO: ori2prime (resizing issue)
	    // TODO: prime2ori (resizing issue)
		// ----: progress based on real time passing ==> now 100 progress == 1 second
		
		
		//config.width = 400;
		//config.height = 400;
		
		new LwjglApplication(new MyGdxGame(), config);
	}
}
