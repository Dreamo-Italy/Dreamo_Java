package it.dreamo.engine.audio;

import processing.core.PApplet;
import it.dreamo.engine.util.*;

public class Dynamic extends FeaturesExtractor
{

  public static final double DEFAULT_SILENCE_THRESHOLD = -60.0;//db
  
  private float RMS;
  private final float THEORETICAL_MAX_RMS=0.4f; //based on empirical tests

  //keep track of ~3 seconds of music and average RMS values
  private final int W=129; // 43=~1s
  
  private float RMSslope;
  
  private Statistics RMSstats;
  
  //CONSTRUCTOR
  public Dynamic(int bSize, float sRate)
  {
    buffSize=bSize;
    sampleRate=sRate;
    
    RMSstats=new Statistics(W);

  }


  //GET METHODS
  public float getRMS()
  {
   return RMS;
  }
    
  public float getRMSAvg()
  {
    return RMSstats.getAverage();
  }
  
  public float getRMSStdDev()
  {
    return RMSstats.getStdDev();
  }
  
  public float getRMSVariance()
  {
    return RMSstats.getVariance();
  }
  
  public float getSPL()
  {
    return soundPressureLevel(RMS);
  }
  
  public float getDynamicityIndex() { return RMSstats.getStdDev()/RMSstats.getAverage(); }
  
  public void reset()
  {
    //maxRMS=0.4;
  }
  
  //OVERRIDE CALC FEATURES METHOD
  public void calcFeatures()
  {
    calcRMS();
  }

  public boolean isSilence(final float silenceThreshold) { return soundPressureLevel(RMS) < silenceThreshold; }
  
  public boolean isSilence() { return soundPressureLevel(RMS) < DEFAULT_SILENCE_THRESHOLD; }
  
  //**** PRIVATE METHODS
  /**
   * RMS standard comptation
   */
  private void calcRMS()
  {
      float level=0;
      for(int i=0;i<samples.length;i++)
      {
        level += (samples[i]*samples[i]);
       }       
      
      level /= samples.length;
      level = (float) Math.sqrt(level);

      
      
      
      //if(level > maxRMS) maxRMS = level;

      //normalize level in 0-1 range
      level=PApplet.map(level,0,THEORETICAL_MAX_RMS,0,1);
      
      RMSslope=realTimeSlope(level);
      //average      
      RMSstats.accumulate(level);
      
      //smoothing
      RMS=expSmooth(level,RMS,5);
      
  }
  


  
  private float soundPressureLevel(final float RMS) { return linearToDecibel(RMS); }
  
  private float getRmsSlope()
  {
    return RMSslope;
  }
  
  
  


}
