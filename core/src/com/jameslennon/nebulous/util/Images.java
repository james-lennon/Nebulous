package com.jameslennon.nebulous.util;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Images {
	
	private static TextureAtlas atlas;
	
	public static void load(){
		atlas = new TextureAtlas("res/game.atlas");
	}
	
	public static TextureRegion getImage(String name){
		return atlas.findRegion(name);
	}
	
	public static void dispose(){
		atlas.dispose();
	}
	
	public static TextureAtlas getAtlas(){
		return atlas;
	}

}
