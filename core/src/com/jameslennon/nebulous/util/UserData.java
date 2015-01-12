package com.jameslennon.nebulous.util;

import java.util.Scanner;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.jameslennon.nebulous.Nebulous;

public class UserData {
	private static String name;
	private static int shipType = 0;
	private static boolean pro;

	public static String getName() {
		return name;
	}

	public static void setName(String name) {
		UserData.name = name;
	}

	public static int getShipType() {
		return shipType;
	}

	public static void setShipType(int shipType) {
		UserData.shipType = shipType;
	}

	public static void load() {
		if (!exists()) {
			Gdx.app.log(Nebulous.log, "No user data stored");
			name = "Player" + (int) (Math.random() * 1000);
			return;
		}
		try {
			FileHandle f = Gdx.files.external(".nebulous/user.dat");
			Scanner in = new Scanner(f.reader());

			setName(in.next());
			setShipType(Integer.parseInt(in.next()));
			setPro(in.nextBoolean());
			// setName("");
			// setShipType(Integer.parseInt(in.next()));
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
			Gdx.files.external(".nebulous/user.dat").delete();
		}
	}

	public static void save() {
		FileHandle f = Gdx.files.external(".nebulous/user.dat");
		f.writeString(getName() + "\n" + shipType + "\n" + pro, false);
	}

	public static boolean exists() {
		FileHandle f = Gdx.files.external(".nebulous");
		if (!f.exists()) {
			f.mkdirs();
			return false;
		}
		return Gdx.files.external(".nebulous/user.dat").exists();
	}

	public static void setPro(boolean b) {
		pro = b;
	}

	public static boolean isPro() {
		return pro;
	}

}
