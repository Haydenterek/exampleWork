import java.sql.*;
import java.util.Scanner;


public class ArborDB {
    private static Connection conn;

    private boolean connect(String username, String password) {
        // Connect to postgresql database with username and password
        // database is at localhost:5432
        try {
            Class.forName("org.postgresql.Driver");
            conn = DriverManager.getConnection(
                    "jdbc:postgresql://localhost:5432/", username, password);
            System.out.println("Connected to database");
            return true;
        } catch (Exception e) {
            System.out.println("Connection failure.");
            System.out.println(e.getMessage());
            return false;
        }

    }

    //COMPLETED
    private boolean addForest(String name, int area, float acid_level,
                              float MBR_xMin, float MBR_xMax, float MBY_yMin, float MBR_yMax) {
        /*
            Call newForest function in database with given parameters
            Needs to be a transaction
        */
        try {
            // Create a prepared statement with transaction
            PreparedStatement stmt = conn.prepareStatement(
                    "BEGIN TRANSACTION; CALL ArborDB.addForest(?, ?, ?, ?, ?, ?, ?); COMMIT; "
            );

            // Set parameters
            stmt.setString(1, name);
            stmt.setInt(2, area);
            stmt.setFloat(3, acid_level);
            stmt.setFloat(4, MBR_xMin);
            stmt.setFloat(5, MBR_xMax);
            stmt.setFloat(6, MBY_yMin);
            stmt.setFloat(7, MBR_yMax);

            // Execute statement
            stmt.execute();

            // Statement executed successfully return true
            return true;
        } catch (SQLException e) {
            System.out.println("Error adding forest.");
            System.out.println(e.getSQLState());

            // Statement failed to execute return false
            return false;
        }
    }

    //COMPLETED
    private boolean addTreeSpecies(String genus, String epithet, float idealTemp, float largestHeight, String RLF) {
        /*
            Call newTreeSpecies function in database with given parameters
            Needs to be a transaction
        */
        try {
            // Create a prepared statement with transaction
            PreparedStatement stmt = conn.prepareStatement(
                    "BEGIN TRANSACTION; CALL ArborDB.addTreeSpecies(?, ?, ?, ?, ?); COMMIT; "
            );

            // Set parameters
            stmt.setString(1, genus);
            stmt.setString(2, epithet);
            stmt.setFloat(3, idealTemp);
            stmt.setFloat(4, largestHeight);
            stmt.setString(5, RLF);

            // Execute statement
            stmt.execute();

            // Statement executed successfully return true
            return true;
        } catch (SQLException e) {
            System.out.println("Error adding tree species.");
            System.out.println(e.getSQLState());

            // Statement failed to execute return false
            return false;
        }
    }

    //COMPLETED
    private boolean addSpeciesToForest(int forestNo, String genus, String epithet) {
        /*
            Call addSpeciesToForest function in database with given parameters
            Needs to be a transaction
        */
        try {
            // Create a prepared statement with transaction
            PreparedStatement stmt = conn.prepareStatement(
                    "BEGIN TRANSACTION; CALL ArborDB.addSpeciesToForest(?, ?, ?); COMMIT; "
            );

            // Set parameters
            stmt.setInt(1, forestNo);
            stmt.setString(2, genus);
            stmt.setString(3, epithet);

            // Execute statement
            stmt.execute();

            // Statement executed successfully return true
            return true;
        } catch (SQLException e) {
            System.out.println("Error adding species to forest.");
            System.out.println(e.getSQLState());

            // Statement failed to execute return false
            return false;
        }
    }


    //COMPLETED
    private boolean addWorker(String SSN, String firstName, String lastName, char middleInitial, String rank, String abbreviation) {
        /*
            Call newWorker function in database with given parameters
            Needs to be a transaction
        */
        try {
            // Create a prepared statement with transaction
            PreparedStatement stmt = conn.prepareStatement(
                    "BEGIN TRANSACTION; CALL ArborDB.newWorker(?, ?, ?, ?, ?, ?); COMMIT; "
            );

            // Set parameters
            stmt.setString(1, SSN);
            stmt.setString(2, firstName);
            stmt.setString(3, lastName);
            stmt.setString(4, Character.toString(middleInitial));
            stmt.setString(5, rank);
            stmt.setString(6, abbreviation);

            // Execute statement
            stmt.execute();

            // Statement executed successfully return true
            return true;
        } catch (SQLException e) {
            System.out.println("Error adding worker.");
            System.out.println(e.getSQLState());

            // Statement failed to execute return false
            return false;
        }

    }

