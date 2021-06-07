package ch.epfl.cs107.play.game.twic.area;

import ch.epfl.cs107.play.game.actor.Actor;
import ch.epfl.cs107.play.game.twic.area.story.Story;
import ch.epfl.cs107.play.game.twic.area.story.StoryChateau;
import ch.epfl.cs107.play.game.areagame.actor.Background;
import ch.epfl.cs107.play.game.areagame.actor.Foreground;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.rpg.actor.Door;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.signal.logic.Logic;

public class Chateau extends TWICArea {
	
	private final DiscreteCoordinates[][] coordinates = { {new DiscreteCoordinates(9, 12), 
		 new DiscreteCoordinates(7, 0), new DiscreteCoordinates(8, 0)} };

	private final Story story = new StoryChateau(this);
	
	@Override
	public String getTitle() {
		return "Chateau";
	}

	@Override
	protected void createArea() {
		// TODO Auto-generated method stub
		registerActor(new Background(this));
        registerActor(new Foreground(this));
        registerActor(new Door("RouteChateau", coordinates[0][0], Logic.TRUE, this,
        		Orientation.DOWN , coordinates[0][1], coordinates[0][2]));
        
        registerActor(story);
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
		return story;
	}

}
