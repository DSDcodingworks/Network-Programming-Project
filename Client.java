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

class Client extends JFrame
             implements ActionListener, DocumentListener, ListSelectionListener, KeyListener, MouseListener
{

public static void main(String[] args)
{
new Client();
}

Socket                      socket;
BufferedReader              br;
DataOutputStream            dos;
String                      msg;
Talker                      talker;
String                      ip;
int                         port;
String                      id;
JButton                     sendButton;
JButton                     exitButton;
JButton						chatButton;
JList                       myListBox;
DefaultListModel <Buddy>    dlm;
JPanel                      buttonPanel;
JScrollPane                 myScrollPane;
JTextField                  myInputTF;
CTS                         cts;
LoginDialog                 loginDialog;
User                        user;
JButton                     loginButton;
JLabel                      buddyLabel;
JPanel                      buddyPanel;

Client()
{
    user = new User();

    chatButton = new JButton("CHAT");
    chatButton.setActionCommand("CHAT");
    chatButton.addActionListener(this);

    loginButton = new JButton("LOGIN");
    loginButton.setActionCommand("LOGIN");
    loginButton.addActionListener(this);
    //loginButton.setEnabled(true);

    sendButton = new JButton("ADD BUDDY");
    sendButton.setActionCommand("ADDBUDDY");
    sendButton.addActionListener(this);
    sendButton.setEnabled(false);

    exitButton = new JButton("EXIT");
    exitButton.setActionCommand("EXIT");
    exitButton.addActionListener(this);

    myInputTF = new JTextField(30);
    myInputTF.getDocument().addDocumentListener(this);//add inputTF as a document listener
    myInputTF.addKeyListener(this);
    myInputTF.setEnabled(false);

    buddyLabel = new JLabel("Add Buddy:");

    dlm = new DefaultListModel <Buddy>();
    myListBox = new JList(dlm);
    myListBox.addListSelectionListener(this);
    myListBox.addMouseListener(this);

    buttonPanel = new JPanel();
    buddyPanel = new JPanel();

    buttonPanel.add(loginButton);
    buttonPanel.add(sendButton);
    buttonPanel.add(exitButton);
    buttonPanel.add(chatButton);
    this.add(buttonPanel, BorderLayout.SOUTH);

    buddyPanel.add(buddyLabel);
    buddyPanel.add(myInputTF);

    myScrollPane = new JScrollPane(myListBox);
    add(myScrollPane, BorderLayout.CENTER);

    add(buddyPanel, BorderLayout.NORTH);

    //ip = "127.0.0.1";
    //port = 6789;
    //id = "b";

    //id = "";
    //id = JOptionPane.showInputDialog(null, "Please enter your user ID: ");

    //if(id.trim().equals(""))
    //System.exit(0);

    //cts = new CTS(dlm);

    ip = "127.0.0.1";
    port = 6789;

    id = "";



setupMainFrame();

loginDialog = new LoginDialog(this);

}// End of Client constructor



void setupMainFrame()
{
Toolkit tk;
Dimension d;

tk = Toolkit.getDefaultToolkit();
d = tk.getScreenSize();
setSize(d.width/2, d.height/2);
setLocation(d.width/4, d.height/4);

setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

setTitle("Client - NetProg4" /*+ user.username*/);

setVisible(true);


}// End of setupMainFrame


public void actionPerformed(ActionEvent e)
{

    if(e.getActionCommand().equals("LOGIN"))
    {
		if(user.username == null || user.password == null)
        	loginDialog = new LoginDialog(this);
        else
        {

			int response = JOptionPane.showConfirmDialog(null, "You are currently logged in, trying to login again will log you off, do you wish to proceed?", "Log Off Confirmation", JOptionPane.YES_NO_OPTION);
	        if (response == JOptionPane.YES_OPTION)
	        {
				msg = "DISCONNECT";
				clientSend(msg);

				user.username = null;
				user.password = null;

				dlm.clear();

				loginDialog = new LoginDialog(this);
	        }
	        else
	        {
	            //JOptionPane.showMessageDialog(null, "GOODBYE");
	            //System.exit(0);
   			}

		}// Username or password are filled in meaning the user is logged in

    }// End of if(LOGIN)


    else if(e.getActionCommand().equals("ADDBUDDY"))
    {
		addBuddy(e.getActionCommand());
    }// End of actionPerformed ADDBUDDY
    else if(e.getActionCommand().equals("CHAT"))
    {
		openChat();
	}// End of actionPerformed CHAT

    else if(e.getActionCommand().equals("EXIT"))
    {
        System.exit(0);
    }// End of actionPerformed CHAT

}// End of actionPerformed


public void keyPressed(KeyEvent k)
{

msg = myInputTF.getText().trim();

if(k.getKeyCode() == 10 && !msg.equals(""))
{
//dlm.addElement(msg);
//clientSend(msg);

    String input = myInputTF.getText().trim();

    if(!input.contains(" ") && !input.contains("\\"))
    {
    	if(user.findBuddy(input) < 0 && !input.equals(user.username))
    	{
    		msg = "ADDBUDDY" + '\0' + myInputTF.getText().trim();
    		clientSend(msg);
		}
		else if(user.findBuddy(input) > 0)
			JOptionPane.showMessageDialog(null, input + " is already your friend.");
		else
			JOptionPane.showMessageDialog(null, "Sorry, but you can not add yourself to your buddy list.");

    }// If input was valid
    else // Input was not valid
    {
    JOptionPane.showMessageDialog(null, "Usernames can not contain any backslashes or spaces.");
    }

}// End of if

}// End of keyPressed method

public void keyReleased(KeyEvent k)
{
}

public void keyTyped(KeyEvent k)
{
}

public void insertUpdate(DocumentEvent d)
{
    sendButton.setEnabled(!myInputTF.getText().trim().equals(""));
    //goButton.setEnabled(!inputTF.getText().trim().equals(""));
    //System.out.println("Got a new document event.");
    //adjustOutputField();
}

public void removeUpdate(DocumentEvent d)
{
    sendButton.setEnabled(!myInputTF.getText().trim().equals(""));
    //goButton.setEnabled(!inputTF.getText().trim().equals(""));
    //System.out.println("Deleted a document event.");
    //adjustOutputField();
}

public void changedUpdate(DocumentEvent d)
{
}

public void mouseClicked(MouseEvent e)
{
    //int index;
    Buddy buddy;
    if (e.getClickCount() == 2)
    {
        //index = myListBox.getSelectedIndex();
        //dlm.get(index).chatBox = new ChatBox();
        //if(myListBox.getSelectedIndex() >= 0)
        //buddy = dlm.get(myListBox.getSelectedIndex());

		if(myListBox.getSelectedIndex() < 0)
		{
			JOptionPane.showMessageDialog(null, "Please select an index to open up a chatbox.");
			return;
		}
		buddy = dlm.get(myListBox.getSelectedIndex());
        if(buddy.onlineStatus.equals("(Offline)"))
        {
		    JOptionPane.showMessageDialog(null, "Sorry, but " + buddy.userID + " is currently offline.");
		}
        else if(buddy.chatBox == null)
        {
            buddy.chatBox = new ChatBox(buddy.userID, this);
        }// End of if(chatbox == null)
        else
        {
			buddy.chatBox.setVisible(true);
            buddy.chatBox.requestFocus();
        }

        System.out.println("double clicked");
    }

}// End of mouseClicked method
public void mouseEntered(MouseEvent e)
{
}// End of mouseEntered method

public void mouseExited(MouseEvent e)
{
}// End of mouseExited method

public void mousePressed(MouseEvent e)
{
}// End of mousePressed method

public void mouseReleased(MouseEvent e)
{
}// End of mouseReleased method

public void valueChanged(ListSelectionEvent e)
{
}// End of valueChanged method

public void clientSend(String message)
{
    cts.ctsSend(message);
}// End of clientSend method

public void sendLoginRegisterMessage(String processorVerb)
{

    cts = new CTS(dlm, ip, port, user.username, this);

    msg = processorVerb + "\0" + user.username + "\0" + user.password;

    clientSend(msg);

    setTitle("Client - NetProg4 - " + user.username);

    System.out.println("Client - " + user.username);

    //setupMainFrame();


}// End of loadLoginDialog

public void openChat()
{
	Buddy buddy;

	if(myListBox.getSelectedIndex() < 0)
	{
		JOptionPane.showMessageDialog(null, "Please select an index to open up a chatbox.");
		return;
	}
	buddy = dlm.get(myListBox.getSelectedIndex());
    if(buddy.onlineStatus.equals("(Offline)"))
    {
	    JOptionPane.showMessageDialog(null, "Sorry, but " + buddy.userID + " is currently offline.");
	}
    else if(buddy.chatBox == null)
    {
	    buddy.chatBox = new ChatBox(buddy.userID, this);
	}// End of if(chatbox == null)
	else
	{
	buddy.chatBox.setVisible(true);
    buddy.chatBox.requestFocus();
    }
	System.out.println("Pressed chat button");

}//End of openChat method

public void addBuddy(String command)
{
    String input = myInputTF.getText().trim();

    if(!input.contains(" ") && !input.contains("\\"))
    {

  		if(user.findBuddy(input) < 0 && !input.equals(user.username))
   		{
   			msg = command + '\0' + myInputTF.getText().trim();
   			clientSend(msg);
		}
		else if(user.findBuddy(input) > 0)
			JOptionPane.showMessageDialog(null, input + " is already your friend.");
		else
			JOptionPane.showMessageDialog(null, "Sorry, but you can not add yourself to your buddy list.");

    }// If input was valid
    else // Input was not valid
    {
       JOptionPane.showMessageDialog(null, "Usernames can not contain any backslashes or spaces.");
    }

}// End of addBuddy method

public void clientShutDown()
{
	System.exit(0);
}// End of clientShutDown method

}// End of Client class