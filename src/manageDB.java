import java.sql.*;
import java.util.ArrayList;

/* gameDB- "jdbc:sqlite:gameDB.db"
 * 
 *   
 */
public class manageDB {
    private static Connection conn;

    public manageDB(String ad){
        try {
            conn = DriverManager.getConnection(ad); 
            System.out.println("Connected to database."); 
        } 
        catch (SQLException e) { // if we couldnt connect to the DB
            System.out.println("Connection failed: \n" + e.getMessage());
        }
    }

    public boolean addEntry(String tableName, Object[] data){ // MADE BY AI: used AI in this function :(
        switch(tableName){
            case "users" -> {
                // user - ID, username, password
                String sql = "INSERT INTO " + tableName + "(username,password) VALUES(?,?)";
                try(PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setString(1, (String)data[0]);
                    pstmt.setString(2, (String)data[1]);
                    pstmt.executeUpdate();
                    System.out.println("New user added.");
                    return true;
                } 
                catch (SQLException e) {
                    System.out.println("error when creating an account: \n" + e.getMessage());
                    return false;
                }
            }
            case "players" -> { 
                // data- id[0], maxHp[1], hp[2], class[3], masteries[4], exp[5], expToNextLevel[6] - length=7 [id, 3 strings then 3 are arraylists!!!]
                String newPlayerSql =         "INSERT INTO playerStats (id,maxHP,currentHP,class,location) VALUES(?,?,?,?,?)";
                String newMasteriesSQL =      "INSERT INTO playerMasteries (id,swords,daggers,axes,bows,crossbows,firearms,tomes,staves,wands) VALUES(?,?,?,?,?,?,?,?,?,?)";
                String newExpSQL =            "INSERT INTO playerExperience (id,hp,swords,daggers,axes,bows,crossbows,firearms,tomes,staves,wands) VALUES(?,?,?,?,?,?,?,?,?,?,?)";
                String newExpToNextLevelSQL = "INSERT INTO expToNextLevel (id,hp,swords,daggers,axes,bows,crossbows,firearms,tomes,staves,wands) VALUES(?,?,?,?,?,?,?,?,?,?,?)";
                String newPlayerEquipment = "INSERT INTO EquippedItems (id,weapon,helmet,chestplate,leggings,boots, coins) VALUES(?,?,?,?,?,?,?)";
                
                
                // playerStats:
                try(PreparedStatement playerSql = conn.prepareStatement(newPlayerSql)) {
                    playerSql.setInt(1, (Integer)data[0]);
                    playerSql.setInt(2, (Integer)data[1]);
                    playerSql.setInt(3, (Integer)data[2]);
                    playerSql.setString(4, (String)data[3]);
                    playerSql.setString(5, (String)data[4]); 
                    if(playerSql.executeUpdate()!=0) System.out.println("New player added.");
                } catch (SQLException e) {
                    System.out.println("error when creating player data: \n" + e.getMessage());
                    return false;
                }    
                // playerMasteries:
                try(PreparedStatement masterySql = conn.prepareStatement(newMasteriesSQL)) {
                    if(data[5] instanceof ArrayList mast){ 
                        for(int i=0; i<10;i++){
                            masterySql.setInt(i+1, (Integer)(mast.get(i)));
                        }
                    }
                    if(masterySql.executeUpdate()!=0) System.out.println("New masteries added.");
                } catch (SQLException e) {
                    System.out.println("error when creating masteries list: \n" + e.getMessage());
                    return false;
                }    
                // playerExperience
                try(PreparedStatement expSql = conn.prepareStatement(newExpSQL)) {
                    if(data[6] instanceof ArrayList exp){ 
                        for(int i=0; i<11;i++){
                            expSql.setInt(i+1, (Integer)(exp.get(i)));
                        }
                    }
                    if(expSql.executeUpdate()!=0) System.out.println("New experience added.");
                } catch (SQLException e) {
                    System.out.println("error when creating experience list: \n" + e.getMessage());
                    return false;
                }
                // expToNextLevel: 
                try(PreparedStatement nextLevelSql = conn.prepareStatement(newExpToNextLevelSQL)) {
                    if(data[7] instanceof ArrayList nextLvl){ 
                        for(int i=0; i<11;i++){
                            nextLevelSql.setInt(i+1, (Integer)(nextLvl.get(i)));
                        }
                    }
                    if(nextLevelSql.executeUpdate()!=0) System.out.println("New nextLvl added.");
                } catch (SQLException e) {
                    System.out.println("error when creating a nextLvl list: \n" + e.getMessage());
                    return false;
                }
                try(PreparedStatement equipmentSQL = conn.prepareStatement(newPlayerEquipment)) {
                    equipmentSQL.setInt(1, (int)data[0]); // putting id in the equipment sql
                    if(data[3] instanceof String string){  // VSC "AI" helped changed it to a cool switch (the quick fix suggestions)
                        switch (string) {
                            case "Warrior" -> {
                                equipmentSQL.setInt(2, 1); // weapon
                                
                            }
                            case "Ranger" -> {
                                equipmentSQL.setInt(2, 4); // weapon
                            }
                            case "Wizard" -> {
                                equipmentSQL.setInt(2, 7); // weapon
                            }
                            default -> {
                            }
                        }
                        equipmentSQL.setInt(3, 0); // helmet
                        equipmentSQL.setInt(4, 0); // chestplate
                        equipmentSQL.setInt(5, 0); // leggings
                        equipmentSQL.setInt(6, 0); // boots
                        equipmentSQL.setInt(7, 50); //coins - starting with 50 gold
                    }
                    if(equipmentSQL.executeUpdate()!=0) System.out.println("New equipment added.");
                } catch (SQLException e) {
                    System.out.println("error when creating equipment list: \n" + e.getMessage());
                    return false;
                }        

                return true;
            }


            case "Inventory" -> {
                // Inventory - id(userID), item(itemID)
                String sql = "INSERT INTO " + tableName + "(id,item) VALUES(?,?)";
                try(PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setInt(1, (int)data[0]);
                    pstmt.setInt(2, (int)data[1]);
                    pstmt.executeUpdate();
                    System.out.println("New item added to the player's inventory");
                    return true;
                } 
                catch (SQLException e) {
                    System.out.println("error when giving player an item: \n" + e.getMessage());
                    return false;
                }
            }
            case "enemies" -> { // MAKE LATER
            }
            default -> {return false;} //if tablename != any possible case
        }
        // in theory we can add users to the DB 
        return true;
    }