    //COMPLETED
    private boolean employWorkerToState(String abbreviation, String SSN) {
        /*
            Call employWorkerToState function in database with given parameters
            Needs to be a transaction
        */
        try {
            // Create a prepared statement with transaction
            PreparedStatement stmt = conn.prepareStatement(
                    "BEGIN TRANSACTION; CALL ArborDB.employWorkerToState(?, ?); COMMIT; "
            );

            // Set parameters
            stmt.setString(1, abbreviation);
            stmt.setString(2, SSN);

            // Execute statement
            stmt.execute();

            // Statement executed successfully return true
            return true;
        } catch (SQLException e) {
            System.out.println("Error employing worker to state.");
            System.out.println(e.getSQLState());

            // Statement failed to execute return false
            return false;
        }
    }

    //Duplicate PK error?
    private boolean placeSensor(int energy, float x, float y, String maintainer_id) {
        /*
            Call placeSensor function in database with given parameters
            Needs to be a transaction
        */
        try {
            // Create a prepared statement with transaction
            PreparedStatement stmt = conn.prepareStatement(
                    "BEGIN TRANSACTION; CALL ArborDB.placeSensor(?, ?, ?, ?); COMMIT; "
            );

            // Set parameters
            stmt.setInt(1, energy);
            stmt.setFloat(2, x);
            stmt.setFloat(3, y);
            stmt.setString(4, maintainer_id);

            // Execute statement
            stmt.execute();

            // Statement executed successfully return true
            return true;
        } catch (SQLException e) {
            System.out.println("Error placing sensor.");
            System.out.println(e.getSQLState());
            System.out.println(e);

            // Statement failed to execute return false
            return false;
        }
    }

    //COMPLETED
    private boolean generateReport(int sensorID, Timestamp reportTime, float temperature) {
        /*
            Call generateReport function in database with given parameters
            Needs to be a transaction
        */
        try {
            // Create a prepared statement with transaction
            PreparedStatement stmt = conn.prepareStatement(
                    "BEGIN TRANSACTION; CALL ArborDB.generateReport(?, ?, ?); COMMIT; "
            );


            // Set parameters
            stmt.setInt(1, sensorID);
            stmt.setTimestamp(2, reportTime);
            stmt.setFloat(3, temperature);

            // Execute statement
            stmt.execute();

            // Statement executed successfully return true
            return true;
        } catch (SQLException e) {
            System.out.println("Error generating report.");
            System.out.println(e.getSQLState());

            // Statement failed to execute return false
            return false;
        }
    }

    //COMPLETED
    private boolean removeSpeciesFromForest(String genus, String epithet, int forestNo) {
        /*
            Call removeSpeciesFromForest function in database with given parameters
            Needs to be a transaction
        */
        try {
            // Create a prepared statement with transaction
            PreparedStatement stmt = conn.prepareStatement(
                    "BEGIN TRANSACTION; CALL ArborDB.removeSpeciesFromForest(?, ?, ?); COMMIT; "
            );

            // Set parameters
            stmt.setString(1, genus);
            stmt.setString(2, epithet);
            stmt.setInt(3, forestNo);

            // Execute statement
            stmt.execute();

            // Statement executed successfully return true
            return true;
        } catch (SQLException e) {
            System.out.println("Error removing species from forest.");
            System.out.println(e.getSQLState());

            // Statement failed to execute return false
            return false;
        }
    }

    //COMPLETED
    private boolean deleteWorker(String SSN) {
        /*
            Call deleteWorker function in database with given parameters
            Needs to be a transaction
        */
        try {
            // Create a prepared statement with transaction
            PreparedStatement stmt = conn.prepareStatement(
                    "BEGIN TRANSACTION; CALL ArborDB.deleteWorker(?); COMMIT; "
            );

            // Set parameters
            stmt.setString(1, SSN);

            // Execute statement
            stmt.execute();

            // Statement executed successfully return true
            return true;
        } catch (SQLException e) {
            System.out.println("Error deleting worker.");
            System.out.println(e.getSQLState());

            // Statement failed to execute return false
            return false;
        }
    }

