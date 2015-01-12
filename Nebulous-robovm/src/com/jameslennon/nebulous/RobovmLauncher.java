package com.jameslennon.nebulous;

import org.robovm.apple.foundation.NSAutoreleasePool;
import org.robovm.apple.foundation.NSDictionary;
import org.robovm.apple.foundation.NSString;
import org.robovm.apple.uikit.UIApplication;

import com.badlogic.gdx.backends.iosrobovm.IOSApplication;
import com.badlogic.gdx.backends.iosrobovm.IOSApplicationConfiguration;

public class RobovmLauncher extends IOSApplication.Delegate {

	@Override
	protected IOSApplication createApplication() {
		Nebulous.social = new IosSocialManager();
		Nebulous.bluetooth = new IOSBluetoothManager();
		Nebulous.iap = new IAPManager();
		
//		Appirater.setAppId("com.jameslennon.nebulous");
//		Appirater.setUsesUntilPrompt(1);
		
		IOSApplicationConfiguration config = new IOSApplicationConfiguration();
		return new IOSApplication(new Nebulous(), config);
	}

	@Override
	public boolean didFinishLaunching(UIApplication application,
			NSDictionary<NSString, ?> launchOptions) {
		super.didFinishLaunching(application, launchOptions);
		return true;
	}

	public static void main(String[] argv) {
		NSAutoreleasePool pool = new NSAutoreleasePool();
		UIApplication.main(argv, null, RobovmLauncher.class);
		pool.close();
	}
}