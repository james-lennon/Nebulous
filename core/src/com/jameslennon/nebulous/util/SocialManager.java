package com.jameslennon.nebulous.util;

public interface SocialManager {
	public void init();
	public void showLeaderboard();
	public void finish();
	public void singlePlayerScore(long score);
	public void onAchievement(String name);
	public void startMatchmaking();
	public void send(byte[] data, boolean reliable);
	void checkInvite();
	void disconnect();
}
