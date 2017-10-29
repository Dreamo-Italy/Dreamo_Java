package it.dreamo.engine;

import processing.core.*;
import it.dreamo.engine.video.*;
import it.dreamo.engine.audio.*;
import it.dreamo.engine.util.*;



public class Dreamo extends PApplet {

	public static void main(String[] args) {
		PApplet.main("Dreamo");
	}
	
	
	
	public void settings(){
		size(1280, 720, FX2D);
	}

	public void setup(){


		//main object for display
		Stage global_stage; 
		//main object for connections
		Connection global_connection;

		Gsr global_gsr;
		Ecg global_ecg;

		BioMood global_bioMood;

		

		DebugPlot global_debugPlots;

		//global audio objects
		AudioManager global_audio;
		AudioProcessor audio_proc;
		//Tone global_tone;
		//Rhythm global_rhythm;
		Dynamic global_dyn;
		Timbre global_timbre;
		Rhythm global_rhythm;

		AudioDecisor audio_decisor;
		
		//****** VIDEO ******
		colorMode(HSB, 360, 100, 100, 100);
		  
		//fullScreen(FX2D,1);
		frameRate(GlobalParams.fps);
		noSmooth();
		//<>//
		//****** CONNECTION //<>// ****** //<>// //<>// //<>//
		global_connection = new Connection(this);

		//****** BIOSENSORS ******
		global_gsr = new Gsr(this);
		global_ecg= new Ecg(this);

		//****** BIOLOGICAL MOOD ******

		global_bioMood = new BioMood();


		  //****** AUDIO ******


		  global_audio = new AudioManager(this); //new audio manager
		  audio_proc = new AudioProcessor(global_audio.getBufferSize(),global_audio.getSampleRate()); //new audio processor
		  global_audio.addListener(audio_proc); //attach processor to manager
		  global_dyn = new Dynamic(global_audio.getBufferSize(),global_audio.getSampleRate()); //new dynamic features extractor
		  global_timbre = new Timbre(global_audio.getBufferSize(),global_audio.getSampleRate()); //new timbric features extractor
		  global_rhythm = new Rhythm(global_audio.getBufferSize(),global_audio.getSampleRate()); //new rhythmic features extractor
		  //add features extractors to our audio processor

		  global_rhythm.setSensitivity(15);
		  global_rhythm.setThreshold(3);


		  audio_proc.addDyn(global_dyn);
		  audio_proc.addTimbre(global_timbre);
		  audio_proc.addRhythm(global_rhythm);


		  audio_decisor = new AudioDecisor();
		  
		  //****** STAGE ******
		  global_stage = new Stage();

		  //scenes
		  global_stage.addScene(new BlankScene() );
		  global_stage.addScene(new ScenePlotter());
		  global_stage.addScene(new Spirals());
		  global_stage.addScene(new HelloShape(1));
		  global_stage.addScene(new Cyclo1());
		  global_stage.addScene(new ScenePresentation() );
		  global_stage.addScene(new Lissajous() );
		  global_stage.addScene(new SceneDynamicGrid());

		  //global_stage.addScene(new AudioDebug());
		  //global_stage.addScene(new Cyclo2());
		  //global_stage.addScene(new LineLine1());
		  //global_stage.addScene(new SceneFireworks());
		  //global_stage.addScene(new SceneDots());
		  //global_stage.addScene(new ScenePerlinNoise());
		  //global_stage.addScene(new HelloShape(0));
		  //global_stage.addScene(new DumbC());
		 

		  //debug plots
		  global_debugPlots = new DebugPlot(this);
		
	}
	
	public void draw(){
		
		long initTimeT = System.nanoTime(); // start time

		   long audioTime = System.nanoTime() - initTimeT;

		   global_connection.update();

		   long conT = System.nanoTime() - audioTime - initTimeT; // time elapsed after CONNECTION UPDATE

		   global_gsr.update();

		   long gsrT= (System.nanoTime() - conT -initTimeT - audioTime);// time elapsed after GSR UPDATE

		   global_ecg.update();

		   global_bioMood.update();

		   long ecgT = (System.nanoTime() - conT -initTimeT - audioTime- gsrT); // time elapsed after ECG UPDATE

		   global_stage.updateAndTrace();

		   long viT = (System.nanoTime() - gsrT - conT - initTimeT - audioTime-ecgT) ; // time elapsed after VIDEO UPDATE

		   global_debugPlots.update();


		   fill(120); // for the DEBUG text
		   stroke(120); // for the DEBUG text

		   text("particles: " + global_stage.getCurrentScene().getParticlesNumber() + "; framerate: " + nf(frameRate,2,1) + " \n", marginSpace, interlineSpace );
		   text("                                                      Frame Count: "+frameCount,marginSpace, interlineSpace );
		   //global_gsr.printDebug();// print the DEBUG TEXT related to the SKIN SENSOR
		   //global_ecg.printDebug();// print the DEBUG TEXT related to the ECG SENSOR

		   audio_decisor.run();

		 //<>//
		   long loopT = (System.nanoTime()  - initTimeT) ; // OVERALL TIME
		 //<>// //<>//

		   //----------- print the durations for debug purposes------------ //<>// //<>// //<>//
		   
		   
		   

		   println("    Audio update duration: "+ audioTime/1000 + " us");
		   println("    Connection update duration: "+ conT/1000 + " us");
		   println("    GSR update duration: "+ gsrT/1000 + " us");
		   println("    Video update duration: "+ viT/1000 + " us");
		   //println("");
		   println("    LOOP duration: "+ loopT/1000 + " us");
		   println("    MAX duration for framerate "+ int(frameRate) +": "+(1/frameRate*1000000)+" us");
		   //println("");
		   
		   

		   println("***************************************");
		   println("************** AUDIO PARAMETERS *****************");
		   //println("***************************************");
		   println("RMS AVG (NORM): "+ global_dyn.getRMSAvg());
		   println("DINAMICITY INDEX: "+global_dyn.getDynamicityIndex());
		   println("RMS SLOPE: "+ global_dyn.getRmsSlope());
		   //println("***************************************");
		   println("***************************************");
		   println("SPECTRAL CENTROID: "+global_timbre.getCentroidShortTimeAvgHz()+" Hz");
		   println("SPECTRAL CENTROID AVG (NORM): "+global_timbre.getCentroidAvg());
		   println("SPECTRAL CENTROID RELATIVE RATIO: "+global_timbre.getCentroidRelativeRatio());
		   //println("***************************************");
		   println("***************************************");
		   println("SPECTRAL COMPLEXITY (NORM): "+global_timbre.getComplexityAvg());
		   println("-50 SILENCE: "+ global_dyn.isSilence(-20));
		   println("*******************END*****************");
		   println("***************************************");
		   
		   //println("CLARITY: " + audio_decisor.getClarity());
		   //println("ENERGY: " + audio_decisor.getEnergy());
		   //println("AGITATION: " + audio_decisor.getAgitation());
		   //println("ROUGHNESS: " + audio_decisor.getRoughness());
		   
		   
		   global_stage.nextSceneIfSilence(-20);
		
	}
	public void mouseClicked()
	{

	  //Mood m = new Mood(random(-1,1), random(-1,1));
	  // global_stage.selectScenebyMood(m);
	  global_stage.nextScene();
	}

	public void keyPressed()
	{
	     if (key == 'c')
	     {
	       global_gsr.restartCalibration();
	       global_ecg.restartCalibration();
	       println("key pressed");
	     }
	}


	public void stop()
	     {
	       global_audio.stop();
	     }
}
