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

import org.bytedeco.javacpp.FlyCapture2.Image;
import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacpp.opencv_core.CvMemStorage;
import org.bytedeco.javacpp.opencv_core.IplImage;
import org.bytedeco.javacpp.opencv_videoio.VideoCapture;
import org.bytedeco.javacv.CanvasFrame;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.bytedeco.javacv.OpenCVFrameGrabber;

public class Draw {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
//		 IplImage img;
		 CvScalar Minc = cvScalar(255,255,255,0),Maxc = cvScalar(145,255,255,0);
//		 CvSeq contour1 = new CvSeq(), contour2;
//		 CvMemStorage storage = CvMemStorage.create();
//		 double areaMax = 1000, areaC = 0;
//		 
//		 img1 = cvLoadImage("ColorImg.jpg");
		IplImage whiteImg = IplImage.create(100,
	            100, IPL_DEPTH_8U, 3);
		
//	       cvSetZero(whiteImg);
//	       cvSetZero(whiteImg);
//	       cvNot(whiteImg,whiteImg);
//	       cvShowImage("", whiteImg);
//	       cvWaitKey(0);
	       
	      
	       for(int i = 0;i<100;i++)
	    	   for(int j = 0;j<100;j++)
	    	   {
	    		   opencv_core.cvSet2D(whiteImg, i, j, Maxc);
	    	   }
	       
	       cvShowImage("", whiteImg);
	       
	       cvWaitKey(0);
	}

}
