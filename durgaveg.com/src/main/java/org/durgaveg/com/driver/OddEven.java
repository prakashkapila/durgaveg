package org.durgaveg.com.driver;

import java.util.Arrays;

public class OddEven {
 	    String ops="";
		public String calcone() {
			return      String.valueOf("0 + 1=1");
	  	}
		public String calcOdd(int input) {
			return input-1+" + 1 ="+input+",";
		}
		
	    public void calculate(int input){
	        if(input ==1) {
	        	ops = ops+(calcone());
	        	return;
	        }
	        int quot = 0;
	        quot = input % 2;
	        if(quot ==1)
	        {
	        	ops = ops+(calcOdd(input));
	        }
	        quot = input/2;
 	        ops = ops+( quot+" * 2 ="+quot*2+", ");
	        calculate(quot);
	    }
	    private String reverse() {
	    	String op[] = ops.split(",");
	    	String ret = "";
	    	for(int i=op.length-1;i>=0;i--)
	    	{
	    		ret += op[i]+",";
	    	}
	    	return ret;
	    }
	    public void checknumbers(int num[])
	    {
	    	for (int i:num)
	    	{
	    		calculate(i);
	    		System.out.println(reverse());
				ops = "";
	    	}
	    }
	    public void start(String args[])
	    {
	        if(args.length < 1)
		    {System.out.println("Please supply atleast one input number");}
		    int[] nums = new int[args.length];
		    for(int i=0;i<args.length;i++)
		    {
		        nums[i] = Integer.parseInt(args[i]);
		    }
			checknumbers(nums);
	    }
		public static void main (String[] args) {
			//code
			OddEven gfg = new OddEven();
		    gfg.start(args);
		}
	
}
