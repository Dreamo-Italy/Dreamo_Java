package it.dreamo.engine.util;

public class Statistics
{

  private float sum;
  private float[] acc;
  private int window;
  private int aidx;
  private float temp_var;


  public Statistics(int window)
  {
    aidx=0;
    sum=0;
    temp_var=0;
    this.window=window;
    acc=new float[window];
  }

  public float getAverage()
  {
    return sum/window;
  }
  
  public float getVariance()
  {
    if(temp_var<0){temp_var=0;}
    return temp_var/window;
  }
  
  public float getStdDev()
  {
    return (float)Math.sqrt(getVariance());
  }

  public void reset()
  {
    for(int i=0;i<acc.length;i++)
    {
      acc[i]=0;
    }
    aidx=0;
    sum=0;
    temp_var=0;   
  }
  
  public void accumulate(float data)
  {
    sum-=acc[aidx];//subtract last value
    temp_var-=Math.pow((acc[aidx]-getAverage()),2);
    
    acc[aidx]=data;//update the value
    
    sum+=acc[aidx];//update the total    
    temp_var+=Math.pow((acc[aidx]-getAverage()),2);
    
    aidx++;//next position
    if (aidx>=window) {
      aidx=0;
    }//if at the end go back

    
  }
}
