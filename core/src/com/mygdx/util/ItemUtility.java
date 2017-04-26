package com.mygdx.util;

import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Random;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Queue;
import com.mygdx.item.ItemAbstract;

public class ItemUtility {
	public static PriorityQueue<ItemAbstract> findItemWithNeed(Queue<ItemAbstract> q, int NEED_ID, Vector2 anchor){
		PriorityQueue<ItemAbstract> candidates = new PriorityQueue<ItemAbstract>(new DistanceComparator(anchor));
    	/*
    	 * Linear search, need to be more efficient, might could be done by stochastic search. 
    	 */
    	synchronized(q){
	    	for(int i=0;i<q.size;i++){
	    		if(q.get(i)!=null){
		    		synchronized(q.get(i)){
			    		if(q.get(i)!=null  &&  q.get(i).getDecreasedNeed_id()==NEED_ID){
			    			candidates.offer(q.get(i));
			    		}
	    			}
	    		}
	    	}
	    	return candidates;
    	}
    }
    
    public static PriorityQueue<ItemAbstract> findItemWithID(Queue<ItemAbstract> q,int iid,  Vector2 anchor){

    	PriorityQueue<ItemAbstract> candidates = new PriorityQueue<ItemAbstract>(new DistanceComparator(anchor));
    	/*
    	 * Linear search, need to be more efficient, might could be done by stochastic search. 
    	 */
    	synchronized(q){
    		for(int i=0;i<q.size;i++){
    			if(q.get(i)!=null){
        			synchronized(q.get(i)){
        				if(q.get(i)!=null  &&  q.get(i).getId()==iid){
                			candidates.offer(q.get(i));
                		}
        			}
    			}
        	}
        	return candidates;
    	}
    	
    }
}

class DistanceComparator implements Comparator<ItemAbstract>{
	private Vector2 anchor;
	public DistanceComparator(Vector2 anchor){
		this.anchor = anchor;
	}
	public void setAnchor(Vector2 anchor){
		this.anchor = anchor;
	}
	public int compare(ItemAbstract ia1, ItemAbstract ia2){
       if (ia1.gPosition.dst(this.anchor) < ia2.gPosition.dst(this.anchor)) return -1;
       else                               return 1;
	}



}

