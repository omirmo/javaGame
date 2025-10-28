
import java.util.ArrayList;

public class Enemy {
   // fields:
   private final String name;
   private final int maxHP;
   private final int hp;
   private final int defense;
   private final int dexterity;
   private final int agility;
   private final int strength;
   private final int accuracy;
   private final double attackSpeed;
   private final int reward;
   private final int loot;

   //constructor
   public Enemy(ArrayList<Object> data){
      // Name, MaxHP, def, dex, agi, str, acc, expReward, lootLevel
      name=(String)data.get(0);
      maxHP=(int)data.get(1);
      hp=maxHP;
      defense=(int)data.get(2);
      dexterity=(int)data.get(3);
      agility=(int)data.get(4);
      accuracy=(int)data.get(5);
      strength=(int)data.get(6); 
      reward = (int)data.get(7); //exp
      loot = (int)data.get(8);
      attackSpeed=(double)data.get(9);
   }

   public String getName() {return name;}
   public int getHP() {return hp;}
   public int getDef() {return defense;}
   public int getDex() {return dexterity;}
   public int getAgi() {return agility;}
   public int getAcc() {return accuracy;}
   public int getStr() {return strength;}
   public double getHealthPercentage() {return (hp/maxHP*100);}
   public int getReward() {return reward;}
   public int getLootLevel() {return loot;}
   public double getAttackSpeed() { return attackSpeed;}

   public ArrayList<Double> getCombatStats(){
      ArrayList<Double> res = new ArrayList<>();
      //[0 hp, 1 def, 2 dex, 3 agi, 4 acc, 5 str, 6 atkSpd]
      res.add((double)hp);
      res.add((double)defense);
      res.add((double)dexterity);
      res.add((double)agility);
      res.add((double)accuracy);
      res.add((double)strength);
      res.add((double)attackSpeed);
      return res;
   }

}
