package com.mygdx.profession;

import com.badlogic.gdx.utils.Queue;
import com.mygdx.character.ObjectNPC;
import com.mygdx.item.ItemAbstract;
import com.mygdx.item.ItemRecipe;
import com.mygdx.mission.MissionCollect;
import com.mygdx.mission.MissionDrop;
import com.mygdx.mission.MissionProduce;

public class ProfessionCollecter extends ProfessionAbstract{

	public ProfessionCollecter(ObjectNPC owner, float professionPoints, String professionName,
			String[] professionLevelTitle) {
		super(owner, professionPoints, professionName, professionLevelTitle);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void checkMissions() {

		if(this.getOwner().isTooBusyToAcceptMission()) return;
		
		ItemAbstract tool = new ItemAbstract(7,this.getOwner().gPosition,0,"",1,0,0,0f,0f,null);		
		
		if(this.getOwner().getMissionQueue().size==0){
			this.getOwner().getMissionQueue().addLast(new MissionCollect());
		}
		
		for(int i=0;i<this.getOwner().getMissionQueue().size;i++){
			if(this.getOwner().getMissionQueue().get(i) instanceof MissionCollect){
				((MissionCollect)this.getOwner().getMissionQueue().get(i)).assign(this.getOwner(), tool);
			}
		}
		
			
	}
		
}