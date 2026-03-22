import java.awt.*;
import java.util.Random;
import javax.swing.*;


public class Forest {
   private int loopStopper;
   private App app;
   private boolean isExploring;

   public Forest(App ap){
      app = ap;
      System.out.println("we are in the Forest now!");
      loopStopper=0;

      Image bg = new ImageIcon("lib/forest_background.png").getImage();
      JPanel panel = new JPanel() {
         @Override
         protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.drawImage(bg, 0, 0, getWidth(), getHeight(), this);
         }
      };
      panel.setLayout(null);
      
      JLabel title = new JLabel("THE FOREST!");
      title.setBounds(100,50,700,50); panel.add(title); title.setVisible(true);
      title.setFont(new Font("Arial", Font.BOLD, 36));
      
      JButton travelVillage = new JButton("<html><div style='text-align:center;'>TRAVEL TO<br>VILLAGE</div></html>"); 
      travelVillage.setBounds(30,530,90,50); panel.add(travelVillage); travelVillage.setVisible(true);
      travelVillage.setMargin(new Insets(2, 3, 2, 3));
      travelVillage.addActionListener(e -> moveTo("Village", app));
      
      JButton travelMountain= new JButton("<html><div style='text-align:center;'>TRAVEL TO<br>MOUNTAINS</div></html>"); 
      travelMountain.setBounds(1060,530,90,50);panel.add(travelMountain);travelMountain.setVisible(true);
      travelMountain.setMargin(new Insets(2, 3, 2, 3));
      travelMountain.addActionListener(e -> moveTo("Mountain", app));
      
      JButton explore= new JButton("<html><div style='text-align:center;'>EXPLORE<br></div></html>"); 
      explore.setBounds(513,313,85,30);panel.add(explore);explore.setVisible(true);
      explore.setMargin(new Insets(2, 2, 2, 2));
      explore.addActionListener(e -> explore(app, explore));
      
      
      app.changePanel(panel);
     
      
   }

   public void moveTo(String loc, App app){
      app.moveTo(loc);
   }

   public void explore(App app, JButton btn){
      Thread explorationThread = new Thread(() -> {
         try {
            int rndNum;
            Random rnd = new Random();
            btn.setText("<html><div style='text-align:center;'>EXPLORING<br></div></html>");
            while(loopStopper==1){
               Thread.sleep(1000);
               rndNum= rnd.nextInt(100)+1; //between 1 and 100, instead of 0 to 99
               if(rndNum>=97){
                  // legendary item or boss, half and half
                  if(rndNum%2 ==0){
                     System.out.println("enemy boss! 1 in 50!");
                     // forest boss - enemy ID 6
                     Enemy tempBoss = app.getDB().getEnemyByID(6);
                     System.out.println("enemy ID:6 - " + tempBoss.getName());
                     System.out.println("STARTING FIGHT:");
                     Encounter a = new Encounter(app, app.getPlayer(), tempBoss);
                     int won = a.combatStart();
                     if(won==1){
                        System.out.println("in the forst, you won! against the boss!");
                     }
                     else {
                        System.out.println("in the forst, you lost.... LOSER but its okay its against the boss after all");
                     }
                  }
                  else{
                     System.out.println("legendary find! 1 in 50!");
                     int finalLoot = rnd.nextInt(0, 50)+100;
                     app.getPlayer().updateMoney(finalLoot);
                  }
               }
               else{
                  if(rndNum>=64){
                     // forest enemies- ID from 1 to 5
                     loopStopper=0;
                     rndNum = rnd.nextInt(5)+1;
                     Enemy tempEnemy = app.getDB().getEnemyByID(rndNum);
                     System.out.println("enemy encounter! 1 in 3!");
                     System.out.println("enemy ID:" + rndNum + " - " + tempEnemy.getName());
                     System.out.println("STARTING FIGHT:");
                     Encounter a = new Encounter(app, app.getPlayer(), tempEnemy);
                     int won = a.combatStart();
                     if(won==1){
                        System.out.println("in the forst, you won!");
                     }
                     else {
                        System.out.println("in the forst, you lost.... LOSER");
                     }
                  }
                  else{
                     if(rndNum>=39){
                        System.out.println("found coin! 1 in 4!");
                        int finalLoot = rnd.nextInt(1, 5);
                        app.getPlayer().updateMoney(finalLoot);
                     }
                  }
               }
            }  
         } catch (InterruptedException e) {
            System.out.println("error in exploration thread");
         }
      });   
      System.out.println("started exploring");
      if(loopStopper==0){ 
         // if its 0 (turned off) then make it 1 and turn the thread on, if its already at 1 then make it 0 (turn it off)
         loopStopper=1;
         if(explorationThread.isAlive()!=true){
            explorationThread.start();
         }
         System.err.println("thread started");
      } else{
         // turn off-
         loopStopper=0;
         btn.setText("<html><div style='text-align:center;'>EXPLORE<br></div></html>");
      }
   }
}
