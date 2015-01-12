package com.jameslennon.nebulous;

import org.robovm.apple.foundation.NSData;
import org.robovm.apple.foundation.NSObject;
import org.robovm.objc.annotation.Method;
import org.robovm.objc.annotation.NativeClass;
import org.robovm.rt.bro.annotation.Library;

@Library(Library.INTERNAL)
@NativeClass
public class GameKitHelper extends NSObject {
	
	@Method(selector="setup")
	public native void setup();

	@Method(selector = "findMatchWithMinPlayers:maxPlayers:")
	public native void findMatchWithMinPlayers(int minPlayers, int maxPlayers);

	@Method(selector = "sendDataToHost:reliable:")
	public native void sendDataToHost(NSData data, boolean reliable);
	
	@Method(selector = "sendDataToClients:reliable:host:")
	public native void sendDataToClients(NSData data, boolean reliable, boolean host);
	
	@Method(selector="setDelegate:")
	public native void setDelegate(GameKitHelperDelegate delegate);
	
	@Method(selector="disconnect")
	public native void disconnect();
	
	@Method(selector="joinInvite")
	public native void joinInvite();

}
