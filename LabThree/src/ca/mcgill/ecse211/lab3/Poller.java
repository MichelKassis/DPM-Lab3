package ca.mcgill.ecse211.lab3;

import lejos.hardware.sensor.*;
import lejos.robotics.SampleProvider;


public class Poller extends Thread {
	private EV3UltrasonicSensor sensor;
	private float[] usData;
	private SampleProvider provider;
	private Navigation gps;
	public volatile int dist;
	public Poller(EV3UltrasonicSensor usSensor, SampleProvider provider, float[] usData) {
		this.sensor= usSensor; 
		this.usData = usData;
		this.provider=provider;
	}
	public float getUsData() {
		return usData[0];
	}
	public void setNavigation(Navigation gps) {
		this.gps= gps;
	}
	public void setUsData(float distance) {
		usData[0]= distance; 
		gps.setUsData((int)distance);
	}
	public int getDistance() {
		return dist; 
	}
	public void run(){
		int distance;
		while(true){
			provider.fetchSample(usData, 0);
			distance = (int)(usData[0]*100);
			dist= distance;
			gps.setUsData(distance);
			try{
				Thread.sleep(10);	
			}catch(Exception e){
				
			}
		}
	}
}
