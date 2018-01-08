package com.dong.wechat_jump;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

/**
 * @ClassName MyPositionFinder.java
 * @author 王谨东
 * @version V1.0
 * @Date 2018年1月6日
 */
public class MyPositionFinder {

	public static final int R_TARGET = 40;
	public static final int G_TARGET = 43;
	public static final int B_TARGET = 86; // 棋子的RGB值

	/**
	 * 判断给定的RGB值是否在目标范围内
	 * 
	 * @param r
	 * @param g
	 * @param b
	 * @param t_r
	 * @param t_g
	 * @param t_b
	 * @param dev
	 *            允许的误差范围
	 * @return true在范围内 false不在范围内
	 */
	public static boolean match(int r, int g, int b, int t_r, int t_g, int t_b, int dev) {
		if (r > t_r - dev && r < t_r + dev && b > t_b - dev && b < t_b + dev && g > t_g - dev && g < t_g + dev) {
			return true;
		}
		return false;
	}

	/**
	 * @param image
	 * 
	 * @return int[]:棋子的x,y值 null:没有找到棋子
	 */
	public int[] getPosition(BufferedImage image) {
		if (image == null) {
			System.err.println("image is null,error");
			return null;
		}

		int imageWidth = image.getWidth();
		int imageHeight = image.getHeight();

		int[] pos = new int[2];
		int minX = 0, maxX = 0;
		for (int x = 0; x < imageWidth; x++) {
			for (int y = imageHeight * 4 / 5; y > imageHeight / 5; y--) {
				int pixel = image.getRGB(x, y); // 返回坐标为i,j的像素的RGB值

				// 提取rbg值
				int r = (pixel & 0xff0000) >> 16;
				int g = (pixel & 0xff00) >> 8;
				int b = (pixel & 0xff);

				// 开始匹配棋子
				if (match(r, g, b, R_TARGET, G_TARGET, B_TARGET, 16/* 允许误差值 */)) {
					minX = x;
					pos[1] = y;
					break;
				}
			}
			if (minX != 0)
				break;
		}
		for (int x = minX + imageWidth / 8; x > minX; x--) {
			int pixel = image.getRGB(x, pos[1]);
			int r = (pixel & 0xff0000) >> 16;
			int g = (pixel & 0xff00) >> 8;
			int b = (pixel & 0xff);
			if (match(r, g, b, R_TARGET, G_TARGET, B_TARGET, 16/* 允许误差值 */)) {
				maxX = x;
				break;
			}
		}
		if (minX != 0 && maxX != 0) {
			pos[0] = (minX + maxX) / 2;
			pos[0] += 6;
			System.out.println("my position is " + pos[0] + "," + pos[1]);
			return pos;
		} else if (minX == 0 && maxX != 0) {
			pos[0] = maxX;
			System.out.println("my position is " + pos[0] + "," + pos[1]);
			return pos;
		} else if (minX != 0 && maxX == 0) {
			pos[0] = minX;
			System.out.println("my position is " + pos[0] + "," + pos[1]);
			return pos;
		} else {
			System.out.println("find my position fail");
			System.exit(1);
			return null;
		}
	}

	/**
	 * 测试函数
	 * 
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		int total = 0;
		while (true) {
			long currentTime = System.currentTimeMillis(); // 计时

			MyPositionFinder myPositionFinder = new MyPositionFinder();
			String appRoot = Main.class.getResource("/").getPath() + "temp_images/input"; // 获取Hack.class所在的顶级目录
			File file = new File(appRoot, ++total + ".png");
			BufferedImage image = ADBcommand.getScreenCap(file.getAbsolutePath());
			int pos[] = myPositionFinder.getPosition(image);
			System.out.println("x:" + pos[0] + " y:" + pos[1]);
			Graphics graphics = image.getGraphics();
			graphics.drawImage(image, 0, 0, image.getWidth(), image.getHeight(), null);
			graphics.setColor(Color.WHITE);
			graphics.fillRect(pos[0], pos[1], 10, 10);
			File descFile = new File(appRoot, file.getName());
			if (!descFile.exists()) {
				descFile.mkdirs();
				descFile.createNewFile();
			}
			ImageIO.write(image, "png", descFile);

			long nowTime = System.currentTimeMillis(); // 停止计时
			System.out.println("cost time is " + (nowTime - currentTime) + "ms");
		}
	}
}
