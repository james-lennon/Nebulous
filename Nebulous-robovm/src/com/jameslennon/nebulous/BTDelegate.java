package com.jameslennon.nebulous;

import org.robovm.apple.foundation.NSData;
import org.robovm.apple.foundation.NSObjectProtocol;
import org.robovm.objc.annotation.Method;

public interface BTDelegate extends NSObjectProtocol{
	
	@Method(selector="onPeerConnect")
	public void onPeerConnect();
	
	@Method(selector="serverBecameUnavailable")
	public void onServerDisconnect();
	
	@Method(selector="onError")
	public void onError();
	
	@Method(selector="onReceiveData:")
	public void onReceive(NSData data);
	
	@Method(selector = "stopServer")
	public void stopServer();
	

}
