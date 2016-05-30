package com.henrywarhurst.bletest;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.lang.Math.*;

import android.content.Context;
import android.util.Log;

public class SOSparser {
	private final static String TAG = "SOSparser";

	// **************************************************parse
	protected static Filter parse(Context context, String fileName) {

		double[][] SOS;
		double[] G;

		List<double[]> tempSOS = new ArrayList<>();
		List<Double> tempG = new ArrayList<>();
		String str="init";
		String[] strSect;
		char ch;
		boolean SOSassign = false;
		boolean Gassign = false;
		int count1 = 0;
		int count2 = 0;

		// ................................................................
		try {
	        InputStream fIn = context.getResources().getAssets()
	                .open(fileName, Context.MODE_WORLD_READABLE);
	        InputStreamReader isr = new InputStreamReader(fIn);
	        BufferedReader br = new BufferedReader(isr);
			// ..............................
			while (str != null) {
				// ..............................
				try {
					str = br.readLine();
					ch = str.charAt(0);

					if (ch == '%') {
						SOSassign = true;
						Gassign = false;
						continue;
					}

					if (ch == '>') {
						SOSassign = false;
						Gassign = true;
						count1 = 0;
						continue;
					}

					if (SOSassign) {
						count2 = 0;
						strSect = str.split("\\|");
						tempSOS.add(count1, new double[strSect.length]);
						for (String s : strSect) {
							tempSOS.get(count1)[count2++] = str2double(s);
						}
						count1++;
					}

					if (Gassign) {
						tempG.add(new Double(str2double(str)));
					}

				} catch (NullPointerException Exc) {
					continue;
				} catch (IndexOutOfBoundsException Exc) {
					continue;
				}
				// ..................................try/catch
			}
			// .........................................while

		} catch (IOException Exc) {
			Exc.printStackTrace();
		}
		// .........................................try/catch

		SOS = new double[tempSOS.size()][tempSOS.get(0).length];
		for(int i=0;i<tempSOS.size();i++){
			for(int j=0;j<tempSOS.get(0).length;j++){
				SOS[i][j]=tempSOS.get(i)[j];
			}
			
		}
		
		
		G = new double[tempG.size()];
		for(int i=0 ; i < tempG.size(); i++){
			G[i]=tempG.get(i);
		}
		
		return new Filter(SOS,G);
	}

	// ****************************************str2double
	private static double str2double(String str) {
		double out = 0;

		switch (str.length()) {

		case 1:
			out = (double) ((int) (str.charAt(0) - 48));
			break;
		case 2:
			out = -(double) ((int) (str.charAt(1) - 48));
			break;
		default:
			int[] num = char2Int(str.toCharArray());

			for (int i = 2; i < num.length; i++) {
				out += (Math.pow(10, num[1] + 2 - i) * num[i]);
			}

			if (num[0] == 1) {
				out *= -1;
			}
			break;
		}
		return out;
	}

	// ****************************************char2Int
	private static int[] char2Int(char[] ch) {
		/*
		 * First entry in output array is sign bit, 0 if positive, 1 if negative. Second entry in array is maximum 
		 * power of 10 present in the number. If the number is negative the first entry will also be negative.
		 */
		int[] out;
		int point = 0;
		int i = 2;

		if (ch[0] == '-') {
			out = new int[ch.length];
			out[0]=1;

		} else {
			out = new int[ch.length+1];
			out[0]=0;
		}

		// .........................
		for (char c : ch) {

			if (c == '-') {
				continue;
			}

			if (c == '.') {
				point=i-3;
				continue;
			}

			out[i++] = ((int) c) - 48; // ASCII for 0 is 48. Assume only
										// characters 0-9 present

		}
		// ..............................
		out[1] = point;
		return out;

	}
	// *******************************************

	
}
