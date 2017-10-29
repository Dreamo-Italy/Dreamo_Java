package it.dreamo.engine.util;

public class GlobalParams {
	
	private GlobalParams(){}
	
	//GLOBAL VARIABLES 
	static public int fps = 30;

	//keeps count of the number of particles that have been instantiated from the beginning of the program
	static public long particlesInstanciatedNumber = 0; 
	static public float sampleRate = 100; 
	//////////////////////////////////////////////////////////////////////////////////
	static public String GsrTable = "LastLogGsr.csv";///"log_conductance_SIM.csv";////
	static public String EcgTable = "TheVeryLastLogEcg.csv";///"log_ecg_SIM.csv";////
	//////////////////////////////////////////////////////////////////////////////////	
	
	static public final int SCENES_MAX = 100; 
	static public final int SOGLIA_SEL = 12;
	static public final int CHANGE_CHECK = 4*8;

	static public final int interlineSpace = 19;
	static public final int marginSpace = 10;
	
	static public final int sensorNumber = 2;

}