    //Completed
    private boolean moveSensor(int sensorID, float x, float y) {
        /*
            Call moveSensor function in database with given parameters
            Needs to be a transaction
        */
        try {
            // Create a prepared statement with transaction
            PreparedStatement stmt = conn.prepareStatement(
                    "BEGIN TRANSACTION; CALL ArborDB.moveSensor(?, ?, ?); COMMIT; "
            );

            // Set parameters
            stmt.setInt(1, sensorID);
            stmt.setFloat(2, x);
            stmt.setFloat(3, y);

            // Execute statement
            stmt.execute();

            // Statement executed successfully return true
            return true;
        } catch (SQLException e) {
            System.out.println("Error moving sensor.");
            System.out.println(e.getSQLState());

            // Statement failed to execute return false
            return false;
        }
    }

    //COMPLETED
    private boolean removeWorkerFromState(String SSN, String abbreviation) {
        /*
            Call removeWorkerFromState function in database with given parameters
            Needs to be a transaction
        */
        try {
            // Create a prepared statement with transaction
            PreparedStatement stmt = conn.prepareStatement(
                    "BEGIN TRANSACTION; CALL ArborDB.removeWorkerFromState(?, ?); COMMIT; "
            );

            // Set parameters
            stmt.setString(1, SSN);
            stmt.setString(2, abbreviation);

            // Execute statement
            stmt.execute();

            // Statement executed successfully return true
            return true;
        } catch (SQLException e) {
            System.out.println("Error removing worker from state.");
            System.out.println(e.getSQLState());

            // Statement failed to execute return false
            return false;
        }
    }

    //COMPLETED
    private boolean removeSensor(boolean deleteAll, Scanner scanner) {
        /*
            Call removeSensor function in database with given parameters
            Needs to be a transaction
        */
        try {
            PreparedStatement stmt;
            if (deleteAll) {
                // Create a prepared statement with transaction
                stmt = conn.prepareStatement(
                        "BEGIN TRANSACTION; CALL ArborDB.removeAllSensors(); COMMIT; "
                );

                // Execute statement
                stmt.execute();

            }
            else {
                /*
                    * Call ArborDB.listAllSensors() to get a list of all sensors
                    * Iterate through each sensor printing out information
                    * Prompt user to enter sensor ID to delete
                    * If user enters 0 don't delete and go to next sensor
                    * If user enters -1 return to menu
                    * If user enters valid sensor ID delete sensor and go to next sensor
                */

                // Get list of all sensors
                stmt = conn.prepareStatement(
                        "select * from ArborDB.sensor;");
                ResultSet set = stmt.executeQuery();

                // If sensor list is empty return to menu
                while (set.next()) {
                    printGeneralSensorInfo(set);

                    System.out.println("Delete ? Enter ID for yes, 0 for no, -1 to return to menu -> ");
                    int sensorID = Integer.parseInt(scanner.nextLine().trim());

                    if (sensorID != 0) {
                        if (sensorID == set.getInt("sensor_id")) {
                            // Create a prepared statement with transaction
                            stmt = conn.prepareStatement(
                                    "BEGIN TRANSACTION; CALL ArborDB.removeSensor(?); COMMIT; "
                            );

                            // Set parameters
                            stmt.setInt(1, sensorID);

                            // Execute statement
                            stmt.execute();
                        } else if (sensorID == -1) {
                            break;
                        } else {
                            System.out.println("Invalid input. Returning to menu.");
                            break;
                        }
                    }
                }
            }
            return true;
        } catch (SQLException e) {
            System.out.println("Error removing sensor.");
            System.out.println(e.getSQLState());

            // Statement failed to execute return false
            return false;
        }
    }

    //COMPLETED
    private boolean listSensors(int forestID) {
        try {
            PreparedStatement stmt = conn.prepareStatement(
                    "select * from ArborDB.listSensors(?);");
            stmt.setInt(1, forestID);
            ResultSet set = stmt.executeQuery();

            System.out.println("List of Forest #" + forestID + " sensors:");

            while(set.next()) {
                printGeneralSensorInfo(set);
                System.out.print("Maintainer ID: " + set.getString("maintainer_id") + "\n");
            }

            return true;
        }
        catch(SQLException e) {
            System.out.println("Error getting list of sensors for forest " + forestID);
            System.out.println(e.getSQLState());

            return false;
        }
    }

