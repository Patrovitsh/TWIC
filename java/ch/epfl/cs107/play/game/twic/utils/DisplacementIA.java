/*
 *	Author:      Jean-Baptiste Moreau
 *	Date:        18 déc. 2019
 */

package ch.epfl.cs107.play.game.twic.utils;

import java.util.Collections;
import java.util.List;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.Interactable;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.RandomGenerator;
import ch.epfl.cs107.play.math.Vector;

public class DisplacementIA {
	
	private static Area area;
	private static Interactable interactable;
	private static DiscreteCoordinates myCoords;
	
	public static Orientation searchingDisplacement(Interactable anInteractable, DiscreteCoordinates destination,
			DiscreteCoordinates myCoordinates, Area anArea, int scope) {
		
		area = anArea;
		interactable = anInteractable;
		myCoords = myCoordinates;
		
		int dx = destination.x - myCoords.x;
		int dy = destination.y - myCoords.y;
		
		Vector vectorHorizontale = new Vector(dx, 0);
		Vector vectorVerticale = new Vector(0, dy);
		
		float lgHorizontale = vectorHorizontale.getLength();
		float lgVerticale = vectorVerticale.getLength();
		
		Orientation orientation;
		
		if(lgHorizontale < lgVerticale) {
			orientation = Orientation.fromVector(vectorVerticale);
			
			if(!canEnter(orientation) && (lgHorizontale > scope || lgVerticale > scope)) 
				orientation = smartOrientation(orientation, vectorHorizontale);
			
		} else {
			orientation = Orientation.fromVector(vectorHorizontale);
			
			if(!canEnter(orientation) && (lgHorizontale > scope || lgVerticale > scope)) 
				orientation = smartOrientation(orientation, vectorVerticale);
		}
		
		return orientation;
		
	}
	
	private static Orientation smartOrientation(Orientation orientation, Vector vector) {
		
		if(vector.x != 0.f || vector.y != 0.f)
			return Orientation.fromVector(vector);
		
		Orientation unOrientation;
		int randomInt = RandomGenerator.getInstance().nextInt(2);
		
		if(randomInt == 0) {
			unOrientation = orientation.hisLeft();
			if(canEnter(unOrientation)) 
				return unOrientation;
		} else {
			unOrientation = orientation.hisRight();
			if(canEnter(unOrientation)) 
				return unOrientation;
		}
		return null;
	}
	
	private static boolean canEnter(Orientation orientation) {
		if(orientation == null) 
			return false;
		List<DiscreteCoordinates> coords = Collections.singletonList(myCoords.jump(orientation.toVector()));
		return area.canEnterAreaCells(interactable, coords);
	}
	
}
