package ca.mcgill.ecse211.lab3;
//
//import ca.mcgill.ecse211.sensor.UltrasonicPoller;
import lejos.hardware.ev3.LocalEV3;
//import lejos.hardware.motor.EV3MediumRegulatedMotor;
//import lejos.hardware.sensor.EV3UltrasonicSensor;
//import lejos.robotics.SampleProvider;
//
//public class Avoidance extends Thread implements Runnable{
//
//	//declare avoidance element ports
//	public static final EV3UltrasonicSensor ultraSensor = 	new EV3UltrasonicSensor(LocalEV3.get().getPort("S1"));
//import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.hardware.sensor.SensorModes;

//public static final EV3MediumRegulatedMotor spinMotor = 	new EV3MediumRegulatedMotor(LocalEV3.get().getPort("S3"));
////
////	private static SampleProvider usDist = ultraSensor.getMode("Distance");;
////	private static float[] usData = new float[usDist.sampleSize()];
//	
//
//	public void run() {
//		
//		
////		usDist.fetchSample(usData,0);
////		float obDist = usData[0]*100;
////		System.out.println("odDist  " + obDist);
//		
//		if(UltrasonicPoller.distance < 50) {
//			System.out.println("odDist  " + UltrasonicPoller.distance);
//			stop  (or maybe it doesn't have to)
//			Navigation.leftMotor.stop();
//			Navigation.rightMotor.stop();
//			//turn right 90 degree
//			Navigation.leftMotor.rotate(Navigation.convertAngle(lab3.WHEEL_RAD, lab3.TRACK,90), true);
//		    Navigation.rightMotor.rotate(-Navigation.convertAngle(lab3.WHEEL_RAD, lab3.TRACK, 90), false);
//		    //go forward 20 cm 
//		    Navigation.leftMotor.rotate( Navigation.convertDistance(lab3.WHEEL_RAD, 20), true);
//		    Navigation.rightMotor.rotate( Navigation.convertDistance(lab3.WHEEL_RAD, 20), false);
//		    //turn left 90 degree
//		    Navigation.leftMotor.rotate(-Navigation.convertAngle(lab3.WHEEL_RAD, lab3.TRACK,90), true);
//		    Navigation.rightMotor.rotate(Navigation.convertAngle(lab3.WHEEL_RAD, lab3.TRACK, 90), false);
//		    //go forward 10 cm
//		    Navigation.leftMotor.rotate( Navigation.convertDistance(lab3.WHEEL_RAD, 10), true);
//		    Navigation.rightMotor.rotate( Navigation.convertDistance(lab3.WHEEL_RAD, 10), false);
//		    //turn left 90 degree
//		    Navigation.leftMotor.rotate(-Navigation.convertAngle(lab3.WHEEL_RAD, lab3.TRACK,90), true);
//		    Navigation.rightMotor.rotate(Navigation.convertAngle(lab3.WHEEL_RAD, lab3.TRACK, 90), false);
//		    //go forward 20 cm
//		    Navigation.leftMotor.rotate( Navigation.convertDistance(lab3.WHEEL_RAD, 20), true);
//		    Navigation.rightMotor.rotate( Navigation.convertDistance(lab3.WHEEL_RAD, 20), false);
//		    //turn right 90 degree
//		    Navigation.leftMotor.rotate(Navigation.convertAngle(lab3.WHEEL_RAD, lab3.TRACK,90), true);
//		    Navigation.rightMotor.rotate(-Navigation.convertAngle(lab3.WHEEL_RAD, lab3.TRACK, 90), false);
//		    
//	
//				// 23, 14
//				//get data from the ultrasonic sensor
//			    Navigation.leftMotor.stop();
//			    Navigation.rightMotor.stop();
//			    Navigation.leftMotor.setSpeed(lab3.ROTATE_SPEED);
//			    Navigation.rightMotor.setSpeed(lab3.ROTATE_SPEED);
//			    Navigation.leftMotor.rotate(Navigation.convertAngle(lab3.WHEEL_RAD, lab3.TRACK,90), true);
//			    Navigation.rightMotor.rotate(-Navigation.convertAngle(lab3.WHEEL_RAD, lab3.TRACK, 90), false);
//			    double currentX = Navigation.odo.getX();
//			    double currentY = Navigation.odo.getY();
//			    while(currentX + 20 > Navigation.odo.getX() && currentY + 20 > Navigation.odo.getY()) {
//			    	Navigation.	leftMotor.forward();
//			    	Navigation.	rightMotor.forward();
//			    }
//			    Navigation.leftMotor.stop();
//			    Navigation.rightMotor.stop();
//			    Navigation.leftMotor.rotate(-Navigation.convertAngle(lab3.WHEEL_RAD, lab3.TRACK,75), true);
//			    Navigation.rightMotor.rotate(Navigation.convertAngle(lab3.WHEEL_RAD, lab3.TRACK, 75), false);
//				currentX = Navigation.odo.getX();
//				currentY = Navigation.odo.getY();
//				while(currentX + 20 > Navigation.odo.getX() && currentY + 20 > Navigation.odo.getY()) {
//					Navigation.leftMotor.forward();
//					Navigation.rightMotor.forward();
//			    }
//				Navigation.isNavigating = false;
//			}
//		}			
//}


import lejos.robotics.SampleProvider;

/**
 * Control of the wall follower is applied periodically by the UltrasonicPoller thread. The while
 * loop at the bottom executes in a loop. Assuming that the us.fetchSample, and cont.processUSData
 * methods operate in about 20mS, and that the thread sleeps for 35 mS at the end of each loop, then
 * one cycle through the loop is approximately 70 mS. This corresponds to a sampling rate of 1/70mS
 * or about 14 Hz.
 */
public class Avoidance extends Thread implements Runnable {
  private SampleProvider us;
  //private float[] usData;
  public static int distance = 0;
  
  public static final EV3UltrasonicSensor ultraSensor = 	new EV3UltrasonicSensor(LocalEV3.get().getPort("S1"));
  @SuppressWarnings("resource") // Because we don't bother to close this resource
  //SensorModes usSensor = new EV3UltrasonicSensor(usPort); // usSensor is the instance
  SampleProvider usDistance = ultraSensor.getMode("Distance");
  
   private float[] usData = new float[usDistance.sampleSize()];
  /*
   * Sensors now return floats using a uniform protocol. Need to convert US result to an integer
   * [0,255] (non-Javadoc)
   * 
   * @see java.lang.Thread#run()
   */
  public void run() {
    int distance;
    while (true) {
    	usDistance.fetchSample(usData, 0); // acquire data
      distance = (int) ( ( usData[0] * 100.0 ) );
      this.distance = distance;// extract from buffer, cast to int
      System.out.println("distance is " + this.distance);
      try {
        Thread.sleep(35);
      } catch (Exception e) {
     } // Poor man's timed sampling
    }
  }

}

