package com.jameslennon.nebulous;

import android.content.Intent;
import android.os.Bundle;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;

public class MainActivity extends AndroidApplication {
	private BluetoothManager bm;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		AndroidApplicationConfiguration cfg = new AndroidApplicationConfiguration();

//		Nebulous.bluetooth = bm = new BluetoothManager(this);
		
		initialize(new Nebulous(), cfg);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == BluetoothManager.REQUEST_ENABLE_BT) {
			Gdx.app.log("", resultCode + "");
			bm.findGames();
		} else if (requestCode == BluetoothManager.REQUEST_ENABLE_DISCOVERY) {
//			bm.connect();
		}
	}
}