package com.mygdx.game;

import java.util.Random;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Queue;
import com.mygdx.item.ItemAbstract;
import com.mygdx.item.ItemFood;
import com.mygdx.need.NeedAbstract;
import com.mygdx.need.NeedHunger;
import com.mygdx.need.NeedThirst;



/* https://www.youtube.com/watch?v=oNPD78okXUw&list=PLXY8okVWvwZ0JOwHiH1TntAdq-UDPnC2L&index=3
 * 
 * 
 * 
 * 
 * 
 * */
public class MyGdxGame extends ApplicationAdapter {

	public static final int npc_number = 1000;
	private int npc_resource_nubmer = 250;
	public static int current_block_size = 16;
	private SpriteBatch batch;
	private ObjectMap map;
	
	
	
	private int map_render_size_x ;
	private int map_render_size_y ;
	private int[] map_buffer;
	private Queue<ObjectNPC> npc_queue;
	private Queue<ItemAbstract> item_queue;
	
	private Random random = new Random();
	
	@Override
	public void create () {
		batch = new SpriteBatch();
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
		//this.drawTerrain();
		
		this.callItem();
		this.callNpc();
		
		this.drawItem();
		this.drawNpc();
		
		this.drawItemFont();
		this.drawNpcFont();
		
		Gdx.graphics.setTitle("Current AI_NPCs number : "+ npc_number+" / FPS : "+Gdx.graphics.getFramesPerSecond());
		
		batch.end();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
		disposeNpc();
	}
	private void initModule(){
		InGameInputProcessor inputProcessor = new InGameInputProcessor(this);
		Gdx.input.setInputProcessor(inputProcessor);
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
			npc_queue.addLast( new ObjectNPC(id,id%npc_resource_nubmer,ObjectNPC.HUMAN,map) );
		}
	}	
	private void initItem(){
		for(int i=0;i<npc_number;i++){
			item_queue.addFirst(new ItemFood(5,getRandomLoc() ,0,"free food",1,NeedAbstract.NEED_HUNGER_ID,NeedAbstract.NEED_THIRST_ID,50,50,null));
		}
	}
	public void addRandomItem(Vector2 loc){
		item_queue.addFirst(new ItemFood(5,loc ,0,"free food",1,NeedAbstract.NEED_HUNGER_ID,NeedAbstract.NEED_THIRST_ID,50,50,null));
	}
	private Vector2 getRandomLoc(){
		return new Vector2(random.nextFloat()*Gdx.graphics.getWidth(),random.nextFloat()*Gdx.graphics.getHeight());
	}
	
	private void disposeNpc(){
		for(int i=0;i<npc_number;i++){
			npc_queue.get(i).texture.dispose();
		}
	}
	private void callNpc(){
		for(int i=0;i<npc_number;i++){
			npc_queue.get(i).doAI();
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
		for(int i=0;i<npc_number;i++){
			//npc_list[i].render(batch);
			npc_queue.get(i).c2s();
			npc_queue.get(i).renderSelf(batch);
		}	
	}
	
	private void drawItem(){
		for(int i=0;i<item_queue.size;i++){
			item_queue.get(i).c2s();
			item_queue.get(i).renderSelf(batch);
		}
	}
	
	private void drawNpcFont(){
		for(int i=0;i<npc_number;i++){
			//npc_list[i].render(batch);
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
	public Vector2 s2c(int x, int y){
		return new Vector2(x,Gdx.graphics.getHeight()-y);

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
    		mgg.addRandomItem(mgg.s2c(x, y));
    		return true;     
    	}
    	return false;
    }

	@Override
	public boolean keyDown(int keycode) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		// TODO Auto-generated method stub
		return false;
	}
}
