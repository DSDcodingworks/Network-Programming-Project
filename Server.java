import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import javax.swing.event.*;
import java.io.*;
import java.text.*;
import java.net.*;
import javax.swing.text.*;
import java.lang.*;
import javax.swing.event.*;

public class Server
{

public static void main(String[] args)
{
new Server();
}


ServerSocket        serverSocket;
Socket              normalSocket;
BufferedReader      br;
DataOutputStream    dos;
String              msg;
int                 port;
UserHashTable       activeClients;
CTC                 ctc;

Server()
{

port = 6789;
activeClients = new <String, User> UserHashTable();

try
{
serverSocket = new ServerSocket(port);
}
catch(Exception exe)
{
    System.out.println("Exception caught in Server Constructor, tried to construct a ServerSocket");
}

activeClients.loadHash();

System.out.println(activeClients.size());

while(true)
{
try
{
normalSocket = serverSocket.accept();
//talker = new Talker(normalSocket);
//talker.recieve();
ctc = new CTC(normalSocket, this);
//activeClients.addElement(ctc);
}
catch(IOException e)
{
    System.out.println("problems man");
    e.printStackTrace();
}

}// End of while

}// End of Server constructor

/*
void broadcast(String msg, CTC passedCTC)
{

    String tempmsg = passedCTC.id + ": ";
    tempmsg += msg;

    for(CTC forCTC: activeClients)
    if(forCTC != passedCTC)
        forCTC.ctcSend(tempmsg);
    //System.out.println("This is being broadcast: " + msg);
//  forCTC.send(msg);

}// End of broadcast method
*/


public void addUser(User passedUser)
{
    activeClients.put(passedUser.username, passedUser);
    activeClients.storeHash();
    System.out.println("User added to the hashtable in server method - addUser!");
}

}// End of Server class