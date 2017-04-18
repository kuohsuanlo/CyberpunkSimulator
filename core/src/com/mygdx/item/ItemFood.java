package com.mygdx.item;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.ObjectNPC;
import com.mygdx.need.NeedAbstract;

public class ItemFood extends ItemAbstract{

	public ItemFood(int id, Vector2 gp, float price, String name, int stack_number, int decreasedNeed_id,
			int increasedNeed_id, float decreasedNeed_amount, float increasedNeed_amount, ObjectNPC owner, ItemAbstract destroyedItem) {
		super(id, gp, price, name, stack_number, decreasedNeed_id, increasedNeed_id, decreasedNeed_amount, increasedNeed_amount,
				owner,destroyedItem);
	}

	public ItemFood(int id, Vector2 gp, float price, String name, int stack_number, int decreasedNeed_id,
			int increasedNeed_id, float decreasedNeed_amount, float increasedNeed_amount, ObjectNPC owner) {
		super(id, gp, price, name, stack_number, decreasedNeed_id, increasedNeed_id, decreasedNeed_amount, increasedNeed_amount,
				owner);
	}






	
}
