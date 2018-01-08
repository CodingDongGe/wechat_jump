package com.dong.wechat_jump;

import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Random;

import javax.imageio.ImageIO;

/**
 * @ClassName ADBcommand.java
 * @author 王谨东
 * @version V1.0
 * @Date 2018年1月6日
 */
public class ADBcommand {
	private static String ADB_PATH = "adb"; // adb保存路径，若已配置环境变量则直接使用"adb"

	private static Random random = new Random();

	public ADBcommand(String ADBpath) {
		ADB_PATH = ADBpath;
	}

	/**
	 * @param savePath
	 *            截图在磁盘上的保存路径
	 * 
	 * @return BufferedImage:加载成功 null:加载失败
	 */
	public static BufferedImage getScreenCap(String savePath) {
		String path = savePath; // 保存路径
		try {
			Process process = Runtime.getRuntime()
					.exec(ADB_PATH + " shell /system/bin/screencap -p /sdcard/temp_screenshot.png");
			process.waitFor();
			process = Runtime.getRuntime().exec(ADB_PATH + " pull /sdcard/temp_screenshot.png " + path);
			process.waitFor();
			BufferedImage image = ImageIO.read(new FileInputStream(path));
			return image;
			// return null;
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Method getScreenCap error");
			return null;
		}
	}

	public static void press(int time) {
		Process process;
		try {
			
			//随机范围触摸位置，防止作弊检测
			String string = String.format(" shell input swipe %d %d %d %d ", 500 + random.nextInt(100),
					500 + random.nextInt(100), 1500 + random.nextInt(100), 1500 + random.nextInt(100));
			process = Runtime.getRuntime().exec(ADB_PATH + string + time);
			process.waitFor();
		} catch (Exception e) {
			System.out.println("Method press error");
			e.printStackTrace();
		}
	}
}
