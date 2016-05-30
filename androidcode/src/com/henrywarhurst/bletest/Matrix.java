package com.henrywarhurst.bletest;

public  class Matrix {
	
	private double[][] mat;
	private int dim;
	
	
	//****************constructor
	protected Matrix(int d){
		mat=new double[d][d];
		dim=d;
	}
	
	
	protected Matrix(double[][] a){
		mat=a;
		dim=mat.length;
		
	}
	
	
	//**********************right multiplication
	protected Vect Rm(Vect Vin) {

		double[] out = new double[dim];
		for (int i = 0; i < dim; i++) {
			for (int j = 0; j < dim; j++) {
				out[i] += mat[i][j] * Vin.get(j);
			}
		}

		return (new Vect(out));
	}
	

	
	//***********************scalar multiplication
	
	protected Matrix Sm(double s) {
		double[][] out = new double[dim][dim];
		for (int i = 0; i < dim; i++) {
			for (int j = 0; j < dim; j++) {
				out[i][j] = s * mat[j][j];
			}
		}
		return new Matrix(out);
	}
	
	
	//***************************get()/set()
	protected void set(int r, int c, double Val){
		mat[r][c]=Val;
	}
	
	protected double[][] get(){
		return mat;
	}
	
	protected double get(int i, int j){
		return mat[i][j];
	}
	
	protected int size(){
		return dim;
	}
	
}
