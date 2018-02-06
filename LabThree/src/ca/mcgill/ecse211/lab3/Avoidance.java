package ca.mcgill.ecse211.lab3;

import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.motor.EV3MediumRegulatedMotor;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.robotics.SampleProvider;

public class Avoidance extends Thread{

	//declare avoidance element ports
	public static final EV3UltrasonicSensor ultraSensor = 	new EV3UltrasonicSensor(LocalEV3.get().getPort("S1"));
	public static final EV3MediumRegulatedMotor spinMotor = 	new EV3MediumRegulatedMotor(LocalEV3.get().getPort("S3"));

	private static SampleProvider usDist = ultraSensor.getMode("Distance");;
	private static float[] usData = new float[usDist.sampleSize()];
	

	public void run() {
		
		
		usDist.fetchSample(usData,0);
		float obDist = usData[0];
		
		
		if(obDist<50) {
			//stop  (or maybe it doesn't have to)
			Navigation.leftMotor.stop();
			Navigation.rightMotor.stop();
			//turn right 90 degree
			Navigation.leftMotor.rotate(Navigation.convertAngle(lab3.WHEEL_RAD, lab3.TRACK,90), true);
		    Navigation.rightMotor.rotate(-Navigation.convertAngle(lab3.WHEEL_RAD, lab3.TRACK, 90), false);
		    //go forward 20 cm 
		    Navigation.leftMotor.rotate( Navigation.convertDistance(lab3.WHEEL_RAD, 20), true);
		    Navigation.rightMotor.rotate( Navigation.convertDistance(lab3.WHEEL_RAD, 20), false);
		    //turn left 90 degree
		    Navigation.leftMotor.rotate(-Navigation.convertAngle(lab3.WHEEL_RAD, lab3.TRACK,90), true);
		    Navigation.rightMotor.rotate(Navigation.convertAngle(lab3.WHEEL_RAD, lab3.TRACK, 90), false);
		    //go forward 10 cm
		    Navigation.leftMotor.rotate( Navigation.convertDistance(lab3.WHEEL_RAD, 10), true);
		    Navigation.rightMotor.rotate( Navigation.convertDistance(lab3.WHEEL_RAD, 10), false);
		    //turn left 90 degree
		    Navigation.leftMotor.rotate(-Navigation.convertAngle(lab3.WHEEL_RAD, lab3.TRACK,90), true);
		    Navigation.rightMotor.rotate(Navigation.convertAngle(lab3.WHEEL_RAD, lab3.TRACK, 90), false);
		    //go forward 20 cm
		    Navigation.leftMotor.rotate( Navigation.convertDistance(lab3.WHEEL_RAD, 20), true);
		    Navigation.rightMotor.rotate( Navigation.convertDistance(lab3.WHEEL_RAD, 20), false);
		    //turn right 90 degree
		    Navigation.leftMotor.rotate(Navigation.convertAngle(lab3.WHEEL_RAD, lab3.TRACK,90), true);
		    Navigation.rightMotor.rotate(-Navigation.convertAngle(lab3.WHEEL_RAD, lab3.TRACK, 90), false);
		}
		
		
	
		
	}
	
			


}
