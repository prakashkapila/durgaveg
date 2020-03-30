package org.durgaveg.com.vo;

public class Vege {
 enum wt {PAU,ARDHA,MUPPAU,kg,KONNIKG};
	String vegeName;
	
	private double weight; // weight can only be 250gm,500gm375gam, or multiples of 1 kg.
	double unitprice;
	double vegePrice;
	public String getVegeName() {
		return vegeName;
	}
	public void setVegeName(String vegeName) {
		this.vegeName = vegeName;
	}
	public double getUnitprice() {
		return unitprice;
	}
	public void setUnitprice(double unitprice) {
		this.unitprice = unitprice;
	}
	public double getVegePrice() {
		return vegePrice;
	}
	public void setVegePrice(double vegePrice) {
		this.vegePrice = vegePrice;
	}
	public Vege(String itemname, int i, int j) {
	 	this.vegeName =itemname;
		this.setWeight(i);
		
	}
	public double getWeight() {
		return weight;
	}
	public void setWeight(double weight) {
		this.weight = weight;
	}

}
