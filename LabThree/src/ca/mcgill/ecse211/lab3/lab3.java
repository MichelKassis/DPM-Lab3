package ca.mcgill.ecse211.lab3;


import java.util.ArrayList;

import ca.mcgill.ecse211.lab3.Display;

import ca.mcgill.ecse211.sensor.Odometer;
import ca.mcgill.ecse211.sensor.OdometerExceptions;
import lejos.hardware.Button;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.TextLCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.motor.EV3MediumRegulatedMotor;
import lejos.hardware.port.Port;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.hardware.sensor.SensorModes;
import lejos.robotics.SampleProvider;

public class lab3 {

	

	//basic info about my EV3
	public static final double WHEEL_RAD = 2.2;  
	public static final double TRACK = 14.8; 
	public static final int FORWARD_SPEED = 150;
	public static final int ROTATE_SPEED = 150;
	public static float OBSTACLEDIST = 100;
	
	  private static final EV3LargeRegulatedMotor leftMotor =
		      new EV3LargeRegulatedMotor(LocalEV3.get().getPort("A"));
		  
		  private static final EV3LargeRegulatedMotor rightMotor =
		      new EV3LargeRegulatedMotor(LocalEV3.get().getPort("D"));

	
	
	//declare display and odometer needed
	private static final TextLCD lcd = LocalEV3.get().getTextLCD();
	public static Odometer odometer;


	//maps
	
	
//	private static int xarr[] = {1,2,2,0,1};                                   
//	private static int yarr[] = {0,1,2,2,1};

	private static int map2x[] = {1,0,2,2,1};
	private static int map2y[] = {1,2,2,1,0};


	public static void main(String[] args) throws OdometerExceptions {
		
//		int buttonChoice;
//		// Odometer related objects
//		odometer = Odometer.getOdometer(Navigation.leftMotor, Navigation.rightMotor, TRACK, WHEEL_RAD); 
//		// implementation
//		Display odometryDisplay = new Display(lcd); 
//		Thread odoThread = new Thread(odometer);
//		odoThread.start();
////		Thread odoDisplayThread = new Thread(odometryDisplay);
////		odoDisplayThread.start();
//		Thread navigation = new Thread();
////		//start
//		Button.waitForAnyPress();
//		Navigation navigator = new Navigation(map2x,map2y);
////		UltraSonicSensor ultra = new UltraSonicSensor();
//		navigator.start();
////		ultra.start();
//		while (Button.waitForAnyPress() != Button.ID_ESCAPE);
//		System.exit(0);
		  int buttonChoice;

		    final TextLCD t = LocalEV3.get().getTextLCD();
		    Odometer odometer = Odometer.getOdometer(Navigation.leftMotor, Navigation.rightMotor, TRACK, WHEEL_RAD); 
		    EV3UltrasonicSensor usSensor = new EV3UltrasonicSensor(LocalEV3.get().getPort("S1"));
		    SampleProvider provider = usSensor.getMode("Distance");
		    float [] sample= new float[provider.sampleSize()];
		    Poller usPoller= new Poller(usSensor,provider,sample );
		    OdometryDisplay odometryDisplay = new OdometryDisplay(odometer, t, usPoller);
		   
		    Navigation gps = new Navigation(odometer, usPoller, leftMotor, rightMotor);
		    do {
		      // clear the display
		      t.clear();

		      // ask the user whether the motors should drive in a square or float
		      t.drawString("      Map1 ", 0, 0);
		      t.drawString("     __|__      ", 0, 1);
		      t.drawString("Map3 __|__  Map2 ", 0, 2);
		      t.drawString("       |    ", 0, 3);
		      t.drawString("      Map4  ", 0, 4);

		      buttonChoice = Button.waitForAnyPress();
		    } while (buttonChoice != Button.ID_LEFT && buttonChoice != Button.ID_RIGHT
		    		&&buttonChoice != Button.ID_UP && buttonChoice != Button.ID_DOWN);
		    Thread odoThread = new Thread(odometer);
		    if (buttonChoice == Button.ID_UP) {
		      t.clear();
		      gps.setPath(0,2,1,1,2,2,2,1,1,0);
		      //gps.setPath(1,2); 
		      gps.start();
		      odoThread.start();
		      usPoller.start();
		      odometryDisplay.start();

		    } else if (buttonChoice == Button.ID_RIGHT) {
		      // clear the display
		      t.clear();
		      gps.setPath(1,1,0,2,2,2,2,1,1,0);
		      //gps.setPath(2,2);
		      gps.start();
		      odoThread.start();
		      usPoller.start();
		      odometryDisplay.start();
		   
		    }
		    else if(buttonChoice== Button.ID_LEFT) {
		    	t.clear();
		    	gps.setPath(1,0,2,1,2,2,0,2,1,1);
		    	gps.start();
		    	 odoThread.start();
		    	usPoller.start();
		    	odometryDisplay.start();
		    }
		    else if(buttonChoice==Button.ID_DOWN) {
		    	t.clear();
		    	gps.setPath(0,1,1,2,1,0,2,1,2,2);
		    	gps.start();
		    	odoThread.start();
		    	usPoller.start();
		    	odometryDisplay.start();
		    }

		    while (Button.waitForAnyPress() != Button.ID_ESCAPE);
		    System.exit(0);

	}
}
