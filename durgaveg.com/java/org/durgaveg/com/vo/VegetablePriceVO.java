package org.durgaveg.com.vo;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

public class VegetablePriceVO implements Serializable {

	private static final long serialVersionUID = -7604044620584908112L;
	String districtName, marketName,commodity,  variety, grade;
	Date priceDate;
	double minPrice=0.0, maxPrice=0.0, modalPrice=0.0;

	public static String retrieveMetaData() { 
		return "districtName, marketName,commodity,  variety, grade,minPrice, maxPrice, modalPrice,priceDate".toUpperCase();
	}
	
	
	public void apply(Iterable<String> iterable)
	{ 
		Iterator<String> vals = iterable.iterator();
		
			districtName = vals.next();
			marketName = vals.next();
			commodity = vals.next();
			variety = vals.next();
			grade  = vals.next();
			try {
			minPrice = Double.valueOf(vals.next());
			maxPrice = Double.valueOf(vals.next()); 
			modalPrice = Double.valueOf(vals.next());
			priceDate = new SimpleDateFormat().parse(vals.next());
			} catch (ParseException e) {
				priceDate =null;
			 	e.printStackTrace();
			}
	}
	
	public String getDistrictName() {
		return districtName;
	}

	public void setDistrictName(String districtName) {
		this.districtName = districtName;
	}

	public String getCommodity() {
		return commodity;
	}

	public void setCommodity(String commodity) {
		this.commodity = commodity;
	}

	public String getMarketName() {
		return marketName;
	}

	public void setMarketName(String marketName) {
		this.marketName = marketName;
	}

	public String getVariety() {
		return variety;
	}

	public void setVariety(String variety) {
		this.variety = variety;
	}

	public String getGrade() {
		return grade;
	}

	public void setGrade(String grade) {
		this.grade = grade;
	}

	public Date getPriceDate() {
		return priceDate;
	}

	public void setPriceDate(Date priceDate) {
		this.priceDate = priceDate;
	}

	public double getMinPrice() {
		return minPrice;
	}

	public void setMinPrice(double minPrice) {
		this.minPrice = minPrice;
	}

	public double getMaxPrice() {
		return maxPrice;
	}

	public void setMaxPrice(double maxPrice) {
		this.maxPrice = maxPrice;
	}

	public double getModalPrice() {
		return modalPrice;
	}

	public void setModalPrice(double modalPrice) {
		this.modalPrice = modalPrice;
	}

 

	
}