    /**
    * Tries to find a user in the database based on the given fields.
    * @see Example: users, data={system,12345} -> SELECT id FROM users WHERE username=system AND password=12345
    * @param tableName name of the table where the sql will look at
    * @param data An array of the fields (in order) which will be used in the WHERE area of the sql
    * @return The ID of the user if found, or -1 if not found.
    */
    public int findEntryID(String tableName, String[] data){
        switch(tableName){
            case "users" -> {
                // user - id, username, password
                try {
                    String sql = "SELECT id FROM users WHERE username = ? AND password = ?";
                    PreparedStatement prep = conn.prepareStatement(sql);
                    prep.setString(1, data[0]);
                    prep.setString(2, data[1]);
                    ResultSet res = prep.executeQuery(); 
                    if (res.next()) {
                        return res.getInt("id");
                    } else {
                        return -1;
                    }
                } catch(SQLException e){
                    System.out.println("Insert error when finding entry/entries: \n" + e.getMessage());
                    return -1;
                }
            }
            case "players" -> { // MAKE LATER
            }

            case "enemies" -> { // MAKE LATER
            }
            default -> {return -1;} //if tablename != any possible case
        }
        return -1;
    }

    /**
     * finds if there's an entry in the given tablename and the given id
     * @see Example- (playerStats,3) -> will return true if there is a playerStat entry with id of 3. aka does user (with id 3) exist or not
     * @param tableName - looks for the entry in this table
     * @param id - looks for an entry with this id
     * @return 1 if entry exists, 0 if it doesnt, -1 if there's an error
     */
    public int doesEntryExist(String tableName, int id){
        try {
            String sql = "SELECT * FROM " + tableName + " WHERE id = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            ResultSet res = pstmt.executeQuery(); 
            if(res.next()){
                return 1;
            } 
        } catch(SQLException e) {
            System.out.println("error finding table for the entry: \n" + e.getMessage());
            return -1;
        }
        // table exists and entry doesnt exist-
        return 0;
    }

