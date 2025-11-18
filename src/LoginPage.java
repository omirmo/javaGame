import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class LoginPage {
    private boolean signUpOpen;
    private final App app;

    public LoginPage(App app) {
        this.app=app;

        signUpOpen=false;
        JPanel panel = new JPanel();
        panel.setLayout(null);
        
        JLabel titleLogin = new JLabel("Please enter your login info");
        JLabel userLbl = new JLabel("Username:");
        JLabel passLbl = new JLabel("Password:");

        JButton newAccount = new JButton("make a new account");
        titleLogin.setBounds(350,100,200,50); panel.add(titleLogin);
        userLbl.setBounds(400,150,70,30);     panel.add(userLbl);
        passLbl.setBounds(400,180,70,30);     panel.add(passLbl);
        newAccount.setBounds(400,300,180,30); panel.add(newAccount);
        newAccount.addActionListener(e -> signUp());
        
        JTextField username = new JTextField(""); username.setBounds(470,155,150,25); panel.add(username);
        JTextField password = new JTextField(""); password.setBounds(470,185,150,25); panel.add(password);
        JButton loginButton = new JButton("login");
        loginButton.setBounds(400,250,180,30); panel.add(loginButton);
        loginButton.addActionListener(e -> logIn(username, password));
        app.getFrame().setTitle("Omri's Game");
        app.changePanel(panel);
    }

    
    // popup for signing up for a new account
    private void signUp() {
        if(!signUpOpen)
        {
            // popup is not open -> open it & change variable to true
            JFrame signUp = new JFrame("new account");
            signUp.setSize(350, 250);
            signUp.setMinimumSize(new Dimension(150,50));
            signUp.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); 
            signUp.setLayout(null);
            JLabel label = new JLabel("enter the details for the new account:"); label.setBounds(40, 40, 250, 30); signUp.add(label);
            JLabel username = new JLabel("username:"); username.setBounds(40, 70, 80, 20); signUp.add(username);
            JLabel password = new JLabel("password:"); password.setBounds(40, 100, 80, 20); signUp.add(password);
            JTextField userText = new JTextField(); userText.setBounds(107, 70, 150, 20); signUp.add(userText);
            JTextField userPass = new JTextField(); userPass.setBounds(107, 100, 150, 20); signUp.add(userPass);
            JButton submitSignUp = new JButton("SignUp!"); submitSignUp.setBounds(107,130,100,30); signUp.add(submitSignUp);
            submitSignUp.addActionListener(e -> {
                    String user = userText.getText();
                    String pass = userPass.getText();
                    String[] thing = {user,pass};
                    if(user.length()>0 && pass.length()>0 && app.getDB().addEntry("users",thing)){
                        popupMessage("User created successfully "); //if the user was created we just need to pass that information and display it
                        signUp.dispose();
                    }
                    else{
                        if(user.length()==0 || pass.length()==0){
                            popupMessage("Username and Password can not be empty");
                        }
                        else{ //both fields are full yet there was an error -> username is taken
                            popupMessage("Username already taken");
                        }
                    }
                });
            signUpOpen = true;
            signUp.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    signUpOpen = false;
                }
            });
            signUp.setVisible(true);
        }    
    }

    private void logIn(JTextField username, JTextField password) {
            String[] data = {username.getText(),password.getText()};
            int userID = app.getDB().findEntryID("users", data);
            if(userID!=-1){
                System.out.println("user found! id:" + userID);
                app.setID(userID);
                int entryExists = app.getDB().doesEntryExist("playerStats",userID);
                if(entryExists != -1){ // no error happened finding the user
                    if(entryExists==0) app.moveTo("CharacterCreation");  // if character does not exist- send to make a new one
                    else{
                        // there IS an entry, therefore go to the location 
                        Player p = new Player(app, userID);
                        app.setPlayer(p);
                        System.out.println("pspsps");
                        String loc = (String)app.getDB().getEntryField(userID, "playerStats", "location");
                        System.out.println("moving to " + loc +"!");
                        app.moveTo(loc);
                    }
                }
                else {
                    // entry exists = -1 -> error, 
                    System.out.println("uhhh this shouldnt happen?");
                }   
                
            }
            else { // issue with login inputs
                if(username.getText().length()==0){
                    popupMessage("username can not be empty");
                }
                if(password.getText().length()==0){
                    popupMessage("password can not be empty");
                }
                popupMessage("username or password are incorrect");
            }
        }

    // popup for "the user was created" or "username already exists" or "username and password cant be empty"
    public void popupMessage(String message){ 
        JFrame frame = new JFrame("accountDataBase");
        frame.setSize(300, 100);
        frame.setMinimumSize(new Dimension(150,100));
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); 
        frame.setLayout(null);
        JLabel label = new JLabel(message); label.setBounds(10, 20, 250, 20); frame.add(label);
        frame.setVisible(true);
    }


}
