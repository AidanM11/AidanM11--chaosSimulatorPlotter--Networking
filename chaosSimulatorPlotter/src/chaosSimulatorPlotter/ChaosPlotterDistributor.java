package chaosSimulatorPlotter;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;

public class ChaosPlotterDistributor extends Thread {
	private World world;
	private ArrayList<ConnectionHandler> connections;
	
	public ChaosPlotterDistributor(World world, ArrayList<ConnectionHandler> connections) {
		super();
		this.world = world;
		this.connections = connections;
	}
	public void run(){
		//thread vars
				int numThreads = connections.size();
				
				//setup vars
				int minX = 0;
				int maxX = 400;
				int minY = 0;
				int maxY = 400;
				
				int resX = 20;
				int resY = 20;

				//define vars for later
				int numPoints = resX*resY;
				double gapX = ((double)maxX - minX)/(resX - 1);
				double gapY = ((double)maxY - minY)/(resY - 1);
				int spaceX = 0; //keep at zero
				int spaceY = 0; //keep at zero
				
				//start timer
				final long startTime = System.currentTimeMillis();
				
				//threads...
				//generate points
				double totalPoints[][] = new double[numPoints][2];
				for (int i = 0; i < resX; i++) {
					for (int j = 0; j < resY; j++) {
						totalPoints[i*resX+j][0] = i*gapX+minX+spaceX;
						totalPoints[i*resX+j][1] = j*gapY+minY+spaceY;
					}
				}
				
				//divide points
				double threadPoints[][][] = new double[numThreads][2][2];
				double pointsPerThread = (double)numPoints/numThreads;
				for(int i = 0; i < numThreads; i++) {
					int startIndex = (int)(pointsPerThread*i);
					int endIndex = (int)(pointsPerThread*(i+1));
					threadPoints[i] = Arrays.copyOfRange(totalPoints, startIndex, endIndex);
				}
				
				//create threads 
				System.out.println("creating "+numThreads+" threads");
				//Generate threadArray[] = new Generate[numThreads];
				for (int i = 0; i < numThreads; i++) {
					try {
						connections.get(i).startGeneration(threadPoints[i], world);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}		
				
				//wait for threads to finish
				boolean active = true;
				while (active) {
					active = false;
					for (int i = 0; i < numThreads; i++) {
						if (!connections.get(i).isGenFinished()) {
							active = true;
						}
					}
					//sleep to use less cpu
					int sleepTime = 100; //in ms
					try {
						Thread.sleep(sleepTime);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
				//stop timers
				final long endTime = System.currentTimeMillis();
				double execTime = (endTime - startTime)/(double)1000;
				double timePerPoint = execTime/numPoints;
				System.out.println("program took: "+execTime+" s, "+timePerPoint+" s per point");
				
				//write data
				int lenPoints = 0;
				PrintWriter writer;
				writer = null;
				try {
					writer = new PrintWriter("output.txt","UTF-8");
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				for (int i = 0; i < numThreads; i++) {
					for (int j = 0; j < connections.get(i).getOutputArray().length; j++) {
						writer.println(Arrays.toString(connections.get(i).getOutputArray()[j]));
						lenPoints++;
					}
				}
				writer.close();
				System.out.println(lenPoints+" points in output.txt");
	}
}
