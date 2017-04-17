package com.mygdx.game;

public class ObjectBodyPart {
	public int bodyPosition;
	public String displayName;
	public BodyPartTraits traits;
	
	
	public ObjectBodyPart (int bodyPosition, float str, float dex, float vit){
		this.bodyPosition = bodyPosition;
		this.displayName = "BodyPart:"+bodyPosition;
		this.traits = new BodyPartTraits(0,100,str,dex,vit);
	}
}

class BodyPartTraits{
	public float currentDurability;
	public float maxDurability;
	public float str;
	public float dex;
	public float vit;
	
	public BodyPartTraits (float currentDurability, float maxDurability, float str, float dex, float vit) {
		this.currentDurability = currentDurability;
		this.maxDurability = maxDurability;
		this.str = str;
		this.dex = dex;
		this.vit = vit;
	}
}
