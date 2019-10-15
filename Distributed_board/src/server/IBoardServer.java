package server;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import client.BoardEvent;

public interface IBoardServer extends Remote{
	
	//String joinBoard(String candidateID) throws RemoteException;
	
	void addBoardEvent(BoardEvent event) throws RemoteException;
	
	ArrayList<BoardEvent> getBoardEvents(int fromHere) throws RemoteException;
}
