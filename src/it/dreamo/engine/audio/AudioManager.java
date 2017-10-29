package it.dreamo.engine.audio;

import processing.core.PApplet;
import ddf.minim.*;

public class AudioManager

{
 //********* PRIVATE MEMBERS ***********
 private Minim minim;
 private AudioInput in=null; 
 private float[] buffer;
 private boolean initialized = false;
 private PApplet parent;
  
 //********* CONTRUCTORS ***********
 public AudioManager(){}
   /* //<>// //<>// //<>//
   * @param fileSystemHandler
   *        The Object that will be used for file operations.
   *        When using Processing, simply pass <strong>this</strong> to AudioFeatures constructor.
   */
 //TODO: monitoring audio input from Raspberry sound card
 //temporary: use pc input  
 public AudioManager(PApplet p)
 {
   parent=p;
   minim = new Minim(parent);
   in = minim.getLineIn(Minim.STEREO,1024,44100); //stereo stream, 2048 samples of buffer size
   //in.enableMonitoring();
   //bufferize();
   
   if(in!=null)
   {
   initialized=true;
   }
   else {PApplet.println("AUDIO INPUT NOT AVAILABLE");}
   
 }

 //********* PUBLIC METHODS ***********
 public void addListener(AudioListener l)
 { 
   if (isInitialized())
    { 
       in.addListener(l);
    }
    else{PApplet.println("AUDIO FEATURE OBJECT NOT INITIALIZED");} 
 }
 
 public void enableMonitoring()
 {
    if (isInitialized())
    { 
      in.enableMonitoring();
    }
    else{PApplet.println("AUDIO FEATURE OBJECT NOT INITIALIZED");}   
 }
  
 public void updateBuffer()
  {       
    if (isInitialized())
    { 
      buffer=in.mix.toArray();
    }
    else{PApplet.println("AUDIO FEATURE OBJECT NOT INITIALIZED");} 
  }
  
  public float[] getSamples()
  {
    return buffer;
  }
  
  public void stop(){
    in.close();
    minim.stop();
  }
  
  public int getBufferSize()
  {
    if(isInitialized()){return in.bufferSize();}
    else return 0;
  }
  
  public float getSampleRate()
  {
    if(isInitialized()){return in.sampleRate();}
    else return 0;
  }
  
  
  //********* PRIVATE METHODS ***********
  
  private boolean isInitialized()
  {
    return initialized;
  }    
  

  
}