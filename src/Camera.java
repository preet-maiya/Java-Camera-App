import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.io.File;
import java.lang.*;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.ImageIcon;
import javax.swing.*;

import org.opencv.core.*;
import org.opencv.highgui.Highgui;        
import org.opencv.highgui.VideoCapture; 

public class Camera extends JPanel {
	BufferedImage image;
	
	public class VideoGrabber extends Thread {
		VideoCapture camera;
		Mat frame;
		BufferedImage image;
		
	 	public VideoGrabber() {
	 		camera = new VideoCapture(0);
	 		frame = new Mat();
	 		
	 		if(!camera.isOpened()){
	        	System.out.println("Error");
	        }
	 	}
	 	
	 	public BufferedImage MatToBufferedImage(Mat frame) {
	        //Mat() to BufferedImage
	        int type = 0;
	        if (frame.channels() == 1) {
	            type = BufferedImage.TYPE_BYTE_GRAY;
	        } else if (frame.channels() == 3) {
	            type = BufferedImage.TYPE_3BYTE_BGR;
	        }
	        BufferedImage image = new BufferedImage(frame.width(), frame.height(), type);
	        WritableRaster raster = image.getRaster();
	        DataBufferByte dataBuffer = (DataBufferByte) raster.getDataBuffer();
	        byte[] data = dataBuffer.getData();
	        frame.get(0, 0, data);

	        return image;
	    }
	 	
	 	public void run() {
	 		while(true) {
		 		camera.read(frame);
		 		image = MatToBufferedImage(frame);
	 		}
	 	}
	 	
	 	public BufferedImage getFrame() {
	 			return image;
	 	}
	}
	
	public void saveImage(BufferedImage img, String location) {        
        try {
            File outputfile = new File(location);
            ImageIO.write(img, "png", outputfile);
        } catch (Exception e) {
            System.out.println("error");
        }
    }
	 @Override
	    public void paint(Graphics g) {
	        g.drawImage(image, 0, 0, this);
	    }
	 
	 public Camera(BufferedImage img) {
	        image = img;
	    }
	 public Camera() {
	 }
	
	 public void paintComponent(Graphics g) {
		 if(image != null) {
			 g.drawImage(image, 0, 0, this);
			 super.paintComponent(g);
		 }
		 }
	 
	public static void main(String args[]) {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		Camera c = new Camera();
		VideoGrabber grabber = c.new VideoGrabber();
		grabber.start();
		
		BufferedImage im;
		im = grabber.getFrame();
		while(im != null)
			im = grabber.getFrame();
		
		try {
			Thread.currentThread();
			Thread.sleep(2500);
		}
		
		catch(Exception e) {
			System.out.println(e);
		}
		
		JFrame main_frame = new JFrame();

		main_frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		main_frame.setTitle("Camera");
		main_frame.setSize(640, 480 + 300);
		main_frame.setLocation(60, 40);
		
		String fname;
		String location = "/home/preetham/snaps/";
        int i=0;
		
		JPanel camera_feed = new Camera(im);
		while(true) {
			im = grabber.getFrame();
			System.out.println(im);
			fname = "snap"+i+".png";
			c.saveImage(im, location+fname);
			
			System.out.println("Saved image "+i);
			try {
				Thread.currentThread();
				Thread.sleep(2500);
			}
			
			catch(Exception e) {
				System.out.println(e);
			}
			i++;
		}
	}
}
