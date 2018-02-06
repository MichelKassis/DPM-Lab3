package ca.mcgill.ecse211.lab3;



import ca.mcgill.ecse211.sensor.Odometer;
import ca.mcgill.ecse211.sensor.UltrasonicPoller;
import lejos.hardware.Sound;
import lejos.hardware.ev3.LocalEV3;

import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.motor.EV3MediumRegulatedMotor;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.robotics.SampleProvider;

public class Navigation extends Thread{
	//declare the port occupied by the motor and sensor
		public static EV3LargeRegulatedMotor leftMotor =
				new EV3LargeRegulatedMotor(LocalEV3.get().getPort("A"));
		public static EV3LargeRegulatedMotor rightMotor =
				new EV3LargeRegulatedMotor(LocalEV3.get().getPort("B"));

//		public static final EV3MediumRegulatedMotor spinMotor= 
//				new EV3MediumRegulatedMotor(LocalEV3.get().getPort("S3"));
//	
	
	
	
	private static Odometer odo;

		
	public final double tileSize = 30.48;
	private static boolean isNavigating;
	private static double x[];
	private static double y[];
	private int usData;
	private Poller poller;
	private boolean avoiding;





	public Navigation(Odometer odometer, Poller usPoller, EV3LargeRegulatedMotor leftMotor,EV3LargeRegulatedMotor rightMotor) {
		this.odo = odometer;
		this.poller= usPoller;
		this.avoiding= false;
		this.isNavigating =false;
		this.leftMotor = leftMotor;
		this.rightMotor = rightMotor;
		poller.setNavigation(this);
	}

	public void run() {
		odo = lab3.odometer;
		//loop all the points
		for (int i = 0; i < x.length; i++) {
			travelTo(x[i]*tileSize, y[i]*tileSize);
		}
		//end program
		System.exit(0);

	}
	public void travelTo(double x, double y) {
		isNavigating = true;
		try {
			Navigation.sleep(2000);
		} catch (InterruptedException e) {}
			Double differenceX= x-odo.getX();
			Double differenceY= y-odo.getY();
			double distance = Math.sqrt(Math.pow(differenceY, 2) + Math.pow(differenceX, 2)); 
			double thetaC= Math.atan2(differenceY,differenceX);
			if (differenceX<0&&differenceY>=0) {
				turnTo((2.5*Math.PI) - thetaC);
			}	
			else if(differenceX<=0&&differenceY<0) {
				turnTo(/*thetaC+*/(Math.PI/2) - thetaC);
			}
			else {
				turnTo((Math.PI/2) - thetaC);
			}
			leftMotor.setSpeed(lab3.LOW_SPEED);
			rightMotor.setSpeed(lab3.LOW_SPEED);
			
			leftMotor.rotate(convertDistance(lab3.WHEEL_RAD, distance), true);
			rightMotor.rotate(convertDistance(lab3.WHEEL_RAD, distance), false);
			leftMotor.stop();
			rightMotor.stop();
			leftMotor.setSpeed(0);
		    rightMotor.setSpeed(0);
			differenceX = x - odo.getX();
			differenceY = y - odo.getY();
			if (Math.sqrt(differenceX)+Math.sqrt(differenceY) < Math.sqrt(lab3.errorRange)) {
				leftMotor.stop();
				rightMotor.stop();
				Sound.playNote(Sound.FLUTE, 440, 250);
				isNavigating = false;
}
			
		}

//	public void travelto(double i, double j) {
//		
//		isNavigating = true;
//		//initialize
//		double direction = 0;
//		//find direction and distance in cm
//		double deltaX = i - odo.getX();
//		double deltaY = j - odo.getY();	
//		double distance = Math.sqrt((deltaX*deltaX)+(deltaY*deltaY));
//		
//		// detect wall infront of you
//	
////		if(obDist < 50){
////			avoid();
////		 }
//		
//		
//	
//		
//		//in case deltaY goes to 0 though it has really small possibility to be actually 0
//		try{
//			direction = Math.toDegrees(Math.atan(deltaX/deltaY));
//		}
//		catch(ArithmeticException e ){
//			
//		}
//		
//		//filter out small error that cause deltaX/deltaY to be negative
//		if( deltaY < 5 && deltaY > -5){
//			if (deltaY < 0) {
//				direction = 180;
//			}
//			else{
//				direction = 0;
//			}
//			
//		}
//
//		if(deltaX<5&&deltaX>-5){
//			if (deltaX<0) {
//				direction = -90;
//			}
//			else{
//				direction = 90;
//			}	
//		}
//		
//		// if delta X and delta Y both <0, direction+180
//		if (!(deltaY< 5 && deltaY > -5) && !(deltaY < 5 && deltaY > -5) && deltaX < 0 && deltaY < 0) {
//			direction +=180;
//		}
//
//		turnTo(direction);
//		
//		// move forward with the distance calculated
//		leftMotor.rotate(convertDistance(lab3.WHEEL_RAD, distance), true);
//		rightMotor.rotate(convertDistance(lab3.WHEEL_RAD, distance), false);
//		
//		isNavigating = false;
//		 	
//	
//
//	}

