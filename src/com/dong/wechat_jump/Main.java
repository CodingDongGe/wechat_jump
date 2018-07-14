package com.dong.wechat_jump;

import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URLDecoder;
import java.util.Random;

import org.omg.CORBA.PRIVATE_MEMBER;

/**
 * @ClassName Main.java
 * @author ������
 * @version V1.0
 * @Date 2018��1��6��
 */
public class Main {
	public static void main(String[] strings) throws Exception{

		final double JUMP_RATIO = 1.390f; // ����ϵ��
		Random random = new Random();

		String appRoot = Main.class.getResource("/").getPath() + "temp_images/input"; // ��ȡMain.class���ڵĶ���Ŀ¼
		appRoot=URLDecoder.decode(appRoot, "UTF-8");
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
				Thread.sleep(2500+random.nextInt(2000));  //���sleep��ֹ���׼��
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
