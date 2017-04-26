package com.mygdx.game;

import java.util.Random;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Queue;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.mygdx.character.ObjectNPC;
import com.mygdx.item.ItemAbstract;
import com.mygdx.job.JobRest;
import com.mygdx.need.NeedAbstract;
import com.mygdx.need.NeedHunger;
import com.mygdx.need.NeedThirst;
import com.mygdx.util.CoorUtility;
import com.mygdx.util.ThreadNpcAI;



/* https://www.youtube.com/watch?v=oNPD78okXUw&list=PLXY8okVWvwZ0JOwHiH1TntAdq-UDPnC2L&index=3
 * 
 * 
 * 
 * 
 * 
 * */
public class MyGdxGame extends ApplicationAdapter {

	public static final int npc_number = 500;
	public static final int avg_aiq_number = 400;
	public static final int npc_resource_nubmer = 1000;
	
	private SpriteBatch batch;
	private BitmapFont font;

	private Queue<ObjectNPC> npc_queue;
	private Queue<ItemAbstract> item_queue;
	
	private Queue<ThreadNpcAI> threadnpc_pool;
	private int threadnpc_pool_number;
	private Random random = new Random();
	
	public OrthographicCamera cam;
	public FitViewport viewport ;
	
	private int mouseX;
	private int mouseY;
	
	private boolean gamePause;
	private boolean fontPrint;
	private long lastTick;
	private float lastTimeElapsed;
	/*
	 * 100 = NORMAL SPEED;
	 */
	private float realTimeRatio = 100; 
	
	
	@Override
	public void create () {
		batch = new SpriteBatch();
		font = new BitmapFont();
		
		this.initThreadPool();
		Gdx.app.log("STARTING","initThreadPool done");
		this.initMap();
		Gdx.app.log("STARTING","initMap done");
		this.initNpc();
		Gdx.app.log("STARTING","initNpc done");
		this.initItem();
		Gdx.app.log("STARTING","initItem done");
		this.initModule();
		Gdx.app.log("STARTING","initModule done");
	}
	

	@Override
	public void render () {
		batch.begin();
		
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		if(!this.gamePause  &&  this.realTimeRatio!=0){
			this.setLastTick(System.currentTimeMillis());
			
			this.callItem();
			this.callNpc();
		}
		
		this.cam.update();
		this.batch.setProjectionMatrix(cam.combined); //Important

		this.drawItem();
		this.drawNpc();
		
		if(this.isFontPrint()){
			this.drawItemFont();
			this.drawNpcFont();
		}
		this.drawGameFont();
		
		Gdx.graphics.setTitle("Current AI_NPCs number : "+ npc_queue.size+" / FPS : "+Gdx.graphics.getFramesPerSecond()+" / GAME SPEED : "+this.realTimeRatio/100f);
		//Gdx.app.log("FPS",Gdx.graphics.getFramesPerSecond()+"");
		batch.end();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
		disposeNpc();
	}
	
	@Override
	public void resize(int width, int height){
	    viewport.update(width, height, true);

	}
	
