package ch.epfl.cs107.play.game.twic.area;

import ch.epfl.cs107.play.game.actor.Actor;
import ch.epfl.cs107.play.game.twic.actor.TWICCollectableAreaEntity;
import ch.epfl.cs107.play.game.twic.area.story.Story;
import ch.epfl.cs107.play.game.areagame.actor.Background;
import ch.epfl.cs107.play.game.areagame.actor.Foreground;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.rpg.actor.Door;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.signal.logic.Logic;

public class Temple extends TWICArea {
	
	private final DiscreteCoordinates[] coordinates = {new DiscreteCoordinates(5, 5), 
		 new DiscreteCoordinates(4, 0)};

	@Override
	public String getTitle() {
		return "Temple";
	}

	@Override
	protected void createArea() {
		registerActor(new Background(this));
        registerActor(new Foreground(this));
        registerActor(new Door("RouteTemple", coordinates[0], Logic.TRUE, this,
        		Orientation.DOWN, coordinates[1]));
        
        registerActor(new TWICCollectableAreaEntity(this, Orientation.UP, new DiscreteCoordinates(4, 3), TWICCollectableAreaEntity.CollectableObject.STAFF_WATER));
	}

	@Override
	protected  Actor[] getActors() {
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