package com.dong.wechat_jump;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import javax.imageio.ImageIO;


/**
 * @ClassName Test.java
 * @author 王谨东
 * @version V1.0
 * @Date 2018年1月8日
 */
public class Test {
	/**
	 * 测试函数
	 * 
	 * @param args
	 * 
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {

		long currentTime = System.currentTimeMillis(); // 计时开始

		// NextPositionFinder nextPositionFinder = new NextPositionFinder();
		String appRoot = Main.class.getResource("/").getPath(); // 获取Main.class所在的顶级目录
		String imagesInput = appRoot + "temp_images/input";
		String imagesOutput = appRoot + "teme_images/output";

		MyPositionFinder myPositionFinder=new MyPositionFinder();
		NextBoxFinder nextBoxFinder=new NextBoxFinder();
		
		File imageSrc = new File(imagesInput);

		for (File file : imageSrc.listFiles()) {

			System.out.println("handle " + file);
			BufferedImage image = ImageIO.read(new FileInputStream(file.getAbsolutePath()));
			int myPos[] = myPositionFinder.getPosition(image);
			int nextPos[] = nextBoxFinder.getPosition(image);
			int nextCenter[] = nextBoxFinder.getCenter(image);
			Graphics graphics = image.getGraphics();
			graphics.drawImage(image, 0, 0, image.getWidth(), image.getHeight(), null);
			graphics.setColor(Color.WHITE);
			graphics.fillRect(myPos[0]-7, myPos[1]-7, 15, 15);
			graphics.setColor(Color.RED);
			graphics.fillRect(nextPos[0]-7, nextPos[1]-7, 15, 15);
			graphics.fillRect(nextPos[2]-7, nextPos[3]-7, 15, 15);
			graphics.fillRect(nextPos[4]-7, nextPos[5]-7, 15, 15);
			graphics.setColor(Color.BLACK);
			graphics.fillRect(nextCenter[0], nextCenter[1], 15, 15);
			File descFile = new File(imagesOutput, file.getName());
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
