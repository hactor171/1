/*
 * Program Klient-Serwer
 * Autor: Roman Kovalchuk
 * Data: 28 stycznia 2017 r.
 */
 
import javax.swing.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;


class Serwer extends JFrame implements Runnable {

	private static final long serialVersionUID = 1L;
	private static final int SERVER_PORT = 5000;
	 String host;
	 ServerSocket server;
	
     Vector<Clients> clients = new Vector<>();
     JTextArea textarea = new JTextArea(16, 20);
     JScrollPane scroll = new JScrollPane(textarea, 
    		ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, 
    		ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
     PhoneBook phoneBook = new PhoneBook();
     volatile boolean zabij = false;
  


    public Serwer() {
        super("Serwer");
        setSize(300, 340);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        JPanel panel = new JPanel();
        
        textarea.setLineWrap(true);
        textarea.setWrapStyleWord(true);
        textarea.setEditable(false);
        
        panel.add(scroll);
        
        Thread t = new Thread(this);
        t.start();
        
        setContentPane(panel);
        setVisible(true);
    }

    private synchronized void showMessage(Clients c, String s) {
        textarea.append(c.getNick() + " ==> " + s + "\n");
    }

    synchronized void addClient(Clients clients_all) {
        clients.add(clients_all);
    }

    synchronized void deleteClient(Clients clients_all) {
        clients.remove(clients_all);
    }


    public void run() {
        Socket s;

        try {
            host = InetAddress.getLocalHost().getHostName();
            server = new ServerSocket(SERVER_PORT);
        } catch (IOException e) {
            System.out.println(e.getMessage());
            JOptionPane.showMessageDialog(null, "Gniazdo dla Servera nie może być utworzone");
            System.exit(0);
        }
        System.out.println("Server zostal uruchomiony na danym hoscie " + host);

        while (!zabij) {
            try {
                s = server.accept();
                if (s != null) new Clients(this, s);
            } catch (IOException e) {
                System.out.println(" Nie mozna połączyć się z klientem ");
            }
        }
        try {
            server.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new Serwer();
    }


    void Mycom(Clients clients_all) {
        String s = "błąd"; 
        String c = "błąd"; 
        String m = "błąd"; 
        String m1 ="błąd";
        try {
            s = (String) clients_all.getInput().readObject();
            if (s.contains("&")) {
            	m1 = s.substring(s.indexOf("&") + 1, s.length());
                m = s.substring(s.indexOf("*") + 1, s.indexOf("&"));
                c = s.substring(0, s.indexOf("*"));
            } else if (s.contains("*")) {
                m = s.substring(s.indexOf("*") + 1, s.length());
                c = s.substring(0, s.indexOf("*"));
            } else {
                c = s;
            }

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        
        try {
            switch (c) {
                case "Load":
                    showMessage(clients_all, "<Load>\n" + ">>" + m +"<<" + "\n");
                    clients_all.getOutput().writeObject(phoneBook.load(m));
                    break;
                case "Save":
                    showMessage(clients_all, "<Save>\n" + ">>" + m +"<<" + "\n");
                    clients_all.getOutput().writeObject(phoneBook.save(m));
                    break;
                case "Get":
                    showMessage(clients_all, "<Get>\n" + ">>" + m +"<<" + "\n");
                    clients_all.getOutput().writeObject(phoneBook.get(m));
                    break;
                case "Put":
                    showMessage(clients_all, "<Put>\n" + ">>" + m +"<<" +"==>>" +"["+ m1 +"]" + "\n");
                    clients_all.getOutput().writeObject(phoneBook.put(m, m1));
                    break;
                case "Replace":
                    showMessage(clients_all, "<Replace>\n" + ">>" + m +"<<" +"==>>" +"["+ m1 +"]" + "\n");
                    clients_all.getOutput().writeObject(phoneBook.replace(m, m1));
                    break;
                case "Delete":
                    showMessage(clients_all, "<Delete>\n" + ">>" + m +"<<" + "\n");
                    clients_all.getOutput().writeObject(phoneBook.delete(m));
                    break;
                case "List":
                    showMessage(clients_all, "<List>\n" + ">>" + m +"<<" + "\n");
                    clients_all.getOutput().writeObject(phoneBook.list());
                    break;
                case "Close":
                    zabij = true;
                    server.close();
                    showMessage(clients_all, "<Close>\n" + ">>" + m +"<<" + "\n");
                    deleteClient(clients_all);
                    clients_all.zabij();
                    break;
                case "Buy":
                    deleteClient(clients_all);
                    clients_all.zabij();
                    break;
            }
        } catch (Exception e) {
            e.getMessage();
        }
    }
}

class Clients implements Runnable {
    private Socket socket;
    private ObjectOutputStream output;
    private ObjectInputStream input;

    private String nick;
    private Serwer okno;
    private volatile boolean zabij = false;

    Clients(Serwer ok, Socket s) throws IOException {
        okno = ok;
        socket = s;
        Thread t = new Thread(this);
        t.start();
    }

    String getNick() {
        return nick;
    }

    ObjectOutputStream getOutput() {
        return output;
    }

    ObjectInputStream getInput() {
        return input;
    }

    void zabij() {
        zabij = true;
    }

    public String toString() {
        return nick;
    }

    public void run() {
        try {
            output = new ObjectOutputStream(socket.getOutputStream());
            input = new ObjectInputStream(socket.getInputStream());
            nick = (String) input.readObject();
            okno.addClient(this);
            while (!zabij) okno.Mycom(this);
            okno.deleteClient(this);
            input.close();
            output.close();
            socket.close();
            socket = null;
        } catch (Exception i) {}
    }
}
