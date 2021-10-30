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
public class Talker
{

private BufferedReader      br;
private DataOutputStream    dos;
private int                 ipAddress;
private int                 portNum;
private String              id;
private ServerSocket        serverSocket;
private Socket              normalSocket;
String                      message;


Talker()
{
}

public Talker(String ip, int port, String identification)
{

try
{
	normalSocket = new Socket(ip, port);

	br = new BufferedReader(new InputStreamReader(normalSocket.getInputStream()));
	dos = new DataOutputStream(normalSocket.getOutputStream());
	//msg = br.readLine();
	//System.out.println("I just constructed a socket with: " + ip + " " + port + " " + " " + identification);
	//dos.writeBytes("Nice to meet you! \n");
}
catch(Exception e)
{
    System.out.println("Talker constructor1 exception");
}

}// End of constructor1

public Talker(Socket socket) throws IOException
{
    br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    dos = new DataOutputStream(socket.getOutputStream());
}// End of constructor2

public void send(String msg) throws IOException
{
	//msg = "Welcome to my service!";
	msg += "\n";
	dos.writeBytes(msg);
	System.out.println("Message being sent: " + msg);
}// End of send method

public String recieve() throws IOException
{
	message = br.readLine();
	System.out.println("Message Recieved: " + message);
	return message;
}// End of recieve method

}// End of Talker class