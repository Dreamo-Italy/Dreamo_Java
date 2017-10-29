package it.dreamo.engine;

import processing.core.*; 
import processing.serial.*;
import processing.data.*;
//import java.util.Queue;
//import java.util.ArrayDeque;
import it.dreamo.engine.util.*;


public class Connection
{
  //********* CONSTANTS ***********
     
  private final int lineFeed = 10;    // Linefeed in ASCII    
  private final int totSampleToExtract, sampleToExtract;
  
  //********* PUBLIC MEMBERS ***********

  Serial myPort;  // Create object from Serial class
  public PApplet parent; //needed for the Serial object istantiation
      
  //********* PRIVATE MEMBERS ***********
  
  private boolean wifiAvailable;
  private boolean serialAvailable;
  private boolean connectionAvailable;
  private int [] executionNumber; // # of times StoreFromText has been called 

  private FloatList incomingGsr, incomingEcg;
  private Table table_gsr, table_ecg;

  //********* CONSTRUCTOR ***********
  
  // p = parent is needed for the Serial myport ( -->parent<--, list[0], baudRate...)
  public Connection( PApplet p ) 
  {
    wifiAvailable = false;
    serialAvailable = false;
    connectionAvailable = false;
    parent = p;
    
    executionNumber = new int[GlobalParams.sensorNumber];
    
    executionNumber[0] = PApplet.round( parent.random ( 1000 ) );
    executionNumber[1] = executionNumber[0];
 
    incomingGsr = new FloatList();
    incomingEcg = new FloatList();
    
    parent = p;
    
    // number of BIOMEDICAL VALUES to extract at each update() cycle   
    totSampleToExtract = PApplet.ceil ((GlobalParams.global_sampleRate/GlobalParams.global_fps));  //<>//
    sampleToExtract = totSampleToExtract; /*/global_sensorNumber*/
     //<>//
    //serial check
    
    if(!wifiAvailable) 
      { 
        PApplet.println("WARNING: Wifi is not available");
        if ( serialConnect() )
          serialAvailable = true;
        else
        {
          PApplet.println("WARNING: Serial port is not available");
          loadOfflineTables();
        }
      } 
     
   // IF WIFI OR SERIAL ARE AVAILABLE SET BOOLEAN TO "TRUE"
   if(! (!wifiAvailable && !serialAvailable) ) 
     connectionAvailable = true;
       
  }
  
  private boolean wifiConnect()
  {
    boolean wifiAvailable = false;  
    return wifiAvailable;
  }
  
  public boolean networkAvailable()
  {
    return connectionAvailable;
  }
  
    private boolean serialConnect() // return TRUE if a serial connection is available
{
    // I know that the first port in the serial list on my mac
    // is Serial.list()[0].
    // On Windows machines, this generally opens COM1.
    // Open whatever port is the one you're using.
    
    boolean portAvailable = false;
    final String[] ports = Serial.list();
    PApplet.println( ports );
    
    if (ports.length  == 1) // DEBUG = 1 ; RIGHT ONE = 0;
    {
      String portName = Serial.list()[0]; //change the 0 to a 1 or 2 etc. to match your port
      myPort = new Serial(parent, portName, 38400);
      portAvailable = true; 
    }
    PApplet.println("Serial connection is available.");
    return portAvailable;   
 }

  
  
  public void update()
  {     //<>//
    if( !serialAvailable ) //<>//
         storeFromText();      // read the data from an OFFLINE TABLE
    else if ( serialAvailable )
        storeFromSerial();    // read the data from the SERIAL LINE
 }
 
  private void loadOfflineTables()
  {
    table_gsr = parent.loadTable(GlobalParams.globalGsrTable, "header"); // content of log_conductance
    table_ecg = parent.loadTable(GlobalParams.globalEcgTable, "header"); // content of log_ECG
    PApplet.println(table_gsr.getRowCount() + " total rows in table conductance"); 
    PApplet.println(table_ecg.getRowCount() + " total rows in table ECG");  
  }

  public void storeFromText()
  { 
    storeOfflineSamples("gsr");
    storeOfflineSamples("ecg");  
    PApplet.println("Offline sensor reading OK");
  }
  
