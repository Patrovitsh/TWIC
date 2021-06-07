package ch.epfl.cs107.play.game.twic.area;

import java.util.Collections;
import java.util.List;

import ch.epfl.cs107.play.game.actor.Actor;
import ch.epfl.cs107.play.game.twic.actor.*;
import ch.epfl.cs107.play.game.twic.area.deco.WaterFall;
import ch.epfl.cs107.play.game.twic.area.story.Story;
import ch.epfl.cs107.play.game.twic.area.story.StoryRoute;
import ch.epfl.cs107.play.game.areagame.actor.Background;
import ch.epfl.cs107.play.game.areagame.actor.Foreground;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.rpg.actor.Door;
import ch.epfl.cs107.play.game.twic.actor.Bridge;
import ch.epfl.cs107.play.game.twic.actor.Grass;
import ch.epfl.cs107.play.game.twic.actor.LogMonster;
import ch.epfl.cs107.play.game.twic.actor.Monster;
import ch.epfl.cs107.play.game.twic.actor.Orb;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.RandomGenerator;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.signal.logic.Logic;

public class Route extends TWICArea {
	
	private final StoryRoute story = new StoryRoute(this);
	private final Monster[] monsters = new Monster[3];
	
	private final DiscreteCoordinates[][] coordinates = { {new DiscreteCoordinates(18, 15), 
		 new DiscreteCoordinates(0, 15), new DiscreteCoordinates(0, 16)} , 
		 {new DiscreteCoordinates(29, 18), new DiscreteCoordinates(9, 0), 
			 new DiscreteCoordinates(10, 0)},
		 {new DiscreteCoordinates(9, 1), new DiscreteCoordinates(9, 19), 
				 new DiscreteCoordinates(10, 19)}, 
		 {new DiscreteCoordinates(1, 4), new DiscreteCoordinates(19, 9), 
				 new DiscreteCoordinates(19, 10), new DiscreteCoordinates(19, 11)} };
	
	@Override
	protected  Actor[] getActors() {
		return monsters;
	}
	
	@Override
	public String getTitle() {
		return "Route";
	}

	protected void createArea() {
        registerActor(new Background(this));
        registerActor(new Foreground(this));
        registerActor(new Door("Ferme", coordinates[0][0], Logic.TRUE, this,
        		Orientation.UP, coordinates[0][1], coordinates[0][2]));
        registerActor(new Door("Village", coordinates[1][0], Logic.TRUE, this,
        		Orientation.DOWN, coordinates[1][1], coordinates[1][2]));
        registerActor(new Door("RouteChateau", coordinates[2][0], Logic.TRUE, this,
        		Orientation.UP, coordinates[2][1], coordinates[2][2]));
        registerActor(new Door("RouteTemple", coordinates[3][0], Logic.TRUE, this,
        		Orientation.RIGHT, coordinates[3][1], coordinates[3][2], coordinates[3][3]));
        
        for(int i = 5; i <= 7; ++i) {
        	for(int j = 6; j <= 11; ++j) {
        		registerActor(new Grass(this, Orientation.UP, new DiscreteCoordinates(i, j)));
        	}
        }
        
        registerActor(new WaterFall(new Vector(15, 3)));
        registerActor(new Orb(this, Orientation.UP, new DiscreteCoordinates(19, 8)));
        registerActor(new Bridge(this, Orientation.UP, new DiscreteCoordinates(15, 9)));
        
        registerActor(story);

	}
	
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
		return 120;
	}

	@Override
	protected Story getStory() {
		return story;
	}
	
}
