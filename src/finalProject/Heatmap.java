package finalProject;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.imageio.ImageIO;

import org.apache.commons.math.stat.descriptive.moment.Mean;
import org.apache.commons.math.stat.descriptive.moment.StandardDeviation;

import sun.nio.cs.Surrogate;

public class Heatmap
{
	
	/**
	 * @param args
	 *        The filename prefix, x-dimension, y-dimension, and z-dimension.
	 */
	public static void main(String[] args)
	{
		String prefix = args[0];
		int x = Integer.parseInt(args[1]);
		int y = Integer.parseInt(args[2]);
		int z = Integer.parseInt(args[3]);
		
		float[][][] averages = new float[z][y][x];
		float[][][] stdDeviations = new float[z][y][x];
		
		ArrayList<Thread> threads = new ArrayList<Thread>(z * x * y);
		for(int k = 0; k < z; k++)
		{
			for(int j = 0; j < y; j++)
			{
				for(int i = 0; i < x; i++)
				{
//					Thread t = new Thread(new ReadingJob(averages, stdDeviations, i, j, k, prefix));
//					t.start();
//					threads.add(t);
					(new ReadingJob(averages, stdDeviations, i, j, k, prefix)).run();
				}
			}
		}
		
		for(Thread t : threads)
			try
			{
				t.join();
			}
			catch(InterruptedException e)
			{/* nothing to do */}
		
		
		render(prefix, "averages", averages);
		render(prefix, "stdev", stdDeviations);
	}

	private static class ReadingJob implements Runnable
	{
		private float[][][] averages, stdDeviations;
		private int zPoint, yPoint, xPoint;
		private String prefix;
		
		public ReadingJob(float[][][] averages, float[][][] stdDeviations, int x, int y, int z, String prefix)
		{
			this.averages = averages;
			this.stdDeviations = stdDeviations;
			xPoint = x;
			yPoint = y;
			zPoint = z;
			this.prefix = prefix;
		}
		
		public void run()
		{
			//open file
			String fileName = String.format("%s_%d_%d_%d_.txt", prefix, xPoint, yPoint, zPoint);
			Scanner fileIn;
			try
			{
				fileIn = new Scanner(new File(fileName));
			}
			catch(FileNotFoundException e)
			{
				System.out.println("could not open "+ fileName);
				averages[zPoint][yPoint][xPoint] = stdDeviations[zPoint][yPoint][xPoint] = Float.NaN;
				return;
			}
			
			//find line with key
			String input = null;
			while(fileIn.hasNextLine())
			{
				input = fileIn.nextLine();
				if(input.contains("Primary Seller Profit")) break;
			}
			
			StandardDeviation sd = new StandardDeviation();
			Mean average = new Mean();
			boolean complete = false;
			while(fileIn.hasNextLine())
			{
				input = fileIn.nextLine();
				if(input.contains("Batch Done")) 
				{
					complete = true;
					break;
				}


				float profit = Float.parseFloat(input);
				
				sd.increment(profit);
				average.increment(profit);
			}
			
			if(complete)
			{
				averages[zPoint][yPoint][xPoint] = (float) average.getResult();
				stdDeviations[zPoint][yPoint][xPoint] = (float) sd.getResult();	
			}
			else
			{
				System.out.println(fileName + " is missing info");
				averages[zPoint][yPoint][xPoint] = stdDeviations[zPoint][yPoint][xPoint] = Float.NaN;
			}

			fileIn.close();
		}
	}
	
	private static void render(String prefix, String type, float[][][] data)
	{
		float min = min(data);
		float max= max(data);
		
		System.out.println(type + " max: " + max);
		System.out.println(type + " min: " + min);
		
		for(int z=0;z<data.length;z++)
		{
			render(prefix, type, z, data[z], max, min);
		}
		
	}
	
	private static float max(float[][][] data)
	{
		float max = Float.NEGATIVE_INFINITY;
		for(int z=0;z<data.length;z++)
			for(int y=0;y<data[z].length;y++)
				for(int x=0;x<data[z][y].length;x++)
					if (!Float.isNaN(data[z][y][x]) && data[z][y][x] > max)
						max = data[z][y][x];
		return max;
	}

	private static float min(float[][][] data)
	{
		float min = Float.POSITIVE_INFINITY;
		for(int z=0;z<data.length;z++)
			for(int y=0;y<data[z].length;y++)
				for(int x=0;x<data[z][y].length;x++)
					if (!Float.isNaN(data[z][y][x]) && data[z][y][x] < min)
						min = data[z][y][x];
		return min;
	}

	private static void render(String prefix, String type, int z, float[][] data, float max, float min)
	{
		BufferedImage outputImage = new BufferedImage(data.length, data[0].length , BufferedImage.TYPE_INT_RGB);
		for(int y=0; y < data.length; y++)
		{
			for(int x=0; x < data[0].length; x++)
			{
				if(Float.isNaN(data[y][x]))
				{
					outputImage.setRGB(x,y,0);
					continue;
				}
				
				//normalize data point
				float normalized = data[y][x]/ max; 
				//convert to RGB and set
				Color color = Color.getHSBColor(normalized * 2.0f/3.0f, 1.0f, 1.0f);//2/3rds==true blue
				int r = color.getRed();
				int g = color.getGreen();
				int b = color.getBlue();
				outputImage.setRGB(x,y,color.getRGB());
			}
		}
		
		String fileName = String.format("%s-%s_%d.png", prefix, type, z);
		File outputFile = new File(fileName);
		System.out.println("Writing to " + fileName);
		try
		{
			ImageIO.write(outputImage, "PNG", outputFile);
		}
		catch(IOException e)
		{
			throw new RuntimeException("Couldn't write file " + fileName, e);
		}
	}
}
