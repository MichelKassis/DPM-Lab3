package ca.mcgill.ecse211.lab3;






import ca.mcgill.ecse211.sensor.Odometer;
import ca.mcgill.ecse211.sensor.OdometerExceptions;
import lejos.hardware.Button;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.TextLCD;


public class lab3 {

	

	//basic info about my EV3
	public static final double WHEEL_RAD = 2.16;  
	public static final double TRACK = 11.7; 
	public static final int FORWARD_SPEED = 150;
	public static final int ROTATE_SPEED = 150;
	public static final int LOW_SPEED = 100;
	public static final int errorRange = 150;
	public static float OBSTACLEDIST = 100;

	
	
	//declare display and odometer needed
	
	public static Odometer odometer;


	//maps
	
	
	private static int map1x[] = {0,1,2,2,1};                                   
	private static int map1y[] = {2,1,2,1,0};

	private static int map2x[] = {1,0,2,2,1};
	private static int map2y[] = {1,2,2,1,0};
	
	private static int map3x[] = {1,2,2,0,1};
	private static int map3y[] = {0,1,2,2,1};
	
	private static int map4x[] = {0,1,1,2,2};
	private static int map4y[] = {1,2,0,1,2};
			


	public static void main(String[] args) throws OdometerExceptions {
		

		int buttonChoice;

		// Odometer related objects
		odometer = Odometer.getOdometer(Navigation.leftMotor, Navigation.rightMotor, TRACK, WHEEL_RAD); 
		Navigation navigator = new Navigation(map3x,map3y);
		
		 
		// implementation			
		Thread odoThread = new Thread(odometer);
		Thread Avoidance = new Thread();
		Thread Navigator = new Thread(navigator);
		odoThread.setPriority(3);
		Avoidance.setPriority(2);
		Navigator.setPriority(1);
		
		odoThread.start();
		Avoidance.start();
		
		


//		//start
		Button.waitForAnyPress();
		

		navigator.start();



		while (Button.waitForAnyPress() != Button.ID_ESCAPE);
		System.exit(0);

	}
}
