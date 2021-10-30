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

public class LoginDialog extends JDialog
                         implements ActionListener
{

JLabel              usernameLabel;
JLabel              passwordLabel;
JTextField          usernameTF;
JPasswordField      passwordPF;
JButton             loginButton;
JButton             registerButton;
JPanel              clientLoginButtonPanel;
JPanel              clientInputPanel;
Client              dialogClient;

LoginDialog(Client c)
{
	dialogClient = c;

	usernameLabel = new JLabel("Username: ");
	passwordLabel = new JLabel("Password: ");

	usernameTF = new JTextField();
	passwordPF = new JPasswordField();

	if(dialogClient.user.username != null)
	{
		usernameTF.setText(dialogClient.user.username);
	}

	loginButton = new JButton("Login");
	loginButton.setActionCommand("LOGIN");
	loginButton.addActionListener(this);

	registerButton = new JButton("Register");
	registerButton.setActionCommand("REGISTER");
	registerButton.addActionListener(this);

	clientInputPanel = new JPanel(new GridLayout(2, 2));
	clientInputPanel.add(usernameLabel);
	clientInputPanel.add(usernameTF);
	clientInputPanel.add(passwordLabel);
	clientInputPanel.add(passwordPF);


	clientLoginButtonPanel = new JPanel();
	clientLoginButtonPanel.add(loginButton);
	clientLoginButtonPanel.add(registerButton);

	this.add(clientInputPanel, BorderLayout.NORTH);
	this.add(clientLoginButtonPanel, BorderLayout.SOUTH);

	setupLoginDialog();

}// End of LoginDialog Constructor



void setupLoginDialog()
{
	Toolkit tk;
	Dimension d;

	tk = Toolkit.getDefaultToolkit();
	d = tk.getScreenSize();
	setSize(d.width/2, d.height/2);
	setLocation(d.width/4, d.height/4);

	setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

	setTitle("NetProg4 - Server and Client - Login/Register");

	this.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);

	setVisible(true);

}// End of setupLoginDialog


public void actionPerformed(ActionEvent e)
{

String cmd = e.getActionCommand();

    if(validFields())
    {
        dialogClient.user.username = usernameTF.getText().trim();
        dialogClient.user.password = passwordPF.getText().trim();
        if(e.getActionCommand().equals("LOGIN"))
        {
            System.out.println("User login!");
            dialogClient.sendLoginRegisterMessage(cmd);
            this.dispose();
        }

        else if (e.getActionCommand().equals("REGISTER"))
        {
            System.out.println("User register!");
            dialogClient.sendLoginRegisterMessage(cmd);
            this.dispose();
        }

    }// End of if(validFields())
    else
	    JOptionPane.showMessageDialog(null, "Username or Password can't contain backslash or spaces.");

}// End of actionPerformed method

public boolean validFields()
{
    String name = usernameTF.getText().trim();
    String pass = passwordPF.getText().trim();

    if(name.equals("") || pass.equals(""))
	    return false;

    else if(name.contains("\\") || pass.contains("\\"))
	    return false;

    else if(name.contains(" ") || pass.contains(" "))
	    return false;



    return true;

}// End of validFields method

}// End of LoginDialog class