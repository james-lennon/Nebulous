package com.jameslennon.nebulous;

import java.nio.ByteBuffer;
import java.util.ArrayList;

import org.robovm.apple.foundation.NSData;
import org.robovm.apple.foundation.NSObject;
import org.robovm.objc.annotation.Method;

import com.jameslennon.nebulous.game.Globals;
import com.jameslennon.nebulous.net.BluetoothClient;
import com.jameslennon.nebulous.net.NetworkManager;
import com.jameslennon.nebulous.net.Peer;

public class IOSBluetoothManager extends BluetoothClient {

	private final byte SERV = 17;

	private BTHelper btHelper;
	private Delegate del;
	private int curID;
	private boolean hosting, shouldhost;
	private long startTime;
	private long startHost;
	private ArrayList<Peer> peers;

	byte map;

	@Override
	public void init() {
		btHelper = new BTHelper();
		btHelper.setDelegate(del = new Delegate());
	}

	@Override
	public void sendToServer(byte[] data, boolean reliable) {
		btHelper.sendDataToServer(new NSData(data), reliable);
	}

	@Override
	public void startGame() {
		if (startTime == 0)
			startTime = System.currentTimeMillis();
		ByteBuffer bf = ByteBuffer.allocate(11);
		bf.put((byte) NetworkManager.START);
		bf.put((byte) 2);
		bf.put(map);
		bf.putLong(180000 - System.currentTimeMillis() + startTime);
		btHelper.sendDataToClients(new NSData(bf.array()), true, true);
		// btHelper.sendDataToClients(new NSData(new byte[] {
		// NetworkManager.START, (byte) 2, map }), true, true);
	}

	@Override
	public void disconnect() {
		System.out.println("disconnecting");
		if (started)
			sendToServer(new byte[] { 10, playerid }, true);
		btHelper.disconnect();
		hosting = false;
		curID = 0;
		endGame();
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
	}

	private void hostGame() {
		getLobby().getMembers().clear();
		hosting = true;
		peers = new ArrayList<>();
		curID = 0;
		startHost = System.currentTimeMillis();
		map = (byte) (Math.random() * 9 + 1);
		// client.stopSearching();
		// client.disconnect();
		// btHelper.startAccepting();
		btHelper.startServer();
		sendInfoToServer();
	}

	public void start() {
		startTime = 0;
		hosting = false;
		shouldhost = true;
		// btHelper.startSession();
		btHelper.searchForServers();
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					Thread.sleep((long) (5000 + Math.random() * 2000));
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				if (shouldhost) {
					System.out.println("hosting match");
					hostGame();
				}
			}
		}).start();
	}

	@Override
	public void startServer() {
		disconnect();
		hostGame();
	}

	@Override
	public void startClient() {
		disconnect();
		startTime = 0;
		hosting = false;
		btHelper.startClient();
	}

	private class Delegate extends NSObject implements BTDelegate {

		public void connectToServer() {
			hosting = false;
			shouldhost = false;
			// btHelper.stopSearching();
			// mcm.advertise(false);
			getLobby().getMembers().clear();
			System.out.println("joined server!");
			sendInfoToServer();
		}

		@Override
		@Method(selector = "onPeerConnect")
		public void onPeerConnect() {
			if (hosting) {
				ByteBuffer bf = ByteBuffer.allocate(9);
				bf.put(SERV);
				bf.putLong(startHost);
				sendToClients(bf.array(), true, false);
				System.out.println("fixing server contradiction");
			} else {
				connectToServer();
			}
		}

		@Override
		@Method(selector = "serverBecameUnavailable")
		public void onServerDisconnect() {
			// TODO Auto-generated method stub

		}

		@Override
		@Method(selector = "onError")
		public void onError() {
			disconnect();
			Globals.getNebulous().setState(Nebulous.STATE_CHOOSE_GAME);
		}

		private void handleData(NSData data) {
			byte[] buff = data.getBytes();
			// System.out.println(hosting);
			if (hosting) {
				if (buff[0] == 5) {
					Peer p = new Peer(buff);
					p.setId((byte) curID++);
					peers.add(p);
					for (Peer p1 : peers) {
						if (p1.isDisconnected())
							continue;
						System.out.println("sending " + p1.getName()); 
						btHelper.sendDataToClients(
								new NSData(p1.toByteArray()), true, true);
					}
					if (curID >= 2) {
						startGame();
					}
				} else if (buff[0] == NetworkManager.UPDATEPOS) {
					// System.out.println("sending pos from " + buff[2]);
					btHelper.sendDataToClients(data, false, false);
				} else if (buff[0] == NetworkManager.UPDATEHEALTH) {
					btHelper.sendDataToClients(data, true, false);
				} else if (buff[0] == NetworkManager.ADDSHOT) {
					btHelper.sendDataToClients(data, false, false);
				} else if (buff[0] == NetworkManager.KILL) {
					btHelper.sendDataToClients(data, true, false);
				} else if (buff[0] == NetworkManager.DIE) {
					btHelper.sendDataToClients(data, true, false);
				} else if (buff[0] == SERV) {
					long s = ByteBuffer.wrap(buff, 1, 8).getLong();
					if (s < startHost) {
						System.out.println("cancelling server: " + s + "<"
								+ startHost);
						// del.connectToServer();
						disconnect();
						start();
					} else {
						System.out.println("keeping server: " + s + ">"
								+ startHost);
					}
					return;
				}else if(buff[0]==10){
					for(int i=0; i<peers.size(); i++){
						if(peers.get(i).getId()==buff[1]){
							peers.remove(i);
							break;
						}
					}
				}
			}
			getData(data.getBytes());
		}

		@Override
		@Method(selector = "onReceiveData:")
		public void onReceive(NSData data) {
			// System.out.println(data);
			// if (!started) {
			// msgqueue.add(data);
			// return;
			// }
			handleData(data);
		}

		@Override
		@Method(selector = "stopServer")
		public void stopServer() {
			// shouldhost = false;
			getCallback().onFindHost();
		}

		// @Override
		// @Method(selector = "onFindParallelServer")
		// public void onFindParallel() {
		// if (hosting) {
		// ByteBuffer bf = ByteBuffer.allocate(9);
		// bf.put(SERV);
		// bf.putLong(startHost);
		// sendToClients(bf.array(), true, false);
		// System.out.println("fixing server contradiction");
		// }
		// }

	}

	@Override
	public void sendToClients(byte[] data, boolean reliable, boolean toHost) {
		btHelper.sendDataToClients(new NSData(data), reliable, toHost);
	}

}