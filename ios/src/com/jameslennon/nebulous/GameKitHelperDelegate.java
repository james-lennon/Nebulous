package com.jameslennon.nebulous;

import org.robovm.apple.foundation.NSData;
import org.robovm.apple.foundation.NSObjectProtocol;
import org.robovm.objc.annotation.Method;

public interface GameKitHelperDelegate extends NSObjectProtocol{
	
	@Method(selector="onReceiveData:")
	public void onReceive(NSData data);
	
	@Method(selector="matchStartedAsHost:")
	public void matchStarted(boolean isHost);
	
	@Method(selector="foundMatchWithSize:")
	public void foundMatch(int size);
	
	@Method(selector="onMatchInvite")
	public void onMatchInvite();

}
