package com.jameslennon.nebulous;

import org.robovm.apple.foundation.NSData;
import org.robovm.apple.foundation.NSObject;
import org.robovm.objc.annotation.Method;
import org.robovm.objc.annotation.NativeClass;
import org.robovm.rt.bro.annotation.Library;

@Library(Library.INTERNAL)
@NativeClass
public class BTHelper extends NSObject {

	@Method(selector = "startSearchingForServers")
	public native void searchForServers();

	@Method(selector = "setDelegate:")
	public native void setDelegate(BTDelegate delegate);

	@Method(selector = "sendDataToServer:reliable:")
	public native void sendDataToServer(NSData data, boolean reliable);

	@Method(selector = "startAcceptingConnections")
	public native void startAccepting();

	@Method(selector = "stopAcceptingConnections")
	public native void stopAccepting();

	@Method(selector = "sendDataToClients:reliable:host:")
	public native void sendDataToClients(NSData data, boolean reliable,
			boolean host);

	@Method(selector = "endSession")
	public native void disconnect();

	@Method(selector = "startServer")
	public native void startServer();

	@Method(selector = "startClient")
	public native void startClient();

	// @Method(selector = "startSession")
	// public native void startSession();
}
