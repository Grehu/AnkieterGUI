package server.gui;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

import java.util.Scanner;

import server.main.Server;

public class ServerInitialization {
	
	public static ExecutorService executor;
	public static FutureTask<String> server;
	public static int port = 10055;
	
	public synchronized static FutureTask<String> runServer(){
		executor = Executors.newSingleThreadExecutor();
		server = null;
		Server.on = true;
		FutureTask<String> server = new FutureTask<String>(new Server(port));
		executor.execute(server);
		System.out.println("Serwer uruchomiono: "+Server.currentDateStr());
		return server;
	}
	
	public synchronized static String stopServer(){
		Server.on = false;
		String wynik = "";
		try {
			wynik = server.get();
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
		executor.shutdown();
		return wynik;
	}
	
	public static void main(String[] args){
		
		
		server = runServer();
		System.out.println(stopServer());
		Scanner skaner = new Scanner(System.in);
		String odpowiedü = skaner.nextLine();
		server = runServer();
		System.out.println(stopServer());
	}

}
