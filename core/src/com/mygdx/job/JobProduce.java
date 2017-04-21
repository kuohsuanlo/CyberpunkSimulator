package com.mygdx.job;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Queue;
import com.mygdx.item.ItemAbstract;
import com.mygdx.item.ItemRecipe;
import com.mygdx.need.NeedAbstract;

public class JobProduce extends JobAbstract{

	public ItemRecipe recipe;

	public JobProduce(Vector2 position, float maxProgress, float currentProgress, NeedAbstract decreasedNeed,
			NeedAbstract increasedNeed, float decreaseNeed_amount, float increaseNeed_amount,ItemRecipe recipe) {
		super(position, maxProgress, currentProgress, decreasedNeed, increasedNeed, decreaseNeed_amount, increaseNeed_amount);

		this.recipe = recipe;
	}
	public boolean compareJobAbstract(JobAbstract ja) {
		if(ja==null) return false;
		
		if(ja instanceof JobProduce){
			return this.recipe.compareRecipe(((JobProduce) ja).recipe);
		}
		else{
			return false;
		}
		
	}
}
