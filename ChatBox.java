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
import javax.swing.text.html.HTMLDocument;

public class ChatBox extends JDialog
                     implements ActionListener, AdjustmentListener, KeyListener
{

JTextArea               myInputTA;
JButton                 sendButton;
JButton                 exitButton;
JEditorPane             myEditorPane;
JPanel                  myButtonPanel;
JPanel                  typingPanel;
JScrollPane             myScrollPane;
JScrollPane				myTAScrollPane;
JLabel                  myTALabel;
Client					chatClient;
String					buddyID;
boolean					wasTextAdded;

ChatBox(String connectedID, Client c)
{
chatClient = c;

buddyID = connectedID;

wasTextAdded = false;

myInputTA = new JTextArea(null, 10, 50);
myInputTA.setLineWrap(true);
myInputTA.addKeyListener(this);

myTALabel = new JLabel("Type your message below.");

sendButton = new JButton("Send");
sendButton.setActionCommand("SEND");
sendButton.addActionListener(this);

exitButton = new JButton("Exit");
exitButton.setActionCommand("EXIT");
exitButton.addActionListener(this);

myEditorPane = new JEditorPane("text/html", "System: You are now chatting with " + connectedID);
//myEditorPane.setContentType("Text/html");
myEditorPane.setEditable(false);

myScrollPane = new JScrollPane(myEditorPane);
myScrollPane.getVerticalScrollBar().addAdjustmentListener(this);

myButtonPanel = new JPanel(new GridLayout(2, 1));

myTAScrollPane = new JScrollPane(myInputTA);

myButtonPanel.add(sendButton);
myButtonPanel.add(exitButton);

typingPanel = new JPanel();
typingPanel.add(myTALabel);
typingPanel.add(myTAScrollPane);
typingPanel.add(myButtonPanel);

//this.add(myButtonPanel, BorderLayout.NORTH);
this.add(myScrollPane, BorderLayout.CENTER);
this.add(typingPanel, BorderLayout.SOUTH);

setupChatBox(connectedID);

}// End of ChatBox constructor


void setupChatBox(String buddyID)
{
Toolkit tk;
Dimension d;

tk = Toolkit.getDefaultToolkit();
d = tk.getScreenSize();
setSize(d.width/2, d.height/2);
setLocation(d.width/4, d.height/4);

setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

setTitle("NetProg4 - ChatRoom - " + chatClient.user.username + " to " + buddyID);

//this.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);

setVisible(true);


}// End of setupLoginDialog


public void actionPerformed(ActionEvent e)
{
	String text;
	String msg;
    if(e.getActionCommand().equals("SEND"))
    {
		sendMSG();
    }
    else if(e.getActionCommand().equals("EXIT"))
    {
		this.dispose();
    }

}// End of actionPerformed method



public void keyPressed(KeyEvent k)
{

//msg = myInputTF.getText().trim();

if(k.isShiftDown())
	System.out.println("Yes!");

if(k.getKeyCode() == 10)
{
	//myInputTA.setText(myInputTA.getText() + "<BR>");
	System.out.println("Enter is pressed!");
}

if(k.isShiftDown() && k.getKeyCode() == 10)
	sendMSG();

System.out.println("This is the modifier: " + k.getKeyModifiersText(1));
System.out.println("This is the keycode: " + k.getKeyCode());

/*
if(k.getKeyText(k.getKeyCode()).equals("ENTER") && k.getKeyModifiersText(1).equals("Shift"))
{
	sendMSG();
}
*/

}// End of keyPressed method

public void keyReleased(KeyEvent k)
{
}

public void keyTyped(KeyEvent k)
{
}

public void adjustmentValueChanged(AdjustmentEvent e)
{

	if(wasTextAdded)
	{
		e.getAdjustable().setValue(e.getAdjustable().getMaximum());
		wasTextAdded = false;
	}

}// End of adjustmentValueChanged method

public void addText(String txt)
{
	HTMLDocument	doc;
	Element			html;
	Element			body;

	doc = (HTMLDocument)myEditorPane.getDocument();
	html = doc.getRootElements()[0];
	body = html.getElement(1);

	try
	{
		doc.insertBeforeEnd(body, txt);
	}
	catch(Exception exe)
	{
		System.out.println("Caught an exception when inserting HTML text.");
	}

	wasTextAdded = true;


}// End of addText method

public void sendMSG()
{
	String			text;
	String			msg;
	//JScrollBar		verticalbar;

	text = myInputTA.getText();
	text = text.replace("\n", "<BR>");
	String htmltext = "<div align = \"left\"><font color = \"blue\"> \"text\" </div>";
	htmltext = htmltext.replace("text", text);
	System.out.println("This is the htmltext: " + htmltext);
	addText(htmltext);

	//verticalbar = myScrollPane.getVerticalScrollBar();

	//verticalbar.setValue(verticalbar.getMaximum());

	//verticalbar.setValue((int) verticalbar.getMaximumSize().getHeight());

	//myScrollPane.verticalScrollBar.setValue(myScrollPane.verticalScrollBar.getVisibleAmount());

	htmltext = htmltext.replace("\n", "\1");
	msg = "FORWARD" + '\0' + chatClient.user.username + '\0' + buddyID + '\0' + htmltext;
	chatClient.clientSend(msg);

}// End of sendMSG method

}// End of ChatBox class