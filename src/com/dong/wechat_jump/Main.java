package com.dong.wechat_jump;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Random;

import org.omg.CORBA.PRIVATE_MEMBER;

/**
 * @ClassName Main.java
 * @author 王谨东
 * @version V1.0
 * @Date 2018年1月6日
 */
public class Main {
	public static void main(String[] strings) {

		final double JUMP_RATIO = 1.390f; // 弹跳系数
		Random random = new Random();

		String appRoot = Main.class.getResource("/").getPath() + "temp_images/input"; // 获取Main.class所在的顶级目录
		System.out.println("screenshot save path is " + appRoot);

		MyPositionFinder myPositionFinder = new MyPositionFinder();
		NextBoxFinder nextBoxFinder = new NextBoxFinder();

		int total = 0;
		while (true) {

			File file = new File(appRoot, ++total + ".png");
			BufferedImage image = ADBcommand.getScreenCap(file.getAbsolutePath());
			int myPos[] = myPositionFinder.getPosition(image);
			System.out.println("my position, x:" + myPos[0] + " y:" + myPos[1]);
			int nextPos[] = nextBoxFinder.getCenter(image);
			System.out.println(nextPos[0] + "," + nextPos[1]);
			double distance = (Math.sqrt((nextPos[0] - myPos[0]) * (nextPos[0] - myPos[0])
					+ (nextPos[1] - myPos[1]) * (nextPos[1] - myPos[1])));
			System.out.println("distance is " + distance);
			ADBcommand.press((int) (distance * JUMP_RATIO));

			try {
				Thread.sleep(2500+random.nextInt(2000));  //随机sleep防止作弊检测
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
