package ch.epfl.cs107.play.game.twic.area.story;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ch.epfl.cs107.play.game.twic.handler.TWICInteractionVisitor;
import ch.epfl.cs107.play.game.actor.ImageGraphics;
import ch.epfl.cs107.play.game.areagame.actor.Interactable;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.areagame.io.ResourcePath;
import ch.epfl.cs107.play.game.twic.actor.Monster;
import ch.epfl.cs107.play.game.twic.actor.Personnage;
import ch.epfl.cs107.play.game.twic.actor.StoryPersonnage;
import ch.epfl.cs107.play.game.twic.actor.TWICPlayer;
import ch.epfl.cs107.play.game.twic.actor.TWICPlayerStatusGUI;
import ch.epfl.cs107.play.game.twic.actor.Zombie;
import ch.epfl.cs107.play.game.twic.area.Village;
import ch.epfl.cs107.play.game.twic.area.deco.GrotteClose;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.RandomGenerator;
import ch.epfl.cs107.play.math.RegionOfInterest;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.window.Canvas;
import ch.epfl.cs107.play.window.Keyboard;

public class StoryVillage extends Story {
	
	private final StoryVillageHandler handler;
	private final Village area;
	private StoryAdvancement advancement;
	private static boolean isStarted = false;
	
	private TWICPlayer player;
	private final Zombie[] zombies = new Zombie[2];
	private final StoryPersonnage niceHarpy;
	private final GrotteClose grotteClose;

	private int indexKeyboard = 1;
	
	private boolean thereAreZombie = true;
	private boolean thereArePersonnage = true;

	public StoryVillage(Village area) {
		super(area);
		this.area = area;
		handler = new StoryVillageHandler();
		advancement = StoryAdvancement.ADD_GROTTE_CLOSE;
		
		niceHarpy = new StoryPersonnage(area, Orientation.LEFT, new DiscreteCoordinates(25, 7), StoryPersonnage.StoryPerso.NICE_HARPY, false);
		grotteClose = new GrotteClose(this.area, Orientation.UP, new DiscreteCoordinates(25, 18));
	}
	
	protected static void setStart() {
		isStarted = true;
	}
	
	public boolean isStarted() {
		return isStarted;
	}
	
	@Override 
	public void draw(Canvas canvas) {
		
		switch(advancement) {
		case GO_TO_MID :
		case ZOMBIES_WIN :
		case PERSONNAGE_WIN :
			break;
		default : return;
		}
		
		float width = canvas.getScaledWidth();
		float height = canvas.getScaledHeight();
		Vector anchor = canvas.getTransform().getOrigin().sub(new Vector(width/2, height/2));

		ImageGraphics dialog = new ImageGraphics(ResourcePath.getSprite("personalAdds/txtVillage" + (char) (indexKeyboard + 48)),
				9, 3, new RegionOfInterest(0, 0, 240, 80), Vector.ZERO, 1, TWICPlayerStatusGUI.DEPTH);
		
		dialog.setAnchor(anchor.add(new Vector(width-9.5f, 0.5f)));
		dialog.draw(canvas);
	}

	@Override
	public void updateStory(float deltaTime) {
		niceHarpy.updateStory(deltaTime);
		if(advancement == StoryAdvancement.MOVE)
			player.updateStory(deltaTime);
		
		switch(advancement) {
		case ADD_GROTTE_CLOSE :
			area.registerActor(grotteClose);
			advancement = StoryAdvancement.NOT_START;
			break;
		case NOT_START:
			if(isStarted) {
				advancement = StoryAdvancement.PASSIF;
				area.unregisterActor(grotteClose);
			}
			break;
		case PASSIF:
			break;
		case ADD_ZOMBIES:
			for(int i = 0; i < zombies.length; ++i) 
				placeActors(i);
			advancement = StoryAdvancement.WAIT;
			break;
		case WAIT : 
			if(!thereAreZombie) 
				advancement = StoryAdvancement.GO_TO_MID;
			break;
		case GO_TO_MID : List<DiscreteCoordinates> collection = newList(new DiscreteCoordinates(16, 7));
		    if(collection.contains(player.getCurrentCells().get(0))) {
		    	++indexKeyboard;
		    	advancement = StoryAdvancement.MOVE;
		    	area.registerActor(niceHarpy);
		    	area.suspend();
		    }
			break;
		case MOVE : 
			if(moveActors()) {
				if(!thereArePersonnage)
		    		advancement = StoryAdvancement.ZOMBIES_WIN;
		    	else {
		    		++indexKeyboard;
		    		advancement = StoryAdvancement.PERSONNAGE_WIN;
		    	}
			}
			break;
		case ZOMBIES_WIN : dialog();
			if(indexKeyboard == 3) ++indexKeyboard;
			break;
		case PERSONNAGE_WIN : dialog();
			break;
		case LEAVE_HARPY :
			if(!move(niceHarpy, new DiscreteCoordinates(25, 7), area, 6)) {
				area.unregisterActor(niceHarpy);
				area.resume();
				advancement = StoryAdvancement.END;
				StoryFermeBis.setEnd();
			}
			break;
		case END: 
			break;
		}
		
		thereAreZombie = false;
		thereArePersonnage = false;
	}
	
