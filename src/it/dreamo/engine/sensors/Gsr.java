package it.dreamo.engine.sensors;

import processing.core.PApplet;

public class Gsr extends Biosensor
{
  public Gsr(PApplet p) {
		super(p);
		// TODO Auto-generated constructor stub
	}
float currentValue;
  
  public void init()
  {
    sensorName = "gsr";   
    physicalMin = 0;
    physicalMax = 20;
    setBpm(0);
  }
  public void update()
   {     //<>//
    // store the incoming conductance value from Connection to another FloatLIst
    incomingValues = global_connection.extractFromBuffer("gsr", sampleToExtract );
    
    // currentValue is the average of the Incoming Values
    currentValue = computeAverage(incomingValues, getDefault() );     
        
    // set the actual sensor value to currentValue    
    setValue  ( currentValue );
     
     if ( ! ( incomingValues == null ) )
       checkCalibration();    
  }
  
}
