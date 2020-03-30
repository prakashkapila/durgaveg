package org.durgaveg.com;

import org.durgaveg.com.driver.Spacebus3000;
import org.junit.jupiter.api.Test;

public class RoutesTest {

	/*Infrared Spacehub, Grand Nebula Spaceport
Blue Nova Space Market, Heavy Element Spacemine
Asteroid Research Institute, Oort Cloud Observation Facility
Infrared Spacehub, Oort Cloud Observation Facility
Oort Cloud Observation Facility, Double Ring Space Habitat

Each line means there’s a Spacebus route going back and forth between the two spaceports. In the example above, 
one can take a Spacebus from Asteroid Research Institute to Oort Cloud Observation Facility, 
and also from Oort Cloud Observation Facility to Infrared Spacehub, 
and also from Infrared Spacehub to Grand Nebula Spaceport. 
So it follows that one could travel all the way from Asteroid Research Institute to Grand Nebula Spaceport just using Spacebus.
 On the other hand, there is no way to get from Asteroid Research Institute to Heavy Element Spacemine using Spacebus.

Write a Java (or C++) program called “Spacebus3000” that takes three arguments - the name of a file listing Spacebus’s routes and the names of two spaceports – and outputs “yes” or “no” depending on whether one could travel from one of the spaceports to the other of the spaceports just using Spacebus. If either of the spaceport arguments isn't in the file, then the program should output "no".

Here are some sample Java interactions, assuming the example file above is named spacebusroutes.txt…*/
	Spacebus3000 inst = new Spacebus3000();
	String routes[]= new String[] {
			"Infrared Spacehub,Grand Nebula Spaceport",
			"Blue Nova Space Market,Heavy Element Spacemine",
			"Asteroid Research Institute,Oort Cloud Observation Facility",
			"Infrared Spacehub,Oort Cloud Observation Facility",
			"Oort Cloud Observation Facility,Double Ring Space Habitat"
			};
	@Test
	public void execcute() {
		inst.init(routes);
		String route = inst.containsRoute("Double Ring Space Habitat,Grand Nebula Spaceport".split(","));
		System.out.println(route);
		route = inst.containsRoute("“Asteroid Research Institute” Pluto".split(" +"));
		System.out.println(route);
	}
}
/*
java Spacebus3000 spacebusroutes.txt “Oort Cloud Observation Facility” “Asteroid Research Institute”
> yes

java Spacebus3000 spacebusroutes.txt “Asteroid Research Institute” “Grand Nebula Spaceport”
> yes

java Spacebus3000 spacebusroutes.txt “Asteroid Research Institute” “Heavy Element Spacemine”
> no

java Spacebus3000 spacebusroutes.txt “Asteroid Research Institute” Pluto
> no
*/