package ch.epfl.cs107.play.game.twic.area;

import ch.epfl.cs107.play.game.actor.Actor;
import ch.epfl.cs107.play.game.twic.actor.CastleDoor;
import ch.epfl.cs107.play.game.twic.actor.DarkLord;
import ch.epfl.cs107.play.game.twic.actor.Monster;
import ch.epfl.cs107.play.game.twic.actor.TWICCollectableAreaEntity;
import ch.epfl.cs107.play.game.twic.area.story.Story;
import ch.epfl.cs107.play.game.twic.area.story.StoryRouteChateau;
import ch.epfl.cs107.play.game.areagame.actor.Background;
import ch.epfl.cs107.play.game.areagame.actor.Foreground;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.rpg.actor.Door;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.signal.logic.Logic;

public class RouteChateau extends TWICArea {
	
	private final StoryRouteChateau story = new StoryRouteChateau(this);
	private final Monster[] monsters = new Monster[1];
	
	private final DiscreteCoordinates[][] coordinates = { {new DiscreteCoordinates(9, 18), 
		 new DiscreteCoordinates(9, 0), new DiscreteCoordinates(10, 0)} , 
		 {new DiscreteCoordinates(7, 1), new DiscreteCoordinates(9, 13), 
			 new DiscreteCoordinates(10, 13)} };

	@Override
	public String getTitle() {
		// TODO Auto-generated method stub
		return "RouteChateau";
	}

	@Override
	protected void createArea() {
		registerActor(new Background(this));
        registerActor(new Foreground(this));
        registerActor(new Door("Route", coordinates[0][0], Logic.TRUE, this,
        		Orientation.DOWN, coordinates[0][1], coordinates[0][2]));
        registerActor(new CastleDoor("Chateau", coordinates[1][0], Logic.FALSE, this,
        		Orientation.UP, coordinates[1][1], coordinates[1][2]));
        
        monsters[0] = new DarkLord(this, Orientation.DOWN, new DiscreteCoordinates(10, 12), 10.f, TWICCollectableAreaEntity.CollectableObject.HEART);
        registerActor(monsters[0]);
        
        registerActor(story);
        
	}

	@Override
	public Actor[] getActors() {
		return monsters;
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

