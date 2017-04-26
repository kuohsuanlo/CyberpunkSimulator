package com.mygdx.profession;

import com.badlogic.gdx.utils.Queue;
import com.mygdx.character.ObjectAbstract;
import com.mygdx.character.ObjectNPC;
import com.mygdx.item.ItemAbstract;
import com.mygdx.item.ItemRecipe;
import com.mygdx.mission.MissionAbstract;
import com.mygdx.mission.MissionCollect;
import com.mygdx.mission.MissionDrop;
import com.mygdx.mission.MissionProduce;

public class ProfessionMaker extends ProfessionAbstract{

	public ProfessionMaker(ObjectNPC owner, float professionPoints, String professionName,
			String[] professionLevelTitle) {
		super(owner, professionPoints, professionName, professionLevelTitle);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void checkMissions() {
		if(this.getOwner().isTooBusyToAcceptMission()) return;
		
		if(this.getOwner().getRandom().nextInt(2)==0){
			ItemAbstract ingot_collect = new ItemAbstract(6,this.getOwner().gPosition,0,"",1,0,0,0f,0f,null);
			ItemAbstract ingot_used = new ItemAbstract(6,this.getOwner().gPosition,0,"",1,0,0,0f,0f,null);		
			ItemAbstract tool = new ItemAbstract(7,this.getOwner().gPosition,0,"tool",1,0,0,0f,0f,null);	
			
			Queue<ItemAbstract> usedItems = new Queue<ItemAbstract>();
			Queue<ItemAbstract> producedItems = new Queue<ItemAbstract>();
			usedItems.addFirst(ingot_used);
			producedItems.addFirst(tool);
			ItemRecipe recipe_tool = new ItemRecipe(usedItems, producedItems);
	    	
			if(this.getOwner().getMissionQueue().size==0){
				this.getOwner().getMissionQueue().addLast(new MissionCollect());
				this.getOwner().getMissionQueue().addLast(new MissionProduce());
				this.getOwner().getMissionQueue().addLast(new MissionDrop());
			}
			
			for(int i=0;i<this.getOwner().getMissionQueue().size;i++){
				if(this.getOwner().getMissionQueue().get(i) instanceof MissionCollect){
					((MissionCollect)this.getOwner().getMissionQueue().get(i)).assign(this.getOwner(), ingot_collect);
				}
				else if(this.getOwner().getMissionQueue().get(i) instanceof MissionProduce){
					((MissionProduce)this.getOwner().getMissionQueue().get(i)).assign(this.getOwner(), recipe_tool);
				}
				else if(this.getOwner().getMissionQueue().get(i) instanceof MissionDrop){
					((MissionDrop)this.getOwner().getMissionQueue().get(i)).assign(this.getOwner(), tool, this.getOwner().game.getRandomLoc());
				}
			}
		}
		else{
			ItemAbstract bucket = new ItemAbstract(3,this.getOwner().gPosition,0,"bucket",1,0,0,0f,0f,null);	
			ItemAbstract ingot = new ItemAbstract(6,this.getOwner().gPosition,0,"iron_ingot",1,0,0,0f,0f,null);
			ItemAbstract ingot_dropped = new ItemAbstract(6,this.getOwner().gPosition,0,"iron_ingot",1,0,0,0f,0f,null);
			
			Queue<ItemAbstract> usedItems = new Queue<ItemAbstract>();
			Queue<ItemAbstract> producedItems = new Queue<ItemAbstract>();
			
			ItemRecipe recipe_ingot = new ItemRecipe(usedItems, producedItems);
			usedItems.addFirst(bucket);
			producedItems.addFirst(ingot);
    	
			if(this.getOwner().getMissionQueue().size==0){
				this.getOwner().getMissionQueue().addLast(new MissionCollect());
				this.getOwner().getMissionQueue().addLast(new MissionProduce());
				this.getOwner().getMissionQueue().addLast(new MissionDrop());
			}
			
			for(int i=0;i<this.getOwner().getMissionQueue().size;i++){
				if(this.getOwner().getMissionQueue().get(i) instanceof MissionCollect){
					((MissionCollect)this.getOwner().getMissionQueue().get(i)).assign(this.getOwner(), bucket);
				}
				else if(this.getOwner().getMissionQueue().get(i) instanceof MissionProduce){
					((MissionProduce)this.getOwner().getMissionQueue().get(i)).assign(this.getOwner(), recipe_ingot);
				}
				else if(this.getOwner().getMissionQueue().get(i) instanceof MissionDrop){
					((MissionDrop)this.getOwner().getMissionQueue().get(i)).assign(this.getOwner(), ingot_dropped, this.getOwner().game.getRandomLoc());
				}
			}	
		}
	}

	
}