	public static void turnTo(double theta) {
		
		isNavigating = true;
		
		double turn = theta - odo.getTheta();
		if(turn > 180){
			turn -= 360;
		}
		else if(turn < -180){
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
		// 23, 14
		//get data from the ultrasonic sensor
		leftMotor.stop();
		rightMotor.stop();
		leftMotor.setSpeed(lab3.ROTATE_SPEED);
		rightMotor.setSpeed(lab3.ROTATE_SPEED);
		leftMotor.rotate(convertAngle(lab3.WHEEL_RAD, lab3.TRACK,90), true);
	    rightMotor.rotate(-convertAngle(lab3.WHEEL_RAD, lab3.TRACK, 90), false);
	    double currentX = odo.getX();
	    double currentY = odo.getY();
	    while(currentX+20>odo.getX() &&currentY+20>odo.getY()) {
	    	leftMotor.forward();
	    	rightMotor.forward();
	    }
	    leftMotor.stop();
	    rightMotor.stop();
	    leftMotor.rotate(-convertAngle(lab3.WHEEL_RAD, lab3.TRACK,75), true);
	    rightMotor.rotate(convertAngle(lab3.WHEEL_RAD, lab3.TRACK, 75), false);
		currentX=odo.getX();
		currentY=odo.getY();
		while(currentX+20 > odo.getX() && currentY+20 > odo.getY()) {
	    	leftMotor.forward();
	    	rightMotor.forward();
	    }
		
		isNavigating = false;
	}
	public void avoid(Double x, Double y) {
		rightMotor.stop();
		leftMotor.setSpeed(lab3.ROTATE_SPEED);
		rightMotor.setSpeed(lab3.ROTATE_SPEED);
		leftMotor.rotate(convertAngle(lab3.WHEEL_RAD, lab3.TRACK,90), true);
	    rightMotor.rotate(-convertAngle(lab3.WHEEL_RAD, lab3.TRACK, 90), false);
	    double currentX = odo.getX();
	    double currentY = odo.getY();
	    while(currentX+20>odo.getX() &&currentY+20>odo.getY()) {
	    	leftMotor.forward();
	    	rightMotor.forward();
	    }
	    leftMotor.stop();
	    rightMotor.stop();
	    leftMotor.rotate(-convertAngle(lab3.WHEEL_RAD, lab3.TRACK,75), true);
	    rightMotor.rotate(convertAngle(lab3.WHEEL_RAD, lab3.TRACK, 75), false);
		currentX=odo.getX();
		currentY=odo.getY();
		while(currentX+20 > odo.getX() && currentY+20 > odo.getY()) {
	    	leftMotor.forward();
	    	rightMotor.forward();
	    }
		
		isNavigating = false;
		travelto(x,y);
	}
	
	public boolean isNavigating(){
		return isNavigating;
	}
    // method to set the data 
	public void setUsData(int data) {
		this.usData = data;
	}
	
	// method to get the data
	public int getUsData() {
		return usData;
	}
	//copied from code given in last lab
	private static int convertDistance(double radius, double distance) {
		    return (int) ((180.0 * distance) / (Math.PI * radius));
		  }
    public static int convertAngle(double radius, double width, double angle) {
		    return convertDistance(radius, Math.PI * width * angle / 360.0);
		  }
    
    public void setPath(double... waypoints) {
		for (int i = 0; i < waypoints.length; i++) {
			this.x = waypoints;
			this.x[i]= waypoints[i]* 30.48;
		}
	}
}
