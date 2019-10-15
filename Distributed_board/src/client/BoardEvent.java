package client;

import java.io.Serializable;
import java.awt.*;
import java.util.ArrayList;

public class BoardEvent implements Serializable{
	
	private static final long serialVersionUID = 1L;
	public String eventType;
	public int eventID;
	public String userID;
	public ArrayList<String> userList;
	public ArrayList<String> textInput;
	public drawings[] mdrawings;
	public drawings drawing;
	public drawings[] points;
	
	public BoardEvent(String etype) {
		eventType=etype;
	}

}
