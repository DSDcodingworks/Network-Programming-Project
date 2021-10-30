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

public class Buddy
{
String              userID;
String              onlineStatus;
ChatBox             chatBox;

Buddy(String id, String status)
{

    userID = id;
    onlineStatus = status;
    chatBox = null;

}// End of Buddy constructor

@Override
public String toString()
{
    String tempStr = userID + " " + onlineStatus;
    return tempStr;
}// End of toString method

}// End of Buddy class