    //COMPLETED
    private boolean listMaintainedSensors(String SSN) {
        /*
            Call listMaintainedSensors function in database with given parameters

            listMaintainedSensors returns a table of values with column types:
            sensor_id int
            last_charged timestamp
            energy int
            last_read timestamp
            x double precision
            y double precision
            maintainer_id String

            Does not need to be a transaction
        */

        try {
            PreparedStatement stmt = conn.prepareStatement(
                    "select * from ArborDB.listMaintainedSensors(?);");
            stmt.setString(1, SSN);
            ResultSet set = stmt.executeQuery();

            System.out.println("List of sensors maintained by worker #" + SSN + ":\n");

            while(set.next()) {
                printGeneralSensorInfo(set);
            }

            return true;
        }
        catch(SQLException e) {
            System.out.println("Error getting list of sensors for worker number #" + SSN);
            System.out.println(e.getSQLState());

            return false;
        }

    }

    //COMPLETED
    private boolean locateTreeSpecies(String genus, String epithet) {
        /*
            Call locateTreeSpecies function in database with given parameters

            locateTreeSpecies returns a table of values with column types:
            forest_id int
            name String
            area int
            acid_level double precision
            MBR_xMin double precision
            MBR_xMax double precision
            MBR_yMin double precision
            MBR_yMax double precision

            Does not need to be a transaction
        */

        try {
            PreparedStatement stmt = conn.prepareStatement(
                    "select * from ArborDB.locateTreeSpecies(?, ?);");
            stmt.setString(1, genus);
            stmt.setString(2,epithet);
            ResultSet set = stmt.executeQuery();

            System.out.println("Forest Where Tree Species is Located:" );

            while(set.next()) {
                System.out.print("\nForest ID: " + set.getInt("forest_no"));
                System.out.print("\nForest Name: " + set.getString("name"));
                System.out.print("\nArea: " + set.getInt("area"));
                System.out.print("\nAcid Level: " + set.getDouble("acid_level"));
                System.out.print("\nMBR X Min: " + set.getDouble("MBR_XMin"));
                System.out.print("\nMBR X Max: " + set.getDouble("MBR_XMax"));
                System.out.print("\nMBR Y Min: " + set.getDouble("MBR_YMin"));
                System.out.print("\nMBR Y Max: " + set.getDouble("MBR_YMax"));
            }

            return true;
        }
        catch(SQLException e) {
            System.out.println("Error finding tree species with genus " + genus + " and epithet " + epithet);
            System.out.println(e.getSQLState());

            return false;
        }
    }

    //COMPLETED
    private boolean rankForestSenors() {
        /*
            Select * From ArborDB.RankForestSensors View

            Returns a table of values with column types:
            forest_id int
            name String
            COUNT(sensor_id) int
        */

        try{
            PreparedStatement stmt = conn.prepareStatement(
                    "select * from ArborDB.RankForestSensors;");
            ResultSet set = stmt.executeQuery();

            System.out.println("Forests ranked by amount of sensors they contain:");

            while(set.next()) {
                System.out.print("\nForest Number: " + set.getInt("forest_no"));
                System.out.print("\nForest Name: " + set.getString("name"));
                System.out.print("\nSensor Count: " + set.getString("sensor_count") + "\n");
            }

            return true;

        }
        catch(SQLException e){
            System.out.println("There was an error ranking the sensors in the forests.");
            System.out.println(e.getSQLState());
            return false;

        }
    }

