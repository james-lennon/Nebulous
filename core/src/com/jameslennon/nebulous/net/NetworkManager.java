package com.jameslennon.nebulous.net;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.nio.ByteBuffer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.jameslennon.nebulous.Nebulous;
import com.jameslennon.nebulous.game.Globals;
import com.jameslennon.nebulous.game.grid.Flag;
import com.jameslennon.nebulous.game.shots.EMP;
import com.jameslennon.nebulous.game.shots.GravityProjectile;
import com.jameslennon.nebulous.game.shots.LaserProjectile;
import com.jameslennon.nebulous.game.shots.MineProjectile;
import com.jameslennon.nebulous.game.shots.Missile;
import com.jameslennon.nebulous.game.shots.Projectile;
import com.jameslennon.nebulous.game.shots.SmallLaser;
import com.jameslennon.nebulous.game.shots.SniperShot;
import com.jameslennon.nebulous.game.shots.WaveProjectile;
import com.jameslennon.nebulous.util.UserData;

public class NetworkManager {

	public final static int UPDATEPOS = 4, ADDSHOT = 8, UPDATEHEALTH = 12,
			UPDATESCORE = 13, KILL = 16, ADDPEER = 6, PEERINFO = 5, SETID = 3,
			DIE = 11, START = 1;

	private static InetAddress hostaddr;
	private static final int port = 9876;
	private static DatagramSocket udpClient;
	private static Socket tcpClient;
	private static Nebulous nebulous;
	private static boolean inGame, connecting, joining;
	private static byte playerId;
	private static Peer myPeer;
	private static Lobby mLobby;
	private static boolean online, started;
	private static String ip;
	private static byte gameSize;
	private static long gameEnd, ping, lastping;
	private static int[] scores;
	private static Thread tcpGameThread;
	private static Peer op;
	private static int size, map;

	public static String getAddress() {
		return ip;
	}

