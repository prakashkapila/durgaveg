package org.durgaveg.com;

import org.durgaveg.com.driver.OrderCollector;
import org.junit.Test;

/**
 * Unit test for simple App.
 */
public class OrderDriverTest 
   
{

	@Test
    public void printSample( )
    {
		OrderCollector driver = new OrderCollector();
		try {
			//driver.getAllMessages();
		} catch (Exception e) {
			e.printStackTrace();
		}
     }

  }
