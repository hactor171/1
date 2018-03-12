/*
 * Program Klient-Serwer
 * Autor: Roman Kovalchuk
 * Data: 28 stycznia 2017 r.
 */
import javax.swing.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;


 class Klient extends JFrame implements ActionListener, Runnable {
	private static final long serialVersionUID = 1L;
	private static final int SERVER_PORT = 5000;
	 String nick;
     String host;
     Socket socket;
    ObjectOutputStream output;
	ObjectInputStream input;
     boolean zabij = false; 
     boolean zacznij;
    JPanel panel = new JPanel();
    JTextField m = new JTextField(10);
	JLabel labelm = new JLabel();
    JTextField m1 = new JTextField(10);
    JLabel labelm1 = new JLabel();
    JTextArea textarea = new JTextArea(12, 35);
    JComboBox<String> wybierz = new JComboBox<>();
    JMenuBar menubar = new JMenuBar();
    JMenu menu = new JMenu("Pomoc");
    JMenuItem autor = new JMenuItem("Autor");
    
    public Klient(String nick1) {
        super(nick1);
        nick = nick1;
        setSize(550, 350);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        wybierz.addActionListener(this);
        wybierz.addItem("Co chcesz zrobic?");
        wybierz.addItem("Load");
        wybierz.addItem("Save");
        wybierz.addItem("Get");
        wybierz.addItem("Put");
        wybierz.addItem("Replace");
        wybierz.addItem("Delete");
        wybierz.addItem("List");
        wybierz.addItem("Close");
        wybierz.addItem("Buy");
        
        panel.add( wybierz);
        panel.add(labelm);
        panel.add(m);
        panel.add(labelm1);
        panel.add(m1);
        menubar.add(menu);
        menu.add(autor);
        autor.addActionListener(this);
        labelm1.setVisible(false);
        labelm.setVisible(false);
        m.setVisible(false);
        m1.setVisible(false);
        
        textarea.setLineWrap(true);
        textarea.setWrapStyleWord(true);
        textarea.setEditable(false);
        JScrollPane scroll = new JScrollPane(textarea, 
        		ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, 
        		ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        panel.add(scroll);
        
        Thread t = new Thread(this);
        t.start();
        
        addKeyListener(m);
        addKeyListener(m1);
        addKeyListener(wybierz);
      
        setJMenuBar(menubar);
        setContentPane(panel);
        setVisible(true);

    }
    @Override
	public void actionPerformed(ActionEvent e) {
    	Object zrodlo = e.getSource();
    	if(zrodlo == autor){
    		JOptionPane.showMessageDialog(null, "Roman Kovalchuk");
    	}
    	if(!zacznij){
         switch ( wybierz.getSelectedIndex()) {
         case 0:
             setMessages(false, false, "", "");
             break;
         case 1:
             setMessages(true, false, "Nazwa pliku:", "");
             break;
         case 2:
             setMessages(true, false, "Nazwa pliku:", "");
             break;
         case 3:
             setMessages(true, false, "Imię:", "");
             break;
         case 4:
             setMessages(true, true, "Imię:", "Numer:");
             break;
         case 5:
             setMessages(true, true, "Imię:", "Numer:");
             break;
         case 6:
             setMessages(true, false, "Imię:", "");
             break;
         case 7:
             setMessages(false, false, "", "");
             break;
         case 8:
             setMessages(false, false, "", "");
             break;
         case 9:
             setMessages(false, false, "", "");
             break;
           }	
    	}
         
		
	}
    private void addKeyListener(JComponent f) {
    	f.addKeyListener(new KeyAdapter() {
        	@Override
        	public void keyTyped(KeyEvent e) {
        		if (e.getKeyChar() == KeyEvent.VK_ENTER) {
        			sendKeyPressed();
        		}
        	}
        });
    }
    
    private void sendKeyPressed() {
    	try{
			textarea.append("<<" +  wybierz.getSelectedItem() + ">>\n");
            switch ( wybierz.getSelectedIndex()) {
                case 0:
                    return;
                case 1: 
                    output.writeObject("Load*" + m.getText());
                    break;
                case 2: 
                    output.writeObject("Save*" + m.getText());
                    break;
                case 3: 
                    output.writeObject("Get*" + m.getText());
                    break;
                case 4: 
                    output.writeObject("Put*" + m.getText() + "&" + Integer.valueOf(m1.getText()));
                    
                    break;
                case 5: 
                    output.writeObject("Replace*" + m.getText() + "&" + Integer.valueOf(m1.getText()));
                    break;
                case 6: 
                    output.writeObject("Delete*" + m.getText());
                    break;
                case 7: 
                    output.writeObject("List");
                    break;
                case 8: 
                    output.writeObject("Close");
                    input.close();
                    output.close();
                    socket.close();
                    setVisible(false);
                    dispose();
                    break;
                case 9: 
                    output.writeObject("Buy");
                    zabij = true;
                    input.close();
                    output.close();
                    socket.close();
                    setVisible(false);
                    dispose();
                    break;
            }
        } catch (IOException ea) {
            System.out.println("Wyjątek klienta: " + ea.getMessage());
        }
        repaint();
    }

   private void setMessages(boolean boolm, boolean boolm1, String strm, String strm1) {
        labelm.setVisible(boolm);
        labelm1.setVisible(boolm1);
        labelm.setText(strm);
        labelm1.setText(strm1);
        m.setVisible(boolm);
        m1.setVisible(boolm1);
    }

    public void run() {
        try {
            host = InetAddress.getLocalHost().getHostName();
            socket = new Socket(host, SERVER_PORT);
            input = new ObjectInputStream(socket.getInputStream());
            output = new ObjectOutputStream(socket.getOutputStream());
            output.writeObject(nick);
        } catch (IOException e) {
            System.out.println(e.getMessage());
            JOptionPane.showMessageDialog(null, "Połączenie nie może być utworzone");
            setVisible(false);
            dispose();
            return;
        }
        try {
            while (!zabij) {
                textarea.append("<<Serwer>>\n" +"<"+ input.readObject()+">"+ "\n" );
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            JOptionPane.showMessageDialog(null, "Polaczenie sieciowe przerwane.");
            setVisible(false);
            dispose();
        }
    }

    public static void main(String[] args) {
        String nick;

        nick = JOptionPane.showInputDialog("Podaj nazw klienta:");
        if (nick != null ) {
            new Klient(nick);
        }
    }

	

	

}

