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

public class User
{
String              username;
String              password;
Vector <Buddy>      buddyList;
CTC                 connectedUser;

User()
{
	username = new String();
	password = new String();
	buddyList = new <Buddy> Vector();
	connectedUser = null;
}// End of User Constructor

public void userStore(DataOutputStream dos)
{

    try
    {
        dos.writeUTF(username);
        dos.writeUTF(password);
        int j = buddyList.size();
        dos.writeInt(j);
        for(int i = 0; i < j; i++)
        dos.writeUTF(buddyList.elementAt(i).userID);
    }
    catch(Exception e)
    {
        System.out.println("Exception caught in userStore method!");
    }

}// End of userStore method

public User userLoad(DataInputStream dis)
{
    Buddy buddy;
    try
    {
        username = dis.readUTF();
        password = dis.readUTF();
        int j = dis.readInt();
        for(int i = 0; i < j; i++)
        {
            buddyList.addElement(buddy = new Buddy(dis.readUTF(), "(Offline)"));
        }

    }
    catch(Exception e)
    {
        System.out.println("Exception caught in userLoad method!");
    }

    return this;

}// End of userLoad method

public int findBuddy(String buddyName)
{
	System.out.println("The user class is searching for this buddy: <" + buddyName + ">");
    for(int i = 0; i < buddyList.size(); i++)
    {
		System.out.println("This is the buddy's being checked in the buddyList: <" + buddyList.elementAt(i).userID);
        if(buddyList.elementAt(i).userID.equals(buddyName))
        return i;

    }// End of for loop

    return -1;

}// End of findBuddy method


}// End of User class