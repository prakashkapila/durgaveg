package org.durgaveg.com.vo;

import java.io.Serializable;

import org.apache.spark.sql.Row;

public class MergeVegeVO implements Serializable {
 
	private static final long serialVersionUID = 6493319361578413400L;
	
	private String name, category,productVariation;
	private Integer noOfItems;
	private double qty, itemCost, productCurrentPrice, totalamount,unitCost;

	public Integer getNoOfItems() {
		return noOfItems;
	}

	public void setNoOfItems(Integer noOfItems) {
		this.noOfItems = noOfItems;
	}


 
	
	public double getUnitCost() {
		return unitCost;
	}

	public void setUnitCost(double unitCost) {
		this.unitCost = unitCost;
	}

	public String getProductVariation() {
		return productVariation;
	}

	public void setProductVariation(String productVariation) {
		this.productVariation = productVariation;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public double getQty() {
		return qty;
	}

	public void setQty(double qty) {
		
		this.qty = qty;
	}

	public double getItemCost() {
		return itemCost;
	}

	public void setItemCost(double itemCost) {
		this.itemCost = itemCost;
	}

	public double getProductCurrentPrice() {
		return productCurrentPrice;
	}

	public void setProductCurrentPrice(double productCurrentPrice) {
		this.productCurrentPrice = productCurrentPrice;
	}

	public double getTotalamount() {
		return totalamount;
	}

	public void setTotalamount(double totalamount) {
		this.totalamount = totalamount;
	}

	public static MergeVegeVO getInstance(Row arg0) {
		MergeVegeVO vo = new MergeVegeVO();
		vo.setCategory(arg0.getString(0));
		vo.setName(arg0.getString(1));
		Integer num = arg0.get(3) != null ? arg0.getInt(2):0;
		vo.noOfItems = num;
		vo.setQty();
		Double val = 0.0;
		val = arg0.get(5) != null ?arg0.getDouble(5):0.0 ;
		vo.setItemCost(val);
		val = arg0.get(6) != null ?arg0.getDouble(6):0.0; 
 		vo.setProductCurrentPrice(val);
 		vo.setTotalamount(vo.getQty() * vo.getProductCurrentPrice());
 		vo.setUnitCost();
		return vo;
	}

	private void setUnitCost() {
		unitCost = totalamount/qty;
	}
	private void setQty() {
		// TODO Auto-generated method stub
		StringBuilder qt = new StringBuilder();
		String[] qtys = name.split("-");
		if(!(name.toLowerCase().contains("kg") || name.contains("Bunch") ||name.contains(".")))
				{
					qt.append(".");
				}
		
		
		char[] temp = qtys[qtys.length-1].toCharArray(); 
		for(char c:temp)
		{
			if(Character.isDigit(c) || c=='.')
			{
				qt.append(c);
			}
		}
		this.qty = Double.valueOf(qt.toString()); 
		this.qty *=this.noOfItems;
		name = qtys[0];
	}
}
