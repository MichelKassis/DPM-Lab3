package ca.mcgill.ecse211.lab3;



import ca.mcgill.ecse211.sensor.Odometer;
import lejos.hardware.Sound;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.motor.EV3LargeRegulatedMotor;


public class Navigation extends Thread{
	//declare the port occupied by the motor and sensor
	public static final EV3LargeRegulatedMotor leftMotor  =	new EV3LargeRegulatedMotor(LocalEV3.get().getPort("A"));
	public static final EV3LargeRegulatedMotor rightMotor =	new EV3LargeRegulatedMotor(LocalEV3.get().getPort("B"));

	static Odometer odo;
	
	public final double tileSize= 30.48;
	static boolean isNavigating;
	private static int x[];
	private static int y[];
	private boolean notSmallValue = true;

	public Navigation(int X[], int Y[]) {
		this.x = X;
		this.y = Y;
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

	public void travelTo(double i, double j) {
		isNavigating = true;
		//initialize
		double direction = 0;
		//find direction and distance in cm
		double deltaX = i - odo.getX();
		double deltaY = j - odo.getY();	
		double distance = Math.sqrt((deltaX*deltaX)+(deltaY*deltaY));
		
		System.out.println("X:"+ deltaX);
		System.out.println("Y:"+ deltaY);

		//in case deltaY goes to 0 though it has really small possibility to be actually 0
		try{
			direction = Math.toDegrees(Math.atan(deltaX/deltaY));
		}
		catch(ArithmeticException e){
			
		}
		//filter out small error that cause deltaX/deltaY to be negative
		if( deltaY < 3 && deltaY > -3){
			if (deltaX<0) {
				direction = -90;
			}
			else{
				direction = 90;
			}	
			System.out.println("b");
			notSmallValue = false;
			
		}

		if(deltaX<3&&deltaX>-3){
			if (deltaY < 0) {
				direction = 180;
			}
			else{
				direction = 0;
			}
			System.out.println("a");
		notSmallValue = false;
		}

		System.out.println("smallv:"+notSmallValue);
		// if delta X and delta Y both <0, direction+180
		if (notSmallValue  && deltaY < 0) {
			direction +=180;
			System.out.println("c");
			
		}
		if(Avoidance.distance < 50) {
			this.avoid();
		}

		System.out.println(direction);
			
		turnTo(direction);

		// move forward with the distance calculated
		leftMotor.rotate(convertDistance(lab3.WHEEL_RAD, distance), true);
		rightMotor.rotate(convertDistance(lab3.WHEEL_RAD, distance), false);
		isNavigating = false;
		notSmallValue = true;
	}
	

	public static void turnTo(double theta) {

		isNavigating = true;

		//find the direction
		double turn = theta - odo.getTheta();
		if(turn > 180){
			turn -= 360;
		}
		else if(turn < -180){
			turn +=360;
		}
		
		turn %=360;
		
		// set rotate speed
		leftMotor.setSpeed(lab3.ROTATE_SPEED);
		rightMotor.setSpeed(lab3.ROTATE_SPEED);

		
		//decide which way to turn
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

	public boolean isNavigating(){
		return isNavigating;
	}

	//copied from code given in last lab
	public static int convertDistance(double radius, double distance) {
		return (int) ((180.0 * distance) / (Math.PI * radius));
	}

	public static int convertAngle(double radius, double width, double angle) {
		return convertDistance(radius, Math.PI * width * angle / 360.0);
	}

}
