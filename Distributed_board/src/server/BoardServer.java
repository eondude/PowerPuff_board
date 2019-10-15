package server;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

import client.BoardEvent;

public class BoardServer extends UnicastRemoteObject implements IBoardServer {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	int eventSequence=0;
	ArrayList<BoardEvent> ogEvents;
	
	protected BoardServer() throws RemoteException {
		super();
	}
	
	public synchronized void addBoardEvent(BoardEvent event) {
		event.eventID = eventSequence++;
		ogEvents.add(event);
	}
	
	public synchronized ArrayList<BoardEvent> getBoardEvents(int fromHere) {
		return new ArrayList<BoardEvent>(ogEvents.subList(fromHere, ogEvents.size()));
	}
	
	public static void main(String args[]) {
		try {
			String serverName = "localhost";
			String serviceName = "BoardServer";
			
			IBoardServer obj = new BoardServer();
			
			Registry registry = LocateRegistry.getRegistry(serverName);
			registry.rebind(serviceName, obj);
            
            System.out.println("Successful binding");
            
		}catch(Exception e){
			System.out.println("BoardServer Err: "+ e.getMessage());
			e.printStackTrace();
		}
	}
}
