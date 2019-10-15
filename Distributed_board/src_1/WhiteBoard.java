/*
 * @Author: Puffrora
 * @Date:   2019-09-13 12:36:07
 * @Last Modified by:   Puffrora
 * @Last Modified time: 2019-10-06 09:31:23
 */
package client
;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import server.IBoardServer;

import java.io.*;

import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.RemoteException;

import javax.net.ServerSocketFactory;

public class WhiteBoard extends JFrame
{

	private static final long serialVersionUID = 1L;
	private JButton choices[];
    private String names[] = {"New", "Open", "Save", "Pencil", "Line", "Rect", "Oval", "Circle", "Rubber", "Color", "Stroke", "Word"};
    private String styleNames[] = {"Garamond"};
    private Icon items[];
    private Color color = Color.black;
    final IBoardServer bServer;
    private JLabel statusBar;
    public DrawPanel drawingArea;
    private ObjectInputStream input;
    private ObjectOutputStream output;
    private int width = 1800, height = 1000;
    private int currentChoice = 3;
    private float stroke = 1.0f;
    int R, G, B;
    int style1, style2;
    int index = 0;
    String styleCur;
    JToolBar buttonPanel;
    JCheckBox bold, italic;
    JComboBox<String> styles;;
    int number = 0;
    drawings[] iArray = new drawings[9999];//where the line, circle... are stored
    
    private static int counter = 0;
    private static int port = 9090;

