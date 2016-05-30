package com.henrywarhurst.bletest;

import java.lang.Math;

import android.content.Context;
import android.util.Log;

public class AdaptiveFilter {
	private static final String TAG = "AdaptiveFilter";
	
	// Private internal filters
	private Filter lowPass;
	private Filter diff;
	private Filter int2;
	// Flags/counters
	private boolean integrate;
	private boolean reset;
	private int inActivityCount;
	private int activityCount;
	// data buffer
	double BUF;
	// Constant parameters
	final int RESPONSE = 5; // no. of sampling periods after detection of jerk
							// before integration is turned on/off	
	final double TOL = 0.25;  //value of |da_dt| above which integration  is triggered.
	
	
	
	public AdaptiveFilter(Context context){
		lowPass = SOSparser.parse(context, "low_4_10Hz_2Hz.txt");
		diff = SOSparser.parse(context, "Opt_Diff.txt");
		int2 = SOSparser.parse(context, "single_Opt_Int.txt");
		
		integrate=false;
		reset = true;
		activityCount=0;
		inActivityCount=0;
		
		
		BUF = 0; // buffer to hold one previous value of displacement when da_dt is inactive
		
		
	}
	
	
	public double process(double x){
		Log.d(TAG, "Raw value = " + Double.toString(x));
		double a = lowPass.process(x);
		double da_dt = diff.process(a);
		double out = 0; // output is velocity
		
		
		if(Math.abs(da_dt)>TOL){ //..................................|da_dt|>TOL
			
			if(!integrate){ //........integration OFF
				activityCount++;
				
				out = BUF;
				
				if(activityCount > RESPONSE){
					integrate = true;
					activityCount = 0;
					reset = false;
				}
			}
			else{ //.................integration ON
				
				out = int2.process(a);
				BUF=out;
			}
		}
		else{ //...................................................|da_dt|<TOL
			
			if(integrate){ //..........integration ON
				
				activityCount++;
				
				if(activityCount > RESPONSE){
					integrate = false;
					activityCount = 0;
				}
				
				out = int2.process(a);

				
			}
			else{ //....................integration OFF
				
				out = BUF;
				
				if(!reset){
					inActivityCount++;
				}
				
				if(inActivityCount>3*RESPONSE){
					int2.reset();
					inActivityCount = 0;
					reset = true;
				}
			}
		}
		Log.d(TAG, "Processed data = " + Double.toString(out));
		return out;
	}
}