	public static void init(Nebulous n) {
		online = true;
		mLobby = new Lobby((byte) 0);
		try {
			ip = "localhost";// "169.254.140.79";
			hostaddr = InetAddress.getByName("10.0.1.9");
			nebulous = n;
			inGame = false;
			connecting = false;
			joining = false;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void dispose() {
		try {
			if (udpClient != null && !udpClient.isClosed())
				udpClient.close();
			if (tcpClient != null && !tcpClient.isClosed())
				tcpClient.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void sendToServerUDP(byte[] data) {
		// DatagramPacket packet = new DatagramPacket(data, data.length,
		// hostaddr,
		// port);
		// try {
		// udpClient.send(packet);
		// } catch (IOException e) {
		// // e.printStackTrace();
		// }
		Nebulous.social.send(data, false);
	}

	public static void stopConnecting() {
		connecting = false;
	}

	public static boolean isConnected() {
		return tcpClient != null && udpClient != null
				&& tcpClient.isConnected() && !tcpClient.isClosed()
				&& !udpClient.isClosed();
	}

	public static void connect(ConnectionListener l) {
		connecting = true;
		online = true;
		try {
			if (!isConnected()) {
				hostaddr = InetAddress.getByName(ip);
				connecting = true;
				while (connecting
						&& (tcpClient == null || !tcpClient.isConnected() || tcpClient
								.isClosed())) {
					try {
						tcpClient = new Socket(hostaddr, 9877);
					} catch (Exception e) {
						// e.printStackTrace();
					}
					Thread.sleep(500);
				}
				tcpClient.setSoTimeout(1);
				udpClient = new DatagramSocket(null);
				udpClient.bind(new InetSocketAddress(tcpClient.getLocalPort()));
			}
			Gdx.app.log(Nebulous.log, "Connected Sockets");
			String name = UserData.getName();
			byte[] buff = new byte[1024];
			buff[0] = 0;
			buff[1] = (byte) name.length();
			int i;
			for (i = 0; i < name.length(); i++) {
				buff[i + 2] = (byte) name.charAt(i);
			}
			sendToServerTCP(buff);
			int n = -1;
			while (n == -1 && connecting) {
				try {
					n = tcpClient.getInputStream().read(buff);
				} catch (Exception e) {
					// e.printStackTrace();
				}
			}
			Gdx.app.log(Nebulous.log, "Received Response");
			if (!connecting)
				return;
			l.onConnect(n != -1, buff);
			name = UserData.getName();
			Gdx.app.log(Nebulous.log, "TCP client started on "
					+ tcpClient.getLocalAddress().getHostAddress() + ":"
					+ tcpClient.getLocalPort());
			Gdx.app.log(Nebulous.log, "UDP client started on "
					+ udpClient.getLocalAddress().getHostAddress() + ":"
					+ udpClient.getLocalPort());
			tcpClient.setSoTimeout(0);
			sendShipType((byte) UserData.getShipType());
		} catch (Exception e) {
			l.onConnect(false, null);
			e.printStackTrace();
		}
	}

	public interface ConnectionListener {
		void onConnect(boolean success, byte[] response);
	}

	public static void stopJoining() {
		joining = false;
	}

	public interface LobbyCallback {
		public void onFindGame();

		public void addMember(Peer peer);

		public void removeMember(Peer peer);

		public void start();
	}

	public static boolean joinLobby(LobbyCallback ls) {
		reset();
		// ls.setMessage("Finding Game...");
		byte[] data = new byte[] { 5, gameSize };
		sendToServerTCP(data);
		// Gdx.app.log(Nebulous.log, "Sent " + Arrays.toString(data)
		// + " to server");
		byte[] buff = new byte[1024];
		boolean not_joined = true, data_started = false;
		try {
			tcpClient.setSoTimeout(1000);
		} catch (SocketException e1) {
			e1.printStackTrace();
		}
		joining = true;
		while (not_joined && joining) {
			try {
				buff = new byte[1024];
				int n = tcpClient.getInputStream().read(buff);
				// Gdx.app.log(Nebulous.log, "Received data: " + buff[0] + ": "
				// + Arrays.toString(buff));
				if (n == -1) {
					Gdx.app.postRunnable(new Runnable() {

						@Override
						public void run() {
							nebulous.setState(Nebulous.STATE_MENU);
						}
					});
					break;
				}
				if (buff[0] == 0) {
					// Gdx.app.log(Nebulous.log, "Error with lobby(again)");
					byte[] tmp = new byte[buff.length - 3];
					System.arraycopy(buff, 3, tmp, 0, buff.length - 3);
					buff = tmp;
				}
				if (!data_started) {
					if (buff[0] != 3) {
						// Gdx.app.log(Nebulous.log, "Erroneous header 1: "
						// + buff[0]);
						continue;
					} else {
						// Gdx.app.log(Nebulous.log,
						// "Started Receiving Lobby Info");
						data_started = true;
						mLobby = new Lobby(buff[1]);
						playerId = buff[2];
						// Gdx.app.log(Nebulous.log, "Set Player id" + buff[2]);
						ls.onFindGame();
					}
				} else {
					if (buff[0] == 6) {
						Peer peer = new Peer(buff);
						mLobby.addPeer(peer);
						// Gdx.app.log(Nebulous.log, "Added peer " + peer);
						ls.addMember(peer);
						if (peer.getId() == playerId) {
							// Gdx.app.log(Nebulous.log, "^This is me");
							myPeer = peer;
						}
					} else if (buff[0] == 10) {
						ls.removeMember(mLobby.getMembers().get((int) buff[1]));
						mLobby.removePeer(buff[1]);
						// Gdx.app.log(Nebulous.log, "Removed Peer " + buff[1]);
					} else if (buff[0] == 7) {
						not_joined = false;
						inGame = true;
						// Gdx.app.log(Nebulous.log,
						// "Ended Receiving Lobby Info");
						NetworkManager.setGameEnd(ByteBuffer.wrap(buff)
								.getLong(1));
						ls.start();
					} else {
						// Gdx.app.log(Nebulous.log, "Erroneous header 2: "
						// + Arrays.toString(buff));
						continue;
					}
				}
			} catch (IOException e) {
				// e.printStackTrace();
			}
		}
		// ls.setMessage("Starting Game...");
		return !not_joined;
	}

	private static void reset() {
		if (getLobby() != null)
			getLobby().reset();
	}

	public static void setGameEnd(long l) {
		gameEnd = System.currentTimeMillis() + l;
	}

	public static long getGameEnd() {
		return gameEnd;
	}

	public static void sendToServerTCP(byte[] data) {
		if (!online)
			return;
		// try {
		// tcpClient.getOutputStream().write(data);
		// } catch (IOException e) {
		// e.printStackTrace();
		// }
		Nebulous.social.send(data, true);
	}

	public static void sendPosToGame(float x, float y, float dx, float dy,
			float rot, float drot) {
		if (!online)
			return;
		ByteBuffer bf = ByteBuffer.allocate(27);
		bf.put((byte) UPDATEPOS);
		bf.put(mLobby.getId());
		bf.put(playerId);
		bf.putFloat(x);
		bf.putFloat(y);
		bf.putFloat(dx);
		bf.putFloat(dy);
		bf.putFloat(rot);
		bf.putFloat(drot);
		byte[] data = bf.array();
		sendToServerUDP(data);
	}

	public static void setMap(int m) {
		map = m;
	}

	public static int getMap() {
		return map;
	}

	private static void updatePing() {
		long now = System.currentTimeMillis();
		ping = now - lastping;
		lastping = now;
	}

	public static void getData(byte[] data) {
		int h = data[0];
		if (h == UPDATEPOS) {
			// System.out.println("got pos from "+data[2]);
			if (data[2] != playerId) {
				getLobby().receiveData(data);
				// System.out.println("updating pos");
			} else {
				updatePing();
				// System.out.println("ignoring pos from "+data[2]);
				// System.out.println("updating ping");
			}
		} else if (h == ADDSHOT) {
			if (data[2] != playerId)
				getLobby().addShot(data);
			else {
				updatePing();
			}
		} else if (h == 10) {
			getLobby().getMembers().get((int) data[1]).disconnect();
			// Ship s = getLobby().getMembers().get((int) data[1]).getShip();
			// Globals.getMiniMap().remove(s);
			// s.disconnect();
			// getLobby().removePeer(data[1]);
		} else if (h == UPDATEHEALTH) {
			getLobby().updateHealth(data);
		} else if (h == UPDATESCORE) {
			receiveScores(data);
		} else if (h == KILL) {
			receiveKill(data);
		} else if (h == DIE) {
			Peer peer = getLobby().getMembers().get((int) data[1]);
			peer.addDeath();
		} else if (h == ADDPEER) {
			final Peer peer = new Peer(data);
			mLobby.addPeer(peer);
			// Gdx.app.log(Nebulous.log,
			// "Got peer " + peer.getName() + "," + peer.getShipType()
			// + "," + peer.getId());
			if (peer.getName().equals(UserData.getName())) {
				playerId = peer.getId();
				// Gdx.app.log(Nebulous.log, "My id is " + playerId);
			}
			if (started && size == getLobby().getMembers().size()) {
				started = false;
				size = 0;
				Gdx.app.postRunnable(new Runnable() {
					@Override
					public void run() {
						Globals.getNebulous().setState(
								Nebulous.STATE_PLAY_ONLINE);
					}
				});
			}
		} else if (h == START) {
			// Gdx.app.log(Nebulous.log, "Received Start game...");
			int s = data[1];
			setMap(data[2]);
			if (s == getLobby().getMembers().size()) {
				started = false;
				size = 0;
				Gdx.app.postRunnable(new Runnable() {
					@Override
					public void run() {
						Globals.getNebulous().setState(
								Nebulous.STATE_PLAY_ONLINE);
					}
				});
			} else {
				// Gdx.app.log(Nebulous.log, "got start too soon! size = " + s);
				started = true;
				size = s;
			}
		} else {
			// Gdx.app.log(Nebulous.log, "Received erroneous header: " + h);
		}
	}

	// public static void listenForUpdates() {
	// if (!online)
	// return;
	// new Thread(new Runnable() {
	//
	// @Override
	// public void run() {
	// byte[] buff = new byte[1024];
	// final DatagramPacket packet = new DatagramPacket(buff,
	// buff.length);
	// while (inGame) {
	// try {
	// if (udpClient == null || udpClient.isClosed())
	// return;
	// udpClient.receive(packet);
	// // System.out.println("Received update data "
	// // + Arrays.toString(packet.getData()));
	// if (packet.getData()[0] == 4)
	// getGame().receiveData(packet.getData());
	// else if (packet.getData()[0] == 8)
	// getGame().addShot(packet.getData());
	// else if (packet.getData()[0] == 14)
	// updateFlag(packet.getData());
	// } catch (IOException e) {
	// // e.printStackTrace();
	// }
	// }
	// }
	// }).start();
	// }
	//
	// public static void listenOnTCP() {
	// if (!online)
	// return;
	// tcpGameThread = new Thread(new Runnable() {
	// @Override
	// public void run() {
	// try {
	// tcpClient.setSoTimeout(1000);
	// } catch (SocketException e) {
	// e.printStackTrace();
	// }
	// byte[] buff = new byte[1024];
	// while (inGame) {
	// try {
	// if (tcpClient == null || tcpClient.isClosed())
	// return;
	// int n = tcpClient.getInputStream().read(buff);
	// if (n == -1) {
	// inGame = false;
	// quitGame();
	// }
	// // Gdx.app.log(Nebulous.log, "TCP: Received header "
	// // + buff[0]);
	// if (buff[0] == 10)
	// getGame().removePeer(buff[1]);
	// else if (buff[0] == 12) {
	// getGame().updateHealth(buff);
	// } else if (buff[0] == 13) {
	// receiveScores(buff);
	// } else if (buff[0] == 16) {
	// receiveKill(buff);
	// } else if (buff[0] == 11) {
	// // Peer peer = getGame().getMembers().get(
	// // (int) buff[1]);
	// } else if (buff[0] == 6) {
	// final Peer peer = new Peer(buff);
	// mLobby.addPeer(peer);
	// Gdx.app.postRunnable(new Runnable() {
	//
	// @Override
	// public void run() {
	// peer.initShip();
	// }
	// });
	// } else {
	// Gdx.app.log(Nebulous.log,
	// "Received erroneous header: " + buff[0]);
	// }
	// } catch (IOException e) {
	// // e.printStackTrace();
	// }
	// }
	// }
	// });
	// tcpGameThread.start();
	// }

	private static void receiveScores(byte[] buff) {
		int n = buff[1];
		ByteBuffer bf = ByteBuffer.wrap(buff);
		for (int i = 0; i < n; i++) {
			Peer peer = getLobby().getMembers().get(i);
			int k = bf.getInt(i * 8 + 2), d = bf.getInt(i * 8 + 6);
			peer.setKills(k);
			peer.setDeaths(d);
		}
	}

	public static void quitGame() {
		inGame = false;
		if (!online)
			return;
		if (tcpGameThread != null && !tcpGameThread.isInterrupted())
			tcpGameThread.interrupt();
		sendToServerTCP(new byte[] { 10, playerId });
		Nebulous.social.disconnect();
	}

	public static Lobby getLobby() {
		return mLobby;
	}

	public static byte getPlayerId() {
		return playerId;
	}

	public static void updatePlayers() {
		if (!online)
			return;
		mLobby.update();
	}

	public static void sendShot(Projectile p) {
		if (!online)
			return;
		ByteBuffer bf = ByteBuffer.allocate(16);
		bf.put((byte) ADDSHOT);
		bf.put(mLobby.getId());
		bf.put(playerId);
		bf.putFloat(p.getBody().getPosition().x);
		bf.putFloat(p.getBody().getPosition().y);
		bf.putFloat(p.getBody().getAngle());
		byte type = 0;
		if (p instanceof LaserProjectile) {
			type = Projectile.TYPE_LASER;
		} else if (p instanceof WaveProjectile) {
			type = Projectile.TYPE_WAVE;
		} else if (p instanceof Missile) {
			type = Projectile.TYPE_MISSILE;
		} else if (p instanceof EMP) {
			type = Projectile.TYPE_EMP;
		} else if (p instanceof MineProjectile) {
			type = Projectile.TYPE_MINE;
		} else if (p instanceof GravityProjectile) {
			type = Projectile.TYPE_GRAVITY;
		} else if (p instanceof SniperShot) {
			type = Projectile.TYPE_SNIPER;
		} else if (p instanceof SmallLaser)
			type = Projectile.TYPE_SMALL_LASER;
		bf.put(type);
		byte[] data = bf.array();
		sendToServerUDP(data);
	}

	public static void sendShipType(byte type) {
		if (!online)
			return;
		byte[] buff = new byte[] { 9, type };
		sendToServerTCP(buff);
	}

	public static Peer getPeer() {
		return getLobby().getMembers().get((int) playerId);
	}

	public static void setOnline(boolean b) {
		online = b;
	}

	public static boolean isOnline() {
		return online;
	}

	public static Peer getOfflinePeer() {
		if (op == null)
			op = new Peer(UserData.getName(), UserData.getShipType());
		op.setName(UserData.getName());
		return op;
	}

	public static void setIP(String text) {
		ip = text;
	}

	public static void setGameSize(byte s) {
		gameSize = s;
	}

	public static byte getGameSize() {
		return gameSize;
	}

	public static void sendDie() {
		sendToServerTCP(new byte[] { DIE, playerId });
	}

	public static int[] getScores() {
		return scores;
	}

	public static void sendAwardFlag() {
		if (!online)
			return;
		sendToServerTCP(new byte[] { 15, getPlayerId() });
	}

	public static void sendUpdateFlag(Flag flag) {
		if (!online)
			return;
		ByteBuffer bf = ByteBuffer.allocate(19);
		bf.put((byte) 14);
		bf.put(getLobby().getId());
		bf.put(getPlayerId());
		Vector2 pos = flag.getBody().getPosition();
		bf.putFloat(pos.x);
		bf.putFloat(pos.y);
		Vector2 vel = flag.getBody().getLinearVelocity();
		bf.putFloat(vel.x);
		bf.putFloat(vel.y);
		sendToServerUDP(bf.array());
	}

	public static void updateFlag(byte[] b) {
		float x = ByteBuffer.wrap(b, 3, 4).getFloat(), y = ByteBuffer.wrap(b,
				7, 4).getFloat(), dx = ByteBuffer.wrap(b, 11, 4).getFloat(), dy = ByteBuffer
				.wrap(b, 15, 4).getFloat();
		Globals.getMap().updateFlag(x, y, dx, dy);
	}

	public static void sendKill(byte id) {
		// Gdx.app.log(Nebulous.log, "sending kill");
		sendToServerTCP(new byte[] { KILL, id, getPlayerId() });
	}

	private static void receiveKill(byte[] buff) {
		// Gdx.app.log(Nebulous.log, "got kill");
		Peer peer = getLobby().getMembers().get((int) buff[1]);
		if (peer == null)
			return;
		peer.addKill();
		if (buff[1] == getPlayerId())
			Globals.getScreen().message(
					"Killed "
							+ getLobby().getMembers().get((int) buff[2])
									.getName() + "!");
		getLobby().getMembers().get((int) buff[2]).addDeath();
	}

	public static long getPing() {
		return ping;
	}
}
