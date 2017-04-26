package com.mygdx.character;

public class ObjectBodyPart {
	public int bodyPosition;
	public String displayName;
	public ObjectTraits traits;
	
	
	public ObjectBodyPart (int bodyPosition, float str, float dex, float vit){
		this.bodyPosition = bodyPosition;
		this.displayName = "BodyPart:"+bodyPosition;
		this.traits = new ObjectTraits(0,100,str,dex,vit);
	}
}


