/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/
//testing
package frc.robot;
/*

___
|  \
|   |
|___/       84115
|   \
|    |
|___/
5318008
7177135
84115


*/
import java.util.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Date;
import java.time.format.DateTimeFormatter;  
import java.time.LocalDateTime;   
import edu.wpi.first.wpilibj.Compressor;  
// import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.TimedRobot;
//import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.parent.ControMap;
import frc.parent.RobotMap;
import edu.wpi.first.wpilibj.PneumaticsModuleType;

import java.io.FileWriter;   // Import the FileWriter class
import java.io.IOException;  // Import the IOException class to handle errors
/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the TimedRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the build.gradle file in the
 * project.
 */
public class Robot extends TimedRobot implements ControMap{
  private static final String kDefaultAuto = "Default";
  private static final String kCustomAuto = "My Auto";
  private static final String kResetPIDs = "Reset PIDs";
  private String m_autoSelected;

  
  
  //private final SendableChooser<String> m_chooser = new SendableChooser<>();
  private Compressor c = new Compressor(PneumaticsModuleType.REVPH);


  int alliance;
  double spdmlt = 1;
 
  /**
   * This function is run when the robot is first started up and should be
   * used for any initialization code.
   */
  @Override
  public void robotInit() {
    
   // diagnostics = new Diagnostics2(Chassis.fLeft, Chassis.fRight, Chassis.bLeft, Chassis.bRight, Chassis.climberLeft, Chassis.climberRight);
    // m_chooser.addOption("My Auto", kCustomAuto);
    // m_chooser.setDefaultOption("Default Auto", kDefaultAuto);
    // m_chooser.addOption("Reset PID Values", kResetPIDs);
    // SmartDashboard.putNumber("Distance", 0.0);
    // SmartDashboard.putNumber("Angle", 0.0);
    // SmartDashboard.putData("Auto choices", m_chooser);
    // Chassis.reset();

    switch(DriverStation.getAlliance()){
      case Blue:
        alliance = 1;
      break;

      case Red:
        alliance = 0; 
      break;
      
      
      case Invalid:
        alliance = -1;
      break;
    }
  }
  /**
   * This function is called every robot packet, no matter the mode. Use
   * this for items like diagnostics that you want ran during disabled,
   * autonomous, teleoperated and test.
   *
   * <p>This runs after the mode specific periodic functions, but before
   * LiveWindow and SmartDashboard integrated updating.
   */
  @Override
  public void robotPeriodic() {
    
    
    if(RobotMap.COMPRESSOR_ENABLE)
      c.enableDigital();
    else 
      c.disable();
  }
  
  //Declare and initialize variables for playback
  //private FileWriter myWriter = new FileWriter("filename.txt");

  private List<List<String>> data = new ArrayList<List<String>>(); // Makes the main array
  private int dataIndex = 0; // Stores the place where the array needs to be read
  private boolean [] encodersReached = {false, false, false, false}; // Stores which specific encoders have reached the goal
  public boolean[] encoders;

//stage deez
  /**
   * This autonomous (along with the chooser code above) shows how to select
   * between different autonomous modes using the dashboard. The sendable
   * chooser code works with the Java SmartDashboard. If you prefer the
   * LabVIEW Dashboard, remove all of the chooser code and uncomment the
   * getString line to get the auto name from the text box below the Gyro
   *
   * <p>You can add additional auto modes by adding additional comparisons to
   * the switch structure below with additional strings. If using the
   * SendableChooser make sure to add them to the chooser code above as well.
   */
  @Override
  public void autonomousInit() {
    Chassis.reset();
    // System.out.println("Auto selected: " + m_autoSelected);
    
    // double dist = SmartDashboard.getNumber("Distance", 0);
    // double angl = SmartDashboard.getNumber("Angle", 0);
    // switch (m_autoSelected) {
    //   case kCustomAuto:
    //     break;
    //   case kDefaultAuto:
    //     Chassis.driveDist(dist, 0.05, 0.04, 0.25, false);
    //     Chassis.turnToAngle(angl, 0.005, 0.5, 0.25, false);
    //     break;
    //   case kResetPIDs:
    //     break;
    //   default:
    //     break;
    // }

    //Get latest file
    File dir = new File("test");
    File[] directoryListing = dir.listFiles();
    long latestFile = 0;
    if (directoryListing != null) {
        for (File f : directoryListing) {
//                System.out.println(Long.parseLong(f.getName().substring(0, f.getName().length() - 4)));
            long tempDate = Long.parseLong(f.getName().substring(0, f.getName().length() - 4));
            if(tempDate > latestFile){
                latestFile = tempDate;
            }
        }
    } else {
        // Handle the case where dir is not really a directory.
        // Checking dir.isDirectory() above would not be sufficient
        // to avoid race conditions with another process that deletes
        // directories.
        System.err.println("Path does not exist bruh");
    }

    //Read file
//     try {
//       File f = new File(latestFile+".txt");
//       BufferedReader br = new BufferedReader(new FileReader(f));
//       String line = br.readLine();
// //            System.out.println("The line is :" + line);
//       while (line != null) {
//           // process the line.
// //                System.out.println("Line: "+line);
//           line = br.readLine();
//       }
// //            System.out.println("asdfasdfasdf");
//   } catch (IOException e) {
//       e.printStackTrace();
//   }

  //Old code for autonomous
  //Read file adn put into arraylist
    try {        
      File myObj = new File(latestFile+".txt"); // Selects file
      Scanner myReader = new Scanner(myObj); // Used to read the file
      List<String> data2 = new ArrayList<String>(); // Makes a temporary array
      encoders = Chassis.getEncoderSigns(); // Stores encoder signs
      while (myReader.hasNextLine()) {
        // If the reader has not reached the end of the file:
        String movement = myReader.nextLine(); // Stores the next file line as a string
        data2 = Arrays.asList(movement.split(",")); // Reads the file line string and puts every value into an temporary array
        data.add(data2); // Adds the temporary array into the main array
      }
       myReader.close();
    } catch (FileNotFoundException e) {
      System.out.println("An error finding the file occurred.");
      e.printStackTrace();
    } 
    Chassis.reset();


  }

