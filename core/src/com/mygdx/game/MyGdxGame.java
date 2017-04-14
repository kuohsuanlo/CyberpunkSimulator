package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;



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
	private ObjectNPC[] npc_list;
	
	@Override
	public void create () {
		batch = new SpriteBatch();
		this.initMap();
		this.initNpc();
	}
	

	@Override
	public void render () {
		batch.begin();
		
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		//this.drawTerrain();
		this.callNpcAI();
		this.drawNpc();
		Gdx.graphics.setTitle("Current AI_NPCs number : "+ npc_number+" / FPS : "+Gdx.graphics.getFramesPerSecond());
		
		batch.end();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
		disposeNpc();
	}
	
	
	
	private void initMap(){
		map = new ObjectMap();

		this.map_render_size_x = this.map.x_MAX;
		this.map_render_size_y = this.map.y_MAX;
		this.map_buffer = new int[this.map_render_size_x*this.map_render_size_y];
	}
	private void initNpc(){
		npc_list = new ObjectNPC[npc_number];
		for(int id=0;id<npc_number;id++){
			npc_list[id] = new ObjectNPC(id,id%npc_resource_nubmer,map);
		}
	}	
	private void disposeNpc(){
		for(int i=0;i<npc_number;i++){
			npc_list[i].texture.dispose();
		}
	}
	private void callNpcAI(){
		for(int i=0;i<npc_number;i++){
			npc_list[i].doAI();
		}
		
	}
	
	private void drawNpc(){
		for(int i=0;i<npc_number;i++){
			npc_list[i].render(batch);
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

	
	

}
