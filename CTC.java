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

public class CTC // Connection To Client
             implements Runnable
{

String      msg;
Talker      talker;
String      id;
Thread      thread;
Socket      normalSocket;
Server      server;
User        ctcUser;

CTC(Socket socket, Server server)
{

normalSocket = socket;
this.server = server;

ctcUser = new User();
//ctcUser.connectedUser = this;


try
{
talker = new Talker(normalSocket);
}
catch(Exception exe)
{
    System.out.println("Caught Exception in CTC constructor, attempted to construct a talker");
}
/*
try
{
id = talker.recieve();
}
catch(Exception exe)
{
    System.out.println("CTC - Exception caught when trying to read user id!");
}
*/

//server.setID(id);

thread = new Thread(this);
thread.start();

}



public void run()
{

//msg = "This message came from the CTC!";
try
{
    msg = talker.recieve();
}
catch(Exception e)
{
    System.out.println("Exception trying to recieve a login/register request in CTC run!");
}
if(ctcHandleConnectionMessage(msg))
{
System.out.println("Recieved connection request from client in CTC: " + msg);
//else
//{
try
{
while(true)
    {
    System.out.println("CTC - waiting to recieve a message!");
    msg = talker.recieve();
    processClientRequests(msg);

    //server.broadcast(msg, this);

    }// End of while loop
}// End of try
catch(Exception exe)
{
    System.out.println("Exception caught in CTC run method!");
    exe.printStackTrace();
    handleClientDisconnect();
}

}
//}// End of else // ctcHandleConnectionMessage reurned false i.e. register or login failed




}// End of run method

void ctcSend(String message)
{

    try
    {
    talker.send(message);
    }
    catch(Exception exe)
    {
    System.out.println("Exception caught in CTS method, ctsSend!");
    }


}// End of ctcSend method

public boolean ctcHandleConnectionMessage(String request)
{
    String [] tempmsg = request.split("\0");
    User buddyUser;

    if(tempmsg[0].equals("LOGIN"))
    {
		if(handleLogin(tempmsg[1], tempmsg[2]))
			return true;
		else
			return false;

    }
    else if(tempmsg[0].equals("REGISTER"))
    {

        if(checkRegisterCredentials(tempmsg[1]))
        {
            ctcSend("REGISTERDENIED");
            //JOptionPane.showMessageDialog(null, tempmsg[1] + " has already been taken.");
        }
        else
        {
            ctcSend("REGISTERACCEPTED");
            ctcUser.username = tempmsg[1];
            ctcUser.password = tempmsg[2];
            ctcUser.connectedUser = this;
            server.addUser(ctcUser);
            return true;
        }

        return false;

    }// End of if(REGISTER)

    return false;

}// End of ctcProcessMessage method

public boolean checkLoginCredentials(String passedusername, String passedpassword)
{

    if(server.activeClients.containsKey(passedusername))
    {
        User tempuser = server.activeClients.get(passedusername);

        if(tempuser.password.equals(passedpassword))
        return true;

        else
        return false;
    }

    return false;

}// End of checkLoginCredentials method

public boolean checkRegisterCredentials(String passedusername)
{

    if(server.activeClients.containsKey(passedusername))
    return true;

    else
    return false;


}// End of checkRegisterCredentials method

public void processClientRequests(String request)
{
    System.out.println("This is a request being processed in CTC - processClientRequests: " + request);
    String [] tempmsg = request.split("\0");
    User tempuser;
    Buddy buddy;

    if(tempmsg[0].equals("ADDBUDDY"))
    {
        if(server.activeClients.containsKey(tempmsg[1]))
        {
            tempuser = server.activeClients.get(tempmsg[1]);
            if(tempuser.connectedUser == null)
            {
                msg = "BUDDYOFFLINE" + '\0' + tempmsg[1];
                ctcSend(msg);
            }// if recipient of message is in the hashtable but is offline
            else // Recipient is in the hashtable and is online
            {
                msg = "BUDDYREQUEST" + '\0' + ctcUser.username;
                System.out.println("This is the message being sent after receiving addbuddy: " + msg + "xxxxx");
                tempuser.connectedUser.ctcSend(msg);
            }

        }
        else // Recipient is not in the hashtable
        {
            msg = "BUDDYINVALID" + '\0' + tempmsg[1];
            ctcSend(msg);
        }

    }// End of if(ADDBUDDY)

    else if(tempmsg[0].equals("BUDDYACCEPTED"))
    {
        tempuser = server.activeClients.get(tempmsg[2]);
        tempuser.buddyList.addElement(buddy = new Buddy(tempmsg[1], "(Online)"));

        tempuser = server.activeClients.get(tempmsg[1]);
        tempuser.buddyList.addElement(buddy = new Buddy(tempmsg[2], "(Online)"));
        msg = tempmsg[0] + '\0' + tempmsg[2];
        tempuser.connectedUser.ctcSend(msg);
    }// End of else if(BUDDYACCEPTED)

    else if(tempmsg[0].equals("UPDATEHASH"))
    {
        server.activeClients.storeHash();
    }// End of else if(UPDATEHASH)

    else if(tempmsg[0].equals("DISCONNECT"))
    {
		handleClientDisconnect();
	}// End of else if(DISCONNECT)

	else if(tempmsg[0].equals("FORWARD"))
	{
		tempuser = server.activeClients.get(tempmsg[2]);
		tempmsg[3] = tempmsg[3].replace("blue", "red");
		tempmsg[3] = tempmsg[3].replace("left", "right");
		msg = tempmsg[0] + '\0' + tempmsg[1] + '\0' + tempmsg[3];
		tempuser.connectedUser.ctcSend(msg);
	}


}// End of processClientRequests method

public void handleClientDisconnect()
{
	User buddyUser;
	User tempuser = server.activeClients.get(ctcUser.username);
    //tempuser.connectedUser = this;
    ctcUser.username = tempuser.username;
    ctcUser.password = tempuser.password;
    ctcUser.buddyList = tempuser.buddyList;
    int j = tempuser.buddyList.size();

	for(int i = 0; i < j; i++)
	{
		buddyUser = server.activeClients.get(tempuser.buddyList.elementAt(i).userID);
		msg = "UPDATEONLINESTATUS" + '\0' + ctcUser.username + '\0' + "(Offline)";
		//System.out.println("the message in the ctc: " + msg);
		//System.out.println("the buddyUser's ctc in the CTC run handleConnection method: " + buddyUser.connectedUser);
		if(buddyUser.connectedUser == null)
		{
			System.out.println(buddyUser.username + " is offline. (from login inside ctc)");
		}
		else
			buddyUser.connectedUser.ctcSend(msg);
	}// End of for loop, updates the logged off client's buddies that the client logged off

	tempuser.connectedUser = null;

}// End of handleClientDisconnect method

public boolean handleLogin(String passedUsername, String passedPassword)
{
	User buddyUser;

	        if(checkLoginCredentials(passedUsername, passedPassword))
	        {
	            //User buddyUser;
	            User tempuser = server.activeClients.get(passedUsername);

	            if(tempuser.connectedUser != this && tempuser.connectedUser != null)
	            {

					msg = "CONFIRMLOGIN";
					ctcSend(msg);

					try
					{
						msg = talker.recieve();
					}
					catch(Exception exe)
					{
						System.out.println("Exception caught in CTC - if(LOGIN) - when trying to get confirmation from the second instance of a user.");
					}

					if(msg.equals("LOGOFFOTHER"))
					{
						msg = "LOGOFF";
						tempuser.connectedUser.ctcSend(msg);

					}
					else
					return false;
				}// The username typed in is currently logged on
			// Otherwise the username typed in is currently offline

					tempuser.connectedUser = this;
					ctcUser.username = tempuser.username;
					ctcUser.password = tempuser.password;
					ctcUser.buddyList = tempuser.buddyList;
					int j = tempuser.buddyList.size();

					System.out.println("buddyList size: " + Integer.toString(j));

					msg = "LOGINACCEPTED" + "\0" + Integer.toString(j);

					for(int i = 0; i < j; i++)
					{
						System.out.println("Getting the buddy names from ctc: " + tempuser.buddyList.elementAt(i).userID);
						msg += '\0' + tempuser.buddyList.elementAt(i).userID;
					}// Sends the username's of each buddy to the newly logged in Client
					ctcSend(msg);

					for(int i = 0; i < j; i++)
					{
						buddyUser = server.activeClients.get(tempuser.buddyList.elementAt(i).userID);
						msg = "UPDATEONLINESTATUS" + '\0' + ctcUser.username + '\0' + "(Online)";
						//System.out.println("the message in the ctc: " + msg);
						//System.out.println("the buddyUser's ctc in the CTC run handleConnection method: " + buddyUser.connectedUser);
						if(buddyUser.connectedUser == null)
						{
							System.out.println(buddyUser.username + " is offline. (from login inside ctc)");
						}
						else
						buddyUser.connectedUser.ctcSend(msg);

					}// Update the newly logged on client's buddies that the client is now online

					for(int i = 0; i < j; i++)
					{
						msg = "UPDATEONLINESTATUS" + '\0' + tempuser.buddyList.elementAt(i).userID + '\0' /*+ tempuser.buddyList.elementAt(i).onlineStatus*/;
						buddyUser = server.activeClients.get(tempuser.buddyList.elementAt(i).userID);
						//System.out.println("buddyUser: " + tempuser.buddyList.elementAt(i).userID);
						if(buddyUser.connectedUser == null)
							msg += "(Offline)";
						else
							msg += "(Online)";
						System.out.println("This is the message being sent to update the buddylist: " + msg);

						ctcSend(msg);
					}// Update the newly logged in client buddylist with the online status of their buddies

	        return true;
	        }
	        else
	            ctcSend("LOGINDENIED");
	            //JOptionPane.showMessageDialog(null, "Username or Password is incorrect.");

	        return false;

}// End of handleLogin method

}// End of CTC class