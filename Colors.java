package project.mis.group8.phase1;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.color.*;

import javax.swing.JFrame;
import javax.swing.JPanel;

import static org.bytedeco.javacpp.opencv_core.*;
import static org.bytedeco.javacpp.opencv_imgproc.*;
import static org.bytedeco.javacpp.opencv_highgui.*;
import static org.bytedeco.javacpp.opencv_imgcodecs.*;
import static org.bytedeco.javacpp.opencv_calib3d.*;
import static org.bytedeco.javacpp.opencv_objdetect.*;
import static org.bytedeco.javacpp.opencv_videoio.cvCaptureFromFile;

import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacpp.FlyCapture2.Image;
import org.bytedeco.javacpp.opencv_core.CvScalar;
import org.bytedeco.javacpp.opencv_core.IplImage;
import org.bytedeco.javacpp.opencv_videoio.VideoCapture;
import org.bytedeco.javacv.CanvasFrame;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.bytedeco.javacv.OpenCVFrameGrabber;


/**
 * @author kvivekanandan Sep 11, 2015 Test.java
 */

public class Colors {

	public static int NUMBER_OF_BITS = 9;
	public static ColorMap COLOR_MAP;
	public static ColorInstance one;
	public static ColorInstance two;
	public static ColorInstance three;

	enum COLOR_MODEL {
		RGB(8, 8, 8), XYZ(8, 8, 8), YUV(8, 4, 4), YCbCr(8, 2, 4), YIQ(8, 4, 2), HSL(1, 1, 1);
		COLOR_MODEL(int xbits, int ybits, int zbits) {
			this.x_bits = xbits;
			this.y_bits = ybits;
			this.z_bits = zbits;
		}

		private int x_bits;
		private int y_bits;
		private int z_bits;

		public int getXbits() {
			return x_bits;
		}

		public int getYbits() {
			return y_bits;
		}

		public int getZbits() {
			return z_bits;
		}
	}

	public static void main(String args[]) {
//		captureFrame();

		Colors t = new Colors();
		COLOR_MAP = new ColorMap();
		one = t.new ColorInstance(10, 252, 150);
		two = t.new ColorInstance(70, 130, 155);
		three = t.new ColorInstance(230, 22, 130);

		// one = t.new ColorInstance(0, 0, 0);
		// two = t.new ColorInstance(50, 50, 50);
		// three = t.new ColorInstance(100, 100, 100);

//		convertColorScale();

		colorMap(COLOR_MODEL.RGB, one, two, three, NUMBER_OF_BITS);
	}

