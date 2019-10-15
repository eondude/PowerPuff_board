package client;

import java.rmi.RemoteException;
import java.util.ArrayList;

public class EventThread implements Runnable {
	public final WhiteBoard wb;
	private final int PENCIL = 3;
	private final int RUBBER = 8;
	private drawings[] iArray;
	private static int index=0;
	private int nextevntID =0;
	public EventThread(WhiteBoard whiteb) {
		wb=whiteb;
	}
	
	private void dispatchEvent(BoardEvent event) {
		
		switch(event.eventType) {
		case "mousePressed":
			switch (event.drawing.type) {
			case PENCIL:
				iArray[index]=event.drawing;
				index++;
			}
		case "mouseDragged":
			switch (event.drawing.type) {
			case PENCIL:
					iArray[index-1]= event.points[0];
					iArray[index]= event.points[1];
	                wb.repaint();
	                index++;
			}
		case "mouseReleased":
					iArray[index]=event.drawing;
					wb.repaint();
					index++;
			
		}
	}
	
	public void run() {
		while (true) {
			ArrayList<BoardEvent> bEvnts;
			try {
				bEvnts = wb.bServer.getBoardEvents(nextevntID);
				
				for (BoardEvent event : bEvnts) {
					dispatchEvent(event);
				}
				
				if (bEvnts.size()>0) {
					nextevntID=bEvnts.get(bEvnts.size() - 1).eventID +1;
				}
			}catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	}
}
