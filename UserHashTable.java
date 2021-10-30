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

public class UserHashTable extends  Hashtable <String, User>
{

DataOutputStream        dos;
DataInputStream         dis;
final String            userFileName = "userCredentials.txt";
File                    userFile;
Enumeration <User>      enumeration;

UserHashTable()
{

userFile = new File(userFileName);
//enumeration = this.elements();

}// End of UserHashTable constructor

public void storeHash()
{

    try
    {
        if(!userFile.exists())
            userFile.createNewFile();

        enumeration = this.elements();

        dos = new DataOutputStream(new FileOutputStream(userFileName));

        dos.writeInt(this.size());

        while(enumeration.hasMoreElements())
            enumeration.nextElement().userStore(dos);
    }
    catch(Exception e)
    {
        System.out.println("loadHash Exception");
    }

}// End of storeHash method

public void loadHash()
{

    if(!userFile.exists())
        return;

    try
    {
        dis = new DataInputStream(new FileInputStream(userFileName));

        int numOfUsers = dis.readInt();

        for(int i = 0; i < numOfUsers; i++)
        {
            User user = new User();
            user = user.userLoad(dis);
            this.put(user.username, user);

        }// End of while

    }
    catch(Exception e)
    {
        System.out.println("storeHash Exception");
    }


}// End of loadHash method


}// End of UserHashTable class