	static void captureFrame() {
		FrameGrabber frameGrabber = new OpenCVFrameGrabber("/Users/kvivekanandan/Desktop/ASU/CSE_598_Multimedia_Information_Systems/sampleDataP1/1.mp4");

		try {
			frameGrabber.start();
			OpenCVFrameConverter.ToIplImage converter = new OpenCVFrameConverter.ToIplImage();
			frameGrabber.setFrameRate(30);
			int length = frameGrabber.getLengthInFrames();
			int frame_one = 8;
			int frame_two = 220;
			Frame f;
			Frame f1 = null;
			Frame f2 = null;
			Frame g1, g2;
			CanvasFrame canvas = null;
			while ((f = frameGrabber.grab()) != null) {
				if (frameGrabber.getFrameNumber() == frame_one) {
					f1 = f;
					canvas = new CanvasFrame("" + frameGrabber.getFrameNumber());
					canvas.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
					canvas.setCanvasSize(frameGrabber.getImageWidth(), frameGrabber.getImageHeight());
					canvas.showImage(f);

				} else if (frameGrabber.getFrameNumber() == frame_two) {
					f2 = f;
					canvas = new CanvasFrame("" + frameGrabber.getFrameNumber());
					canvas.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
					canvas.setCanvasSize(frameGrabber.getImageWidth(), frameGrabber.getImageHeight());
					canvas.showImage(f);
					break;
				}
			}

			frameGrabber.stop();
			Mat mf1 = converter.convertToMat(f1);
			Mat mf2 = converter.convertToMat(f2);
			Mat mg1 = new Mat();
			Mat mg2 = new Mat();
			cvtColor(mf1, mg1, COLOR_BGR2GRAY);
			Frame gray = converter.convert(mg1);
			canvas.showImage(gray);

			cvtColor(mf2, mg2, COLOR_BGR2GRAY);
			Frame gray2 = converter.convert(mg2);
			canvas.showImage(gray2);

			IplImage diffGray = IplImage.create(converter.convert(gray).width(), converter.convert(gray).height(), IPL_DEPTH_8U, 1);
			IplImage iplGray = converter.convertToIplImage(gray);
			IplImage iplGray2 = converter.convertToIplImage(gray2);

			cvAbsDiff(iplGray, iplGray2, diffGray);

			CanvasFrame s = new CanvasFrame("" + frameGrabber.getFrameNumber());
			s.showImage(converter.convert(diffGray));
			Mat diffMatGray = converter.convertToMat(converter.convert(diffGray));
			Mat diffDestGray = new Mat();
			applyColorMap(diffMatGray, diffDestGray, COLORMAP_SPRING);

			Frame finalColorMapDiff = converter.convert(diffDestGray);
			s.showImage(finalColorMapDiff);
			canvas.dispose();
			s.dispose();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	static HashMap<Double, Double> colorScale(double one, double two, int min, int max, HashMap<Double, Double> channel) {
		double cnx_range = (two - one);
		for (double i = one; i <= two; i += 1) {
			double num = (i - one);
			double x_std = num / cnx_range;
			double x_scaled = (x_std * (max - min)) + min;
			channel.put(i, x_scaled);
		}
		return channel;
	}

	static double colorFromScale(double x_scaled, double one, double two, int min, int max) {
		double cnx_range = (two - one);
		double x_std = (x_scaled - min) / (max - min);
		double num = cnx_range * x_std;
		double i = num + one;
		return i;
	}

	static HashMap<Integer, Double> calculateChannelBucket(double c, double l, int buckets, HashMap<Double, Double> channel, HashMap<Integer, Double> channelBucket) {
		Double i = new Double(c);
		int counter = 0;
		double value = 0;
		int prev_bucket = 0;
		int current_bucket = 0;
		while (i <= l) {
			int b = buckets;
			current_bucket = (counter / b);
			if (prev_bucket != current_bucket) {
				channelBucket.put(prev_bucket, (value / b));
				prev_bucket = current_bucket;
				value = 0;
				continue;
			} else {
				value = value + channel.get(i);
			}
			counter++;
			i++;
		}
		return channelBucket;
	}

	static void colorMap(COLOR_MODEL colorModel, ColorInstance one, ColorInstance two, ColorInstance three, int number_bits) {
		switch (colorModel) {
		case RGB: {
			
			ArrayList<Double> value_a = new ArrayList<Double>();
			ArrayList<Integer> color_x = new ArrayList<Integer>();
			ArrayList<Integer> color_y = new ArrayList<Integer>();
			ArrayList<Integer> color_z = new ArrayList<Integer>();
			double interval_x1 = 0,interval_y1 = 0,interval_z1 = 0;
			double interval_x2 = 0,interval_y2 = 0,interval_z2 = 0;
			double min_x1 = one.x,middle_x1 = two.x,min_y1 = one.y,middle_y1 = two.y,min_z1 = one.z,middle_z1 = two.z;
			boolean flag1  = false, flag2 = false;
			
			double number = Math.pow(2, NUMBER_OF_BITS);
			double interval = 2.0/number;
			/*for (Entry e : xChannel.entrySet()) {
				if(Math.abs((Double)e.getValue()+1)<0.00000001)
				{
					flag1 = true;
					continue;
				}
				if(flag1)
				{
					interval_x1 = (Double)e.getValue()+1;
					flag1 = false;
				}
				if(Math.abs((Double)e.getValue())<0.00000001)
				{
					flag2 = true;
					continue;
				}
				if(flag2)
				{
					interval_x2 = (Double)e.getValue();
					flag2 = false;
					break;
				}
			}
			
			for (Entry e : yChannel.entrySet()) {
				if(Math.abs((Double)e.getValue()+1)<0.00000001)
				{
					flag1 = true;
					continue;
				}
				if(flag1)
				{
					interval_y1 = (Double)e.getValue()+1;
					flag1 = false;
				}
				if(Math.abs((Double)e.getValue())<0.00000001)
				{
					flag2 = true;
					continue;
				}
				if(flag2)
				{
					interval_y2 = (Double)e.getValue();
					flag2 = false;
					break;
				}
			}
			
			for (Entry e : zChannel.entrySet()) {
				if(Math.abs((Double)e.getValue()+1)<0.00000001)
				{
					flag1 = true;
					continue;
				}
				if(flag1)
				{
					interval_z1 = (Double)e.getValue()+1;
					flag1 = false;
				}
				if(Math.abs((Double)e.getValue())<0.00000001)
				{
					flag2 = true;
					continue;
				}
				if(flag2)
				{
					interval_z2 = (Double)e.getValue();
					flag2 = false;
					break;
				}
			}*/
			
			
			/*
			for(int i = 0;i<(int)Math.pow(2,NUMBER_OF_BITS);i++)
			{
				double value = -1.0+2.0*i/(Math.pow(2, NUMBER_OF_BITS));
				value_a.add(value);
				double devision = 2.0*i/(Math.pow(2, NUMBER_OF_BITS));
				
				if(value<=0)
				{
					double x = 0,y = 0,z = 0;
					for (Entry e : xChannel.entrySet()) {
						if(Math.abs(value - (Double)e.getValue())<=devision)
						{
							x = (Double)e.getKey();
							break;
						}
					}
					for (Entry e : yChannel.entrySet()) {
						if(Math.abs(value - (Double)e.getValue())<=devision)
						{
							y = (Double)e.getKey();
							break;
						}
					}
					for (Entry e : zChannel.entrySet()) {
						if(Math.abs(value - (Double)e.getValue())<=devision)
						{
							z = (Double)e.getKey();
							break;
						}					
					}
					color_x.add(x);
					color_y.add(y);
					color_z.add(z);
				}
				
				else
				{
					double x = 0,y = 0,z = 0;
					for (Entry e : xChannel.entrySet()) {
						if(Math.abs(value - (Double)e.getValue())<=devision)
						{
							x = (Double)e.getKey();
							break;
						}
					}
					for (Entry e : yChannel.entrySet()) {
						if(Math.abs(value - (Double)e.getValue())<=devision)
						{
							y = (Double)e.getKey();
							break;
						}
					}
					for (Entry e : zChannel.entrySet()) {
						if(Math.abs(value - (Double)e.getValue())<=devision)
						{
							z = (Double)e.getKey();
							break;
						}
					}
					color_x.add(x);
					color_y.add(y);
					color_z.add(z);
				}
							
				
			}*/
			double x_range1 = two.x - one.x,x_range2 = three.x - two.x;
			double y_range1 = two.y - one.y,y_range2 = three.y - two.y;
			double z_range1 = two.z - one.z,z_range2 = three.z - two.z;
			
			for(int i = 0;i<Math.pow(2, NUMBER_OF_BITS);i++)
			{
				double value = -1.0+2.0*i/(Math.pow(2, NUMBER_OF_BITS));
				value_a.add(value);
				System.out.println(value);
				System.out.println(value_a.size());
				
				if(value<=0)
				{
					int x = (int) (i/Math.pow(2, NUMBER_OF_BITS-1)*x_range1+min_x1);
					int y = (int) (i/Math.pow(2, NUMBER_OF_BITS-1)*y_range1+min_y1);
					int z = (int) (i/Math.pow(2, NUMBER_OF_BITS-1)*z_range1+min_z1);
					color_x.add(x);
					color_y.add(y);
					color_z.add(z);
				}
				else
				{
					int j = (int) (i-Math.pow(2, NUMBER_OF_BITS-1));
					int x = (int) (j/Math.pow(2, NUMBER_OF_BITS-1)*x_range2+middle_x1);
					int y = (int) (j/Math.pow(2, NUMBER_OF_BITS-1)*y_range2+middle_y1);
					int z = (int) (j/Math.pow(2, NUMBER_OF_BITS-1)*z_range2+middle_z1);
					color_x.add(x);
					color_y.add(y);
					color_z.add(z);
				}
			}
			
			for(int i = 0;i<value_a.size();i++)
			{
				System.out.print("ColorID: "+i+"\t");
//				System.out.println(value_a.get(i)+"\t");
				//System.out.print(value_a.get(i));
				System.out.print(color_x.get(i)+"  ");
				System.out.print(color_y.get(i)+"  ");
				System.out.print(color_z.get(i)+"\n");	
			}
			
			System.out.println(min_x1);
			System.out.println(middle_x1);
			

			System.out.println(min_y1);
			System.out.println(middle_y1);
			

			System.out.println(min_z1);
			System.out.println(middle_z1);
			

//			IplImage whiteImg = IplImage.create((int)Math.pow(2, NUMBER_OF_BITS)+1,
//		            100, IPL_DEPTH_8U, 3);
			
			IplImage whiteImg = IplImage.create(100,
            (int) Math.pow(2, NUMBER_OF_BITS), IPL_DEPTH_8U, 3);
		       
			System.out.println(color_x.size());
			System.out.println(interval);
			/*for(int i = 0;i<30;i++)
				for(int j = 0;j<100;j++)
				{
					CvScalar Minc = cvScalar(10, 252, 150,0);
					opencv_core.cvSet2D(whiteImg, i, j, Minc);
					
				}
			
			for(int i = 30;i<60;i++)
				for(int j = 0;j<100;j++)
				{
					CvScalar Minc = cvScalar(180, 130, 155,0);
					opencv_core.cvSet2D(whiteImg, i, j, Minc);
					
				}
			
			for(int i = 60;i<90;i++)
				for(int j = 0;j<100;j++)
				{
					CvScalar Minc = cvScalar(230, 22, 130,0);
					opencv_core.cvSet2D(whiteImg, i, j, Minc);
					
				}*/
			
			for(int i = 0;i<Math.pow(2, NUMBER_OF_BITS);i++)
			{
				CvScalar Minc = cvScalar(color_x.get(i),color_y.get(i),color_z.get(i),0);
				for(int j = 0;j<100;j++)
				{
					opencv_core.cvSet2D(whiteImg, i, j, Minc);
				}
			}
			
		    cvShowImage("", whiteImg);
					       
		    cvWaitKey(0);
			
		}
		default:
			break;
		}

	}

	/**
	 * @param xChannel
	 * @param yChannel
	 * @param zChannel
	 */
	private static void saveChannelIntensities(HashMap<Double, Double> xChannel, HashMap<Double, Double> yChannel, HashMap<Double, Double> zChannel) {
		StringBuffer x = new StringBuffer();
		StringBuffer y = new StringBuffer();
		StringBuffer z = new StringBuffer();
		for (Entry e : xChannel.entrySet()) {
			x.append(e.getValue() + " " + e.getKey() + "\r\n");
		}
		for (Entry e : yChannel.entrySet()) {
			y.append(e.getValue() + " " + e.getKey() + "\r\n");
		}
		for (Entry e : zChannel.entrySet()) {
			z.append(e.getValue() + " " + e.getKey() + "\r\n");
		}
		saveFile("colorMap/rgb_x_channel_scale", x);
		saveFile("colorMap/rgb_y_channel_scale", y);
		saveFile("colorMap/rgb_z_channel_scale", z);
	}

//	class Scale extends JPanel {
//		public void paint(Graphics g) {
//			Graphics2D g2 = (Graphics2D) g;
//			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
//			Font font = new Font("Serif", Font.PLAIN, 96);
//			g2.setFont(font);
//			g2.drawString("Text", 40, 120);
//		}
//	}
//
//	static void convertColorScale() {
//		float[] hsbValues = new float[3];
//
//		hsbValues = Color.RGBtoHSB(one.x, one.y, one.z, hsbValues);
//
//		float hue, saturation, brightness;
//		hue = hsbValues[0];
//		saturation = hsbValues[1];
//		brightness = hsbValues[2];
//
//		JFrame f = new JFrame();
//		f.getContentPane().add(new Colors().new Scale());
//		f.setSize(300, 200);
//		f.setVisible(true);
//		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//
//		Color.HSBtoRGB(hue, saturation, brightness);
//
//	}

	static void visualizeColorScale(HashMap<Double, Double> xChannel, HashMap<Double, Double> yChannel, HashMap<Double, Double> zChannel) {
		// IplImage image = new IplImage();
		// OpenCVFrameConverter.ToIplImage converter = new
		// OpenCVFrameConverter.ToIplImage();
		// // Mat mat = converter.convertToMat(converter.convert(image));
		// Mat mat = new Mat();
		// CanvasFrame canvas = new CanvasFrame("Color Scale");
		// line(mat,new Point(0,0), new Point(200) , new
		// Scalar(130.0,130.0,130,0));
		// canvas.showImage(converter.convert(mat));
		// System.out.print("tst");
	}

	/**
	 * @param xChannelBucket
	 * @param yChannelBucket
	 * @param zChannelBucket
	 */
	private static void generateColorSets(HashMap<Integer, Double> xChannelBucket, HashMap<Integer, Double> yChannelBucket, HashMap<Integer, Double> zChannelBucket) {
		System.out.println("Color Sets: ");
		StringBuffer b = new StringBuffer();
		StringBuffer colors = new StringBuffer();
		colors.append("ColorMAP: RGB " + "number of bits: " + NUMBER_OF_BITS + "\r\n");
		int color_id = 0;
		for (int i = 0; i < xChannelBucket.size(); i++) {
			double x = xChannelBucket.get(i);
			double xcolor;
			if (x > 0) {
				xcolor = colorFromScale(x, two.x, three.x, 0, 1);
			} else {
				xcolor = colorFromScale(x, one.x, two.x, -1, 0);
			}
			for (int j = 0; j < yChannelBucket.size(); j++) {
				double y = yChannelBucket.get(j);
				double ycolor;
				if (x > 0) {
					ycolor = colorFromScale(y, two.y, three.y, 0, 1);
				} else {
					ycolor = colorFromScale(y, one.y, two.y, -1, 0);
				}
				for (int k = 0; k < zChannelBucket.size(); k++) {
					double z = zChannelBucket.get(k);
					double zcolor;
					if (x > 0) {
						zcolor = colorFromScale(z, two.z, three.z, 0, 1);
					} else {
						zcolor = colorFromScale(z, one.z, two.z, -1, 0);
					}
					b.append("colorID: " + color_id + "     " + x + " " + y + " " + z + "\r\n");
					colors.append("ColorID: " + color_id + "	" + xcolor + "  " + ycolor + "  " + zcolor + "\r\n");
					System.out.println("ColorID: " + color_id + "	" + xcolor + "  " + ycolor + "  " + zcolor);
					color_id++;
				}
			}
		}
		saveFile("colorMap/rgb_", b);
		saveFile("colorMap/rgb_colors_", colors);
	}

	static void saveFile(String fileName, StringBuffer b) {
		File cMap;
		BufferedWriter bWriter = null;
		try {
			if (fileName != null && !fileName.isEmpty()) {
				cMap = new File(fileName + System.currentTimeMillis() + ".txt");
				if (!cMap.exists()) {
					try {
						cMap.createNewFile();
						System.out.println("Created file");
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				try {
					bWriter = new BufferedWriter(new FileWriter(cMap));
					bWriter.write(b.toString());
					bWriter.flush();
				} catch (IOException e) {
					e.printStackTrace();

				}
			}
		} finally {
			if (bWriter != null)
				try {
					bWriter.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
	}

	static void printMap(Map<?, ?> map) {
		for (Entry e : map.entrySet()) {
			System.out.println(e.getKey() + " " + e.getValue());
		}
	}

	static void splitBitsByColorChannel(COLOR_MODEL colorModel) {

		int eachChannelBits = NUMBER_OF_BITS / 3;
		int excessChannelBit = NUMBER_OF_BITS % 3;

		COLOR_MAP.x_bits = eachChannelBits + excessChannelBit;
		COLOR_MAP.y_bits = eachChannelBits;
		COLOR_MAP.z_bits = eachChannelBits;

		int boxWidthX = (int) Math.pow(2, (colorModel.getXbits() - COLOR_MAP.x_bits));
		int boxWidthY = (int) Math.pow(2, (colorModel.getYbits() - COLOR_MAP.y_bits));
		int boxWidthZ = (int) Math.pow(2, (colorModel.getZbits() - COLOR_MAP.z_bits));

		COLOR_MAP.x_buckets = (int) (Math.pow(2, colorModel.getXbits()) / boxWidthX);
		COLOR_MAP.y_buckets = (int) (Math.pow(2, colorModel.getYbits()) / boxWidthY);
		COLOR_MAP.z_buckets = (int) (Math.pow(2, colorModel.getZbits()) / boxWidthZ);
	}

	class ColorInstance {
		int x;
		int y;
		int z;

		public ColorInstance(int x, int y, int z) {
			this.x = x;
			this.y = y;
			this.z = z;
		}
	}

}
