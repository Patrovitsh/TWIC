package ch.epfl.cs107.play.game.twic.area;

import ch.epfl.cs107.play.game.actor.Actor;
import ch.epfl.cs107.play.game.twic.area.story.Story;
import ch.epfl.cs107.play.game.areagame.actor.Background;
import ch.epfl.cs107.play.game.areagame.actor.Foreground;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.rpg.actor.Door;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.signal.logic.Logic;

public class Chambre extends TWICArea {
	
	private final DiscreteCoordinates[] coordinates = {new DiscreteCoordinates(6, 10), 
		 new DiscreteCoordinates(3, 0), new DiscreteCoordinates(4, 0)};  

	@Override
	public String getTitle() {
		return "PetalburgTimmy";
	}

	@Override
	protected void createArea() {
		registerActor(new Background(this));
        registerActor(new Foreground(this));
        registerActor(new Door("Ferme", coordinates[0], Logic.TRUE, this,
        		Orientation.DOWN, coordinates[1], coordinates[2]));
	}

	@Override
	protected Actor[] getActors() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void placeActors(int i) {
		// TODO Auto-generated method stub
	}

	@Override
	protected int getTimeSpawn() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	protected Story getStory() {
		// TODO Auto-generated method stub
		return null;
	}

}