	private void initModule(){
		
		cam = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		viewport = new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), cam);
		cam.position.set(cam.viewportWidth / 2f, cam.viewportHeight / 2f, 0);
		cam.update();
		
		InGameInputProcessor inputProcessor = new InGameInputProcessor(this);
		Gdx.input.setInputProcessor(inputProcessor);
	}
	private void initThreadPool(){
		this.gamePause = false;
		this.lastTick = System.currentTimeMillis();
		
		this.threadnpc_pool_number = Math.max(2, Runtime.getRuntime().availableProcessors());
		//this.threadnpc_pool_number = Math.max(2, 2);
		this.threadnpc_pool= new Queue<ThreadNpcAI>();
		for(int i=0;i<threadnpc_pool_number;i++){
			ThreadNpcAI Ttmp = new ThreadNpcAI(this);
			this.threadnpc_pool.addLast(Ttmp);
			Ttmp.start();
		}
	}
	
	private void initMap(){
		item_queue = new Queue<ItemAbstract>();
	}
	private void initNpc(){
		npc_queue = new Queue<ObjectNPC>();
		for(int id=0;id<npc_number;id++){
			npc_queue.addLast( new ObjectNPC(id,id%npc_resource_nubmer,ObjectNPC.HUMAN,random,this) );
		}
	}	
	private void initItem(){
		ItemAbstract bucket = new ItemAbstract(3,getRandomLoc(),0,"bucket",1,0,0,0f,0f,null);
		
		for(int i=0;i<npc_number/5;i++){
			item_queue.addFirst(new ItemAbstract(3,getRandomLoc() ,0,"free bucket",200,0,0,0f,0f,null));
			item_queue.addFirst(new ItemAbstract(5,getRandomLoc() ,0,"free food",200,NeedAbstract.NEED_HUNGER_ID,NeedAbstract.NEED_FATIGUE_ID,200,0,null));
			item_queue.addFirst(new ItemAbstract(4,getRandomLoc() ,0,"free water",200,NeedAbstract.NEED_THIRST_ID,NeedAbstract.NEED_FATIGUE_ID,200,0,null,bucket));
		}
	}
	public void addItem(ItemAbstract ia,Vector2 loc){
		ia.gPosition.x = loc.x;
		ia.gPosition.y = loc.y;
		item_queue.addFirst(ia);
	}
	public void addRandomItem(Vector2 loc, int times){
		for(int i=0;i<times;i++){
			item_queue.addFirst(new ItemAbstract(5,getRandomLoc() ,0,"free food",1,NeedAbstract.NEED_HUNGER_ID,NeedAbstract.NEED_FATIGUE_ID,200,0,null));
			ItemAbstract bucket = new ItemAbstract(3,getRandomLoc(),0,"bucket",1,0,0,0f,0f,null);
			item_queue.addFirst(new ItemAbstract(4,getRandomLoc() ,0,"free water",1,NeedAbstract.NEED_THIRST_ID,NeedAbstract.NEED_FATIGUE_ID,200,0,null,bucket));
			
			item_queue.addFirst(new ItemAbstract(3,getRandomLoc() ,0,"free bucket",1,0,0,0f,0f,null));
			item_queue.addFirst(new ItemAbstract(6,getRandomLoc() ,0,"free ingot",1,0,0,0f,0f,null));
			item_queue.addFirst(new ItemAbstract(7,getRandomLoc() ,0,"free tool",1,0,0,0f,0f,null));
			
		}
	}
	
	public Vector2 getRandomLoc(){
		return new Vector2(random.nextFloat()*Gdx.graphics.getWidth(),random.nextFloat()*Gdx.graphics.getHeight());
	}
	
	private void disposeNpc(){
		for(int i=0;i<npc_queue.size;i++){
			npc_queue.get(i).texture.dispose();
		}
	}
	private void callNpc(){
		for(int i=0;i<npc_queue.size;i++){
			npc_queue.get(i).doAI();
			if(npc_queue.get(i).isNpcDead()){
				npc_queue.removeIndex(i);
			}
		}
		
	}
	private void callItem(){
		timePassItem();
		cleanItem();
	}
	private void timePassItem(){
		for(int i=0;i<item_queue.size;i++){
			item_queue.get(i).itemTimePass(this.realTimeRatio);
		}
	}
	private void cleanItem(){
		synchronized(item_queue){
			for(int i=0;i<item_queue.size;i++){
				synchronized(item_queue.get(i)){
					if(item_queue.get(i).itemNeedDestroy()){
						item_queue.removeIndex(i);
					}
				}
			}
		}
		
	}
	private void drawNpc(){
		for(int i=0;i<npc_queue.size;i++){
			npc_queue.get(i).updateScreenCoor(this);
			npc_queue.get(i).renderSelf(batch);
		}	
	}
	
	private void drawItem(){
		for(int i=0;i<item_queue.size;i++){
			item_queue.get(i).updateScreenCoor(this);
			item_queue.get(i).renderSelf(batch);
		}
	}
	
	private void drawNpcFont(){
		for(int i=0;i<npc_queue.size;i++){
			npc_queue.get(i).renderFont(batch,font);
		}	
	}
	
	private void drawItemFont(){
		for(int i=0;i<item_queue.size;i++){
			item_queue.get(i).renderFont(batch,font);
		}
	}
	private void drawGameFont(){
		font.draw(batch, Gdx.graphics.getFramesPerSecond()+"/ Mouse"+this.getMouseX()+","+this.getMouseY(), Gdx.graphics.getWidth()/2,Gdx.graphics.getHeight()/2);
	}
	public int getThreadPoolNpcNumber(){
		return this.threadnpc_pool_number;
	}
	public ThreadNpcAI getThreadNpc(){
		return this.threadnpc_pool.get(random.nextInt(threadnpc_pool_number));
		//return getLeastBusyThread();
	}
	public int getThreadNpcAIQueueMaxNumber(){
		return npc_number*avg_aiq_number/this.threadnpc_pool_number;
	}
	public ThreadNpcAI getLeastBusyThread(){
		int min_idx=-1;
		int min_number=getThreadNpcAIQueueMaxNumber()+1;
		for(int i=0;i<threadnpc_pool.size;i++){
			if(threadnpc_pool.get(i).getCurrentRequestNumber()<min_number){
				min_idx = i;
				min_number = threadnpc_pool.get(i).getCurrentRequestNumber();
			}
		}
		if(min_idx==-1) return null;
		else{
			return this.threadnpc_pool.get(min_idx);
		}
	}
	public Queue<ItemAbstract> getItemQueue() {
		return item_queue;
	}
	public boolean isGamePause() {
		return gamePause;
	}
	public void setGamePause(boolean gamePause) {
		if(gamePause==false){
			this.setLastTick(System.currentTimeMillis());
		}
		this.gamePause = gamePause;
	}
	public void setRealTimeRatio(float rtr){
		if(this.realTimeRatio==0  &&  rtr!=0){
			this.setLastTick(System.currentTimeMillis());
		}
		this.realTimeRatio = rtr;
	}
	public float getRealTimeRatio(){
		return this.realTimeRatio;
	}
	public boolean isFontPrint() {
		return fontPrint;
	}
	public void setFontPrint(boolean fontPrint) {
		this.fontPrint = fontPrint;
	}
	public float getLastTimeElapsed() {
		return this.lastTimeElapsed;
	}
	
	private void setLastTick(long lastTick) {
		this.lastTimeElapsed =  (lastTick -this.lastTick)*1.0f/1000f;
		this.lastTick = lastTick;
		
	}

	public int getMouseX() {
		return mouseX;
	}
	public void setMouseX(int mouseX) {
		this.mouseX = mouseX;
	}
	public int getMouseY() {
		return mouseY;
	}
	public void setMouseY(int mouseY) {
		this.mouseY = mouseY;
	}
}

