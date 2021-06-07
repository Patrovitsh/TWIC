package ch.epfl.cs107.play.game.twic.area;

import java.util.Collections;
import java.util.List;

import ch.epfl.cs107.play.game.actor.Actor;
import ch.epfl.cs107.play.game.twic.actor.LogMonster;
import ch.epfl.cs107.play.game.twic.actor.Monster;
import ch.epfl.cs107.play.game.twic.actor.TWICCollectableAreaEntity;
import ch.epfl.cs107.play.game.twic.area.story.Story;
import ch.epfl.cs107.play.game.areagame.actor.Background;
import ch.epfl.cs107.play.game.areagame.actor.Foreground;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.rpg.actor.Door;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.RandomGenerator;
import ch.epfl.cs107.play.signal.logic.Logic;

public class RouteTemple extends TWICArea {
	
	private final DiscreteCoordinates[][] coordinates = { {new DiscreteCoordinates(18, 10), 
		 new DiscreteCoordinates(0, 4), new DiscreteCoordinates(0, 5), new DiscreteCoordinates(0, 6)} , 
		 {new DiscreteCoordinates(4, 1), new DiscreteCoordinates(5, 6)} };
	
	private final Monster[] monsters = new Monster[1];

	@Override
	public String getTitle() {
		return "RouteTemple";
	}

	@Override
	protected void createArea() {
		registerActor(new Background(this));
        registerActor(new Foreground(this));
        registerActor(new Door("Route", coordinates[0][0], Logic.TRUE, this,
        		Orientation.LEFT, coordinates[0][1], coordinates[0][2], coordinates[0][3]));
        registerActor(new Door("Temple", coordinates[1][0], Logic.TRUE, this,
        		Orientation.UP, coordinates[1][1]));
        
	}

	@Override
	protected  Actor[] getActors() {
		return monsters;
	}

	@Override
	protected void placeActors(int i) {
    int randomWidth, randomHeight;
    List<DiscreteCoordinates> collection;
		
		do {
			randomWidth = RandomGenerator.getInstance().nextInt(getWidth());
			randomHeight = RandomGenerator.getInstance().nextInt(getHeight());
			monsters[i] = new LogMonster(this, Orientation.fromInt(randomWidth % 3),
					new DiscreteCoordinates(randomWidth, randomHeight), 4.f, TWICCollectableAreaEntity.CollectableObject.COIN);
			collection = Collections.singletonList(new DiscreteCoordinates(randomWidth, randomHeight));
		} while(!canSpawnAreaCell(collection));
		
		registerActor(monsters[i]);
	}

	@Override
	protected int getTimeSpawn() {
		return 80;
	}

	@Override
	protected Story getStory() {
		// TODO Auto-generated method stub
		return null;
	}

}
