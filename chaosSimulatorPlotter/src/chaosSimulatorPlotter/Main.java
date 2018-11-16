package chaosSimulatorPlotter;

import java.io.DataInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;

import chaosSimulatorPlotter.Logic;
import chaosSimulatorPlotter.World;

public class Main{
	private static ArrayList<ConnectionHandler> connections = new ArrayList<ConnectionHandler>();
	public static final int LISTENING_PORT = 42021;
	public static final String ipAddress = "10.2.22.134";
	public static ChaosPlotterDistributor chaosPlotDistri;
	
	public static void main(String[] args) throws IOException, InterruptedException {
		DataInputStream in = new DataInputStream(System.in);
		ServerSocket listener;  // Listens for incoming connections.
        Socket connection;
        listener = new ServerSocket(LISTENING_PORT, 5, InetAddress.getByName(ipAddress));
        System.out.println("Listening on port " + LISTENING_PORT);
		while(connections.size() <= 0) {
			System.out.println(connections.size());
			connection = listener.accept(); 
            ConnectionHandler handler = new ConnectionHandler(connection);
            handler.start();
            handler.join();
		}
		System.out.println("Starting Generation...");
		//WORLD GENERATION
		int width = 800;
		int height = 800;
		int maxTicks = 100000;
		int posArraySize = 1000;
		
		World world = new World(posArraySize);
		
		//set world vars
		//REMEMBER TO SET LOGIC CLASS'S MAX FORCE TO BE EQUAL TO WORLD MAX FORCE
		world.setMaxForce(1000);
		world.setHomeX(400);
		world.setHomeY(400);
		world.setDefaultCoef(10);
		world.setHomeCoef(10);
		world.setFricition(.95);
		world.setMaxStopDist(15);
		world.setHomeX(width/2);
		world.setHomeY(height/2);
		chaosPlotDistri = new ChaosPlotterDistributor(world, connections);
		chaosPlotDistri.start();
		
		
		
		
		
		
		//writer   OLD
		/*System.out.println("writing to file");
		PrintWriter writer = new PrintWriter("output.txt","UTF-8");
		for(int i = 0; i < output.length; i++) {
			writer.println("["+output[i][0]+", "+output[i][1]+", "+output[i][2]+", "+output[i][3]+"]");
		}
		writer.close();
		*/
		
		//logWriter.println("program took: "+execTime+" s, "+timePerPoint+" s per point");
		
		//close file
		//writer.close();
		//logWriter.close();
		//System.out.println("data in output.txt");
	}
	
	public static void runSimulation(World world) {
		
	}
	
	public static void addThread(ConnectionHandler thread) {
		connections.add(thread);
		
	}
}
