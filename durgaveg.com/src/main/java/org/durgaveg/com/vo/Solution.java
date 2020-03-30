package org.durgaveg.com.vo;

import java.util.List;

public class Solution {
	
	// list of Veges to put in the bag to have the maximal value
	public List<Vege> Veges;
	// maximal value possible
	public int value;
	
	public Solution(List<Vege> Veges, int value) {
		this.Veges = Veges;
		this.value = value;
	}
	
	public void display() {
		if (Veges != null  &&  !Veges.isEmpty()){
			System.out.println("\nKnapsack solution");
			System.out.println("Value = " + value);
			System.out.println("Veges to pick :");
			
			for (Vege Vege : Veges) {
				System.out.println("- " + Vege.toString());
			}
		}
	}

}