  /**
   * This function is called periodically during autonomous.
   */
  @Override
  public void autonomousPeriodic() {
    // Stores encoder signs/values temporarily
    double [] tempEncoderValues = Chassis.getEncoderValues();
    boolean [] tempEncoderSigns = Chassis.getEncoderSigns();
    int tempNumReachedGoal = 0;
    if(dataIndex < data.size()){ // Checks if the end of the main array has been reached
      for(int i = 0; i < 4; i++){
        tempNumReachedGoal = 0; // Tracks # of encoders that have reached the goal out of 4
        // tempEncoderValues[i] == 0
        if(tempEncoderValues[i] >= Double.parseDouble(data.get(dataIndex).get(i))){
          // Encoder goal reached
          Chassis.getMotorByIndex(i).set(0); // Stops specific motor
          encodersReached[i] = true; // Tracks specific encoder that has reached goal.
          tempNumReachedGoal++;
        } 
        else if(!encodersReached[i]){
          // Encoder goal not reached, continue moving
          Chassis.getMotorByIndex(i).set(0.5);
        }
        //tempEncoderValues[i];
        data.get(dataIndex).get(i);
        if(tempNumReachedGoal == 4){
          // Moves to the next line of the main array when all 4 encoders have reached the goal
          dataIndex++;
        }
      }
    } else{
      System.out.println("WE DONDE DID ITISIJFD"); // Translation: the file is full
    }

  }


   // private DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");  
  // private LocalDateTime now;  
  // Date date = new Date(); // This object contains the current date value
  // SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
  // System.out.println(formatter.format(date));

  private boolean [] previousEncoderSigns = {true, true, true, true}; 

  // LocalDateTime now = LocalDateTime.now();
  // String datetime = now.format(formatter);
  // File myObj = new File(formatter.format(dat) + ".txt");
  public long timeMilli = 0;
  
  @Override
  public void teleopInit() {
    
      //Write
      try {
        //Create current file
        Files.createDirectories(Paths.get("test"));
        Date date = new Date();
        timeMilli = date.getTime();
        File textObject = new File("test",timeMilli + ".txt");
        textObject.createNewFile();

    } catch (IOException e) {
        e.printStackTrace();
    }
  }

  public boolean recording = false;
  public boolean pressed = false;
  public static CCSparkMax fLeft = Chassis.fLeft;
  public static CCSparkMax fRight = Chassis.fRight;
  public static CCSparkMax bLeft = Chassis.bLeft;
  public static CCSparkMax bRight = Chassis.bRight;
  public void eReset(){
    fLeft.reset();
    fRight.reset();
    bLeft.reset();
    bRight.reset();
  }

 
  


  