    //COMPLETED
    private boolean habitableEnvironments(String genus, String epithet, int k) {
        /*
            Call habitableEnvironments function in database with given parameters

            Returns a table of values with column types:
            forest_id int
            name String
            genus String
            epithet String
            temperature double precision
        */

        try {
            int num = 0;

            PreparedStatement stmt = conn.prepareStatement(
                    "select * from ArborDB.habitableEnvironments(?,?,?);");
            stmt.setString(1, genus);
            stmt.setString(2,epithet);
            stmt.setInt(3,k);
            ResultSet set = stmt.executeQuery();

            /* 
            
            if(!set.next()){
                System.out.println("No habitable environments.");
                return true;
            }
            */

            while(set.next()) {
                num += 1;
                System.out.print("\nForest Number: " + set.getInt("forest_no_f"));
                System.out.print("\nForest Name: " + set.getString("forest_name"));
                System.out.print("\nForest Genus: " + set.getString("genus_f"));
                System.out.print("\nForest Epithet: " + set.getString("epithet_f"));
                System.out.print("\nTemperature: " + set.getFloat("temp_f") + "\n");

            }

            if(num == 0)
                System.out.println("No Habitable environments.");

            return true;
        }
        catch(SQLException e) {
            System.out.println("Error listing habitable environments.");
            System.out.println(e.getSQLState());
            System.out.println(e);
            return false;
        }
    }

    private boolean topSensors(int sensorNum, int months) {
        try {
            PreparedStatement stmt = conn.prepareStatement(
                    "select * from ArborDB.topSensors(?,?);");
            stmt.setInt(1, sensorNum);
            stmt.setInt(2, months);
            ResultSet set = stmt.executeQuery();

            while(set.next()) {
                System.out.println("Sensor ID: " + set.getInt("sensor_id"));
                System.out.println("Count of Reports: " + set.getLong("count"));
            }

            return true;

        }
        catch(SQLException e) {
            System.out.println("Could not list top sensors.");
            System.out.println(e.getSQLState());
            return false;
        }
    }

    //COMPLETED
    private boolean threeDegrees(int forestNumOne, int forestNumTwo) {
        /*
            Call threeDegrees function in database with given parameters

            Returns a string of the path between the two forests

        */

        try {
            PreparedStatement stmt = conn.prepareStatement(
                    "select * from ArborDB.threeDegrees(?,?);");
            stmt.setInt(1, forestNumOne);
            stmt.setInt(2, forestNumTwo);
            ResultSet set = stmt.executeQuery();

            System.out.println("Hopping from " + forestNumOne + " to " + forestNumTwo);

            while(set.next()) {
                System.out.println("\nHops: " + set.getString(1)+ "\n");
            }

            return true;
        }
        catch(SQLException e) {
            System.out.println("Could not find best path for forests");
            System.out.println(e.getSQLState());
            return false;
        }
    }

    private boolean disconnect() {
        // Disconnect from database
        try {
            conn.close();
            System.out.println("Disconnected from database");
            return true;
        } catch (SQLException e) {
            System.out.println("Error disconnecting from database");
            System.out.println(e.getSQLState());
            return false;
        }
    }

    public static void main(String[] args) {
        ArborDB db = new ArborDB();
        Scanner scanner = new Scanner(System.in);

        // Put Functions in array for easy access
        Functions[] Functions = ArborDB.Functions.values();

        while(true) {
            // Connect to database
            System.out.print("Enter username -> ");
            String username = scanner.nextLine().trim();

            System.out.print("Enter password -> ");
            String password = scanner.nextLine().trim();

            if(db.connect(username, password)) {
                while(true) {
                    db.printMenu();
                    System.out.print("Enter Number -> ");
                    int choice = Integer.parseInt(scanner.nextLine().trim());

                    if (choice == 20) {
                        if (Functions[19].execute(db, scanner)) {
                            System.out.print("Quit or Load New Database? (q/l) -> ");
                            String quitOrLoad = scanner.nextLine().trim();
                            if (quitOrLoad.equals("q")) {
                                return;
                            }
                            else if (quitOrLoad.equals("l")) {
                                break;
                            }
                            else {
                                System.out.println("Invalid input. Quitting.");
                                return;
                            }
                        }
                        continue;
                    }

                    if (Functions[choice - 1].execute(db, scanner)) {
                        System.out.println("Function executed successfully.");
                    }
                    else {
                        System.out.println("Function failed to execute.");
                    }
                }
            }
        }
    }

