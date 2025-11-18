import java.awt.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;
import javax.swing.text.*;

public class Encounter {
   private int pHP;
   private int eHP;
   private final Player p;
   private final Enemy en;
   private final Thread pThread;
   private final Thread eThread;
   private int loopStopper;
   private static JTextPane combatLog;
   private final App app;
   private JPanel pHpBarFiller;
   private JPanel eHpBarFiller;
   private int eMaxHp;
   private int weaponExpCounter=0;
   private int hpExpCounter=0;

   public Encounter(App ap, Player player, Enemy enemy) {
      //region all of combat
      app = ap;
      en = enemy;
      p = player;
      loopStopper=0;
      //[0 hp, 1 def, 2 dex, 3 agi, 4 acc, 5 str, 6 atkSpd]
      ArrayList<Double> playerStats = player.getCombatStats();
      ArrayList<Double> enemyStats = enemy.getCombatStats(); 
      System.out.println("ENCOUNTER:"); 
      double playerHP = playerStats.get(0);
      double enemyHP = enemyStats.get(0);
      
      pHP = (int)playerHP; 
      eHP = (int)enemyHP;  
      
      double pDef, pDex, pAgi, pAcc, pStr, pAtkSpd;
      double eDef, eDex, eAgi, eAcc, eStr, eAtkSpd;
      
      pDef = playerStats.get(1); pDex = playerStats.get(2); pAgi = playerStats.get(3); pAcc = playerStats.get(4); pStr = playerStats.get(5); pAtkSpd = playerStats.get(6);
      eDef = enemyStats.get(1);  eDex = enemyStats.get(2);  eAgi = enemyStats.get(3);  eAcc = enemyStats.get(4);  eStr = enemyStats.get(5);  eAtkSpd = enemyStats.get(6);
      

      pThread = new Thread(() -> {
         try {
            Random rndPlayer = new Random();
            int rndNum;
            double percent;
            eMaxHp=eHP;
            int threadSpeed=(int)(pAtkSpd*1000);
            while(loopStopper==0){
               Thread.sleep(threadSpeed);
               rndNum= rndPlayer.nextInt(100)+1; //between 1 and 100, instead of 0 to 99
               if(rndNum>pAcc){ // PLAYER DID NOT MISS
                  rndNum= rndPlayer.nextInt(100)+1;
                  if(rndNum<eAgi){ // example: agi is 3%, rng hits 2, so 2 < 3 therefore the rare 3% chance happened and he evaded
                  }
                  else{
                     // PLAYER HITS TARGET - NOT EVADED
                     rndNum= rndPlayer.nextInt(100)+1;
                     double dmg = pStr-eDef;
                     weaponExpCounter+=rndPlayer.nextInt((int)pStr-5,(int)pStr+5);
                     if(rndNum<pDex){ // same logic as agility
                        dmg = (pStr*1.5)-eDef;
                        weaponExpCounter+=rndPlayer.nextInt((int)pStr-5,(int)pStr+5);
                     }
                     if(dmg>0 || dmg<=0){ // ATTACK NOT GUARDED - damage above 0
                        eHP = (eHP - (int)dmg);
                        if(loopStopper==0){  
                           logMessage(player.getName() + " attacked the " + enemy.getName() + " for " + (int)dmg + "damage", 1);
                        }
                     }
                  }
               }
               else{ // player DID miss
                  logMessage(player.getName() + " attacked the " + enemy.getName() + " but missed ", 1);
               }
               percent = ((double)eHP/(double)eMaxHp); 
               eHpBarFiller.setBounds(575, 350, (int)(168*percent) , 20);
               whoDed();
            }  
         } catch (InterruptedException e) {
            System.out.println("error in player thread");
         }
      });
         
      eThread = new Thread(() -> {
         try {
            Random rndEnemy = new Random();
            int rndNum;
            int threadSpeed=(int)(eAtkSpd*1000);
            double percent;
            while(loopStopper==0){
               Thread.sleep(threadSpeed);
               rndNum= rndEnemy.nextInt(100)+1;
               if(rndNum>eAcc){
                  rndNum= rndEnemy.nextInt(100)+1;
                  if(rndNum<pAgi){
                  }
                  else{
                     rndNum= rndEnemy.nextInt(100)+1;
                     double dmg = eStr-pDef;
                     hpExpCounter+=rndEnemy.nextInt((int)eStr-5,(int)eStr+5);
                     if(rndNum<eDex){
                        dmg = (eStr*1.5)-pDef;
                        hpExpCounter+=rndEnemy.nextInt((int)eStr-5,(int)eStr+5);
                     }
                     if(dmg>0 || dmg<=0){
                        pHP = (pHP - (int)dmg);
                        if(loopStopper==0){
                           logMessage("the " + enemy.getName() + " attacked " + player.getName() + " for " + (int)dmg + "damage", -1);
                        }
                     }
                  }
               }
               else{ // enemy DID miss
                  logMessage(enemy.getName() + " attacked " + player.getName() + " but missed ", -1);
               }
               percent = ((double)pHP/(double)p.getMaxHp()); 
               pHpBarFiller.setBounds(36, 350, (int)(168*percent) , 20);
               whoDed();
            }  
         } catch (InterruptedException e) {
            System.out.println("error in enemy thread");
         }
      });
      //endregion
   }

