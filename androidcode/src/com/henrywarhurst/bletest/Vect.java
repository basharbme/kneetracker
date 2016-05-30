package com.henrywarhurst.bletest;

public class Vect {
	
	private double[] vec;
	private int len;
	
	//*************************constructor
	protected Vect(int l){
		vec=new double[l];
		len=l;
	}
	protected Vect(double[] a){
		vec=a;
		len=vec.length;
		
	}
	
	//*************************dot product
	protected double dot(Vect V){
		double out=0;
		for(int i=0;i<len;i++){
			out+=vec[i]*V.vec[i];
		}
		return out;
	}
	
	//***********************left multiplication
	
	protected Vect Lm(Matrix M){
		
		
		double[] out=new double[M.size()];
		
		for(int i=0;i<M.size();i++){
			for(int j=0;j<M.size();j++){
				out[i]+=vec[j]*M.get(j,i);
			}
		}
		return new Vect(out);
	}
	
	//**********************scalar multiplication
	
	protected Vect Sm(double s){
		double[] out = new double[len];
		for (int i=0;i<len;i++){
			out[i]=vec[i]*s;
		}
		return new Vect(out);
	}
	
	//***********************vector addition
	
	protected Vect add(Vect v){
		double[] out = new double[len];
		for(int i=0; i<len; i++){
			out[i] = vec[i] + v.get(i);
		}
		return new Vect(out);
	}
	
	//************************zeros
	
	protected static Vect zeros(int length){
		double[] out = new double[length];
		for (int i=0;i<length;i++){
			out[i]=0;
		}
		return new Vect(out);
	}
	
	//************************get()/set()
	protected void set(int i, double Val){
		 vec[i]=Val;
	 }
	
	protected double[] get(){
		return vec;
	}
	
	protected double get(int i){
		return vec[i];
	}
	
	protected int length(){
		return len;
	}
	

	
	
	

	
	
}
