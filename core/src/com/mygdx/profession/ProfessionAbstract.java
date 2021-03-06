package com.mygdx.profession;

import com.badlogic.gdx.utils.Queue;
import com.mygdx.character.ObjectAbstract;
import com.mygdx.character.ObjectNPC;
import com.mygdx.job.JobAbstract;
import com.mygdx.mission.MissionAbstract;

public abstract class ProfessionAbstract {
	/*
	 * There are several types of things a professional people would do.
	 * 1. produceItem					: produce stuffs from stuffs, including produce air from trash.(cleaner)
	 * 2. produceServiceEffect			: give massage to someone to release its pain
	 * 3. produceServiceManagement		: make people capable of doing something more efficiently, or relaying jobs for others
	 * 4. produceServiceFollowing		: act like a body guard, an assassin, or stalker to gather information
	 * 
	 * These function will assign a specific series of job(s) into one's job queue.
	 * Profession specification could be look up in GURPS-Cyberpunk book from page.8 - page.18.
	 * 
	 * Still, one character shouldn't be restricted to his or her profession, including the NPCs. 
	 * A profession should act like a knowledge base. One character could have more than one (and zero) profession.
	 * A profession itself would have a skill point 0-1000.
	 */
	private ObjectNPC owner=null;
	private int currentLevel=0;
	private float professionPoints =0;
	private float[] professionLevelThreshold = {0,250,500,750,1000};
	private String[] professionLevelTitle;
	private String professionName;
	
	/*
	 * These jobs in the queue will be calculated as this profession's related jobs, 
	 * which will increase the professon's point by calling addProfessionPoints();
	 * 
	 * Notice that JobAbstract's comparator is specified for profession's related jobs testing, 
	 * so the comparator is different from ItemAbstract's.
	 */
	private Queue<MissionAbstract> professionMissionQueue;

	public ProfessionAbstract(ObjectNPC owner, float professionPoints,String professionName,  String[] professionLevelTitle ){
		this.owner = owner;
		this.professionPoints = professionPoints;
		this.professionName = professionName;
		this.professionLevelTitle = professionLevelTitle;
		

		if(this.professionName==null){
			this.professionName="";
		}
		if(this.professionLevelTitle==null){
			this.professionLevelTitle = new String[5];
			this.professionLevelTitle[0] ="Novice";
			this.professionLevelTitle[1] ="Junior";
			this.professionLevelTitle[2] ="Senior";
			this.professionLevelTitle[3] ="Adept";
			this.professionLevelTitle[4] ="Master";
		}
		if(this.professionMissionQueue ==null){
			this.professionMissionQueue = new Queue<MissionAbstract>();
		}
		
		this.updateProfessionLevel();
		
	}

	public abstract void checkMissions();
	
	
	public ObjectNPC getOwner(){
		return this.owner;
	}
	public float getProfessionPoints(){
		return this.professionPoints;
	}
	public int getProfessionLevel(){
		return this.currentLevel;
	}
	public void addProfessionPoints(float p){
		this.professionPoints+=p;
		if(this.professionPoints>=this.professionLevelThreshold[this.professionLevelThreshold.length]){
			this.professionPoints=this.professionLevelThreshold[this.professionLevelThreshold.length];
		}
		else if(this.professionPoints<=this.professionLevelThreshold[0]){
			this.professionPoints=this.professionLevelThreshold[0];
		}
		this.updateProfessionLevel();
	}
	private void updateProfessionLevel(){
		int currentLevel =0;
		for(int i=0;i<this.professionLevelThreshold.length;i++){
			if(this.professionPoints>=this.professionLevelThreshold[i]){
				currentLevel = i;
			}
		}
		this.currentLevel = currentLevel;
	}
	public String getDisplayName(){
		return this.professionLevelTitle[this.getProfessionLevel()]+" "+this.professionName;
	}
	
}