    // Extra Functions
    private void printGeneralSensorInfo(ResultSet set) {
        try {
            System.out.println("Sensor ID: " + set.getInt("sensor_id"));
            System.out.println("Last Charged: " + set.getTimestamp("last_charged"));
            System.out.println("Energy: " + set.getInt("energy"));
            System.out.println("Last Read: " + set.getTimestamp("last_read"));
            System.out.println("X Coordinate: " + set.getDouble("X"));
            System.out.println("Y Coordinate: " + set.getDouble("Y"));
        }
        catch(SQLException e) {
            System.out.println("Error printing general sensor info.");
            System.out.println(e.getSQLState());
        }
    }

    private void printMenu() {
        /*
        System.out.println("""
                1. Add Forest
                2. Add Tree Species
                3. Add Species to Forest
                4. Add Worker
                5. Employ Worker to State
                6. Place Sensor
                7. Generate Report
                8. Remove Species from Forest
                9. Delete Worker
                10. Move Sensor
                11. Remove Worker from State
                12. Remove Sensor
                13. List Sensors
                14. List Maintained Sensors
                15. Locate Tree Species
                16. Rank Forest Sensors
                17. Habitable Environments
                18. Top Sensors
                19. Three Degrees
                20. Quit
                """);
                */
    }

    private static boolean listAllSensors(ResultSet set) {
        int num = 0;

        try {
            while(set.next()) {
                num += 1;
                System.out.println(num + ". " + "Sensor ID: " + set.getInt("sensor_id"));
            }

            return num == 0;
        }
        catch(SQLException e) {
            System.out.println("Error printing general sensor info.");
            System.out.println(e.getSQLState());

            return false;
        }
    }

