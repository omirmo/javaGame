
import java.util.ArrayList;

public class Player {
    // fields:
    private final App app;
    private final int id;
    private final String clas; // player class
    private int maxHP;
    private final int hp;
    private String loc; // location
    private final ArrayList<Integer> masteries;
    private final ArrayList<Integer> experience;
    private final ArrayList<Integer> expToNextLevel;
    
    /* arraylist order: 
    masteries- id, swords, daggers, axes, bows, crossbows, firearms, tomes, staves, wands (id, and then 9 fields)
    experience- id, hp, sword, daggers, axes, bows, crossbows, firearms, tomes, staves, wands (id, and then 10 fields)
    expToNextLevel - same as experience (id, and then 10 fields)
    */


    //#region all constructors
    public Player(App app, int id){
        // a constructor which will make a player based on the ID only, basically getting it from the DB:
        this.id=id;
        this.app=app;
        ArrayList<Object> playerStats = app.getDB().getEntry(id, "playerStats");
        this.clas = (String)playerStats.get(3);
        this.loc = (String)playerStats.get(4);
        this.maxHP = (int)playerStats.get(1);
        this.hp = (int)playerStats.get(2);


        ArrayList<Object> mastery = app.getDB().getEntry(id, "playerMasteries");
        ArrayList<Integer> masteris = new ArrayList<>();
        for(Object mas : mastery){ masteris.add((int)mas); }
        ArrayList<Object> experiences = app.getDB().getEntry(id, "playerExperience");
        ArrayList<Integer> exp = new ArrayList<>();
        for(Object xp : experiences){ exp.add((int)xp); }
        ArrayList<Object> nextlvl = app.getDB().getEntry(id, "ExpToNextLevel");
        ArrayList<Integer> next = new ArrayList<>();
        for(Object lvl : nextlvl){ next.add((int)lvl); }
        this.masteries=masteris;
        this.experience=exp;
        this.expToNextLevel=next;

    }

    public Player(App app, Object[] data){ 
        this.app = app;
        // 0. id, 1.maxhp, 2. currenthp, 3. class, 4. location, 5. masteries, 6. exp, 7.expToNextLevel 
        id=(int)data[0];
        maxHP=(int)data[1];
        hp=(int)data[2];
        clas=(String)data[3];
        loc=(String)data[4];
        masteries=(ArrayList)data[5];
        experience=(ArrayList)data[6];
        expToNextLevel=(ArrayList)data[7];   
    }
    //#endregion

    
    public void updatePlayerDB(){ // NEED TO DO

    }

