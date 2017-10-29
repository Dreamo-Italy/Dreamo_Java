package it.dreamo.engine.util;

public class Hysteresis
{
  private boolean[] hWindow;
  private int idx;
  private int w;
  private float lowerBound;
  private float upperBound;
  private boolean result;

  public Hysteresis(int window)
  {
    this.w=window;
    hWindow = new boolean[this.w];
    idx=0;
    result=false;
  }
  
  public Hysteresis(float lB, float uB, int window)
  {
    this.lowerBound=lB;
    this.upperBound=uB;
    this.w=window;
    hWindow = new boolean[this.w];
    idx=0;
    result=false;
  }


  public boolean check(float value)
  {

    if (value>=upperBound) {
      result=true;
    } else if (value<=lowerBound) {
      result=false;
    }

    return result;
  }

  public boolean checkWindow(float value)
  {
    if (idx==w) {
      idx=0;
    }

    if (value>=upperBound) {
      hWindow[idx]=true; 
      idx++;
    } else if (value<=lowerBound) {
      hWindow[idx]=false; 
      idx++;
    }

    //idx++;

    int k=0;
    for (int j=0; j<w; j++)
    {
      if (hWindow[j]==true) k++;
    }

    if (k==w) {
      result = true;
    } else if (k==0) { 
      result = false;
    }

    return result;
  }
  
  public boolean checkWindow(float value, float upperBound, float lowerBound)
  {
    if (idx==w) {
      idx=0;
    }

    if (value>=upperBound) {
      hWindow[idx]=true; 
      idx++;
    } else if (value<=lowerBound) {
      hWindow[idx]=false; 
      idx++;
    }

    //idx++;

    int k=0;
    for (int j=0; j<w; j++)
    {
      if (hWindow[j]==true) k++;
    }

    if (k==w) {
      result = true;
    } else if (k==0) { 
      result = false;
    }

    return result;
  }
  
  public void restart()
  {
    hWindow = new boolean[this.w];
    idx=0;
    result=false;
    
  }
  
}