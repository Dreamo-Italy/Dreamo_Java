package it.dreamo.engine.audio;

public abstract class FeaturesExtractor
{
  
  protected float[] samples;
  protected int buffSize;
  protected float sampleRate;
  private boolean init;
  
  //SOPE CALC VARIABLES
  private float previousVal;
  private float slope;
  
  
  FeaturesExtractor()
  {
    buffSize=0;
    sampleRate=0;
    init=false; 
    previousVal=0;
    slope=0;
  }
  
  /*FeaturesExtractor(int bSize, float sRate)
  {
    samples=new float[bSize];
    buffSize=bSize;
    sampleRate=sRate;
    init=false;
  }
  */
  
  //SET METHODS
  public void setSamples(float[] _samples)
  {   
      samples=_samples.clone();
      //calcFeatures();   
  }
  
  //public void setBufferSize
  
  public void setInitialized()
  {
    init=true;
  }
  
  //GET METHODS 
  public int getBufferSize()
  {
    return buffSize;
  }
  
  public float getSampleRate()
  {
    return sampleRate;
  }
  
  public boolean isInitialized()
  {
    return init;
  }
  
  //CALC METHODS
  //exponentially decaying moving average -> window=N
  protected float expSmooth(final float currentval, final float smoothedval, int N)
  {
    //averaging constant
    float tiny;
    tiny=1f-(1f/N);

    return tiny*smoothedval+(1-tiny)*currentval;
   
  }
  
  //UTILITY METHODS
  protected float linearToDecibel(final float value)
  {
    return (float) (20.0*Math.log10(value));
  }
  
  protected float realTimeSlope(float input)
  {
    slope=input-previousVal;
    previousVal=input;
    return slope;
  }
  
  //ABSTRACT METHODS  
  abstract void calcFeatures();

  
}