    public enum Functions {
        ADD_FOREST  {
            public boolean execute(ArborDB db, Scanner scanner) {
                System.out.print("Enter Forest Name -> ");
                String name = scanner.nextLine().trim();
                System.out.print("Enter Forest Area -> ");
                int area = Integer.parseInt(scanner.nextLine().trim());
                System.out.print("Enter Forest Acid Level -> ");
                float acid_level = Float.parseFloat(scanner.nextLine().trim());
                System.out.print("Enter Forest MBR X Min -> ");
                float MBR_xMin = Float.parseFloat(scanner.nextLine().trim());
                System.out.print("Enter Forest MBR X Max -> ");
                float MBR_xMax = Float.parseFloat(scanner.nextLine().trim());
                System.out.print("Enter Forest MBR Y Min -> ");
                float MBR_yMin = Float.parseFloat(scanner.nextLine().trim());
                System.out.print("Enter Forest MBR Y Max -> ");
                float MBR_yMax = Float.parseFloat(scanner.nextLine().trim());

                return db.addForest(name, area, acid_level, MBR_xMin, MBR_xMax, MBR_yMin, MBR_yMax);
            }
        },
        ADD_TREE_SPECIES {
            public boolean execute(ArborDB db, Scanner scanner) {
                System.out.print("Enter Tree Species Genus -> ");
                String genus = scanner.nextLine().trim();
                System.out.print("Enter Tree Species Epithet -> ");
                String epithet = scanner.nextLine().trim();
                System.out.print("Enter Tree Species Ideal Temperature -> ");
                float idealTemp = Float.parseFloat(scanner.nextLine().trim());
                System.out.print("Enter Tree Species Largest Height -> ");
                float largestHeight = Float.parseFloat(scanner.nextLine().trim());
                System.out.print("Enter Tree Species RLF -> ");
                String RLF = scanner.nextLine().trim();

                return db.addTreeSpecies(genus, epithet, idealTemp, largestHeight, RLF);
            }
        },
        ADD_SPECIES_TO_FOREST {
            public boolean execute(ArborDB db, Scanner scanner) {
                System.out.print("Enter Forest Number -> ");
                int forestNo = Integer.parseInt(scanner.nextLine().trim());
                System.out.print("Enter Tree Species Genus -> ");
                String genus = scanner.nextLine().trim();
                System.out.print("Enter Tree Species Epithet -> ");
                String epithet = scanner.nextLine().trim();

                return db.addSpeciesToForest(forestNo, genus, epithet);
            }
        },
        ADD_WORKER {
            public boolean execute(ArborDB db, Scanner scanner) {
                System.out.print("Enter Worker SSN -> ");
                String SSN = scanner.nextLine().trim();
                System.out.print("Enter Worker First Name -> ");
                String firstName = scanner.nextLine().trim();
                System.out.print("Enter Worker Last Name -> ");
                String lastName = scanner.nextLine().trim();
                System.out.print("Enter Worker Middle Initial -> ");
                char middleInitial = scanner.nextLine().trim().charAt(0);
                System.out.print("Enter Worker Rank -> ");
                String rank = scanner.nextLine().trim();
                System.out.print("Enter Worker State Abbreviation -> ");
                String abbreviation = scanner.nextLine().trim();

                return db.addWorker(SSN, firstName, lastName, middleInitial, rank, abbreviation);
            }
        },
        EMPLOY_WORKER_TO_STATE {
            public boolean execute(ArborDB db, Scanner scanner) {
                System.out.print("Enter Worker State Abbreviation -> ");
                String abbreviation = scanner.nextLine().trim();
                System.out.print("Enter Worker SSN -> ");
                String SSN = scanner.nextLine().trim();

                return db.employWorkerToState(abbreviation, SSN);
            }
        },
        PLACE_SENSOR {
            public boolean execute(ArborDB db, Scanner scanner) {
                System.out.print("Enter Sensor Energy -> ");
                int energy = Integer.parseInt(scanner.nextLine().trim());
                System.out.print("Enter Sensor X Coordinate -> ");
                float x = Float.parseFloat(scanner.nextLine().trim());
                System.out.print("Enter Sensor Y Coordinate -> ");
                float y = Float.parseFloat(scanner.nextLine().trim());
                System.out.print("Enter Sensor Maintainer ID -> ");
                String maintainer_id = scanner.nextLine().trim();

                return db.placeSensor(energy, x, y, maintainer_id);
            }
        },
        GENERATE_REPORT {
            public boolean execute(ArborDB db, Scanner scanner) {
                boolean empty = false;

                try{
                    PreparedStatement stmt = conn.prepareStatement(
                            "select * from ArborDB.sensor");
                    ResultSet set = stmt.executeQuery();

                    System.out.println("List of Sensors: "); //Needed from doc
                    empty = listAllSensors(set);
                }
                catch(SQLException e) {
                    System.out.println("Error listing sensors: " + e);
                }

                if(!empty) {
                    System.out.println("Enter -1 to return back to menu.");
                    System.out.print("Enter Sensor ID -> ");
                    int sensorID = Integer.parseInt(scanner.nextLine().trim());
                    if(sensorID == -1) //Needed from doc
                        return true;

                    System.out.print("Enter Report Time -> ");
                    Timestamp reportTime = Timestamp.valueOf(scanner.nextLine().trim());
                    System.out.print("Enter Temperature -> ");
                    float temperature = Float.parseFloat(scanner.nextLine().trim());

                    return db.generateReport(sensorID, reportTime, temperature);
                }
                else{
                    System.out.println("There are no sensors currently deployed");
                    return true;
                }
            }
        },
        REMOVE_SPECIES_FROM_FOREST {
            public boolean execute(ArborDB db, Scanner scanner) {
                System.out.print("Enter Tree Species Genus -> ");
                String genus = scanner.nextLine().trim();
                System.out.print("Enter Tree Species Epithet -> ");
                String epithet = scanner.nextLine().trim();
                System.out.print("Enter Forest Number -> ");
                int forestNo = Integer.parseInt(scanner.nextLine().trim());

                return db.removeSpeciesFromForest(genus, epithet, forestNo);
            }
        },
        DELETE_WORKER {
            public boolean execute(ArborDB db, Scanner scanner) {
                System.out.print("Enter Worker SSN -> ");
                String SSN = scanner.nextLine().trim();

                return db.deleteWorker(SSN);
            }
        },
        MOVE_SENSOR {
            public boolean execute(ArborDB db, Scanner scanner) {
                //Need to check if there are any sensors to move
                try{
                    PreparedStatement stmt = conn.prepareStatement(
                            "select * from ArborDB.sensor;");
                    ResultSet set = stmt.executeQuery();

                    if(!set.next()) {
                        System.out.println("There are no sensors to move.");
                        return true;
                    }
                }
                catch(SQLException e){
                    System.out.println("Error checking sensors in move sensor method.");
                }

                System.out.print("Enter Sensor ID -> ");
                int sensorID = Integer.parseInt(scanner.nextLine().trim());
                if(sensorID == -1) //Needed from lab doc
                    return true;

                System.out.print("Enter Sensor X Coordinate -> ");
                float x = Float.parseFloat(scanner.nextLine().trim());
                System.out.print("Enter Sensor Y Coordinate -> ");
                float y = Float.parseFloat(scanner.nextLine().trim());

                return db.moveSensor(sensorID, x, y);
            }
        },
        REMOVE_WORKER_FROM_STATE {
            public boolean execute(ArborDB db, Scanner scanner) {
                System.out.print("Enter Worker SSN -> ");
                String SSN = scanner.nextLine().trim();
                System.out.print("Enter Worker State Abbreviation -> ");
                String abbreviation = scanner.nextLine().trim();

                return db.removeWorkerFromState(SSN, abbreviation);
            }
        },
        REMOVE_SENSOR {
            public boolean execute(ArborDB db, Scanner scanner) {
                System.out.print("Delete All? [y|n] -> ");
                String deleteAll = scanner.nextLine().trim();

                boolean deleteAllBool = deleteAll.equals("y");

                return db.removeSensor(deleteAllBool, scanner);
            }
        },
        LIST_SENSORS {
            public boolean execute(ArborDB db, Scanner scanner) {
                System.out.print("Enter Forest ID -> ");
                int forestID = Integer.parseInt(scanner.nextLine().trim());

                return db.listSensors(forestID);
            }
        },
        LIST_MAINTAINED_SENSORS {
            public boolean execute(ArborDB db, Scanner scanner) {
                System.out.print("Enter Worker SSN -> ");
                String SSN = scanner.nextLine().trim();

                return db.listMaintainedSensors(SSN);
            }
        },
        LOCATE_TREE_SPECIES {
            public boolean execute(ArborDB db, Scanner scanner) {
                System.out.print("Enter Tree Species Genus -> ");
                String genus = scanner.nextLine().trim();
                System.out.print("Enter Tree Species Epithet -> ");
                String epithet = scanner.nextLine().trim();

                return db.locateTreeSpecies(genus, epithet);
            }
        },
        RANK_FOREST_SENSORS {
            public boolean execute(ArborDB db, Scanner scanner) {
                try{
                    PreparedStatement stmt = conn.prepareStatement(
                            "select * from ArborDB.forest;");
                    ResultSet set = stmt.executeQuery();

                    if(!set.next()){
                        System.out.println("There are no forests to rank.");
                        return true;
                    }
                }
                catch(SQLException e) {
                    System.out.println("Error checking if forests had data.");
                }

                return db.rankForestSenors();
            }
        },
        HABITABLE_ENVIRONMENTS {
            public boolean execute(ArborDB db, Scanner scanner) {
                System.out.print("Enter Tree Species Genus -> ");
                String genus = scanner.nextLine().trim();
                System.out.print("Enter Tree Species Epithet -> ");
                String epithet = scanner.nextLine().trim();
                System.out.print("Enter K -> ");
                int k = Integer.parseInt(scanner.nextLine().trim());

                return db.habitableEnvironments(genus, epithet, k);
            }
        },
        TOP_SENSORS{
            public boolean execute(ArborDB db, Scanner scanner) {
                System.out.print("Enter Number of Sensors to View -> ");
                int sensorNum = Integer.parseInt(scanner.nextLine().trim());
                System.out.print("How Many Months Ago Would You Like to View -> ");
                int months = Integer.parseInt(scanner.nextLine().trim());

                return db.topSensors(sensorNum, months);
            }
        },
        THREE_DEGREES {
            public boolean execute(ArborDB db, Scanner scanner) {
                System.out.print("Enter Forest Number One -> ");
                int forestNumOne = Integer.parseInt(scanner.nextLine().trim());
                System.out.print("Enter Forest Number Two -> ");
                int forestNumTwo = Integer.parseInt(scanner.nextLine().trim());

                return db.threeDegrees(forestNumOne, forestNumTwo);
            }
        },
        QUIT {
            public boolean execute(ArborDB db, Scanner scanner) {
                return db.disconnect();
            }
        };

        public abstract boolean execute(ArborDB db, Scanner scanner);
    }
}