    public boolean addExp(String mastery, int amount){
        int index;
        double modifier=1;
        switch (mastery) {
            case "hp" -> {
                index=0;
            }
            case "swords"-> {
                index=1;
                if(clas.equals("Warrior")){
                    modifier=1.25;
                }
            }
            case "daggers"-> {
                index=2;
                if(clas.equals("Warrior")){
                    modifier=1.25;
                }
            }
            case "axes"-> {
                index=3;
                if(clas.equals("Warrior")){
                    modifier=1.25;
                }
            }
            case "bows"-> {
                index=4;
                if(clas.equals("Ranger")){
                    modifier=1.25;
                }
            }
            case "crossbows"-> {
                index=5;
                if(clas.equals("Ranger")){
                    modifier=1.25;
                }
            }
            case "firearms"-> {
                index=6;
                if(clas.equals("Ranger")){
                    modifier=1.25;
                }
            }
            case "tomes"-> {
                index=7;
                if(clas.equals("Wizard")){
                    modifier=1.25;
                }
            }
            case "staves"-> {
                index=8;
                if(clas.equals("Wizard")){
                    modifier=1.25;
                }
            }
            case "wands"-> {
                index=9;
                if(clas.equals("Wizard")){
                    modifier=1.25;
                }
            }
            
            default -> {
                index=-1;
            }
        }
        index++; // setting index one to the side in order to avoid changing ID, id=index[0] and not hp=index[0]
        if(index!=-1){
            experience.set(index, (int)((amount+experience.get(index))*modifier));
        }
        else{
            System.out.println("error adding exp, field does not exist");
            return false;
        }

        // check levelup-
        int expGap=experience.get(index)-expToNextLevel.get(index);
        if(expGap>=0){ //if there is MORE exp than needed to lvl up
            experience.set(index,expGap);
            expToNextLevel.set(index, (int)(expToNextLevel.get(index)*1.6));
            if(index==0) { // hp levelup
                maxHP=maxHP+5;
                System.out.println("===================HP levelup!");
            }
            else{ // weapon mastery levelup
                masteries.set(index,masteries.get(index)+1);
                System.out.println("===================weapon levelup!");
            }
        }
        if(index==0){
            app.getDB().updateFieldByID(id, "playerStats", "maxHP", String.valueOf(maxHP));
            System.out.println("levelup HP EXECUTED");
            app.getDB().updateFieldByID(id, "playerExperiece", "hp", String.valueOf(experience.get(index)));
            app.getDB().updateFieldByID(id, "expToNextLevel", "hp", String.valueOf(expToNextLevel.get(index)));
            System.out.println("levelup HP FINISHED");
        }
        app.getDB().updateFieldByID(id, "playerMasteries", getWeaponTypeUsed(), String.valueOf(masteries.get(index)));
        app.getDB().updateFieldByID(id, "playerExperiece", getWeaponTypeUsed(), String.valueOf(experience.get(index)));
        app.getDB().updateFieldByID(id, "expToNextLevel", getWeaponTypeUsed(), String.valueOf(expToNextLevel.get(index)));
        return true;
    }

    /**
     * @param weaponType - swords\daggers\wands\bows . . . 
     * @return the dex of the player, calculated: 60% current weapon + 20% each similar weapon
     */
    public double getDexStat(String weaponType){ 
        double dex;
        switch (weaponType) {
            case "swords"-> {
                dex= masteries.get(1)*0.6 + masteries.get(2)*0.2 + masteries.get(3)*0.2;
            }
            case "daggers"-> {
                dex= masteries.get(1)*0.2 + masteries.get(2)*0.6 + masteries.get(3)*0.2;
            }
            case "axes"-> {
                dex= masteries.get(1)*0.2 + masteries.get(2)*0.2 + masteries.get(3)*0.6;
            }
            case "bows"-> {
                dex= masteries.get(4)*0.6 + masteries.get(5)*0.2 + masteries.get(6)*0.2;
            }
            case "crossbows"-> {
                dex= masteries.get(4)*0.2 + masteries.get(5)*0.6 + masteries.get(6)*0.2;
            }
            case "firearms"-> {
                dex= masteries.get(4)*0.2 + masteries.get(5)*0.2 + masteries.get(6)*0.6;
            }
            case "tomes"-> {
                dex= masteries.get(7)*0.6 + masteries.get(8)*0.2 + masteries.get(9)*0.2;
            }
            case "staves"-> {
                dex= masteries.get(7)*0.2 + masteries.get(8)*0.6 + masteries.get(9)*0.2;
            }
            case "wands"-> {
                dex= masteries.get(7)*0.2 + masteries.get(8)*0.2 + masteries.get(9)*0.6;
            }
            
            default -> {
                return 0;
            } 
        }
        if(dex*0.2 > 20){
            return 20;
        }
        else{
            return dex*0.2;
        }
    }
    