  /**
   * This function is called periodically during operator control.
   */
  @Override
  public void teleopPeriodic() {
    // System.out.println("method teleopPeriodic() entry");
    Chassis.axisDrive(OI.axis(ControMap.L_JOYSTICK_VERTICAL),
                      OI.axis(ControMap.R_JOYSTICK_HORIZONTAL), 0.5);

    if(recording && previousEncoderSigns != Chassis.getEncoderSigns()){
      // Writes to the recording file and manages file write errors.
      try {
        //Write Line
        FileWriter myWriter = new FileWriter("test/"+timeMilli+".txt");        
        myWriter.write(encoders[0] + "," + encoders[1] + "," + encoders[2] + "," + encoders[3]);
        myWriter.write("\n");
        myWriter.close();
        System.out.println("Successfully wrote to the file.");
      } 
      catch (IOException e) {
        System.out.println("File write error.");
        e.printStackTrace();
      }
    previousEncoderSigns = Chassis.getEncoderSigns();
    }
    // if()
            
if(Arms.climberCont){
      if (OI.axis(LT) > 0){
        Arms.climberLeftDown();
        
      }
      else if (OI.button(LB_BUTTON)){
        Arms.climberLeftUp();
      }
      if(OI.axis(RT) > 0){
        Arms.climberRightDown();
      } else if(OI.button(RB_BUTTON)){
        Arms.climberRightUp();
      } 
    }
    // Enables and disables recording
    if(OI.button(X_BUTTON)){
      if(!pressed){
        pressed = true;
        recording = !recording;
        if(recording){
          Chassis.reset();
      
        }
      }
    } else {
      pressed = false;
    }
    // Follows the instructions on the recording file
    if(OI.button(Y_BUTTON)){
     
    }
    

    if(OI.button(B_BUTTON)){
      Arms.toggleCont();
      Arms.climbMonkeyBars();
    }

    if(OI.button(A_BUTTON))
      BallDumpy.dumpy.set(true);
    else
      BallDumpy.dumpy.set(false);

   /* //shoot slow with A
    if(OI.button(ControMap.A_BUTTON)){
      Chassis.setFastMode(true);
      Chassis.setFactor(0.048);
    }
    //shoot fast with B
    if (OI.button(ControMap.B_BUTTON)){  
      Chassis.setFastMode(false);
      Chassis.setFactor(0.109);
    }
    //climb with DPad
*/
  
previousEncoderSigns = Chassis.getEncoderSigns(); // Used to compare future encoder signs
  }

  /**
   * This function is called right after disabling
   */
  @Override
  public void disabledInit() {
  }

  /**
   * This function is called periodically during test mode.
   */
  @Override
  public void testPeriodic() {
  }

  }
  

  
  

/*⠀⠀⠀⠀⠀⠀⠀
              ⣠⣤⣤⣤⣤⣤⣶⣦⣤⣄⡀⠀⠀⠀⠀⠀⠀⠀⠀
⠀⠀⠀⠀⠀⠀⠀⠀⢀⣴⣿⡿⠛⠉⠙⠛⠛⠛⠛⠻⢿⣿⣷⣤⡀⠀⠀⠀⠀⠀
⠀⠀⠀⠀⠀⠀⠀⠀⣼⣿⠋⠀⠀⠀⠀⠀⠀⠀⢀⣀⣀⠈⢻⣿⣿⡄⠀⠀⠀⠀
⠀⠀⠀⠀⠀⠀⠀⣸⣿⡏⠀⠀⠀⣠⣶⣾⣿⣿⣿⠿⠿⠿⢿⣿⣿⣿⣄⠀⠀⠀
⠀⠀⠀⠀⠀⠀⠀⣿⣿⠁⠀⠀⢰⣿⣿⣯⠁⠀⠀⠀⠀⠀⠀⠀⠈⠙⢿⣷⡄⠀
⠀⠀⣀⣤⣴⣶⣶⣿⡟⠀⠀⠀⢸⣿⣿⣿⣆⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣿⣷⠀
⠀⢰⣿⡟⠋⠉⣹⣿⡇⠀⠀⠀⠘⣿⣿⣿⣿⣷⣦⣤⣤⣤⣶⣶⣶⣶⣿⣿⣿⠀
⠀⢸⣿⡇⠀⠀⣿⣿⡇⠀⠀⠀⠀⠹⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⡿⠃⠀
⠀⣸⣿⡇⠀⠀⣿⣿⡇⠀⠀⠀⠀⠀⠉⠻⠿⣿⣿⣿⣿⡿⠿⠿⠛⢻⣿⡇⠀⠀
⠀⣿⣿⠁⠀⠀⣿⣿⡇⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢸⣿⣧⠀⠀
⠀⣿⣿⠀⠀⠀⣿⣿⡇⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢸⣿⣿⠀⠀
⠀⣿⣿⠀⠀⠀⣿⣿⡇⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢸⣿⣿⠀⠀
⠀⢿⣿⡆⠀⠀⣿⣿⡇⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢸⣿⡇⠀⠀
⠀⠸⣿⣧⡀⠀⣿⣿⡇⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣿⣿⠃⠀⠀
⠀⠀⠛⢿⣿⣿⣿⣿⣇⠀⠀⠀⠀⣰⣿⣿⣷⣶⣶⣶⣶⠶⠀⢠⣿⣿⠀⠀⠀
⠀⠀⠀⠀⠀⠀⠀⣿⣿⠀⠀⠀⠀⠀⣿⣿⡇⠀⣽⣿⡏⠁⠀⠀⢸⣿⡇⠀⠀⠀
⠀⠀⠀⠀⠀⠀⠀⣿⣿⠀⠀⠀⠀⠀⣿⣿⡇⠀⢹⣿⡆⠀⠀⠀⣸⣿⠇⠀⠀⠀
⠀⠀⠀⠀⠀⠀⠀⢿⣿⣦⣄⣀⣠⣴⣿⣿⠁⠀⠈⠻⣿⣿⣿⣿⡿⠏⠀⠀⠀⠀
⠀⠀⠀⠀⠀⠀⠀⠈⠛⠻⠿⠿⠿⠿⠋⠁⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
*/