package com.mygdx.game;

public class ObjectTraits{
	public float currentDurability;
	public float maxDurability;
	public float str;
	public float dex;
	public float vit;
	
	public ObjectTraits (float currentDurability, float maxDurability, float str, float dex, float vit) {
		this.currentDurability = currentDurability;
		this.maxDurability = maxDurability;
		this.str = str;
		this.dex = dex;
		this.vit = vit;
	}
}