    public double getAgiStat(String weaponType){
        double agi;
        switch (weaponType) {
            case "swords"-> {
                agi= masteries.get(1)*0.8 + masteries.get(2)*0.1 + masteries.get(3)*0.1;
            }
            case "daggers"-> {
                agi= masteries.get(1)*0.1 + masteries.get(2)*0.8 + masteries.get(3)*0.1;
            }
            case "axes"-> {
                agi= masteries.get(1)*0.1 + masteries.get(2)*0.1 + masteries.get(3)*0.8;
            }
            case "bows"-> {
                agi= masteries.get(4)*0.8 + masteries.get(5)*0.1 + masteries.get(6)*0.1;
            }
            case "crossbows"-> {
                agi= masteries.get(4)*0.1 + masteries.get(5)*0.8 + masteries.get(6)*0.1;
            }
            case "firearms"-> {
                agi= masteries.get(4)*0.1 + masteries.get(5)*0.1 + masteries.get(6)*0.8;
            }
            case "tomes"-> {
                agi= masteries.get(7)*0.8 + masteries.get(8)*0.1 + masteries.get(9)*0.1;
            }
            case "staves"-> {
                agi= masteries.get(7)*0.1 + masteries.get(8)*0.8 + masteries.get(9)*0.1;
            }
            case "wands"-> {
                agi= masteries.get(7)*0.1 + masteries.get(8)*0.1 + masteries.get(9)*0.8;
            }
            
            default -> {
                return 0;
            } 
        }
        if(agi*0.2 > 90){
            return 90;
        }
        else{
            return agi*0.1;
        }
    }

    public double getAccStat(String weaponType){
        double acc;
        switch (weaponType) {
            case "swords"-> {
                acc= 3*masteries.get(1);
            }
            case "daggers"-> {
                acc= 3*masteries.get(2);
            }
            case "axes"-> {
                acc= 3*masteries.get(3);
            }
            case "bows"-> {
                acc= 3*masteries.get(4);
            }
            case "crossbows"-> {
                acc= 3*masteries.get(5);
            }
            case "firearms"-> {
                acc= 3*masteries.get(6);
            }
            case "tomes"-> {
                acc= 3*masteries.get(7);
            }
            case "staves"-> {
                acc= 3*masteries.get(8);
            }
            case "wands"-> {
                acc= 3*masteries.get(9);
            }
            
            default -> {
                return 0;
            } 
        }
        if(acc+25 > 100){
            return 100;
        }
        else{
            return acc+25;
        }
    }

    public String getWeaponTypeUsed(){ 
        return (String)app.getDB().getEntry((int)app.getDB().getEntry(id, "EquippedItems").get(1), "equipment").get(1);
    }
    
    /**
     * @return order- hp, def, dex, agi, acc, str, atkSpd */
    public ArrayList<Double> getCombatStats(){
        ArrayList<Double> res = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            res.add(0.0);
        }
        String weaponused = getWeaponTypeUsed();
        ArrayList<Object> equipeditems = app.getDB().getEntry(id, "EquippedItems");
        res.set(0,(double)hp);
        for(int i=1; i< equipeditems.size(); i++){
            //skipping first field because its the player ID
            int itemID = (int)equipeditems.get(i);
            System.out.println("current item is: " + itemID);
            if(itemID !=0 && i!=6){ 
                
                ArrayList<Object> itemStats = app.getDB().getEntry(itemID, "equipment"); 
                //order: 0id, 1type, 2str, 3spd, 4acc, 5def, 6dex, 7agi
                res.set(1,(double)((int)itemStats.get(5)+res.get(1))); //def
                res.set(2,(double)((int)itemStats.get(6)+res.get(2))); //dex                    res.set(3,(double)((int)itemStats.get(7)+res.get(3))); //agi
                res.set(4,(double)((int)itemStats.get(4)+res.get(4))); //acc
                res.set(5,(double)((int)itemStats.get(2)+res.get(5))); //str
                res.set(6,(double)itemStats.get(3)+res.get(6));      //atkSpd
            }
        } 
        res.set(2,getDexStat(weaponused)+res.get(2)); //dex
        res.set(3,getAgiStat(weaponused)+res.get(3)); //agi
        res.set(4,getAccStat(weaponused)+res.get(4)); //acc
        return res;
        //[0 hp, 1 def, 2 dex, 3 agi, 4 acc, 5 str, 6 atkSpd]
    }

    public String getLoc(){
        return loc;
    }
    public void changeLoc(String newLoc){
        loc = newLoc;
    }
    public int getID(){
        return id;
    }
    public String getName(){
        return (String)app.getDB().getEntryField(id, "users", "username");
    }
    public int getMaxHp(){
        return maxHP;
   }
    
    public String getKlass(){
        return clas;
    }

   

}
