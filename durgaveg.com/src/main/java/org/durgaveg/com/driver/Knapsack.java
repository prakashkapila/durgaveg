package org.durgaveg.com.driver;

import java.util.ArrayList;
import java.util.List;

import org.durgaveg.com.vo.Solution;
import org.durgaveg.com.vo.Vege;

public class Knapsack {
	
	  // Veges of our problem
	  private Vege[] Veges;
	  // capacity of the bag
	  private int capacity;

	  public Knapsack(Vege[] Veges, int capacity) {
	    this.Veges = Veges;
	    this.capacity = capacity;
	  }

	  public void display() {
	    if (Veges != null  &&  Veges.length > 0) {
	      System.out.println("Knapsack problem");
	      System.out.println("Capacity : " + capacity);
	      System.out.println("Veges :");

	      for (Vege Vege : Veges) {
	        System.out.println("- " + Vege.toString());
	      }
	    }
	  }

	  // we write the solve algorithm
	  public Solution solve() {
	    int NB_VegeS = Veges.length;
	    // we use a matrix to store the max value at each n-th Vege
	    int[][] matrix = new int[NB_VegeS + 1][capacity + 1];

	    // first line is initialized to 0
	    for (int i = 0; i <= capacity; i++)
	      matrix[0][i] = 0;

	    // we iterate on Veges
	    for (int i = 1; i <= NB_VegeS; i++) {
	      // we iterate on each capacity
	      for (int j = 0; j <= capacity; j++) {
	        if (Veges[i - 1].getWeight() > j)
	          matrix[i][j] = matrix[i-1][j];
	        else
	          // we maximize value at this rank in the matrix
	          matrix[i][j] = Math.max(matrix[i-1][j], matrix[i-1][ j -Double.valueOf( Veges[i-1].getWeight()).intValue()] 
					  +(int) Veges[i-1].getUnitprice());
	      }
	    }

	    int res = matrix[NB_VegeS][capacity];
	    int w = capacity;
	    List<Vege> VegesSolution = new ArrayList<>();

	    for (int i = NB_VegeS; i > 0  &&  res > 0; i--) {
	      if (res != matrix[i-1][w]) {
	        VegesSolution.add(Veges[i-1]);
	        // we remove Veges value and weight
	        res -= Veges[i-1].getUnitprice();
	        w -= Veges[i-1].getWeight();
	      }
	    }

	    return new Solution(VegesSolution, matrix[NB_VegeS][capacity]);
	  }

	  public static void main(String[] args) {
	    // we take the same instance of the problem displayed in the image
	    Vege[] Veges = {new Vege("Elt1", 4, 12), 
		                new Vege("Elt2", 2, 1), 
						new Vege("Elt3", 2, 2), 
						new Vege("Elt4", 1, 1),
	                    new Vege("Elt5", 10, 4)};

	    Knapsack knapsack = new Knapsack(Veges, 15);
	    knapsack.display();
	    Solution solution = knapsack.solve();
	    solution.display();
	  }
	}
