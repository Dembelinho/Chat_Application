package ma.enset;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;

public class MTServer extends Thread{
    int ClientsCount;
    static ArrayList<String> Clients_Names=new ArrayList<>();
    public static void main(String[] args) {
        new MTServer().start();
    }

    @Override
    public void run() {
        try {
            ServerSocket ss = new ServerSocket(1234);
            System.out.println("the server is started");
            ArrayList<Socket> clients_Sockets=new ArrayList<>();
            while (true){
                Socket s=ss.accept();
                clients_Sockets.add(s);
                ++ClientsCount;
                new Conversation(s,ClientsCount,clients_Sockets).start();
                String ip = s.getRemoteSocketAddress().toString();
                System.out.println("New client Connection => "+ClientsCount+" IP= "+ip);
            }

        } catch (IOException e) {
            System.out.println(" shut down the server ");
        }
    }

}
