package com.jameslennon.nebulous.util;

public enum GameType {
	singleplayer, online, bluetooth;
	private static GameType type;

	public static void setType(GameType t) {
		type = t;
	}

	public static GameType getType() {
		return type;
	}
}
