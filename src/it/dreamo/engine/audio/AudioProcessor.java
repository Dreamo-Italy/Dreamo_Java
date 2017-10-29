package it.dreamo.engine.audio;

import processing.core.PApplet;
import ddf.minim.AudioListener;
import ddf.minim.analysis.*;
import it.dreamo.engine.util.*;

public class AudioProcessor implements AudioListener
{
  private float[] left;
  private float[] right;
  private float[] mix;
 
  private float[] FFTcoeffs;
  
  private boolean log;
 
  private int frameCounter;
  private final int FRAMES_NUMBER=43;
  private float avgMagnitude;
    
  private FFT fft;
  
  private Dynamic dyn;
  private Timbre timb;
  private Rhythm rhy;
  
  //********* CONSTRUCTOR ***********
  public AudioProcessor(int bSize, float sRate)
  {
    
    left = null; 
    right = null;
    
    dyn=null;
    timb=null;
    rhy=null;
    
    log=false;    
    frameCounter=0;
    
    avgMagnitude=0;
    
    if ( bSize == 0 || sRate == 0) {PApplet.println("ERROR: Impossible to initialize AudioProcessor");}
    
    else
    { 
      fft = new FFT(bSize,sRate);
      
      if(!log) 
      {
        fft.noAverages();
        FFTcoeffs = new float[fft.specSize()];
      }     
      //TODO: implement logarithmic spectrum. 
      //the calculation of the spectroid won't be in Hz -> check it!
      //see http://code.compartmental.net/minim/fft_method_logaverages.html
      //EXAMPLE: 
      else 
      {
        fft.logAverages(  86, 3 );
        FFTcoeffs = new float[fft.avgSize()];
      }
    }
    
  }
  
   
  //********* SYNCHRONIZED METHODS ***********
  public synchronized void samples(float[] samp)
  {
    //update samples
    left = samp;
    
    //calculate fourier transform
    calcFFT(samp);
    
    //run features extractors
    extractFeatures(); 
    
    frameCounter++;
    if(frameCounter>FRAMES_NUMBER){frameCounter=0;}
    
  }
  
  public synchronized void samples(float[] sampL, float[] sampR)
  {
    //update samples - TODO: verify if is mix() could be useful
    left = sampL;
    right = sampR; 
    
    //calculate fourier transform
    calcFFT(sampL);  
    
    //run features extractors
    extractFeatures();
    
    frameCounter++;
    if(frameCounter>FRAMES_NUMBER){frameCounter=0;}
  }
  
  //********* PUBLIC METHODS ***********
  public void addDyn(Dynamic d)
  {
    dyn=d; 
    dyn.setInitialized();
  }
  
  public void addTimbre(Timbre t)
  {
    timb=t;
    timb.setInitialized();
  }
  
  public void addRhythm(Rhythm r)
  {
    rhy=r;
    rhy.setInitialized();
  }
  
  //get methods
  public float[] getLeft()
  {
    return left;
  }
  
  public float[] getRight()
  {
    return right;
  }
  
  public float[] getMix()
  {
    return mix;
  }
  
  //********* PRIVATE METHODS ***********
  //synchrodized is necessary?
  private void mix()
  {
    mix=DSP.plus(left,right); 
  }
  
  public float getFFTcoeff(int i)
  {
    return FFTcoeffs[i];
  }
  
  public int getSpecSize()
  {
    if(!log){return fft.specSize();}
    else{return fft.avgSize();}
  }
  
  //FEATURES CALC METHODS
  //FFT
  private void calcFFT(final float[] samples)
  {
    //fft.window(FFT.HAMMING);
    fft.forward(samples);    
    avgMagnitude=0;
    if(!log)
    {     
      for(int i = 0; i < fft.specSize(); i++)
       {
          FFTcoeffs[i]=fft.getBand(i);
          if(i<=fft.specSize()/2) {avgMagnitude+=fft.getBand(i);}
       }
      avgMagnitude=avgMagnitude/fft.specSize();
      avgMagnitude=avgMagnitude*2;
    }
    
    else
    {
      for(int i = 0; i < fft.avgSize(); i++)
       {
          FFTcoeffs[i]=fft.getAvg(i);
       }
    }
   
  
  }
  

   
  //autocorr
  //zerocrossing rate?
  
  private synchronized void extractFeatures()
  {
    runRhythm();
    runDyn();
    runTimbre(); 
  }
    
  private void runRhythm()
  {
    if(rhy!=null){
      rhy.setSamples(left);
      rhy.setCounter(frameCounter);
      rhy.setFFTCoeffs(FFTcoeffs,fft.specSize());
      rhy.calcFeatures();
    }
    else{PApplet.println("RHYTHM OBJECT HAS TO BE ADDED TO AUDIO PROCESSOR");}
    
  }
  
  private void runDyn()
  { 
    if(dyn!=null)
    {
      dyn.setSamples(left);
      dyn.calcFeatures();
    }
    else{PApplet.println("DYN OBJECT HAS TO BE ADDED TO AUDIO PROCESSOR");}
  }
  
  private void runTimbre()
  { 
    if(timb!=null)
    {
      if(!log) 
      {
        timb.setFFTCoeffs(FFTcoeffs,fft.specSize());
        timb.setAvgMagnitude(avgMagnitude);
      }
      else 
      {  
        timb.setFFTCoeffs(FFTcoeffs,fft.avgSize());
      }
      timb.calcFeatures();
    }
    else{PApplet.println("TIMBRE OBJECT HAS TO BE ADDED TO AUDIO PROCESSOR");}
  }
  
  
}