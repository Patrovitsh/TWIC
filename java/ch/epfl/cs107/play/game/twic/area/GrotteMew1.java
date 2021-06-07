package ch.epfl.cs107.play.game.twic.area;

import java.util.Collections;
import java.util.List;

import ch.epfl.cs107.play.game.actor.Actor;
import ch.epfl.cs107.play.game.twic.actor.FlameSkull;
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

public class GrotteMew1 extends TWICArea {
	
	private final DiscreteCoordinates[][] coordinates = { 
			{new DiscreteCoordinates(6, 31), new DiscreteCoordinates(8, 2)},
			{new DiscreteCoordinates(16, 1), new DiscreteCoordinates(8, 12)}	};
	
	private final Monster[] monsters = new Monster[6];

	@Override
	public String getTitle() {
		return "GrotteMew1";
	}

	@Override
	protected void createArea() {
		registerActor(new Background(this));
        registerActor(new Foreground(this));
        registerActor(new Door("Grotte", coordinates[0][0], Logic.TRUE, this, Orientation.DOWN, coordinates[0][1]));
        registerActor(new Door("Grotte", coordinates[1][0], Logic.TRUE, this, Orientation.UP, coordinates[1][1]));
        
        registerActor(new TWICCollectableAreaEntity(this, Orientation.UP, new DiscreteCoordinates(8, 7), TWICCollectableAreaEntity.CollectableObject.HEART));
        registerActor(new TWICCollectableAreaEntity(this, Orientation.UP, new DiscreteCoordinates(9, 7), TWICCollectableAreaEntity.CollectableObject.HEART));
        registerActor(new TWICCollectableAreaEntity(this, Orientation.UP, new DiscreteCoordinates(8, 8), TWICCollectableAreaEntity.CollectableObject.HEART));
        registerActor(new TWICCollectableAreaEntity(this, Orientation.UP, new DiscreteCoordinates(8, 6), TWICCollectableAreaEntity.CollectableObject.HEART));
        registerActor(new TWICCollectableAreaEntity(this, Orientation.UP, new DiscreteCoordinates(7, 7), TWICCollectableAreaEntity.CollectableObject.HEART));
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
			monsters[i] = new FlameSkull(this, Orientation.fromInt(randomWidth %3 ),
					new DiscreteCoordinates(randomWidth, randomHeight), 0.5f);
			collection = Collections.singletonList(new DiscreteCoordinates(randomWidth, randomHeight));
		} while(!canSpawnAreaCell(collection));
		
		registerActor(monsters[i]);
	}

	@Override
	protected int getTimeSpawn() {
		return 100;
	}

	@Override
	protected Story getStory() {
		// TODO Auto-generated method stub
		return null;
	}

}