  void storeOfflineSamples(String sensorName)
    {
      int sensorIndex = 0;
      String tableHeaderName = null;

      if( !(sensorName.equals("gsr") || sensorName.equals("ecg") ) )
        { 
    	  PApplet.println("ERROR in storeFromText: couldn't recognize the name of the sensor");
          return;
        }
      else
        { 
          if( sensorName.equals("gsr") )
              {sensorIndex = 0;
              tableHeaderName = "conductance";}
           else if ( sensorName.equals("ecg") )
             {sensorIndex = 1;
              tableHeaderName = "ecg_filtered";}             
        }            
        
      // CLEAR the list if the list SIZE is five time bigger than needed
       if ( getList(sensorName).size() > sampleToExtract*5 )
          { getList(sensorName).clear(); PApplet.println("List"+sensorName+" is now empty"); }          
          
      if ( executionNumber[sensorIndex] >= getTable(sensorName).getRowCount()/sampleToExtract )
           executionNumber[sensorIndex] = 0;
      
      // INDEX IS SHIFTED TO AVOID READING ALWAYS THE SAME VALUES
      int multiplier = executionNumber[sensorIndex];
      int iStart = 0 + sampleToExtract*multiplier;
      int iEnd = sampleToExtract*( multiplier + 1);  //index range [sampleToExtract*multiplier, sampleToExtract*( multiplier + 1)]
      
      // add the content of the table to a LIST OF FLOAT
      TableRow row;
      for (int i = iStart; i<iEnd; i++)
      {
        row = getTable(sensorName).getRow(i);
        float newFloat = row.getFloat(tableHeaderName);
        getList(sensorName).append (newFloat);
      }       
     
     if ( getList(sensorName).size() > getTable(sensorName).getRowCount() )
    	 PApplet.println( "WARNING: class connection, storeFromText(): reading is slower than writing for sensor "+sensorName+"\n");
        
      //println("Read from table "+sensorName+" has completed.");
        
     executionNumber[sensorIndex]++;
   } //<>// //<>//
   
    // the function that reads the DATA from the SERIAL LINE BUFFER
   private void storeFromSerial()
  {      
      if ( !serialAvailable ) 
    	  PApplet.println(" ERROR: storeFromSerial has been called, but the port is not available");
      
      checkBufferSize("gsr");
      checkBufferSize("ecg");        
            
      short added = 0, counter = 0; //<>//
      
      myPort.readStringUntil(lineFeed); // clean the garbage //<>//

      // while there is something on the serial buffer, add the data to the "getList(sensorName)" queue      
      while ( serialAvailable && added < totSampleToExtract && counter < totSampleToExtract*3)
        {
            String inputString = myPort.readStringUntil(lineFeed);
            if (inputString != null) 
            {
              inputString = PApplet.trim(inputString); // removes "\t"
              
              //***** TODO: CHECK THIS CODE! *****
              //conversion from 
              //float inputValues[] = float(split(inputString, '\t') );
              
              String inputValuesString[] = ((PApplet.split(inputString, '\t')));              
              float inputValues[]=new float[inputValuesString.length];
              for(int i=0; i<inputValues.length;i++)
              {
            	  inputValues[i]=Float.parseFloat(inputValuesString[i]);
              }
              
              //*******************************************************
            		  
              if ( inputValues.length == 2 )
              {                  
            	  PApplet.println( " cond: " + inputValues[0]);
            	  PApplet.println( " ecg: " + inputValues[1]);
                  
                  getList("gsr").append(inputValues[0]);
                  getList("ecg").append(inputValues[1]);
                  
                  added++;
              }
            }            
            counter++;
        }
      
     myPort.clear();
     PApplet.println("");
     PApplet.println( "DEBUG : incomingGsr queue size: " + getList("gsr").size() );
     PApplet.println( "DEBUG : incomingEcg queue size: " + getList("ecg").size() );
     PApplet.println( "DEBUG : added elements: " + added*2 );
     if ( getList("gsr").size() == 0 ) PApplet.println(" ERROR in storeFromSerial: incomingGsrSize = 0 ");
     if ( getList("ecg").size() == 0 ) PApplet.println(" ERROR in storeFromSerial: incomingEcgSize = 0 "); 
  }
  
  private void checkBufferSize(String sensorName)
  {
      if ( getList(sensorName).size() > sampleToExtract*GlobalParams.global_fps ) // security check
      { 
        getList(sensorName).clear();
        PApplet.println("WARNING: list was getting big: list "+sensorName+" is now empty");
      }
  }
  
  // gives out numberOfElements elements from the selected list and ERASE THOSE ELEMENTS
  public FloatList extractFromBuffer (String sensorName, int numberOfElements) 
    {
      FloatList toOutput = new FloatList();  
      boolean emptyList = false;
      int originalListSize = getList(sensorName).size();   //<>//
      
      float inValue = 0;
 //<>//
     // extract numberOfElements of elements from conductance list

     while(! (getList(sensorName).size() <= originalListSize  - numberOfElements) && !emptyList) 
     {
          int currentListSize = getList(sensorName).size();
          if ( currentListSize > 0 )
             {                   
               // !connectionAvailable: there aren't any connections available, we're reading the DATA from an OFFLINE TABLE
               // with randomIndex we pick a different set of values for each cycle              

               int index = currentListSize - 1;
               if ( index >= 0 && index <= currentListSize )
                   {
                     inValue = getList(sensorName).remove( index );
                     toOutput.append( inValue ); //<>//
                     }               
              }
          else
              emptyList = true; 
     }  //<>//
    
     return toOutput;
    }
  
  public FloatList getList(String sensorName)
  {
    if( sensorName.equals("gsr") )
      return incomingGsr;
      
    if(sensorName.equals("ecg"))
      return incomingEcg;
    else 
      return null;
  }
  public Table getTable(String sensorName)
  {
    if( sensorName.equals("gsr") )
      return table_gsr;
      
    if(sensorName.equals("ecg"))
      return table_ecg;
    else 
      return null;
  }
}