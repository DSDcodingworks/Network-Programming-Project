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

public class CTS // Connection To Server
             implements Runnable
{

String              msg;
Talker              talker;
String              ip;
int                 port;
String              id;
Thread              thread;
DefaultListModel    dlm;
Client              ctsClient;

CTS(DefaultListModel defaultListModel, String ip, int port, String id, Client c)
{

	ctsClient = c;
	this.dlm = defaultListModel;
	this.ip = ip;
	this.port = port;
	this.id = id;

	talker = new Talker(ip, port, id);

	/*
	try
	{
	talker.send(id);
	}
	catch(Exception exe)
	{
		System.out.println("CTS - Exception caught when trying to send user id!");
	}
	*/

	thread = new Thread(this);
	thread.start();

}// End of CTS constructor



public void run()
{
	try
	{
		msg = talker.recieve();
		//System.out.println("First message recieved in talker run: " + msg);
	}
	catch(Exception e)
	{
		System.out.println("Exception caught when trying to recieve a reply for login/register in CTS!");
	}
	if(ctsConnectionWasSuccessful(msg))
	{
		System.out.println("CTS recieved a reply for trying to login or register" + msg);
	//else
	//{
	try
	{
	while(true)
	{
	//  msg = "CTS worked!";
		System.out.println("CTS - waiting to recieve a message!");
		msg = talker.recieve();
		processServerReplies(msg);

		/*
		SwingUtilities.invokeLater(
			new Runnable()
			{
				public void run()
				{
					dlm.addElement(msg);
				}// End run
			} End Runnable );// End invokeLater
		*/
    }// End while loop

}// End of try
catch(Exception exe)
{
    System.out.println("Caught an exception in CTS run method!");
    exe.printStackTrace();
    JOptionPane.showMessageDialog(null, "The server has been shutdown. Sorry for the inconveinence.");
    ctsClient.clientShutDown();
}

}
//}// End of else// ctsConnectionWasSuccessful recieved a denied message

}// End of run method

void ctsSend(String message)
{

    //String [] tempmsg = message.split("\0");

    //if(tempmsg[0].equals("LOGIN"))
    //{

    //}

    try
    {
	    talker.send(message);
    }
    catch(Exception exe)
    {
	    System.out.println("Exception caught in CTS method, ctsSend!");
    }

}// End of ctsSend method

public boolean ctsConnectionWasSuccessful(String reply)
{
    String [] tempmsg = reply.split("\0");
    //String [] buddyComponents;
    int j;
    Buddy buddy;

    if(reply.equals("REGISTERDENIED"))
    {
        System.out.println("CTS - " + reply);
        JOptionPane.showMessageDialog(null, /*tempmsg[1]*/ ctsClient.user.username + " has already been taken.");
        ctsClient.user.username = null;
        ctsClient.user.password = null;
        return false;
    }
    else if(reply.equals("REGISTERACCEPTED"))
    {
        System.out.println("CTS - " + reply);
        ctsClient.myInputTF.setEnabled(true);
        return true;
    }
    else if(reply.equals("LOGINDENIED"))
    {
        System.out.println("CTS - " + reply);
        JOptionPane.showMessageDialog(null, "Username or Password is incorrect.");
        ctsClient.user.username = null;
        ctsClient.user.password = null;
        return false;
    }
    else if(tempmsg[0].equals("LOGINACCEPTED"))
    {
        System.out.println("CTS - " + reply);
        ctsClient.myInputTF.setEnabled(true);

        j = Integer.parseInt(tempmsg[1]);

        for(int i = 0; i < j; i++)
        {
            //System.out.println("This: " + tempmsg[2].replace("\0", "#") + "<<<");
            //buddyComponents = tempmsg[i + 2].split(" ");
            buddy = new Buddy(tempmsg[i + 2], "(Offline)");
            ctsClient.user.buddyList.addElement(buddy);
            ctsClient.dlm.addElement(buddy);
        }// End of for(), This will load the buddies into the buddyList

        return true;
    }
    else if(tempmsg[0].equals("CONFIRMLOGIN"))
    {
        System.out.println("CTS - " + reply);
        ctsClient.myInputTF.setEnabled(true);

		int response = JOptionPane.showConfirmDialog(null, "You are currently logged in on another computer, trying to login again will log you off of the other computer, do you wish to proceed?", "Log Off Confirmation", JOptionPane.YES_NO_OPTION);
        if (response == JOptionPane.YES_OPTION)
        {
			msg = "LOGOFFOTHER";
			ctsSend(msg);

			try
			{
				msg = talker.recieve();
			}
			catch(Exception exe)
			{
				System.out.println("Problem in CTS - confirmlogin");
				exe.printStackTrace();
			}

			tempmsg = reply.split("\0");

			System.out.println("msg: " + tempmsg);

	        j = Integer.parseInt(tempmsg[1]);

	        for(int i = 0; i < j; i++)
	        {
	            //System.out.println("This: " + tempmsg[2].replace("\0", "#") + "<<<");
	            //buddyComponents = tempmsg[i + 2].split(" ");
	            buddy = new Buddy(tempmsg[i + 2], "(Offline)");
	            ctsClient.user.buddyList.addElement(buddy);
	            ctsClient.dlm.addElement(buddy);
        	}// End of for(), This will load the buddies into the buddyList

			return true;
        }
        else
        {
			msg = "DONTLOGOFFOTHER";
			ctsSend(msg);
            //JOptionPane.showMessageDialog(null, "GOODBYE");
            //System.exit(0);
   		}


	}
    else
        System.out.println("Not a login/register request: " + reply);

    return false;

}// End of ctsProcessMessage method

public void processServerReplies(String reply)
{
    System.out.println("This is a request being processed in CTS - processServerReplies: " + reply);
    String [] tempmsg = reply.split("\0");
    Buddy buddy;
    int buddyPos;

    if(tempmsg[0].equals("BUDDYINVALID"))
    {
        JOptionPane.showMessageDialog(null, tempmsg[1] + " not found. Please check spelling and try again.");
    }// End of if(BUDDYINVALID)
    else if(tempmsg[0].equals("BUDDYOFFLINE"))
    {
        JOptionPane.showMessageDialog(null, tempmsg[1] + " is currently offline. Please try again later.");
    }// End of else if(BUDDYOFFLINE)
    else if(tempmsg[0].equals("BUDDYREQUEST"))
    {
        int response = JOptionPane.showConfirmDialog(null, tempmsg[1] + " wants to be your friend.", "Friend Request", JOptionPane.YES_NO_OPTION);
        if (response == JOptionPane.YES_OPTION)
        {
            //JOptionPane.showMessageDialog(null, "HELLO");
            buddy = new Buddy(tempmsg[1], "(Online)");
            ctsClient.dlm.addElement(buddy);
            ctsClient.user.buddyList.addElement(buddy);
            msg = "BUDDYACCEPTED" + '\0' + tempmsg[1] + '\0' + ctsClient.user.username;
            ctsSend(msg);
        }
        else
        {
            //JOptionPane.showMessageDialog(null, "GOODBYE");
            //System.exit(0);
        }
    }// End of else if(BUDDYREQUEST)
    else if(tempmsg[0].equals("BUDDYACCEPTED"))
    {
        buddy = new Buddy(tempmsg[1], "(Online)");
        ctsClient.dlm.addElement(buddy);
        ctsClient.user.buddyList.addElement(buddy);
        msg = "UPDATEHASH";
        ctsSend(msg);
    }// End of else if(BUDDYACCEPTED)
    else if(tempmsg[0].equals("UPDATEONLINESTATUS"))
    {
        //System.out.println("CTS - " + reply);
        buddy = new Buddy(tempmsg[1], tempmsg[2]);
        buddyPos = ctsClient.user.findBuddy(tempmsg[1]);
        System.out.println("update online status of buddy in cts: <" + tempmsg[1] + ">");
        //buddy.chatBox = ctsClient.user.buddyList.elementAt(buddyPos).chatBox;
        buddy = ctsClient.user.buddyList.elementAt(buddyPos);
        buddy.onlineStatus = tempmsg[2];
        ctsClient.dlm.set(buddyPos, buddy);
        //ctsClient.user.buddyList.setElementAt(buddy, buddyPos);

    }// End of else if(UPDATONLINESTATUS)
    else if(tempmsg[0].equals("FORWARD"))
    {
		buddyPos = ctsClient.user.findBuddy(tempmsg[1]);
		//buddy = ctsClient.user.buddyList.elementAt(buddyPos);
		buddy = ctsClient.dlm.get(buddyPos);
		if(buddy.chatBox == null)
		{
			buddy.chatBox = new ChatBox(buddy.userID, ctsClient);
			buddy.chatBox.requestFocus();
			tempmsg[2] = tempmsg[2].replace("\1", "\n");
			buddy.chatBox.addText(tempmsg[2]);
		}
		else
		{
			buddy.chatBox.setVisible(true);
			buddy.chatBox.requestFocus();
			tempmsg[2] = tempmsg[2].replace("\1", "\n");
			buddy.chatBox.addText(tempmsg[2]);
		}

	}
    else if(tempmsg[0].equals("LOGOFF"))
    {
		ctsClient.clientShutDown();
	}


}// End of processServerReplies method

}// End of CTS class