   public synchronized void whoDed(){
      if(eHP<=0) 
      {
         if(loopStopper!=0){
            System.out.println("player wins remaining HP-   " + pHP);
            logMessage("-------------------------------------------------------------------------", 0);
            logMessage("the player won!", loopStopper);
            eHP=0;
            pHP=p.getMaxHp();
            p.addExp("hp", hpExpCounter);
            p.addExp(p.getWeaponTypeUsed(), weaponExpCounter);
            int RL = en.getLootLevel();
            Random rnd = new Random();
            if(RL==0){
               p.updateMoney(rnd.nextInt(0, 5));
            }
            else{
               p.updateMoney(rnd.nextInt(RL*2, RL*7)+5);

            }
         }
         loopStopper = 1; // checks if player won
      }
      else {
         if(pHP<=0) // if not: checks if player lost
         { 
            if(loopStopper!=0){
               System.out.println("enemy wins remaining HP-   " + eHP);
               logMessage("-------------------------------------------------------------------------", 0);
               logMessage("the enemy won...", loopStopper);
               eHP=eMaxHp;
               pHP=p.getMaxHp();
            }
            loopStopper = -1;
         }
      }
   }

   public int combatStart(){
      combatFrameFunc();
      pThread.start();
      eThread.start();
      try {
         pThread.join();
         eThread.join();
      } catch (InterruptedException e) {
         System.out.println("error with joining threads");
      }

      return loopStopper; // my 1 or -1 which is being updates with every iteration of each thread
   }

   public void combatFrameFunc(){ 
      JFrame frame = new JFrame("combat");

      frame.setSize(new Dimension(800,450));
      frame.setVisible(true);
      frame.setResizable(false);
      frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); 
      frame.setLayout(null);
      // player and enemy pictures
      // how will i change pictures according to class and enemy? UHHH idk.... thats for future omri!
      String klass = p.getKlass();
      ImageIcon playerIcon;
      switch (klass) {
         case "Warrior"-> playerIcon = new ImageIcon("lib/player_warrior.png");
         case "Ranger" -> playerIcon = new ImageIcon("lib/player_ranger.png");
         case "Wizard" -> playerIcon = new ImageIcon("lib/player_wizard.png");
         default -> {
            playerIcon = new ImageIcon("lib/player_temp.png");
         }
      }
      JLabel playerPic = new JLabel(playerIcon); playerPic.setBounds(30,50,180,300);
      frame.add(playerPic); playerPic.setVisible(true);
      ImageIcon enemyIcon = new ImageIcon("lib/enemy_temp.png");
      JLabel enemyPic = new JLabel(enemyIcon); enemyPic.setBounds(570,50,180,300);
      frame.add(enemyPic); enemyPic.setVisible(true);

