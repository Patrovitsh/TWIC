package ch.epfl.cs107.play.game.twic.area;

import java.util.Collections;
import java.util.List;

import ch.epfl.cs107.play.game.actor.Actor;
import ch.epfl.cs107.play.game.twic.actor.FlameSkull;
import ch.epfl.cs107.play.game.twic.actor.Monster;
import ch.epfl.cs107.play.game.twic.actor.TWICCollectableAreaEntity;
import ch.epfl.cs107.play.game.twic.actor.Zombie;
import ch.epfl.cs107.play.game.twic.area.story.Story;
import ch.epfl.cs107.play.game.areagame.actor.Background;
import ch.epfl.cs107.play.game.areagame.actor.Foreground;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.rpg.actor.Door;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.RandomGenerator;
import ch.epfl.cs107.play.signal.logic.Logic;

public class Grotte extends TWICArea {
	
	private final DiscreteCoordinates[][] coordinates = { {new DiscreteCoordinates(4, 17), new DiscreteCoordinates(16, 0)} , 
		 {new DiscreteCoordinates(8, 3), new DiscreteCoordinates(6, 32)},
		 {new DiscreteCoordinates(8, 1), new DiscreteCoordinates(18, 38)} };
	
	private final DiscreteCoordinates[] objectCoords = {new DiscreteCoordinates(22, 2), new DiscreteCoordinates(2, 4), 
			new DiscreteCoordinates(14, 12), new DiscreteCoordinates(1, 15), new DiscreteCoordinates(10, 25), 
			new DiscreteCoordinates(16, 25), new DiscreteCoordinates(23, 30), new DiscreteCoordinates(22, 37)};

	private final Monster[] monsters = new Monster[6];
	
	@Override
	public String getTitle() {
		return "Grotte";
	}

	@Override
	protected void createArea() {
		registerActor(new Background(this));
        registerActor(new Foreground(this));
        registerActor(new Door("Ferme", coordinates[0][0], Logic.TRUE, this,
        		Orientation.DOWN, coordinates[0][1]));
        registerActor(new Door("GrotteMew1", coordinates[1][0], Logic.TRUE, this,
        		Orientation.UP, coordinates[1][1]));
        registerActor(new Door("GrotteMewExt", coordinates[2][0], Logic.TRUE, this,
        		Orientation.UP, coordinates[2][1]));
        
        TWICCollectableAreaEntity.CollectableObject object;
        double randomDouble;

		for (DiscreteCoordinates objectCoord : objectCoords) {
			randomDouble = RandomGenerator.getInstance().nextDouble();
			if (randomDouble <= 0.5)
				object = TWICCollectableAreaEntity.CollectableObject.HEART;
			else
				object = TWICCollectableAreaEntity.CollectableObject.COIN;

			registerActor(new TWICCollectableAreaEntity(this, Orientation.UP, objectCoord, object));
		}
        
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
			
			if(i < monsters.length/2)
				monsters[i] = new Zombie(this, Orientation.fromInt(randomWidth % 3),
						new DiscreteCoordinates(randomWidth, randomHeight), 4);
			else 
				monsters[i] = new FlameSkull(this, Orientation.fromInt(randomHeight % 3),
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
