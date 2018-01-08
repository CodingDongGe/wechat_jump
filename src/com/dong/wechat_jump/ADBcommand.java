package com.dong.wechat_jump;

import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Random;

import javax.imageio.ImageIO;

/**
 * @ClassName ADBcommand.java
 * @author ������
 * @version V1.0
 * @Date 2018��1��6��
 */
public class ADBcommand {
	private static String ADB_PATH = "adb"; // adb����·�����������û���������ֱ��ʹ��"adb"

	private static Random random = new Random();

	public ADBcommand(String ADBpath) {
		ADB_PATH = ADBpath;
	}

	/**
	 * @param savePath
	 *            ��ͼ�ڴ����ϵı���·��
	 * 
	 * @return BufferedImage:���سɹ� null:����ʧ��
	 */
	public static BufferedImage getScreenCap(String savePath) {
		String path = savePath; // ����·��
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
			
			//�����Χ����λ�ã���ֹ���׼��
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
