// imports
import java.awt.*;
import java.util.ArrayList;
import javax.swing.*;

public class CharacterCreation{
   //fields
   private String chosenClass;
   public ImageIcon playerIcon;
   
   // app.getID will allow us to find the ID of the user to signed into their account, very helpful
   public CharacterCreation(App app){
      chosenClass="";
      
      //#region test
      Image bg = new ImageIcon("lib/character_background.png").getImage();
      JPanel panel = new JPanel() {
         @Override
         protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.drawImage(bg, 0, 0, getWidth(), getHeight(), this);
         }
      };
      panel.setLayout(null);
      
      JLabel title = new JLabel("CHARACTER CREATION!");
      title.setBounds(100,50,700,50); panel.add(title); title.setVisible(true);
      title.setFont(new Font("Arial", Font.BOLD, 36));

      
      JTextArea info = new JTextArea("""
                                     Warrior- They strike their enemies with swords, daggers and axes.
                                                    Warriors are Strong and defensive, choosing to fight up close and personal.
                                     Ranger- They attack their foes using bows, firearms and crossbows.
                                                    Rangers are sneaky and evasive, choosing to attack from a little further away
                                     Wizard- They cast thier spells using tomes, staves and wands.
                                                    Wizard are choosing to cast massive strong spells to obliterate thier enemies
                                     
                                     - choosing a class grants you 25% mastery exp boost for the class's prefered weapons.
                                     - it does not lock you away from using other weapons""");
      info.setBounds(110,140,470,150);
      panel.add(info);
      info.setVisible(true);

      JLabel choice = new JLabel("so, which class would you decide?");
      choice.setBounds(135,280,250,30); panel.add(choice); choice.setVisible(true);
      JButton warrior = new JButton("WARRIOR"); warrior.setBounds(130,320,100,30); panel.add(warrior); warrior.setVisible(true);
      JButton ranger =  new JButton("RANGER");  ranger.setBounds(250,320,100,30);  panel.add(ranger);  ranger.setVisible(true);
      JButton wizard =    new JButton("WIZARD");    wizard.setBounds(370,320,100,30);    panel.add(wizard);    wizard.setVisible(true);
      //#endregion

      playerIcon = new ImageIcon("lib/player_warrior.png");
      
      JLabel playerPic = new JLabel(playerIcon); 
      playerPic.setBounds(800,50,180,300);
      panel.add(playerPic); 
      playerPic.setVisible(true);


      JLabel choseC = new JLabel("Chosen: " + chosenClass); choseC.setBounds(250, 350, 150, 30); panel.add(choseC); choseC.setVisible(true);
      warrior.addActionListener(e -> {
         chosenClass="Warrior";
         playerIcon = new ImageIcon("lib/player_warrior.png");
         playerPic.setIcon(playerIcon);
         choseC.setText("Chosen: Warrior");
         // set equipment as 1,0,0,0,0 for- weapon, + 4 armour pieces
      });
      ranger.addActionListener(e -> {
         chosenClass="Ranger";
         playerIcon = new ImageIcon("lib/player_ranger.png");
         playerPic.setIcon(playerIcon);
         choseC.setText("Chosen: Ranger");
      });
      wizard.addActionListener(e -> {
         chosenClass="Wizard";
         playerIcon = new ImageIcon("lib/player_wizard.png");
         playerPic.setIcon(playerIcon);
         choseC.setText("Chosen: Wizard");
      });

      
      JButton confirm = new JButton("Confirm Choice"); confirm.setBounds(250,400,150,30); panel.add(confirm); confirm.setVisible(true);
      confirm.addActionListener(e -> {
         Object[] data = newCharacter(app.getID(), chosenClass, app);
         if(app.getDB().addEntry("players", data)){
            app.moveTo("Forest");
         }
         
      });
      
      

      app.getFrame().setTitle("CharacterCreation");
      app.changePanel(panel);
      

   }


   private Object[] newCharacter(int userID, String clas, App app){
      System.out.println("making an account for user:" + userID);
      // data- id[0], maxHp[1], hp[2], class[3], masteries[4], exp[5], expToNextLevel[6] - length=7 [id, 3 strings then 3 are arraylists!!!]
      ArrayList<Integer> masteries = new ArrayList<>(); // masteries
      ArrayList<Integer> exp = new ArrayList<>(); //exp
      ArrayList<Integer> nextLvl = new ArrayList<>(); //nextLvl
      Object[] data = new Object[8];
      
      data[0]=userID;
      data[1] = 50; //max HP
      data[2] = 50; //current HP
      data[3] = clas; // class
      data[4] = "Forest"; //location, new character starts in the forest
      data[5] = masteries;
      data[6] = exp;
      data[7] = nextLvl;
      
      exp.addFirst(userID);
      masteries.addFirst(userID);
      nextLvl.addFirst(userID);
      for (int i = 0; i <= 9; i++) { // runs 10 times (once for HP and then 9 times for each weapon type)
         exp.addLast(0); 
         nextLvl.addLast(100); 
         if(i!=0) masteries.addLast(0); // ignores the iteration for HP
      }
      switch (clas) { // starting mastry of classes
         case "Warrior" -> {
            for (int i = 1; i <= 3; i++) {
               masteries.set(i, 3);
            }
            Object[] data2 = {userID,1};
            app.getDB().addEntry("Inventory",data2);
         }
         case "Ranger" -> {
            for (int i = 4; i <= 6; i++) {
               masteries.set(i, 3);
            }
            Object[] data2 = {userID,4};
            app.getDB().addEntry("Inventory",data2);
         }
         case "Wizard" -> { 
            for (int i = 7; i <= 9; i++) {
               masteries.set(i, 3);
            }
            Object[] data2 = {userID,7};
            app.getDB().addEntry("Inventory",data2);
         }
         default -> {
            System.out.println("error, class isnt viable");
         }
     }

     // new Player instance
     Player p = new Player(app, data);
     app.setPlayer(p);

      return data;
   }

}
