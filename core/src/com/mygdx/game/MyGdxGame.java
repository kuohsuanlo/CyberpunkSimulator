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
import com.mygdx.item.ItemAbstract;
import com.mygdx.item.ItemFood;
import com.mygdx.job.JobRest;
import com.mygdx.need.NeedAbstract;
import com.mygdx.need.NeedHunger;
import com.mygdx.need.NeedThirst;
import com.mygdx.util.ThreadNpcAI;



/* https://www.youtube.com/watch?v=oNPD78okXUw&list=PLXY8okVWvwZ0JOwHiH1TntAdq-UDPnC2L&index=3
 * 
 * 
 * 
 * 
 * 
 * */
public class MyGdxGame extends ApplicationAdapter {

	public static final int npc_number = 10;
	public static final int avg_aiq_number = 200;
	private int npc_resource_nubmer = 250;
	public static int current_block_size = 16;
	private SpriteBatch batch;
	private BitmapFont font;
	private ObjectMap map;
	
	private int map_render_size_x ;
	private int map_render_size_y ;
	private int[] map_buffer;
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
	
	@Override
	public void create () {
		batch = new SpriteBatch();
		font = new BitmapFont();
		
		this.initThreadPool();
		this.initMap();
		this.initNpc();
		this.initItem();
		this.initModule();
	}
	

	@Override
	public void render () {
		batch.begin();
		
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		if(!this.gamePause){
			this.callItem();
			this.callNpc();
			
		}
		
		this.cam.update();
		this.batch.setProjectionMatrix(cam.combined); //Important

		this.drawItem();
		this.drawNpc();
		
		this.drawItemFont();
		this.drawNpcFont();
		
		this.drawGameFont();
		
		Gdx.graphics.setTitle("Current AI_NPCs number : "+ npc_queue.size+" / FPS : "+Gdx.graphics.getFramesPerSecond());
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
		this.threadnpc_pool_number = Math.max(2, Runtime.getRuntime().availableProcessors());
		this.threadnpc_pool= new Queue<ThreadNpcAI>();
		for(int i=0;i<threadnpc_pool_number;i++){
			ThreadNpcAI Ttmp = new ThreadNpcAI(this);
			this.threadnpc_pool.addLast(Ttmp);
			Ttmp.start();
		}
	}
	
	private void initMap(){
		item_queue = new Queue<ItemAbstract>();
		map = new ObjectMap(this.item_queue);

		this.map_render_size_x = this.map.x_MAX;
		this.map_render_size_y = this.map.y_MAX;
		this.map_buffer = new int[this.map_render_size_x*this.map_render_size_y];
	}
	private void initNpc(){
		npc_queue = new Queue<ObjectNPC>();
		for(int id=0;id<npc_number;id++){
			npc_queue.addLast( new ObjectNPC(id,id%npc_resource_nubmer,ObjectNPC.HUMAN,map,random,this) );
		}
	}	
	private void initItem(){
		ItemAbstract bucket = new ItemAbstract(3,getRandomLoc(),0,"bucket",1,0,0,0f,0f,null);
		
		for(int i=0;i<npc_number;i++){
			item_queue.addFirst(new ItemFood(5,getRandomLoc() ,0,"free food",100,NeedAbstract.NEED_HUNGER_ID,NeedAbstract.NEED_FATIGUE_ID,200,0,null));
			item_queue.addFirst(new ItemFood(4,getRandomLoc() ,0,"free water",100,NeedAbstract.NEED_THIRST_ID,NeedAbstract.NEED_FATIGUE_ID,200,0,null,bucket));
		}
	}
	public void addRandomItem(Vector2 loc, int times){
		for(int i=0;i<times;i++){
			if(random.nextBoolean()){
				item_queue.addFirst(new ItemFood(5,getRandomLoc() ,0,"free food",1,NeedAbstract.NEED_HUNGER_ID,NeedAbstract.NEED_FATIGUE_ID,200,0,null));
			}
			else{
				ItemAbstract bucket = new ItemAbstract(3,getRandomLoc(),0,"bucket",1,0,0,0f,0f,null);
				item_queue.addFirst(new ItemFood(4,getRandomLoc() ,0,"free water",1,NeedAbstract.NEED_THIRST_ID,NeedAbstract.NEED_FATIGUE_ID,200,0,null,bucket));
			}
			item_queue.addFirst(new ItemFood(3,getRandomLoc() ,0,"free bucket",1,0,0,0f,0f,null));
			
		}
	}
	
	private Vector2 getRandomLoc(){
		return new Vector2(random.nextFloat()*Gdx.graphics.getWidth(),random.nextFloat()*Gdx.graphics.getHeight());
	}
	
	private void disposeNpc(){
		for(int i=0;i<npc_queue.size;i++){
			npc_queue.get(i).texture.dispose();
		}
	}
	private void callResource(){
		if(random.nextDouble()<0.01){
			this.addRandomItem(this.getRandomLoc(), 1);
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
		for(int i=0;i<item_queue.size;i++){
			item_queue.get(i).itemTimePass();
			if(item_queue.get(i).itemNeedDestroy()){
				item_queue.removeIndex(i);
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
			npc_queue.get(i).renderFont(batch);
		}	
	}
	
	private void drawItemFont(){
		for(int i=0;i<item_queue.size;i++){
			item_queue.get(i).renderFont(batch);
		}
	}
	private void drawTerrain(){
		//Get rendered area's data into 1-dimension array
		for(int i=0;i<this.map_render_size_x;i++){
			for(int j=0;j<this.map_render_size_y;j++){
				this.map_buffer[i*this.map_render_size_x+j] = map.type[i][j];
			}
		}
		
		//Draw 
		for(int i=0;i<this.map_render_size_x*this.map_render_size_y;i++){
			batch.draw(map.tr_texture[this.map_buffer[i]],this.current_block_size*(i/this.map_render_size_x),current_block_size*(i%this.map_render_size_x));
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
	public boolean isGamePause() {
		return gamePause;
	}
	public void setGamePause(boolean gamePause) {
		this.gamePause = gamePause;
	}
	public Vector2 s2c(Vector2 sc){
		return new Vector2(sc.x,Gdx.graphics.getHeight()-sc.y);

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
    		mgg.addRandomItem(mgg.s2c(new Vector2(x, y)),1);
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
