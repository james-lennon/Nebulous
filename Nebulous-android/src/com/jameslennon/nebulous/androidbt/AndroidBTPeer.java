package com.jameslennon.nebulous.androidbt;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

import android.bluetooth.BluetoothSocket;

import com.badlogic.gdx.Gdx;
import com.jameslennon.nebulous.BluetoothManager;
import com.jameslennon.nebulous.Nebulous;
import com.jameslennon.nebulous.net.BTPeerManager;
import com.jameslennon.nebulous.net.Peer;
import com.jameslennon.nebulous.util.UserData;

public class AndroidBTPeer extends Peer {

	private BluetoothSocket sock;
	private ConnectedThread mThread;
	private BTServer server;
	private boolean initialized;

	public AndroidBTPeer(BluetoothSocket sock, BTServer s) {
		this.sock = sock;
		mThread = new ConnectedThread(sock);
		server = s;
	}

	public AndroidBTPeer() {
		setName(UserData.getName());
		setShipType(UserData.getShipType());
	}

	public void listen() {
		if (mThread != null)
			mThread.start();
	}

	public void stop() {
		if (mThread != null)
			mThread.cancel();
	}

	public void write(byte[] data) {
		if (mThread != null)
			mThread.write(data);
		else {
			((BTPeerManager) Nebulous.bluetooth.getLobby()).onReceive(data);
		}
	}

	public void initialize(byte[] data) {
		int ulen = data[1];
		try {
			char[] buff = new char[ulen];
			for (int i = 0; i < ulen; i++) {
				buff[i] = (char) data[i + 2];
			}
			setName(new String(buff));
			setShipType(data[2 + ulen]);
			initialized = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public byte[] toByteArray() {
		String name = getName();
		byte[] buff = new byte[1024];
		buff[0] = 5;
		buff[1] = (byte) name.length();
		System.arraycopy(name.getBytes(), 0, buff, 2, name.length());
		int i = 2 + name.length();
		buff[i++] = (byte) getId();
		buff[i++] = (byte) getShipType();
		return buff;
	}

	private class ConnectedThread extends Thread {
		private final BluetoothSocket mmSocket;
		private final InputStream mmInStream;
		private final OutputStream mmOutStream;

		public ConnectedThread(BluetoothSocket socket) {
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
			while (initialized || BluetoothManager.isHosting()) {
				try {
					bytes = mmInStream.read(buffer);
					server.onReceive(buffer, AndroidBTPeer.this);
				} catch (IOException e) {
					if (initialized)
						server.removePeer(AndroidBTPeer.this);
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

}
