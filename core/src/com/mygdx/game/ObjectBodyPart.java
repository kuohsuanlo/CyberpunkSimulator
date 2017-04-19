package com.mygdx.game;

public class ObjectBodyPart {
	public int bodyPosition;
	public String displayName;
	public ObjectBodyPartTraits traits;
	
	
	public ObjectBodyPart (int bodyPosition, float str, float dex, float vit){
		this.bodyPosition = bodyPosition;
		this.displayName = "BodyPart:"+bodyPosition;
		this.traits = new ObjectBodyPartTraits(0,100,str,dex,vit);
	}
}


