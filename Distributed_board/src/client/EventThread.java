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
		
		case "mouseNulled":
			iArray[index] = new Line();
			iArray[index].x1=iArray[index].x2=0;
			iArray[index].y1=iArray[index].y2=1;
			iArray[index].type = 4;
	        iArray[index].R = 100;
	        iArray[index].G = 100;
	        iArray[index].B = 100;
	        iArray[index].stroke = 1.0f;
	        index++;
		}
		
	}
	
	public void run() {
		while (true) {
			ArrayList<BoardEvent> bEvnts = new ArrayList<BoardEvent>();
			BoardEvent event1=new BoardEvent("mouseNulled");
			
			try {
				bEvnts.add(event1);
			}
			catch(Exception e) {
				e.printStackTrace();
			}
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
			}catch (NullPointerException e) {
				e.printStackTrace();
			}
		}
	}
}
