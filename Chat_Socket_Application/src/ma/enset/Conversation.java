package ma.enset;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

class Conversation extends Thread{
    private Socket socket;
    private int clientId;
    private ArrayList<Socket> clients_Sockets;
    public Conversation(Socket socket, int clientId, ArrayList<Socket> clients_Sockets) {
        this.socket = socket;
        this.clientId = clientId;
        this.clients_Sockets = clients_Sockets;
    }

    @Override
    public void run() {
        try {
            String name=null;
            InputStream is = socket.getInputStream();
            OutputStream os =socket.getOutputStream();
            //read String
            InputStreamReader isr= new InputStreamReader(is);
            BufferedReader br= new BufferedReader(isr);
            PrintWriter pw = new PrintWriter(os, true);
            // String ip= socket.getRemoteSocketAddress().toString();
            pw.println("Welcome , you're the user number=> "+ clientId);
            String ClientMsg; //request

            while ((ClientMsg= br.readLine())!= null){
                if(ClientMsg.contains("name:")){
                    name=(ClientMsg.split(":"))[1];
                    MTServer.Clients_Names.add(name);
                    pw.println(name+".. Welcome in this conversation !!!");

                }else if(ClientMsg.contains("=>")){
                    //for example :  1,2,3=>Hello dear
                    String receivers= (ClientMsg.split("=>"))[0];
                    ClientMsg= (ClientMsg.split("=>"))[1];

                    if(receivers.contains(",")){
                        String[] destination=receivers.split(",");
                        for(String a : destination){
                            int indexs=MTServer.Clients_Names.indexOf(a);
                            if(clients_Sockets.size()>=indexs && indexs!=(clientId-1)) {
                                pw = new PrintWriter(( clients_Sockets.get(indexs)).getOutputStream(),true);
                                pw.println(MTServer.Clients_Names.get(clientId-1) + " : " +ClientMsg);
                            }
                        }
                    }else {
                        int index=MTServer.Clients_Names.indexOf(receivers);
                        if(clients_Sockets.size()>=index && index!=(clientId-1)) {
                            pw = new PrintWriter(( clients_Sockets.get(index)).getOutputStream(), true);
                            pw.println(MTServer.Clients_Names.get(clientId-1) + " : " +ClientMsg);
                        }
                    }

                }else {
                    for (Socket s : clients_Sockets) {
                        if (s != socket) {
                            pw = new PrintWriter(s.getOutputStream(), true);
                            pw.println(name + " : " + ClientMsg);
                        }
                    }
                }

            }
        } catch (IOException e) {
            System.out.println(clientId+ " is deconnected");
        }
    }
}
