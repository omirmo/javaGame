import java.awt.*;
import javax.swing.*;

public class Town {
   public Town(App app){
      System.out.println("we are in the town now!");

      
      Image bg = new ImageIcon("lib/village_background.png").getImage();
      JPanel panel = new JPanel() 
      {
         @Override
         protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.drawImage(bg, 0, 0, getWidth(), getHeight(), this);
         }
      };
      panel.setLayout(null);
      
      JLabel title = new JLabel("THE TOWN!");
      title.setBounds(100,50,700,50); panel.add(title); title.setVisible(true);
      title.setFont(new Font("Arial", Font.BOLD, 36));



      
      JButton travelForest = new JButton("<html><div style='text-align:center;'>TRAVEL TO<br>FOREST</div></html>"); 
      travelForest.setBounds(530,550,90,50); panel.add(travelForest); travelForest.setVisible(true);
      travelForest.setMargin(new Insets(2, 3, 2, 3));
      travelForest.addActionListener(e -> moveTo("Forest", app));



      app.getFrame().setTitle("the town~ owo");
      app.changePanel(panel);
      
   }
   public void moveTo(String loc, App app){
      app.moveTo(loc);
   }
}