      // ===== titles
      JLabel pName = new JLabel("<html><div style='text-align:center;'>" + app.getPlayer().getName() + "</div></html>");
      JLabel eName = new JLabel("<html><div style='text-align:center;'>" + en.getName() + "</div></html>");
      pName.setBounds(35, 20, 180, 30);
      eName.setBounds(575, 20, 180, 30);
      frame.add(pName); pName.setVisible(true);
      frame.add(eName); eName.setVisible(true);
      // enemy health bar
      JPanel pHpBarFrame = new JPanel() {
         @Override
         protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.setColor(Color.BLUE);
            Graphics2D g2 = (Graphics2D) g;
            g2.setStroke(new BasicStroke(4));
            g2.drawRect(0, 0, getWidth(), getHeight());
         }
      };
      pHpBarFrame.setBounds(36, 350, 168 , 20);
      frame.add(pHpBarFrame); pHpBarFrame.setOpaque(false);
      pHpBarFrame.setVisible(true);
      double percent = (pHP/p.getMaxHp());
      pHpBarFiller = new JPanel() {
         @Override
         protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.setColor(Color.BLUE);
            Graphics2D g2 = (Graphics2D) g;
            g2.setStroke(new BasicStroke(4));
            g2.fillRect(0, 0, getWidth(), getHeight());
         }
      };
      pHpBarFiller.setBounds(36, 350, (int)(168*percent) , 20);
      frame.add(pHpBarFiller); pHpBarFiller.setOpaque(false);
      pHpBarFiller.setVisible(true);
      

      // enemy health bar
      JPanel eHpBarFrame = new JPanel() {
         @Override
         protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.setColor(Color.RED);
            Graphics2D g2 = (Graphics2D) g;
            g2.setStroke(new BasicStroke(4));
            g2.drawRect(0, 0, getWidth(), getHeight());
         }
      };
      eHpBarFrame.setBounds(575, 350, 168 , 20);
      frame.add(eHpBarFrame); eHpBarFrame.setOpaque(false);
      eHpBarFrame.setVisible(true);
      double epercent = 1;
      eHpBarFiller = new JPanel() {
         @Override
         protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.setColor(Color.RED);
            Graphics2D g2 = (Graphics2D) g;
            g2.setStroke(new BasicStroke(4));
            g2.fillRect(0, 0, getWidth(), getHeight());
         }
      };
      eHpBarFiller.setBounds(575, 350, (int)(168*epercent) , 20);
      frame.add(eHpBarFiller); eHpBarFiller.setOpaque(false);
      eHpBarFiller.setVisible(true);
      
      // ===== text wall in the middle
      // use the private variable combatLog to hopefully be 
      // able to get text from the threads into the text area??? just an idea though
      combatLog = new JTextPane();
      combatLog.setEditable(false); // optional but recommended
      JScrollPane scrollPane = new JScrollPane(combatLog);
      scrollPane.setBounds(230,50, 320,300);
      frame.add(scrollPane);
      

      frame.setVisible(true);
      frame.revalidate();
      frame.repaint();
   }

   // MADE BY AI :( felt lazy 
   public static void logMessage(String message, int attacker) { 
      StyledDocument doc = combatLog.getStyledDocument();
      SimpleAttributeSet attr = new SimpleAttributeSet();
      switch (attacker) {
         case 1 -> StyleConstants.setForeground(attr, Color.BLUE);
         case -1 -> StyleConstants.setForeground(attr, Color.RED);
         default -> StyleConstants.setForeground(attr, Color.BLACK);
      }
      try {
         doc.insertString(doc.getLength(), message + "\n", attr);
         combatLog.setCaretPosition(doc.getLength());
      } catch (BadLocationException e) {
         System.out.println("error logging message into combatLog");
      }
   }
} // 168 x 300
