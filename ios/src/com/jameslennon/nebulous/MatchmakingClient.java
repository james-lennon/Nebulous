package com.jameslennon.nebulous;

import org.robovm.apple.foundation.NSData;
import org.robovm.apple.foundation.NSObject;
import org.robovm.objc.annotation.Method;
import org.robovm.objc.annotation.NativeClass;
import org.robovm.rt.bro.annotation.Library;

@Library(Library.INTERNAL)
@NativeClass
public class MatchmakingClient extends NSObject {

	@Method(selector = "startSearchingForServers")
	public native void searchForServers();

	@Method(selector = "setDelegate:")
	public native void setDelegate(BTDelegate delegate);

	@Method(selector = "sendDataToServer:reliable:")
	public native void sendDataToServer(NSData data, boolean reliable);

	@Method(selector = "disconnectFromServer")
	public native void disconnect();

	@Method(selector = "stopSearching")
	public native void stopSearching();
}
