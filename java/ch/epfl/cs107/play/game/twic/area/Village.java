package ch.epfl.cs107.play.game.twic.area;

import java.util.Collections;
import java.util.List;

import ch.epfl.cs107.play.game.actor.Actor;
import ch.epfl.cs107.play.game.twic.actor.Personnage;
import ch.epfl.cs107.play.game.twic.actor.Shop;
import ch.epfl.cs107.play.game.twic.area.story.Story;
import ch.epfl.cs107.play.game.twic.area.story.StoryVillage;
import ch.epfl.cs107.play.game.areagame.actor.Background;
import ch.epfl.cs107.play.game.areagame.actor.Foreground;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.rpg.actor.Door;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.RandomGenerator;
import ch.epfl.cs107.play.signal.logic.Logic;

public class Village extends TWICArea {
	
	private final StoryVillage story = new StoryVillage(this);
	private final Personnage[] personnage = new Personnage[8];
	
	private final DiscreteCoordinates[][] coordinates = { {new DiscreteCoordinates(4, 1), 
		 new DiscreteCoordinates(4, 19), new DiscreteCoordinates(5, 19)} , 
		 {new DiscreteCoordinates(14, 1), new DiscreteCoordinates(13, 19), 
			 new DiscreteCoordinates(14, 19), new DiscreteCoordinates(15, 19)},
		 {new DiscreteCoordinates(9, 1), new DiscreteCoordinates(29, 19), 
			 new DiscreteCoordinates(30, 19)},
		 {new DiscreteCoordinates(8, 3), new DiscreteCoordinates(25, 18)} };
	
	@Override
	public String getTitle() {
		return "Village";
	}
	
	protected void createArea() {
        registerActor(new Background(this)) ;
        registerActor(new Foreground(this)) ;
        
        registerActor(new Door("Ferme", coordinates[0][0], Logic.TRUE, this,
        		Orientation.UP, coordinates[0][1], coordinates[0][2]));
        registerActor(new Door("Ferme", coordinates[1][0], Logic.TRUE, this,
        		Orientation.UP, coordinates[1][1], coordinates[1][2], coordinates[1][3]));
        registerActor(new Door("Route", coordinates[2][0], Logic.TRUE, this,
        		Orientation.UP, coordinates[2][1], coordinates[2][2]));
        registerActor(new Door("GrotteMew2", coordinates[3][0], Logic.TRUE, this,
        		Orientation.UP, coordinates[3][1]));
        
        registerActor(new Shop(this, Orientation.DOWN, new DiscreteCoordinates(17, 11)));
        
        registerActor(story);
    }

	@Override
	public Actor[] getActors() {
		return personnage;
	}

	@Override
	protected void placeActors(int i) {
		if(story.isStarted()) return;
		
		int randomWidth, randomHeight;
		List<DiscreteCoordinates> collection;
		
		do {
			randomWidth = RandomGenerator.getInstance().nextInt(getWidth());
			randomHeight = RandomGenerator.getInstance().nextInt(getHeight());
			personnage[i] = new Personnage(this, Orientation.UP, new DiscreteCoordinates(randomWidth, randomHeight), 4);
			collection = Collections.singletonList(new DiscreteCoordinates(randomWidth, randomHeight));
		} while(!canSpawnAreaCell(collection));
		
		registerActor(personnage[i]);
		
	}

	@Override
	protected int getTimeSpawn() {
		return 95;
	}

	@Override
	protected Story getStory() {
		return story;
	}
}