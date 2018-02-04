package ca.mcgill.ecse211.lab3;



import ca.mcgill.ecse211.sensor.Odometer;


import lejos.hardware.ev3.LocalEV3;

import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.motor.EV3MediumRegulatedMotor;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.robotics.SampleProvider;

public class Navigation extends Thread{
	//declare the port occupied by the motor and sensor
		public static final EV3LargeRegulatedMotor leftMotor =
				new EV3LargeRegulatedMotor(LocalEV3.get().getPort("A"));
		public static final EV3LargeRegulatedMotor rightMotor =
				new EV3LargeRegulatedMotor(LocalEV3.get().getPort("B"));
		public static final EV3UltrasonicSensor ultraSensor =
				new EV3UltrasonicSensor(LocalEV3.get().getPort("S1"));
		public static final EV3MediumRegulatedMotor spinMotor= 
				new EV3MediumRegulatedMotor(LocalEV3.get().getPort("S3"));
	
	
	
	
	private static Odometer odo;
	private static SampleProvider us;
	private static float[] usData;
	
	public final double tileSize= 30.48;
	private static int x[];
	private static int y[];





	public Navigation(int X[], int Y[]) {
		x=X;
		y=Y;

	}

	public void run() {
		odo = lab3.odometer;
		//loop all the points
		for (int i = 0; i < x.length; i++) {
			travelto(x[i]*tileSize, y[i]*tileSize);

		}
		//end program
		System.exit(0);

	}

	public void travelto(double i, double j) {

		//initialize
		double direction = 0;
		//find direction and distance in cm
		double deltaX = i-odo.getX();
		double deltaY = j-odo.getY();	
		double distance = Math.sqrt((deltaX*deltaX)+(deltaY*deltaY));
	
		
		//in case deltaY goes to 0 though it has really small possibility to be actually 0
		try{
			direction = Math.toDegrees(Math.atan(deltaX/deltaY));
		}
		catch(ArithmeticException e ){
			
		}
		
		//filter out small error that cause deltaX/deltaY to be negative
		if(deltaY<5&&deltaY>-5){
			if (deltaY<0) {
				direction = 180;
			}
			else{
				direction = 0;
			}
			
		}
		if(deltaY<5&&deltaY>-5){
			if (deltaX<0) {
				direction = -90;
			}
			else{
				direction = 90;
			}	
		}
		
		// if delta X and delta Y both <0, direction+180
		if (!(deltaY<5&&deltaY>-5) && !(deltaY<5&&deltaY>-5) && deltaX<0&&deltaY<0) {
			direction +=180;
		}

		turnTo(direction);
		
		// move forward with the distance calculated
		leftMotor.rotate(convertDistance(lab3.WHEEL_RAD, distance), true);
		rightMotor.rotate(convertDistance(lab3.WHEEL_RAD, distance), false);
		 
	

	}

	public static void turnTo(double theta) {
		
		double turn = theta - odo.getTheta();
		if(turn>180){
			turn -= 360;
		}
		else if(turn<-180){
			turn +=360;
		}
		leftMotor.setSpeed(lab3.ROTATE_SPEED);
		rightMotor.setSpeed(lab3.ROTATE_SPEED);
		
		turn %=360;
		if (turn>0) {
			leftMotor.rotate(convertAngle(lab3.WHEEL_RAD, lab3.TRACK, turn), true);
			rightMotor.rotate(-convertAngle(lab3.WHEEL_RAD, lab3.TRACK, turn), false);
			
		}
		//turn the other way
		else {
			turn = -turn;
			leftMotor.rotate(-convertAngle(lab3.WHEEL_RAD, lab3.TRACK, turn), true);
			rightMotor.rotate(convertAngle(lab3.WHEEL_RAD, lab3.TRACK, turn), false);	
		}
	}

	public static void avoid(){
		
		//get data from the ultrasonic sensor
		us.fetchSample(usData,0);
		float obDist = usData[0];
		
		// if our EV3 is too close to the obstacle
		if(obDist<50){
			turnTo(odo.getTheta()+90);
			leftMotor.rotate(convertDistance(lab3.WHEEL_RAD, 20), true);
			rightMotor.rotate(convertDistance(lab3.WHEEL_RAD, 20), false);
		}
		
	}
	
	
	
	


	//copied from code given in last lab
	  private static int convertDistance(double radius, double distance) {
		    return (int) ((180.0 * distance) / (Math.PI * radius));
		  }

		  public static int convertAngle(double radius, double width, double angle) {
		    return convertDistance(radius, Math.PI * width * angle / 360.0);
		  }

}
