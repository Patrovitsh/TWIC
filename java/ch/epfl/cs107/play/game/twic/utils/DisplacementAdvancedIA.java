/*
 *	Author:      Jean-Baptiste Moreau
 *	Date:        17 déc. 2019
 */

package ch.epfl.cs107.play.game.twic.utils;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.Interactable;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.RandomGenerator;
import ch.epfl.cs107.play.math.Vector;

public class DisplacementAdvancedIA {
	
	private static final int TIMER = 10;
	
	private static Area area;
	private static Interactable interactable;
	private static DiscreteCoordinates myCoords;
	
	private static final Map<Interactable, DiscreteCoordinates> mapOfCoords = new HashMap<>();
	private static final Map<Interactable, Integer> mapOfTimers = new HashMap<>();
	
	
	public static Orientation goToTarget(Interactable anInteractable, DiscreteCoordinates wantedDestination, 
			DiscreteCoordinates myCoordinates, Area anArea, int scope) {
		area = anArea;
		interactable = anInteractable;
		myCoords = myCoordinates;
		
		if(!mapOfTimers.containsKey(interactable))
			mapOfTimers.put(interactable, TIMER);
		
		DiscreteCoordinates destination = getDestinationFromeMap(wantedDestination);
		
		int dx = destination.x - myCoords.x;
		int dy = destination.y - myCoords.y;
		
		Vector vectorHorizontale = new Vector(dx, 0);
		Vector vectorVerticale = new Vector(0, dy);
		
		float lgHorizontale = vectorHorizontale.getLength();
		float lgVerticale = vectorVerticale.getLength();
		
		Orientation orientation;
		Vector chosenVector;
		
		if(lgHorizontale < lgVerticale) 
			chosenVector = vectorVerticale;
			
		else if(lgHorizontale > lgVerticale)
			chosenVector = vectorHorizontale;
			
		else {
			int randomInt = RandomGenerator.getInstance().nextInt(2);
			
			if (randomInt == 0) 
				chosenVector = vectorVerticale;
			else 
				chosenVector = vectorHorizontale;
		}
		
		orientation = Orientation.fromVector(chosenVector);
		
		if(!canEnter(orientation) && (lgHorizontale > scope || lgVerticale > scope)) {
				
			if(chosenVector == vectorVerticale && Orientation.fromVector(vectorHorizontale) != null)
				orientation = Orientation.fromVector(vectorHorizontale);
			
			else if(chosenVector == vectorHorizontale && Orientation.fromVector(vectorVerticale) != null)
				orientation = Orientation.fromVector(vectorVerticale);
			
			else {
				int time = mapOfTimers.replace(interactable, mapOfTimers.get(interactable)-1);
				--time;
				
				if(time > 0) 
					return orientation;
				else 
					mapOfTimers.replace(interactable, TIMER);
				
				chooseNewStepDestination(orientation);
			}
			
		} else 
			mapOfTimers.replace(interactable, TIMER);
		
		return orientation;
	}
	
	
	public static Orientation goToTarget(Interactable anInteractable, DiscreteCoordinates destination, 
			DiscreteCoordinates myCoordinates, Area anArea) {
		return goToTarget(anInteractable, destination, myCoordinates, anArea, 1);
	}
	
	
	private static DiscreteCoordinates getDestinationFromeMap(DiscreteCoordinates wantedDestination) {
		if(mapOfCoords.containsKey(interactable)) {
			DiscreteCoordinates destination = mapOfCoords.get(interactable);
			
			if(destination.equals(myCoords)) {
				mapOfCoords.remove(interactable);
				return wantedDestination;
			} else 
				return destination;
		}
			
		return wantedDestination;
	}
	
	
	private static void chooseNewStepDestination(Orientation orientation) {
		if(mapOfCoords.containsKey(interactable)) 
			return;
		
		DiscreteCoordinates stepDestination = findDestination(orientation, 1, true, true);
		
		mapOfCoords.put(interactable, stepDestination);
		
	}
	
	
	private static DiscreteCoordinates findDestination(Orientation orientation, int size, boolean rightIsOpen, boolean leftIsOpen) {
		Orientation orientationLeft = orientation.hisLeft();
		Orientation orientationRight = orientation.hisRight();
		
		boolean canEnterRight = canEnter(myCoords, orientationRight, size) && rightIsOpen;
		boolean canEnterLeft = canEnter(myCoords, orientationLeft, size) && leftIsOpen;
		
		DiscreteCoordinates coords;
			
		if(canEnterRight || canEnterLeft) {
			
			if(canEnterRight && canEnterDiagonal(orientationRight, true, size)) {
				coords = getCoordinates(myCoords, orientationRight, size);
				coords = getCoordinates(coords, orientationRight.hisLeft(), 1);
				
			} else if(canEnterLeft && canEnterDiagonal(orientationLeft, false, size)) {
				coords = getCoordinates(myCoords, orientationLeft, size);
				coords = getCoordinates(coords, orientationLeft.hisRight(), 1);
				
			} else 
				coords = findDestination(orientation, size+1, canEnterRight, canEnterLeft);
				
			
		} else 
			return myCoords;
		
		return coords;
	}
	
	
	private static boolean canEnterDiagonal(Orientation anOrientation, boolean diagonalRight, int size) {
		DiscreteCoordinates coords = getCoordinates(myCoords, anOrientation, size);
		
		if(diagonalRight)
			return canEnter(coords, anOrientation.hisLeft(), 1);
		else 
			return canEnter(coords, anOrientation.hisRight(), 1);
	}
	
	
	private static boolean canEnter(DiscreteCoordinates coords, Orientation orientation, int size) {
		if(orientation == null) 
			return true;
		List<DiscreteCoordinates> collection = Collections.singletonList(getCoordinates(coords, orientation, size));
		return area.canEnterAreaCells(interactable, collection);
	}
	
	
	private static boolean canEnter(Orientation orientation) {
		return canEnter(myCoords, orientation, 1);
	}
	
	
	private static DiscreteCoordinates getCoordinates(DiscreteCoordinates coords, Orientation orientation, int size) {
		return coords.jump(orientation.toVector().resized(size));
	}
	
}
