package com.jameslennon.nebulous;

import org.robovm.apple.foundation.NSData;
import org.robovm.apple.foundation.NSObject;
import org.robovm.objc.annotation.Method;
import org.robovm.objc.annotation.NativeClass;
import org.robovm.rt.bro.annotation.Library;

@Library(Library.INTERNAL)
@NativeClass
public class MatchmakingServer extends NSObject {

	@Method(selector="startAcceptingConnections")
	public native void startServer();
	
	@Method(selector="stopAcceptingConnections")
	public native void stopAccepting();

	@Method(selector="setDelegate:")
	public native void setDelegate(BTDelegate delegate);
	
	@Method(selector="sendDataToClients:reliable:host:")
	public native void sendData(NSData data, boolean reliable, boolean host);
	
	@Method(selector="endSession")
	public native void disconnect();
	
}