class InGameInputProcessor implements InputProcessor {
    private MyGdxGame mgg;
	public InGameInputProcessor(MyGdxGame mgg){
		this.mgg = mgg;
	}
	
	@Override
    public boolean touchDown (int x, int y, int pointer, int button) {
    	if (button == Input.Buttons.LEFT) {
    		// Put food (testing)
    		mgg.addRandomItem(CoorUtility.cursor2Game(new Vector2(x, y)),1);
    		mgg.setMouseX(x);
    		mgg.setMouseY(y);
    		return true;  
    	}
    	return false;
    }

	@Override
	public boolean keyDown(int keycode) {
		if (keycode == Input.Keys.SPACE) {
			mgg.setGamePause(!mgg.isGamePause());
			return true;  
		}
		else if (keycode == Input.Keys.P) {
			mgg.setFontPrint(!mgg.isFontPrint());
			return true;  
		}
		else if (keycode == Input.Keys.UP) {
			mgg.setRealTimeRatio(mgg.getRealTimeRatio()+20);
			return true;  
		}
		else if (keycode == Input.Keys.DOWN) {
			if(mgg.getRealTimeRatio()>=20)
				mgg.setRealTimeRatio(mgg.getRealTimeRatio()-20);
			
			return true;  
		}

		else if (keycode == Input.Keys.W) {
			mgg.setRealTimeRatio(mgg.getRealTimeRatio()+1000);
			return true;  
		}
		else if (keycode == Input.Keys.S) {
			if(mgg.getRealTimeRatio()>=1000)
				mgg.setRealTimeRatio(mgg.getRealTimeRatio()-1000);
			
			return true;  
		}
		else if (keycode == Input.Keys.ESCAPE) {
			Gdx.app.exit();
		}
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {

		return false;
	}

	@Override
	public boolean keyTyped(char character) {
    	return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		return false;
	}
}
