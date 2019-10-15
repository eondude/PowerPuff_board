package client;


import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import server.IBoardServer;
import javax.swing.UIManager;


public class createWB {
	static IBoardServer boardServer;
	
	private static void initialize() {
		WhiteBoard newPad = new WhiteBoard("Distributed", boardServer);
        newPad.addWindowListener(
            new WindowAdapter() {	//WindowAdapter is an abstract class.Use this to override the default one and
            						// list only what's needed.
                public void windowClosing(WindowEvent e) {
                    System.exit(0);
                }
            });
     
        new Thread(new EventThread(newPad), "EventThread").start();
	}
	
    public static void main(String args[]) {
        try {
        	Registry registry = LocateRegistry.getRegistry("localhost");
            
			//Retrieve the stub/proxy for the remote math object from the registry
			boardServer = (IBoardServer) registry.lookup("BoardServer");
        }catch(RemoteException e){
        	e.printStackTrace();
        	System.exit(-1);
        }catch(NotBoundException e) {
        	e.printStackTrace();
        	System.exit(-1);
        }
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
        	public void run()
        	{
        		initialize();
        	}
        });
    }
}
