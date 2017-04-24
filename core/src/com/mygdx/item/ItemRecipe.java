package com.mygdx.item;

import com.badlogic.gdx.utils.Queue;
import com.mygdx.job.JobProduce;

public class ItemRecipe {
	public Queue<ItemAbstract> usedItemQueue ;
	public Queue<ItemAbstract> producedItemQueue;
	
	public ItemRecipe(Queue<ItemAbstract> usedItemQueue, Queue<ItemAbstract> producedItemQueue){
		this.usedItemQueue = usedItemQueue;
		this.producedItemQueue = producedItemQueue;
	}
	public boolean compareRecipe(ItemRecipe ir){
			return compareItemQueue(this.usedItemQueue,ir.usedItemQueue)  &&
					compareItemQueue(this.producedItemQueue,ir.producedItemQueue) ;
		
	}
	
	public boolean compareItemQueue(Queue<ItemAbstract> ia1, Queue<ItemAbstract> ia2){
		if(ia1==null || ia2==null) return false;
		
		int counter = 0;
		for(int i=0;i<ia1.size;i++){
			for(int j=0;j<ia2.size;j++){
				if(compareItem(ia1.get(i),ia2.get(j))){
					counter+=1;
				}
			}
		}
		if(counter==ia1.size  ||  counter==ia2.size){
			return true;
		}
		
		return false;
	}
	public boolean compareItem(ItemAbstract ia1, ItemAbstract ia2){
		return (ia1.getId()==ia2.getId());
	}
	
}
