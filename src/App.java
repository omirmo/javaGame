import java.awt.*;
import javax.swing.*;

public class App {
   private int userID;
   private Player player;
   private JFrame frame;
   private final manageDB DB;

    public App(){
        frame = new JFrame();
        JMenuBar menuBar = new JMenuBar();

        
        JMenu subMenu = new JMenu("GameActions");
        JMenu subLogOut = new JMenu("LogOut");
        
        JMenuItem inventoryItem = new JMenuItem("openInventory");
        JMenuItem expItem = new JMenuItem("show level Exp");
        JMenuItem equipmentItem = new JMenuItem("openEquipment");

        
        JMenuItem logoutItem = new JMenuItem("Are you sure?");
        logoutItem.addActionListener(e -> {
            new LoginPage(this);
        });
        subLogOut.add(logoutItem);        
        

        menuBar.add(subLogOut);
        subMenu.add(equipmentItem);
        subMenu.add(expItem);
        subMenu.add(inventoryItem);
        menuBar.add(subMenu);
        
        equipmentItem.addActionListener(e -> {
            // last function i will implement
            /*
             * will display all of the equipment from the player's inventory and then will display a choice of
             * "which item would you like to display" and then also will show the stats and currently equipped stats
             * to make sure u really do want to replace it
             * 
             * 
             * how:
             * make a query to get all items where equipment=1 and userID=id
             * display all in a list (a function that takes the item ID and goes for the equipment tab to 
             * find the item name and stats... lots of pull requests from the DB)
             * when selected- show item stats in a text box and a set way (defense- , dexterity-, agility- , attack speed- etc etc etc)
             * once you display a message, just also make a button to update between the current equipped item ID in the related tab
             * 
             */
        });
        
        expItem.addActionListener(e -> {
            // open a popup with Exp information
        });
        
        inventoryItem.addActionListener(e -> {
            // open a poptup with inventory information, as well as 
        });
        
        
        
        
        
        
        
        frame.setJMenuBar(menuBar);
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
                DB.updateFieldByID(userID, "playerStats", "location", "CharacterCreation");
            }
            case "Forest" -> {
                player.changeLoc("Forest");
                DB.updateFieldByID(userID, "playerStats", "location", "Forest");
                new Forest(this);
            }
            case "Village" -> {
                player.changeLoc("Village");
                DB.updateFieldByID(userID, "playerStats", "location", "Village");
                new Town(this);
            }
            case "Desert" -> {
                player.changeLoc("Desert");
                DB.updateFieldByID(userID, "playerStats", "location", "Desert");
                //new Desert(this);
            }
            case "Mountain" -> {
                player.changeLoc("Mountain");
                DB.updateFieldByID(userID,  "playerStats", "location", "Mountain");
                new Mountain(this);
            }
            // map so far: village <--> forest <--> mountains <--> desert
            
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
