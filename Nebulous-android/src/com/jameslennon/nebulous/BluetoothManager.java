package com.jameslennon.nebulous;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Looper;

import com.badlogic.gdx.Gdx;
import com.jameslennon.nebulous.androidbt.BTHostManager;
import com.jameslennon.nebulous.androidbt.BTServer;
import com.jameslennon.nebulous.net.BTPeerManager;
import com.jameslennon.nebulous.net.BluetoothClient;
import com.jameslennon.nebulous.net.Lobby;
import com.jameslennon.nebulous.util.UserData;

public class BluetoothManager extends BluetoothClient {

	private enum state {
		hosting, searching, idle;
	}

	private static state s;
	public static final int REQUEST_ENABLE_BT = 1,
			REQUEST_ENABLE_DISCOVERY = 2;
	private MainActivity ma;
	private static final UUID ID = UUID
			.fromString("00001101-0000-1000-8000-b08b5f9b34fb");
	private static AcceptThread acceptThread;
	private boolean registered = false;

	final BroadcastReceiver bReceiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (BluetoothDevice.ACTION_FOUND.equals(action)) {
				BluetoothDevice device = intent
						.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				Gdx.app.log(Nebulous.log,
						"received device: " + device.getName());
				receiveDevice(device);
			}
		}
	};

	private BluetoothAdapter mBluetoothAdapter;
	private BTServer btserv;
	private BTHostManager hostManager;
	private BTPeerManager pm;
	private ServerThread servThread;

	public BluetoothManager(MainActivity a) {
		ma = a;
	}

	protected void receiveDevice(BluetoothDevice device) {
		hostManager.onGetDevice(device);
	}

	public void init() {
		if (Looper.myLooper() == null)
			Looper.prepare();
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if (mBluetoothAdapter == null) {
			Gdx.app.log(Nebulous.log, "Bluetooth not supported");
			// Device does not support Bluetooth
		}
		// if (!mBluetoothAdapter.isEnabled()) {
		// Intent enableBtIntent = new Intent(
		// BluetoothAdapter.ACTION_REQUEST_ENABLE);
		// ma.startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
		// } else {
		// setDiscoverable();
		// }
	}

	public void setup() {
		s = state.idle;
		if (!mBluetoothAdapter.isEnabled()) {
			// Intent enableBtIntent = new Intent(
			// BluetoothAdapter.ACTION_REQUEST_ENABLE);
			// ma.startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
		} else {
			// setDiscoverable();
		}
	}

	public void setDiscoverable() {
		Intent discoverableIntent = new Intent(
				BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
		discoverableIntent.putExtra(
				BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
		Gdx.app.log(Nebulous.log, "Attempting to enable discovery...");
		ma.startActivityForResult(discoverableIntent, REQUEST_ENABLE_DISCOVERY);
		// ma.startActivity(discoverableIntent);
	}

	@Override
	public void startServer() {
		s = state.hosting;
		setDiscoverable();
		Gdx.app.log(Nebulous.log, "Hosting Game...");
		pm = new BTPeerManager();
		btserv = new BTServer(this);
		acceptThread = new AcceptThread();
		acceptThread.start();
	}

	@Override
	public void startClient() {
		if (!mBluetoothAdapter.isEnabled()) {
			Intent enableBtIntent = new Intent(
					BluetoothAdapter.ACTION_REQUEST_ENABLE);
			ma.startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
		} else {
			hostManager = new BTHostManager();
			Set<BluetoothDevice> s = mBluetoothAdapter.getBondedDevices();
			for (BluetoothDevice device : s) {
				receiveDevice(device);
			}
			boolean b = mBluetoothAdapter.startDiscovery();
			Gdx.app.log(Nebulous.log, "Starting discovery: " + b);
			registered = true;
			ma.registerReceiver(bReceiver, new IntentFilter(
					BluetoothDevice.ACTION_FOUND));
		}
	}

	public void dispose() {
		mBluetoothAdapter.cancelDiscovery();
		mBluetoothAdapter.disable();
		ma.unregisterReceiver(bReceiver);
		Looper.myLooper().quit();
	}

	public void onAcceptClient(BluetoothSocket socket) {
		byte[] data = new byte[1024];
		data[0] = 1;
		System.arraycopy(UserData.getName().getBytes(), 0, data, 1, UserData
				.getName().length());
		try {
			socket.getOutputStream().write(data);
		} catch (IOException e) {
			e.printStackTrace();
		}
		btserv.addSocket(socket);
	}

	private class AcceptThread extends Thread {
		private final BluetoothServerSocket mmServerSocket;

		public AcceptThread() {
			BluetoothServerSocket tmp = null;
			try {
				tmp = mBluetoothAdapter.listenUsingRfcommWithServiceRecord(
						"NebulousBluetooth", ID);
			} catch (IOException e) {
			}
			mmServerSocket = tmp;
		}

		public void run() {
			BluetoothSocket socket = null;
			while (true) {
				try {
					Gdx.app.log(Nebulous.log, "Looking for sockets...");
					socket = mmServerSocket.accept();
					Gdx.app.log(Nebulous.log, "Found Socket");
				} catch (IOException e) {
					break;
				}
				if (socket != null) {
					onAcceptClient(socket);
					// try {
					// mmServerSocket.close();
					// } catch (IOException e) {
					// e.printStackTrace();
					// }
					break;
				}
			}
		}

		public void cancel() {
			try {
				Gdx.app.log(Nebulous.log, "Cancelling Accept Thread");
				mmServerSocket.close();
			} catch (IOException e) {
			}
		}
	}

	// @Override
	// public void joinHost(int id) {
	// stopFinding();
	// BluetoothSocket h = hostManager.getHost(id);
	// // hostManager.removeHost(id);
	// pm = new BTPeerManager();
	// String name = UserData.getName();
	// byte[] buff = new byte[1024];
	// buff[0] = 5;
	// buff[1] = (byte) name.length();
	// System.arraycopy(name.getBytes(), 0, buff, 2, name.length());
	// int i = 2 + name.length();
	// buff[i++] = (byte) UserData.getShipType();
	// try {
	// h.getOutputStream().write(buff);
	// } catch (IOException e) {
	// e.printStackTrace();
	// }
	// servThread = new ServerThread(h);
	// servThread.start();
	// }

	// @Override
	// public void stopFinding() {
	// mBluetoothAdapter.cancelDiscovery();
	// // hostManager.disconnect();
	// }

	// @Override
	// public void stopHosting() {
	// acceptThread.cancel();
	// s = state.idle;
	// }

	public static boolean isHosting() {
		return s == state.hosting;
	}

	private class ServerThread extends Thread {
		private final BluetoothSocket mmSocket;
		private final InputStream mmInStream;
		private final OutputStream mmOutStream;

		public ServerThread(BluetoothSocket socket) {
			mmSocket = socket;
			InputStream tmpIn = null;
			OutputStream tmpOut = null;
			try {
				tmpIn = socket.getInputStream();
				tmpOut = socket.getOutputStream();
			} catch (IOException e) {
			}
			mmInStream = tmpIn;
			mmOutStream = tmpOut;
		}

		public void run() {
			byte[] buffer = new byte[1024];
			int bytes;
			while (true) {
				try {
					bytes = mmInStream.read(buffer);
					// Gdx.app.log(Nebulous.log, Arrays.toString(buffer));
					pm.onReceive(buffer);
				} catch (IOException e) {
					break;
				}
			}
		}

		public void write(byte[] bytes) {
			try {
				mmOutStream.write(bytes);
			} catch (IOException e) {
			}
		}

		public void cancel() {
			try {
				mmSocket.close();
			} catch (IOException e) {
			}
		}

	}

	// @Override
	// public void sendToServer(byte[] data) {
	// if (servThread != null)
	// servThread.write(data);
	// else
	// btserv.onReceive(data, null);
	// }

	@Override
	public void startGame() {
		btserv.broadcast(new byte[] { 2 }, (byte) -1);
	}

	@Override
	public Lobby getLobby() {
		return pm;
	}

	@Override
	public void disconnect() {
		// sendToServer(new byte[] { 10, getPlayerId() });
	}

	@Override
	public void sendToServer(byte[] data, boolean reliable) {
		// TODO Auto-generated method stub

	}

	@Override
	public void sendToClients(byte[] data, boolean reliable, boolean toHost) {
		// TODO Auto-generated method stub

	}

	// @Override
	// public void start() {
	// // TODO Auto-generated method stub
	//
	// }

}
