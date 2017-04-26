package com.mygdx.util;

import com.mygdx.character.ObjectNPC;
import com.mygdx.need.NeedAbstract;

public class CharacterUtility {
	public static float calculateBaseEC(ObjectNPC npc){
		
		float baseEC_tmp=0;
		for(int i=0;i<npc.getBpNumber();i++){
			baseEC_tmp+=(npc.getBpTraits()[i].traits.str+npc.getBpTraits()[i].traits.vit);
		}
		return baseEC_tmp*0.5f*(npc.game.getRealTimeRatio()/3000f);//100f
	}
	public static float calculateSpeed(ObjectNPC npc){
		float speed_tmp=0;
		
		for(int i=0;i<npc.getBpNumber();i++){
			speed_tmp+=npc.getBpTraits()[i].traits.dex;
		}
		return speed_tmp/100;
	}
	
	public static float calculateNeedMax(ObjectNPC npc, int type){
	
		float base_tmp=0;
		switch(type){
			case(NeedAbstract.NEED_FATIGUE_ID):
				for(int i=0;i<npc.getBpNumber();i++){
					base_tmp+=npc.getBpTraits()[i].traits.vit; //0-6
				}
				return base_tmp*100f*(0.5f+0.5f*npc.getRandom().nextFloat());
			case(NeedAbstract.NEED_HUNGER_ID):
				for(int i=0;i<npc.getBpNumber();i++){
					base_tmp+=npc.getBpTraits()[i].traits.vit; //0-6
				}
				return base_tmp*100f*(0.5f+0.5f*npc.getRandom().nextFloat());
			case(NeedAbstract.NEED_THIRST_ID):
				for(int i=0;i<npc.getBpNumber();i++){
					base_tmp+=npc.getBpTraits()[i].traits.vit; //0-6
				}
				return base_tmp*100f*(0.5f+0.5f*npc.getRandom().nextFloat());
			default:
				return 100;
		}
		
	}
}
