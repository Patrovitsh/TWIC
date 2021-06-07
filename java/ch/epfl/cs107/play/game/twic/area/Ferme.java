package ch.epfl.cs107.play.game.twic.area;

import java.util.Collections;
import java.util.List;

import ch.epfl.cs107.play.game.actor.Actor;
import ch.epfl.cs107.play.game.twic.actor.LogMonster;
import ch.epfl.cs107.play.game.twic.actor.Monster;
import ch.epfl.cs107.play.game.twic.actor.TWICCollectableAreaEntity;
import ch.epfl.cs107.play.game.twic.area.story.Story;
import ch.epfl.cs107.play.game.twic.area.story.StoryFerme;
import ch.epfl.cs107.play.game.twic.area.story.StoryFermeBis;
import ch.epfl.cs107.play.game.areagame.actor.Background;
import ch.epfl.cs107.play.game.areagame.actor.Foreground;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.rpg.actor.Door;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.RandomGenerator;
import ch.epfl.cs107.play.signal.logic.Logic;

public class Ferme extends TWICArea {

	private final Story[] story = {new StoryFerme(this), new StoryFermeBis(this)};
	
	private final DiscreteCoordinates[][] coordinates = { {new DiscreteCoordinates(1, 15), 
			 new DiscreteCoordinates(19, 15), new DiscreteCoordinates(19, 16)} , 
			 {new DiscreteCoordinates(4, 18), new DiscreteCoordinates(4, 0), 
				 new DiscreteCoordinates(5, 0)},
			 {new DiscreteCoordinates(14, 18), new DiscreteCoordinates(13, 0), 
				 new DiscreteCoordinates(14,0)},
			 {new DiscreteCoordinates(4, 1), new DiscreteCoordinates(6, 11)},
			 {new DiscreteCoordinates(16, 1), new DiscreteCoordinates(3, 18), 
				 new DiscreteCoordinates(4,18)}};  
	
	private final Monster[] monsters= new Monster[2];
	
	@Override
	public String getTitle() {
		return "Ferme";
	}
	
	@Override
	protected  Actor[] getActors() {
		return monsters;
	}
	
	@Override 
	protected Story getStory() {
		if(!story[0].isEnded())
			return story[0];
		else 	
			return story[1];
	}

	@Override
	protected void createArea() {
        // Base
        registerActor(new Background(this));
        registerActor(new Foreground(this));
        registerActor(new Door("Route", coordinates[0][0], Logic.TRUE, this,
        		Orientation.RIGHT, coordinates[0][1], coordinates[0][2]));
        registerActor(new Door("Village", coordinates[1][0], Logic.TRUE, this,
        		Orientation.DOWN, coordinates[1][1], coordinates[1][2]));
        registerActor(new Door("Village", coordinates[2][0], Logic.TRUE, this,
        		Orientation.DOWN, coordinates[2][1], coordinates[2][2]));
        registerActor(new Door("PetalburgTimmy", coordinates[3][0], Logic.TRUE, this,
        		Orientation.UP, coordinates[3][1]));
        registerActor(new Door("Grotte", coordinates[4][0], Logic.TRUE, this,
        		Orientation.UP, coordinates[4][1], coordinates[4][2]));
        
        registerActor(story[0]);
        registerActor(story[1]);
        
	}
	
	@Override
	protected void placeActors(int i) {
		if(!story[0].isEnded()) return;
		
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
		return 100;
	}
	
}
