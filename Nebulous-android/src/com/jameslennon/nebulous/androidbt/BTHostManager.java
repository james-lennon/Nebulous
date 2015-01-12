package com.jameslennon.nebulous.androidbt;

import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import com.badlogic.gdx.Gdx;
import com.jameslennon.nebulous.Nebulous;

public class BTHostManager {
	private static final UUID ID = UUID
			.fromString("00001101-0000-1000-8000-b08b5f9b34fb");

	private ArrayList<Host> hosts;

	private boolean disconnected;

	public BTHostManager() {
		hosts = new ArrayList<Host>();
	}

	public void findHost(byte[] data, Host h) {
		int id = hosts.size();
		int len = data[1];
		byte[] ch = new byte[200];
		System.arraycopy(data, 1, ch, 0, len);
		String name = new String(ch);
		Gdx.app.log(Nebulous.log, "Found host: " + name);
		Nebulous.bluetooth.getCallback().onFindHost(name, id);
		hosts.add(h);
	}

	public BluetoothSocket getHost(int id) {
		return hosts.get(id).getSocket();
	}

	public void removeHost(int id) {
		hosts.remove(id);
		Nebulous.bluetooth.getCallback().onHostExit(id);
	}

	public void disconnect() {
		disconnected = true;
		for (Host host : hosts) {
			host.disconnect();
		}
	}

	private class Host {
		private BluetoothDevice device;
		private ConnectThread mThread;

		public Host(BluetoothDevice d) {
			this.device = d;
			mThread = new ConnectThread(d);
			mThread.start();
		}

		public BluetoothSocket getSocket() {
			return mThread.mmSocket;
		}

		public void disconnect() {
			mThread.cancel();
		}

		private class ConnectThread extends Thread {
			public final BluetoothSocket mmSocket;
			private final BluetoothDevice mmDevice;

			public ConnectThread(BluetoothDevice device) {
				// Use a temporary object that is later assigned to mmSocket,
				// because mmSocket is final
				BluetoothSocket tmp = null;
				mmDevice = device;
				try {
					tmp = device.createRfcommSocketToServiceRecord(ID);
				} catch (IOException e) {
				}
				mmSocket = tmp;
			}

			public void run() {
				// Cancel discovery because it will slow down the connection
				// mBluetoothAdapter.cancelDiscovery();
				Gdx.app.log(Nebulous.log,
						"Trying to connect to " + mmDevice.getName());
				try {
					// Connect the device through the socket. This will block
					// until it succeeds or throws an exception
					mmSocket.connect();
				} catch (IOException connectException) {
					// Unable to connect; close the socket and get out
					try {
						mmSocket.close();
					} catch (IOException closeException) {
					}
					Gdx.app.log(Nebulous.log,
							"Connection to " + mmDevice.getName() + " failed!");
					return;
				}
				int n;
				byte[] data = new byte[1024];
				while (true) {
					try {
						n = mmSocket.getInputStream().read(data);
						if (data[0] == 1) {
							if (!disconnected)
								findHost(data, Host.this);
							break;
						}
					} catch (Exception e) {
						e.printStackTrace();
						return;
					}
				}
			}

			public void cancel() {
				try {
					Gdx.app.log(Nebulous.log, "Cancelling host:");
					Thread.dumpStack();
					mmSocket.close();
				} catch (IOException e) {
				}
			}
		}
	}

	public void onGetDevice(BluetoothDevice device) {
		new Host(device);
	}
}
