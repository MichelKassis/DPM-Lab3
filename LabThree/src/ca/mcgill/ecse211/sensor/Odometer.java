/**
 * This class is meant as a skeleton for the odometer class to be used.
 * 
 * @author Rodrigo Silva
 * @author Dirk Dubois
 * @author Derek Yu
 * @author Karim El-Baba
 * @author Michael Smith
 */

package ca.mcgill.ecse211.sensor;

import lejos.hardware.motor.EV3LargeRegulatedMotor;

public class Odometer extends OdometerData implements Runnable {

	private OdometerData odoData;
	private static Odometer odo = null; // Returned as singleton

	// Motors and related variables
	private int leftTachoCount;
	private int rightTachoCount;
	private int lastleftTachoCount;
	private int lastrightTachoCount;
	private EV3LargeRegulatedMotor leftMotor;
	private EV3LargeRegulatedMotor rightMotor;
	private double TRACK;
	private double WHEEL_RAD;
	private static double distL, distR, dDis, dtheta,dx,dy;

	private static final long ODOMETER_PERIOD = 50; // odometer update period in ms

	/**
	 * This is the default constructor of this class. It initiates all motors and variables once.It
	 * cannot be accessed externally.
	 * 
	 * @param leftMotor
	 * @param rightMotor
	 * @throws OdometerExceptions
	 */
	private Odometer(EV3LargeRegulatedMotor leftMotor, EV3LargeRegulatedMotor rightMotor,
			final double TRACK, final double WHEEL_RAD) throws OdometerExceptions {
		odoData = OdometerData.getOdometerData(); // Allows access to x,y,z
		// manipulation methods
		this.leftMotor = leftMotor;
		this.rightMotor = rightMotor;

		// Reset the values of x, y and z to 0
		odoData.setXYT(0, 0, 0);

		//		this.leftMotorTachoCount = 0; ******************************
		//		this.rightMotorTachoCount = 0;

		this.TRACK = TRACK;
		this.WHEEL_RAD = WHEEL_RAD;

	}
	
	//added a second constructor 


	/**
	 * This method is meant to ensure only one instance of the odometer is used throughout the code.
	 * 
	 * @param leftMotor
	 * @param rightMotor
	 * @return new or existing Odometer Object
	 * @throws OdometerExceptions
	 */
	public synchronized static Odometer getOdometer(EV3LargeRegulatedMotor leftMotor,
			EV3LargeRegulatedMotor rightMotor, final double TRACK, final double WHEEL_RAD)
					throws OdometerExceptions {
		if (odo != null) { // Return existing object
			return odo;
		} else { // create object and return it
			odo = new Odometer(leftMotor, rightMotor, TRACK, WHEEL_RAD);
			return odo;
		}
	}

	/**
	 * This class is meant to return the existing Odometer Object. It is meant to be used only if an
	 * odometer object has been created
	 * 
	 * @return error if no previous odometer exists
	 */
	public synchronized static Odometer getOdometer() throws OdometerExceptions {

		if (odo == null) {
			throw new OdometerExceptions("No previous Odometer exits.");

		}
		return odo;
	}

	/**
	 * This method is where the logic for the odometer will run. Use the methods provided from the
	 * OdometerData class to implement the odometer.
	 */
	// run method (required for Thread)
	public void run() {
		long updateStart, updateEnd;


		while (true) {
			updateStart = System.currentTimeMillis();
			
			leftTachoCount = leftMotor.getTachoCount();
			rightTachoCount = rightMotor.getTachoCount();
			distL =  Math.PI * WHEEL_RAD * (leftTachoCount - lastleftTachoCount)/180;     // compute wheel displacements lastTachoL=nowTachoL
			distR = Math.PI * WHEEL_RAD * (rightTachoCount - lastrightTachoCount)/180;   
			lastleftTachoCount = leftTachoCount;
			lastrightTachoCount = rightTachoCount;
			
			dDis = 0.5 * (distL + distR);       // vehicle displacement
			
			dtheta = ((distL-distR)/TRACK) * 180/Math.PI;                //  change in angle in degree.

			double theta = (odo.getTheta());
			
			theta = theta + dtheta;

			dx = dDis * Math.sin(Math.toRadians(theta));  //theta should change from degree to radian
			dy = dDis * Math.cos(Math.toRadians(theta));

	
			odo.update(dx, dy, dtheta);

			
			//I did not do lots of comment above since it's almost the same as the code Prof. provide in the slides.






			// this ensures that the odometer only runs once every period
			updateEnd = System.currentTimeMillis();
			if (updateEnd - updateStart < ODOMETER_PERIOD) {
				try {
					Thread.sleep(ODOMETER_PERIOD - (updateEnd - updateStart));
				} catch (InterruptedException e) {
					// there is nothing to be done
				}
			}
		}
	}
}
