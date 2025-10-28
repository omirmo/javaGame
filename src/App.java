import java.awt.*;
import javax.swing.*;

public class App {
   private int userID;
   private Player player;
   private JFrame frame;
   private final manageDB DB;

    public App(){
        frame = new JFrame();
        
        
        frame.setSize(new Dimension(1200,675));
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setVisible(true);
        frame.setResizable(false);
        DB = new manageDB("jdbc:sqlite:gameDB.db");
    }

    public void moveTo(String pageName){
        switch (pageName) {
            case "LoginPage" -> { 
                new LoginPage(this);
            }
            case "CharacterCreation" -> { // will dispose login page in the login class, and then go here
                new CharacterCreation(this);
                DB.updateFieldByID(userID, "location", "playerStats", "CharacterCreation");
            }
            case "Forest" -> {
                player.changeLoc("Forest");
                DB.updateFieldByID(userID, "location", "playerStats", "Forest");
                new Forest(this);
            }
            case "Village" -> {
                player.changeLoc("Village");
                DB.updateFieldByID(userID, "location", "playerStats", "Village");
                new Town(this);
            }
            case "Desert" -> {
                player.changeLoc("Desert");
                DB.updateFieldByID(userID, "location", "playerStats", "Desert");
                //new Desert(this);
            }
            case "Mountain" -> {
                player.changeLoc("Mountain");
                DB.updateFieldByID(userID, "location", "playerStats", "Mountain");
                //new Mountain(this);
            }
            
        
            default -> {
                System.out.println("error: area not found.");
            }
        }
    }

    public void setID(int id){userID = id;}
    public int getID(){return userID;}

    public void setPlayer(Player p){player=p;}
    public Player getPlayer(){return player;}

    public void setFrame(JFrame f) {frame=f;}
    public JFrame getFrame(){return frame;}

    public manageDB getDB() {return DB;}

    public void changePanel(JPanel newPanel){
        frame.setContentPane(newPanel);
        frame.revalidate();
        frame.repaint();
    }
}
