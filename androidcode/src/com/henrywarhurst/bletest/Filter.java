package com.henrywarhurst.bletest;

import android.util.Log;

public class Filter {
	private Matrix[] F;
	private Vect[] q;
	private Vect[] g;
	private double[] d;
	private Vect[] V;
	private int N;
	private int P;
	
	// Circular array
	private double [] circularBuff;
	// Buffer size
	private static final int BUFF_SIZE = 16;
	// Pointer into the buffer
	private int bufferIdx = 0;
	
	private static final String TAG = "Filter";
	
	//**************************************************constructor
	public Filter(double[][] SOS, double[] G) {
		
		// Initialise circular buffer
		circularBuff = new double[BUFF_SIZE];
		
		N = SOS.length;
		
		//......................initializing state space vectors
		V= new Vect[N];

		// ..........................filling F, g, q and d
		F = new Matrix[N];
		q = new Vect[N];
		g = new Vect[N];
		d = new double[N];
		
		Log.d(TAG, Double.toString(SOS[0][5]));

		for (int i = 0; i < N; i++) {
			 P = (SOS[i].length / 2)-1; // filter of order P has SOS of length
			g[i] = new Vect(P);				 // 2(P+1) and state space array of
			F[i] = new Matrix(P);			// P-by-P
			q[i] = new Vect(P); 
			V[i] = Vect.zeros(P);     // initialising to zero;
			
			for (int j = 0; j < P; j++) {
				
				// ...............set q
				if (j == (P - 1)) {
					q[i].set(j, 1);
				} 
				else {
					q[i].set(j, 0);
				}
				// ................set g
				g[i].set(j, G[i] * (SOS[i][P - j] - SOS[i][0] * SOS[i][(2*P + 1) - j]));
				
				//.................set d
				d[i]=G[i]*SOS[i][0];
				
				//.................set F
				double val;
				for (int k = 0; k < P; k++) {

					if (k == (j + 1) && (j < P - 1)) {
						val = 1;
					} else if (j == (P - 1)) {
						val = -SOS[i][(2*P + 1) - k];
					} else {
						val = 0;
					}
					F[i].set(j, k, val);
				}
			}
		}
	}
	
	public Filter(String File){
		
	}
	
	//*************************************Process
	/*
	 * The one public method in the entire package
	 */
	public double process(double x){
		double y = 0;
		for(int k=0; k<N; k++){
			y=g[k].dot(V[k]) + d[k]*x;
			V[k]=F[k].Rm(V[k]).add(q[k].Sm(x));
			x=y;
		}
		return y;
	}
	
	//TODO: Remove this old burrara
//	public double process(double x){
//		Log.d(TAG, "BufferIdx: " + Double.toString(bufferIdx));
//		// Put the value into the buffer
//		circularBuff[bufferIdx] = x;
//		bufferIdxIncrement();
//		// Get mean of buffer
//		double mean = getBufferMean();
//		Log.d(TAG, "Mean " + Double.toString(mean));
//		// Normalise about the mean
//		x = x - mean;
//		// Perform filtering
//		double y = 0;
//		
////		for(int k=0; k<N; k++){
////			y=g[k].dot(V[k]) + d[k]*x;
////			V[k]=F[k].Rm(V[k]).add(q[k].Sm(x));
////			x=y;
////		}
//		Log.d(TAG, "Processed data " + Double.toString(x));
////		return y*2.5;
//		// TODO: remove the following line and put back the old return
//		return x;
//	}
	
	// Cicular buffer increment pointer
	private void bufferIdxIncrement() {
		if (bufferIdx < BUFF_SIZE - 1) {
			// Vanilla increment
			bufferIdx++;
		} else {
			// Circular feature
			bufferIdx = 0;
		}
	}
	
	// Get mean of the circular buffer
	private double getBufferMean() {
		double sum = 0;
		for (int i=0; i<circularBuff.length; ++i) {
			sum += circularBuff[i];
		}
		return sum/circularBuff.length;
	}
	
	
	
	/*public static void main(String[] args){
		
		Filter f= SOSparser.parse("Band_8_0.5_2.txt");
		
		double[] x = Vect.zeros(100).get();
		x[0]=1;
		
		double[] y = new double[x.length];
		
		for(int i=0; i<x.length;i++){
			y[i]= f.process(x[i]);
			System.out.println(y[i]);
		}
		
		
	}*/
	
	protected void reset(){
		for(int i=0; i<N; i++){
			V[i] = Vect.zeros(P);
		}
	}
	
}
