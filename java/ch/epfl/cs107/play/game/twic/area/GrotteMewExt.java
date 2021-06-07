package ch.epfl.cs107.play.game.twic.area;

import ch.epfl.cs107.play.game.actor.Actor;
import ch.epfl.cs107.play.game.twic.actor.Dragon;
import ch.epfl.cs107.play.game.twic.actor.Monster;
import ch.epfl.cs107.play.game.twic.actor.TWICItem;
import ch.epfl.cs107.play.game.twic.area.story.Story;
import ch.epfl.cs107.play.game.twic.area.story.StoryGrotteMewExt;
import ch.epfl.cs107.play.game.areagame.actor.Background;
import ch.epfl.cs107.play.game.areagame.actor.Foreground;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.rpg.actor.Door;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.signal.logic.Logic;

public class GrotteMewExt extends TWICArea {
	
	private final DiscreteCoordinates[] coordinates = {new DiscreteCoordinates(18, 37), new DiscreteCoordinates(8, 0)};
	
	private final Story story =  new StoryGrotteMewExt(this);
	private final Monster[] monsters = new Monster[1];

	@Override
	public String getTitle() {
		return "GrotteMewExt";
	}

	@Override
	protected void createArea() {
		registerActor(new Background(this));
        registerActor(new Foreground(this));
        registerActor(new Door("Grotte", coordinates[0], Logic.TRUE, this, Orientation.DOWN, coordinates[1]));
        
        monsters[0] = new Dragon(this, Orientation.DOWN, new DiscreteCoordinates(8, 9), 15.f, TWICItem.CASTLE_KEY);
        registerActor(monsters[0]);
        
        registerActor(story);
	}

	@Override
	public  Actor[] getActors() {
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
