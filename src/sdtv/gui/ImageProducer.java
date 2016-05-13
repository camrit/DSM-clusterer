package sdtv.gui;

import javax.swing.*;
import javax.imageio.*;
import java.awt.*;
import java.awt.image.*;
import java.io.*;
import java.util.*;


public class ImageProducer {

	private JPanel input;
	private String filename;
	
	public ImageProducer(JPanel input, String filename) {	
		this.input = input;
		this.filename = filename;
		createImage();
	}
	
	
	public void createImage() {	
		int width = input.getWidth();
	    int height = input.getHeight();
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics2D g2r = image.createGraphics();
		input.paint(g2r);
		g2r.dispose();
		
			  	   
	    // Write generated image to a file
	    try {
	        // Save as PNG
	        File file = new File(filename + ".png");
	        ImageIO.write(image, "png", file);
	       
	    } catch (IOException e) {    	
	    	System.err.println("image writing failed : " + e.getMessage());
	    }
	   
	
	   
	   
	}
	/*
	public static void saveComponentAsImage(int compType, Object objToSave) {
		int width;
		int height;
		BufferedImage image;
		if (compType == REPORT) {
		JComponent compToSave = (JComponent)objToSave;
		width = compToSave.getWidth();
		height = compToSave.getHeight();
		image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics2D g2r = image.createGraphics();
		compToSave.paint(g2r);
		g2r.dispose();
//		Now try the actual write
		try {
		ImageIO.write(image, "png", new File(“imagefile.png"));
		}
		catch (IOException ioe) {
		System.out.println(“Error in saving Image”);
		}
		} 
	*/
	
	
}