	private void dialog() {
		Keyboard keyboard = area.getKeyboard();
		
		if(keyboard.get(Keyboard.ENTER).isDown() && !keyboard.get(Keyboard.ENTER).wasDown())
			++indexKeyboard;
		if(indexKeyboard > 6) {
			advancement = StoryAdvancement.LEAVE_HARPY;
		}

	}
	
	private boolean moveActors() {
		DiscreteCoordinates coords = player.getCurrentCells().get(0).jump(player.getOrientation().toVector().resized(2));
		boolean moveComplete = !move(niceHarpy, coords, area, 6);
		
		if(moveComplete)
			if(niceHarpy.getOrientation() != player.getOrientation().opposite())
				niceHarpy.moveOrientate(player.getOrientation().opposite(), 0);

		return moveComplete && niceHarpy.getOrientation() == player.getOrientation().opposite();
	}
	
	private List<DiscreteCoordinates> newList(DiscreteCoordinates coords) {
		List<DiscreteCoordinates> collection = new ArrayList<>();
		for(int i = -3; i <= 3; ++i)
			for(int j = -3; j <= 3; ++j)
				collection.add(new DiscreteCoordinates(coords.x+i, coords.y+j));
		return collection;
	}
	
	private void placeActors(int i) {
		int randomWidth, randomHeight;
		List<DiscreteCoordinates> collection;
		
		do {
			randomWidth = 11 + RandomGenerator.getInstance().nextInt(9);
			randomHeight = 5 + RandomGenerator.getInstance().nextInt(9);
			zombies[i] = new Zombie(area, Orientation.UP, new DiscreteCoordinates(randomWidth, randomHeight), 4);
			collection = Collections.singletonList(new DiscreteCoordinates(randomWidth, randomHeight));
		} while(!area.canEnterAreaCells(zombies[i], collection));
		
		area.registerActor(zombies[i]);
	}

	@Override
	public boolean isEnded() {
		return advancement == StoryAdvancement.END;
	}
	
	@Override
	public boolean wantsCellInteraction() {
		return advancement == StoryAdvancement.PASSIF;
	}
	
	@Override
	public List<DiscreteCoordinates> getCurrentCells() {
		List<DiscreteCoordinates> collection = new ArrayList<>();
		for(int i = 0; i < 26; i += 25) {
			collection.add(new DiscreteCoordinates(3+i, 18));
			collection.add(new DiscreteCoordinates(4+i, 17));
			collection.add(new DiscreteCoordinates(5+i, 17));
			collection.add(new DiscreteCoordinates(6+i, 18));
		}
		return collection;
	}
	
	@Override
	public boolean wantsViewInteraction() {
		return advancement == StoryAdvancement.WAIT || advancement == StoryAdvancement.GO_TO_MID || advancement == StoryAdvancement.MOVE;
	}
	
	@Override
	public List<DiscreteCoordinates> getFieldOfViewCells() {
		List<DiscreteCoordinates> collection = new ArrayList<>();
		for(int i = 0; i < 40; ++i)
			for(int j = 0; j < 20; ++j)
				collection.add(new DiscreteCoordinates(i, j));
		return collection;
	}
	
	@Override
	public void interactWith(Interactable other) {
		other.acceptInteraction(handler);
	}
	
	private class StoryVillageHandler implements TWICInteractionVisitor {
		public void interactWith(TWICPlayer twicPlayer) {
			if(advancement != StoryAdvancement.PASSIF)
				return;
			
			player = twicPlayer;
			advancement = StoryAdvancement.ADD_ZOMBIES;
		}
		
		public void interactWith(Personnage personnage) {
			thereArePersonnage = true;
		}
		
		public void interactWith(Monster monster) {
			thereAreZombie = true;
		}
	}
	
	private enum StoryAdvancement {
		ADD_GROTTE_CLOSE,
		NOT_START,
		PASSIF,
		ADD_ZOMBIES,
		WAIT, 
		GO_TO_MID,
		MOVE,
		ZOMBIES_WIN,
		PERSONNAGE_WIN,
		LEAVE_HARPY,
		END
	}

}
