package com.mygdx.profession;

import com.mygdx.game.ObjectAbstract;
import com.mygdx.item.ItemAbstract;

public abstract class ProfessionAbstract {
	/*
	 * There are several types of things a professional people would do.
	 * 1. produceItem					: produce stuffs from stuffs, including produce air from trash.(cleaner)
	 * 2. produceServiceEffect			: give massage to someone to release its pain
	 * 3. produceServiceManagement		: make people capable of doing something more efficiently, or relaying jobs for others
	 * 4. produceServiceFollowing		: act like a body guard, an assassin, or stalker to gather information
	 * 
	 * These function will assign a specific series of job(s) into one's job queue.
	 * Profession specification could be look up in GURPS Cyberpunk book from page.8 - page.18.
	 * 
	 * Still, one character shouldn't be restricted to his or her profession, including the NPCs. 
	 * A profession should act like a knowledge base. One character could have more than one (and zero) profession.
	 * A profession itself would have a skill point 0-1000.
	 */
	private float professionalPoints =0;
	
	public abstract void produceItem();
	public abstract void produceServiceEffect(ObjectAbstract oa);
	public abstract void produceServiceManagement(ObjectAbstract oa);
	public abstract void produceServiceFollowing(ObjectAbstract oa);

	public float getProfessionalPoints(){
		return this.professionalPoints;
	}
	
}
