package com.mygdx.util;

import java.util.Random;

import com.badlogic.gdx.utils.Queue;
import com.mygdx.item.ItemAbstract;

public class ItemUtility {


	public static Queue<ItemAbstract> findItemWithNeed(Queue<ItemAbstract> q, int NEED_ID){
    	Queue<ItemAbstract> candidates = new Queue<ItemAbstract>();
    	/*
    	 * Linear search, need to be more efficient, might could be done by stochastic search. 
    	 */
    	synchronized(q){
	    	for(int i=0;i<q.size;i++){
	    		synchronized(q.get(i)){
		    		if(q.get(i)!=null  &&  q.get(i).getDecreasedNeed_id()==NEED_ID){
		    			candidates.addFirst(q.get(i));
		    		}
    			}
	    	}
	    	return candidates;
    	}
    }
    
    public static Queue<ItemAbstract> findItemWithID(Queue<ItemAbstract> q,int iid){

    	Queue<ItemAbstract> candidates = new Queue<ItemAbstract>();
    	/*
    	 * Linear search, need to be more efficient, might could be done by stochastic search. 
    	 */
    	synchronized(q){
    		for(int i=0;i<q.size;i++){
    			synchronized(q.get(i)){
    				if(q.get(i)!=null  &&  q.get(i).getId()==iid){
            			candidates.addFirst(q.get(i));
            		}
    			}
        	}
        	return candidates;
    	}
    	
    }
}