    public WhiteBoard(String name,IBoardServer Server) {
        super("Distributed WhiteBoard");
        bServer = Server;
        //Menu Bar
        JMenuBar bar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        fileMenu.setMnemonic('F');
        
        //FOR ADMIN ONLY-START
        JMenuItem newItem = new JMenuItem("New");
        newItem.setMnemonic('N');
        newItem.addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        newFile();//The function
                    }
                });
        fileMenu.add(newItem);
        JMenuItem saveItem = new JMenuItem("Save");
        saveItem.setMnemonic('S');
        saveItem.addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        saveFile();//The function
                    }
                });
        fileMenu.add(saveItem);
        JMenuItem loadItem = new JMenuItem("Load");
        loadItem.setMnemonic('L');
        loadItem.addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        loadFile();//The function
                    }
                });
        fileMenu.add(loadItem);
        fileMenu.addSeparator();
        
        //ADMIN ONLY-END
        
        //Exit menu item as well
        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.setMnemonic('X');
        exitItem.addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        System.exit(0);//The function
                    }
                });
        fileMenu.add(exitItem);
        bar.add(fileMenu);
        
      //Color menu as well
        JMenu colorMenu = new JMenu("Color");
        colorMenu.setMnemonic('C');
        JMenuItem colorItem = new JMenuItem("Choose Color");
        colorItem.setMnemonic('O');
        colorItem.addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        chooseColor();//The function
                    }
                });
        colorMenu.add(colorItem);
        bar.add(colorMenu);
        
        //Stroke Size menu
        JMenu strokeMenu = new JMenu("Stroke");
        strokeMenu.setMnemonic('S');
        JMenuItem strokeItem = new JMenuItem("Set Stroke");
        strokeItem.setMnemonic('K');
        strokeItem.addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        setStroke();
                    }
                });
        strokeMenu.add(strokeItem);
        bar.add(strokeMenu);
        
        //Help menu
        JMenu helpMenu = new JMenu("Help");
        helpMenu.setMnemonic('H');
        JMenuItem aboutItem = new JMenuItem("About this Whiteboard!");
        aboutItem.setMnemonic('A');
        aboutItem.addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        JOptionPane.showMessageDialog(null,
                                "Distributed Whiteboard",
                                "Puffrora",
                                JOptionPane.INFORMATION_MESSAGE);
                    }
                });
        helpMenu.add(aboutItem);
        bar.add(helpMenu);
        
        //ICONS
        items = new ImageIcon[names.length];//names is New,Load,...
        drawingArea = new DrawPanel();//Shared Panel amoung clients
        choices = new JButton[names.length];
        buttonPanel = new JToolBar(JToolBar.HORIZONTAL);
        ButtonHandlery handlery = new ButtonHandlery();
        ButtonHandlerx handlerx = new ButtonHandlerx();
        for (int i = 0; i < choices.length; i++) {
            items[i] = new ImageIcon("pic/" + names[i] + ".png");
            choices[i] = new JButton("", items[i]);
            buttonPanel.add(choices[i]);
        }
        for (int i = 3; i < choices.length - 3; i++) {
            choices[i].addActionListener(handlery);
        }
        for (int i = 1; i < 4; i++) {
            choices[choices.length - i].addActionListener(handlerx);
        }
        choices[0].addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        newFile();
                    }
                });
        choices[1].addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        loadFile();
                    }
                });
        choices[2].addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        saveFile();
                    }
                });
        styles = new JComboBox<String>(styleNames);
        styles.setMaximumRowCount(8);
        styles.addItemListener(
                new ItemListener() {
                    public void itemStateChanged(ItemEvent e) {
                        styleCur = styleNames[styles.getSelectedIndex()];
                    }
                });
        bold = new JCheckBox("B");
        italic = new JCheckBox("I");
        checkBoxHandler cHandler = new checkBoxHandler();
        bold.addItemListener(cHandler);
        italic.addItemListener(cHandler);
        JPanel wordPanel = new JPanel();
        buttonPanel.add(bold);
        buttonPanel.add(italic);
        buttonPanel.add(styles);
        styles.setMinimumSize(new Dimension(80, 26));
        styles.setMaximumSize(new Dimension(120, 26));

        Container cont = getContentPane();
        super.setJMenuBar(bar);
        cont.add(buttonPanel, BorderLayout.NORTH);
        cont.add(drawingArea, BorderLayout.CENTER);
        statusBar = new JLabel();
        cont.add(statusBar, BorderLayout.SOUTH);

        createNewItem();
        setSize(width, height);
        setVisible(true);    
    }
        
    
    public class ButtonHandlery implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            for (int j = 3; j < choices.length - 3; j++) {
                if (e.getSource() == choices[j]) {
                    currentChoice = j;// for createNewItem
                    createNewItem();// Implements only line, rect, oval,circle
                    repaint();
                }
            }
        }
    }
    
    //Second half button handler.// Implements only line stroke, color, Text
    public class ButtonHandlerx implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == choices[choices.length - 3]) {
                chooseColor();//Color
            }
            if (e.getSource() == choices[choices.length - 2]) {
                setStroke();//Stroke
            }
            if (e.getSource() == choices[choices.length - 1]) { //Text
                JOptionPane.showMessageDialog(null,
                        "Please click the drawing pad to choose the word input position",
                        "Hint", JOptionPane.INFORMATION_MESSAGE);
                currentChoice = 14;
                createNewItem();
                repaint();
            }
        }
    }
    
  
    class mouseEvent1 extends MouseAdapter{
        public void mousePressed(MouseEvent e) {
            statusBar.setText("     Mouse Pressed @:[" + e.getX() +
                    ", " + e.getY() + "]");
            
            // Board event for mousePressed
            BoardEvent event= new BoardEvent("mousePressed");
            iArray[index].x1 = iArray[index].x2 = e.getX();
            iArray[index].y1 = iArray[index].y2 = e.getY();
            
            if (currentChoice == 3 || currentChoice == 8) {//Pencil and Eraser
                iArray[index].x1 = iArray[index].x2 = e.getX();
                iArray[index].y1 = iArray[index].y2 = e.getY();
                event.drawing= iArray[index];
                index++;
                createNewItem();
            }
            if (currentChoice == 9) {//Color
                iArray[index].x1 = e.getX();
                iArray[index].y1 = e.getY();
                String input;
                input = JOptionPane.showInputDialog(
                        "Please input the text you want!");
                iArray[index].s1 = input;
                iArray[index].x2 = style1;
                iArray[index].y2 = style2;
                iArray[index].s2 = styleCur;
                index++;
                createNewItem();
                drawingArea.repaint();
            }
            
            //send it to server
            try {
				bServer.addBoardEvent(event);
			} catch (RemoteException e1) {
				e1.printStackTrace();
			}  
        }
        
        public void mouseReleased(MouseEvent e) {
            statusBar.setText("     Mouse Released @:[" + e.getX() +
                    ", " + e.getY() + "]");
            if (currentChoice == 3 || currentChoice == 13) {
                iArray[index].x1 = e.getX();
                iArray[index].y1 = e.getY();
            }
            iArray[index].x2 = e.getX();
            iArray[index].y2 = e.getY();
            
            //Boardevent for mouseReleased
            BoardEvent event= new BoardEvent("mouseReleased");
            event.drawing = iArray[index];
            
            //send it to server
            try {
        		bServer.addBoardEvent(event);
        	} catch (RemoteException e1) {
        		e1.printStackTrace();
        	}
            
            //repaint();//Draws ontop of existing figures.Triggers paintComponnents
            index++;
            createNewItem();
        }
        public void mouseEntered(MouseEvent e) {
            statusBar.setText("     Mouse Entered @:[" + e.getX() +
                    ", " + e.getY() + "]");
        }
        public void mouseExited(MouseEvent e) {
            statusBar.setText("     Mouse Exited @:[" + e.getX() +
                    ", " + e.getY() + "]");
        }

    }
    
    
    class mouseEvent2 implements MouseMotionListener{
    	
        public void mouseDragged(MouseEvent e) {
            statusBar.setText("     Mouse Dragged @:[" + e.getX() +
                    ", " + e.getY() + "]");
            BoardEvent event= new BoardEvent("mouseDragged");
            
            
            if (currentChoice == 3 || currentChoice == 8) {
                
            	iArray[index - 1].x1 = iArray[index].x2 = iArray[index].x1 =e.getX();
                iArray[index - 1].y1 = iArray[index].y2 = iArray[index].y1 = e.getY();
                event.points= new drawings[]{iArray[index-1], iArray[index]};
                index++;
                createNewItem();
            } else {
                iArray[index].x2 = e.getX();
                iArray[index].y2 = e.getY();
                event.drawing = iArray[index];
                
            }
            
            //send to server
            
            try {
            	bServer.addBoardEvent(event);
            } catch (RemoteException e1) {
            	e1.printStackTrace();
            }
            //repaint();
            
           
            
        }
        public void mouseMoved(MouseEvent e) {
            statusBar.setText("     Mouse Moved @:[" + e.getX() +
                    ", " + e.getY() + "]");
        }

    }

    private class checkBoxHandler implements ItemListener {
        public void itemStateChanged(ItemEvent e) {
            if (e.getSource() == bold) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    style1 = Font.BOLD;
                } else {
                    style1 = Font.PLAIN;
                }
            }
            if (e.getSource() == italic) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    style2 = Font.ITALIC;
                } else {
                    style2 = Font.PLAIN;
                }
            }
        }
    }
    class DrawPanel extends JPanel {
        /**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public DrawPanel() {
            setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
            setBackground(Color.white);
            addMouseListener(new mouseEvent1());//Click,entered and exited
            addMouseMotionListener(new mouseEvent2());// Dragged,

        }

        @Override
        
        //Draws the drawing array 
        public synchronized void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            int j = 0;
            while (j <= index) {
                draw(g2d, iArray[j]);
                j++;
            }
        }

        void draw(Graphics2D g2d, drawings i) {
            i.draw(g2d);
        }
    }
    
    //Set drawing array with the selected tool
    void createNewItem() {
        if (currentChoice == 9)
        {
            drawingArea.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
        } else {
            drawingArea.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
        }
        switch (currentChoice) {
            case 3:
                iArray[index] = new Pencil();
                break;
            case 4:
                iArray[index] = new Line();
                break;
            case 5:
                iArray[index] = new Rect();
                break;
            case 6:
                iArray[index] = new Oval();
                break;
            case 7:
                iArray[index] = new Circle();
                break;
            case 8:
                iArray[index] = new Rubber();
                break;
            case 9:
                iArray[index] = new Word();
                break;
        }
        iArray[index].type = currentChoice;
        iArray[index].R = R;
        iArray[index].G = G;
        iArray[index].B = B;
        iArray[index].stroke = stroke;
    }



    public void chooseColor() {
        color = JColorChooser.showDialog(WhiteBoard.this,
                "Choose color", color);
        R = color.getRed();
        G = color.getGreen();
        B = color.getBlue();
        iArray[index].R = R;
        iArray[index].G = G;
        iArray[index].B = B;
    }
    public void setStroke() {
        String input;
        input = JOptionPane.showInputDialog(
                "Please input the size of stroke!");
        stroke = Float.parseFloat(input);
        iArray[index].stroke = stroke;
    }
    public void saveFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        int result = fileChooser.showSaveDialog(this);
        if (result == JFileChooser.CANCEL_OPTION) {
            return;
        }
        File fileName = fileChooser.getSelectedFile();
        fileName.canWrite();
        if (fileName == null || fileName.getName().equals("")) {
            JOptionPane.showMessageDialog(fileChooser, "Invalid File Name",
                    "Invalid File Name", JOptionPane.ERROR_MESSAGE);
        } else {
            try {
                fileName.delete();
                FileOutputStream fos = new FileOutputStream(fileName);
                output = new ObjectOutputStream(fos);
                drawings record;
                output.writeInt(index);
                for (int i = 0; i < index; i++) {
                    drawings p = iArray[i];
                    output.writeObject(p);
                    output.flush();
                }
                output.close();
                fos.close();
            } catch (IOException ee) {
                ee.printStackTrace();
            }
        }
    }
    
    public void loadFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.CANCEL_OPTION) {
            return;
        }
        File fileName = fileChooser.getSelectedFile();
        fileName.canRead();
        if (fileName == null || fileName.getName().equals("")) {
            JOptionPane.showMessageDialog(fileChooser, "Invalid File Name",
                    "Invalid File Name", JOptionPane.ERROR_MESSAGE);
        } else {
            try {
                FileInputStream fis = new FileInputStream(fileName);
                input = new ObjectInputStream(fis);
                drawings inputRecord;
                int countNumber = 0;
                countNumber = input.readInt();
                for (index = 0; index < countNumber; index++) {
                    inputRecord = (drawings) input.readObject();
                    iArray[index] = inputRecord;
                }
                createNewItem();
                input.close();
                repaint();
            } catch (EOFException endofFileException) {
                JOptionPane.showMessageDialog(this, "no more record in file",
                        "class not found", JOptionPane.ERROR_MESSAGE);
            } catch (ClassNotFoundException classNotFoundException) {
                JOptionPane.showMessageDialog(this, "Unable to Create Object",
                        "end of file", JOptionPane.ERROR_MESSAGE);
            } catch (IOException ioException) {
                JOptionPane.showMessageDialog(this, "error during read from file",
                        "read Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    public void newFile() {
        index = 0;
        currentChoice = 3;
        color = Color.black;
        stroke = 1.0f;
        createNewItem();
        repaint();
    }
    
    
    
}