    /**
     * returns the requested field of the given table with the given id
     * pretty self explanitory?
     * @return returns the int\string of the given field, 0 if there was an error\something not found
     */
    public Object getEntryField(int id, String tableName, String field) {
        try {
            String sql = "SELECT " + field + " FROM " + tableName + " WHERE id = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            ResultSet res = pstmt.executeQuery(); 
            if(res.next()){
                return res.getObject(field);
            }
        } catch(SQLException e) {
            System.out.println("Insert error when finding entry/entries: \n" + e.getMessage());
            return 0;
        }
        return 0;
    }

    /**
     * returns ONE entry of a given table name and ID
     */
    public ArrayList<Object> getEntry(int id, String tableName){ // MADE BY AI: used AI in this function as well :( 
        try {
            String sql = "SELECT * FROM " + tableName + " WHERE id = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            ResultSet results = pstmt.executeQuery(); 
            ArrayList<Object> ret = new ArrayList<>();
            if (results.next()) {
                ResultSetMetaData meta = results.getMetaData();
                int columnCount = meta.getColumnCount();
                for (int i = 1; i <= columnCount; i++) {
                    ret.add(results.getObject(i));
                }
            }
            return ret; 
        } catch(SQLException e) {
            System.out.println("error finding table for the entry: \n" + e.getMessage());
            ArrayList<Object> res = new ArrayList<>();
            return res;
        }
    }

    /**
     * @param playerId playerID
     * @param fieldName name of the field
     * @param tableName name of the table
     * @param data the data which will be updated into, deleting the old data
     */
    public void updateFieldByID(int playerId, String tableName, String fieldName, String data) {
        String sql = "UPDATE " + tableName +" SET " + fieldName + " = ? WHERE id = ?";
        try (PreparedStatement editTable = conn.prepareStatement(sql)) {
            editTable.setString(1, data);
            editTable.setInt(2, playerId);
            editTable.executeUpdate();
        } catch (SQLException e) {
            System.out.println("issue when editing field: " + e.getMessage());
        }
    }

    public boolean removeEntry(String tableName){ // MAKE LATER
        return true;
    }

    public int numberOfEntries(String tableName) {
        // will return (N>=0) as the number of entries, -1 if error
        String sql = "SELECT COUNT(*) FROM " + tableName;
        try (Statement state = conn.createStatement();
            ResultSet rs = state.executeQuery(sql)) {
            int count = rs.getInt(1);
            return count;
        } 
        catch (SQLException e) {
            System.out.println("Count error: " + e.getMessage());
            return -1;
        }
    }

    public void disconnect() {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
                System.out.println("Database closed.");
            }
        } 
        catch (SQLException e) {
            System.out.println("Failed to close connection: " + e.getMessage());
        }
    }

    public Enemy getEnemyByID(int enemyID){
        try {
            ArrayList<Object> enemyData = new ArrayList<>();
            String sql = "SELECT * FROM enemies WHERE id = ?";
            PreparedStatement prep = conn.prepareStatement(sql);
            prep.setInt(1, enemyID);
            ResultSet enemyEntry = prep.executeQuery(); 
            if (enemyEntry.next()) {
                // if the entry is not empty -> has fields filled
                enemyData.add(enemyEntry.getObject("name"));
                enemyData.add(enemyEntry.getObject("maxHP"));
                enemyData.add(enemyEntry.getObject("def"));
                enemyData.add(enemyEntry.getObject("dex"));
                enemyData.add(enemyEntry.getObject("agi"));
                enemyData.add(enemyEntry.getObject("accuracy"));
                enemyData.add(enemyEntry.getObject("str"));
                enemyData.add(enemyEntry.getObject("lootTier"));
                enemyData.add(enemyEntry.getObject("attackSpeed"));
            }
            Enemy res = new Enemy(enemyData);
            return res;
        } catch(SQLException e) {
            System.out.println("error finding enemy: \n" + e.getMessage());
            return (Enemy)null;
        }
    }

}
