package com.mygdx.job;

import com.badlogic.gdx.utils.Queue;
import com.mygdx.need.NeedAbstract;

public class JobAbstractBatch {
	private NeedAbstract drivenNeed;
	
	
	public JobAbstractBatch(NeedAbstract drivenNeed){
		this.drivenNeed = drivenNeed;
	}
	public String getDisplayName(){
		if(drivenNeed==null) 
			return "unknown";
		else
			return drivenNeed.getDisplayName();
		
	}
	private Queue<JobAbstract> job_queue = new Queue<JobAbstract>();
	public Queue<JobAbstract> getBatch() {
		return job_queue;
	}
	public void setBatch(Queue<JobAbstract> batch) {
		this.job_queue = batch;
	}
	public JobAbstract getFirstDoableJob(){
		for(int i=0;i<this.job_queue.size;i++){
			if( !this.job_queue.get(i).isJobDone()  &&  !this.job_queue.get(i).isJobAborted()){
				return this.job_queue.get(i);
			}
		}
		return null;
	}
	public boolean isJobBatchDone(){
		for(int i=0;i<this.job_queue.size;i++){
			if( !this.job_queue.get(i).isJobDone()){
				return false;
			}
		}
		return true;
	}
	

	public boolean isJobBatchAborted(){
		for(int i=0;i<this.job_queue.size;i++){
			if( this.job_queue.get(i).isJobAborted()){
				return true;
			}
		}
		return false;
	}
	
	public NeedAbstract getdrivenNeed() {
		return drivenNeed;
	}
	public void setdrivenNeed(NeedAbstract drivenNeed) {
		this.drivenNeed = drivenNeed;
	}
}
