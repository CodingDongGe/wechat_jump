package com.dong.wechat_jump;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Queue;

import javax.imageio.ImageIO;

/**
 * @ClassName NextBoxFinder.java
 * @author 王谨东
 * @version V1.0
 * @Date 2018年1月7日
 */
public class NextBoxFinder extends MyPositionFinder {
	private static int imageWidth = 0;
	private static int imageHeigth = 0;
	// private int[] pos = { 0, 0, 0, 0, 0, 0 }; // 分别为上，左右顶点的xy坐标值
	// private int[] center = { 0, 0 };

	/**
	 * 下个物体的顶点坐标
	 * 
	 * @param image
	 * 
	 * @return int[6] 分别为上，左右顶点的xy坐标值
	 */
	public int[] getPosition(BufferedImage image) {
		imageWidth = image.getWidth();
		imageHeigth = image.getHeight();
		int[] pos = { 0, 0, imageWidth, 0, 0, 0 };
		int bkRGB[] = getBackGroundColor(image);
		int targetR = 0, targetG = 0, targetB = 0; // 盒子的RGB值
		for (int y = imageHeigth / 4; y < imageHeigth * 3 / 5; y++) {
			for (int x = 0; x < imageWidth; x++) {
				// 提取rbg值
				int pixel = image.getRGB(x, y);
				int r = (pixel & 0xff0000) >> 16;
				int g = (pixel & 0xff00) >> 8;
				int b = (pixel & 0xff);
				// 如果检测到此像素点的RGB值与背景和棋子不匹配，则标记为上顶点
				if (!match(r, g, b, bkRGB[0], bkRGB[1], bkRGB[2], 20)
						&& !match(r, g, b, R_TARGET, G_TARGET, B_TARGET, 40)) {
					pos[0] = x;
					pos[1] = y;
					// 向下遍历10个像素，并取其RGB平均值作为盒子的RGB值
					for (int i = pos[1]; i < pos[1] + 10; i++) {
						int temp = image.getRGB(x, y);
						targetR += (temp & 0xff0000) >> 16;
						targetG += (temp & 0xff00) >> 8;
						targetB += (temp & 0xff);
					}
					targetR /= 10;
					targetG /= 10;
					targetB /= 10;
					System.out.println("The next box RGB is " + targetR + "," + targetG + "," + targetB);
					break;
				}
			}
			if (pos[0] != 0 && pos[1] != 0)
				break;
		}

		if (pos[0] == 0 && pos[1] == 0) {
			System.out.println("find next box position fail");
			System.exit(1);
		}

		Queue<int[]> queue = new ArrayDeque<>();
		boolean[][] isVisited = new boolean[imageWidth][imageHeigth];
		queue.add(newArray(pos[0], pos[1]));
		while (!queue.isEmpty()) {
			int[] temp = queue.poll();
			int x = temp[0], y = temp[1];

			// 限定遍历范围，减少时间复杂度
			if (y >= imageHeigth * 3 / 5) {
				continue;
			}
			if (x < Integer.max(pos[0] - 300, 0) || x >= Integer.min(pos[0] + 300, imageWidth)
					|| y < Integer.max(0, pos[1] - 400) || y >= Integer.max(imageHeigth, pos[1] + 400)
					|| isVisited[x][y]) {
				continue;
			}
			isVisited[x][y] = true;

			int tempPixel = image.getRGB(x, y);
			int tempR = (tempPixel & 0xff0000) >> 16;
			int tempG = (tempPixel & 0xff00) >> 8;
			int tempB = (tempPixel & 0xff);

			// 如果此像素点的RGB值与盒子的RGB值匹配
			if (match(tempR, tempG, tempB, targetR, targetG, targetB, 20)) {
				// 如果此像素点的x坐标小于以前的x值，则更新此值
				if (x < pos[2]) {
					pos[2] = x;
					pos[3] = y;
				}

				// 如果此像素点的x坐标大于以前的x值，则更新此值
				if (x > pos[4]) {
					pos[4] = x;
					pos[5] = y;
				}

				// 将与此像素点相邻的像素点加入队列
				queue.add(newArray(x - 1, y));
				queue.add(newArray(x + 1, y));
				queue.add(newArray(x, y + 1));
				queue.add(newArray(x, y - 1));
			}
		}
		System.out.println("top  x:" + pos[0] + " y:" + pos[1]);
		System.out.println("left x:" + pos[2] + " y:" + pos[3]);
		System.out.println("right x:" + pos[4] + " y:" + pos[5]);
		return pos;
	}

	public int[] getCenter(BufferedImage image) {
		CenterPointFinder centerPointFinder = new CenterPointFinder();
		int[] pos = getPosition(image);

		int[] centerPoint = new int[2];
		centerPoint = centerPointFinder.find(image, pos[0] - 200, pos[1], pos[0] + 200, pos[1] + 200);
		if (centerPoint != null)
			return centerPoint;
		// 考虑盒子部分被棋子遮挡额情况
		int[] boxCenter = new int[2];
		if (pos[1] == pos[3] || pos[1] == pos[5] || pos[0] == pos[2] || pos[0] == pos[4]) {
			boxCenter[0] = pos[0];
			boxCenter[1] = pos[1] + 50;
		} else {
			boxCenter[0] = (pos[2] + pos[4]) / 2;
			boxCenter[1] = (pos[3] + pos[5]) / 2 - 5;
		}

		System.out.println("box center is:" + boxCenter[0] + " y:" + boxCenter[1]);
		return boxCenter;
	}

	/**
	 * 提取(imageWidth, imageHeigth/2)的RGB值作为背景图颜色
	 * 
	 * @param image
	 * 
	 * @return int[0]:R值,int[1]:G值,int[2]:B值
	 */
	public int[] getBackGroundColor(BufferedImage image) {
		int[] bkRGB = new int[3];
		for (int i = 0; i < imageWidth; i++) {
			int bkPixel = image.getRGB(i, 0);

			bkRGB[0] += (bkPixel & 0xff0000) >> 16;
			bkRGB[1] += (bkPixel & 0xff00) >> 8;
			bkRGB[2] += (bkPixel & 0xff);
		}
		bkRGB[0] /= imageWidth;
		bkRGB[1] /= imageWidth;
		bkRGB[2] /= imageWidth;
		System.out.println("background r:" + bkRGB[0] + " g:" + bkRGB[1] + " b:" + bkRGB[2]);
		return bkRGB;
	}

	private int[] newArray(int param1, int param2) {
		int myArray[] = { param1, param2 };
		return myArray;
	}
}
