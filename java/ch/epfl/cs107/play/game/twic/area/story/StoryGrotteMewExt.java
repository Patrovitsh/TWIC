package ch.epfl.cs107.play.game.twic.area.story;

import java.util.ArrayList;
import java.util.List;

import ch.epfl.cs107.play.game.twic.handler.TWICInteractionVisitor;
import ch.epfl.cs107.play.game.actor.ImageGraphics;
import ch.epfl.cs107.play.game.areagame.actor.Interactable;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.areagame.io.ResourcePath;
import ch.epfl.cs107.play.game.twic.actor.CastleKey;
import ch.epfl.cs107.play.game.twic.actor.Dragon;
import ch.epfl.cs107.play.game.twic.actor.StoryPersonnage;
import ch.epfl.cs107.play.game.twic.actor.TWICPlayer;
import ch.epfl.cs107.play.game.twic.actor.TWICPlayerStatusGUI;
import ch.epfl.cs107.play.game.twic.area.GrotteMewExt;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.RegionOfInterest;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.window.Canvas;
import ch.epfl.cs107.play.window.Keyboard;

public class StoryGrotteMewExt extends Story {
	
	private final StoryGrotteMewExtHandler handler;
	private final GrotteMewExt area;
	private StoryAdvancement advancement;
	
	private TWICPlayer player;
	private final StoryPersonnage niceHarpy;
	private final StoryPersonnage badHarpy;
	private CastleKey castleKey;
	
	private DiscreteCoordinates coordinates;
	private Orientation orientation;
	private StoryPersonnage harpy;
	private int indexMax;

	private int indexKeyboard = 1;
	
	public StoryGrotteMewExt(GrotteMewExt area) {
		super(area);
		this.area = area;
		handler = new StoryGrotteMewExtHandler();
		advancement = StoryAdvancement.NOT_START;
		
		niceHarpy = new StoryPersonnage(area, Orientation.UP, new DiscreteCoordinates(8, 1), StoryPersonnage.StoryPerso.NICE_HARPY);
		
		badHarpy = new StoryPersonnage(area, Orientation.UP, new DiscreteCoordinates(8, 1), StoryPersonnage.StoryPerso.BAD_HARPY);
	}
	
	@Override 
	public void draw(Canvas canvas) {
		switch(advancement) {
		case DIALOG_1 :
		case CHOOSE_YOUR_SIDE:
		case DIALOG_2 :
			break;
		default: return;
		}
		
		float width = canvas.getScaledWidth();
		float height = canvas.getScaledHeight();
		Vector anchor = canvas.getTransform().getOrigin().sub(new Vector(width/2, height/2));

		ImageGraphics dialog = new ImageGraphics(ResourcePath.getSprite("personalAdds/txtGrotteMewExt" + (char) (indexKeyboard + 48)),
				9, 3, new RegionOfInterest(0, 0, 240, 80), Vector.ZERO, 1, TWICPlayerStatusGUI.DEPTH);
		
		dialog.setAnchor(anchor.add(new Vector(width-9.5f, 0.5f)));
		dialog.draw(canvas);
	}

	@Override
	public void updateStory(float deltaTime) {
		if(advancement != StoryAdvancement.CHOOSE_YOUR_SIDE) {
			niceHarpy.updateStory(deltaTime);
			badHarpy.updateStory(deltaTime);
		}
		
		if(advancement != StoryAdvancement.NOT_START && advancement != StoryAdvancement.CLEAR 
				&& advancement != StoryAdvancement.CHOOSE_YOUR_SIDE)
			player.updateStory(deltaTime);
		
		switch(advancement) {
		case NOT_START : Dragon dragon = (Dragon) area.getActors()[0];
			if(!dragon.isLiving()) 
				advancement = StoryAdvancement.CLEAR;
			break;
		case CLEAR :
			if(castleKey != null && castleKey.isCollect()) {
				advancement = StoryAdvancement.MOVE;
				area.registerActor(niceHarpy);
				area.suspend();
			}
			break;
		case MOVE : faceToFace();
			break;
		case ADD_BAD_HARPY : badHarpyAgainstOthers();
			break;
		case DIALOG_1 : 
			if(dialog(4, StoryAdvancement.CHOOSE_YOUR_SIDE)) {
				area.resume();
			}
			break;
		case CHOOSE_YOUR_SIDE :
			if(!niceHarpy.isLiving()) {
				coordinates = new DiscreteCoordinates(8, 8);
				orientation = Orientation.DOWN;
				harpy = badHarpy;
				indexMax = 8;
				++indexKeyboard;
			} else if(!badHarpy.isLiving()) {
				coordinates = new DiscreteCoordinates(9, 6);
				orientation = Orientation.UP;
				harpy = niceHarpy;
				indexMax = 6;
			}
			if(!niceHarpy.isLiving() || !badHarpy.isLiving()) {
				++indexKeyboard;
				advancement = StoryAdvancement.MOVE_PLAYER;
				area.suspend();
			}
			break;
		case MOVE_PLAYER :
			boolean moveComplete = !move(player, coordinates, area);
			if(moveComplete && player.getOrientation() != orientation)
				player.moveOrientate(orientation, 0);
			if(moveComplete && player.getOrientation() == orientation)
				advancement = StoryAdvancement.DIALOG_2;
			break;
		case DIALOG_2 : dialog(indexMax, StoryAdvancement.LEAVE_HARPY);
			break;
		case LEAVE_HARPY : 
			if(!move(harpy, new DiscreteCoordinates(8, 1), area, 6)) {
				area.unregisterActor(harpy);
				area.resume();
				advancement = StoryAdvancement.END;
				StoryChateau.setPerso(harpy.getTypePerso());
			}
			break;
		case END : 
			break;
		}
		
	}
	
	private boolean dialog(int index, StoryAdvancement nextAdvancement) {
		Keyboard keyboard = area.getKeyboard();
		
		if(keyboard.get(Keyboard.ENTER).isDown() && !keyboard.get(Keyboard.ENTER).wasDown())
			++indexKeyboard;
		if(indexKeyboard > index) {
			advancement = nextAdvancement;
			return true;
		}
		
		return false;
	}
	
	private void badHarpyAgainstOthers() {
		boolean moveComplete2 = !move(badHarpy, new DiscreteCoordinates(8, 6), area, 6);
		
		if(player.getOrientation() != Orientation.DOWN)
			player.moveOrientate(Orientation.DOWN, 0);
		
		if(niceHarpy.getOrientation() != Orientation.DOWN)
			niceHarpy.moveOrientate(Orientation.DOWN,  0);
		
		if(moveComplete2 && badHarpy.getOrientation() != Orientation.UP)
			badHarpy.moveOrientate(Orientation.UP, 0);
		
		if(moveComplete2 && badHarpy.getOrientation() == Orientation.UP)
			advancement = StoryAdvancement.DIALOG_1;
	}
	
	private void faceToFace() {
		boolean moveComplete1 = !move(player, new DiscreteCoordinates(7, 8), area);
		boolean moveComplete2 = !move(niceHarpy, new DiscreteCoordinates(9, 8), area, 6);
		
		if(moveComplete1 && player.getOrientation() != Orientation.RIGHT)
			player.moveOrientate(Orientation.RIGHT, 0);
		
		if(moveComplete2 && niceHarpy.getOrientation() != Orientation.LEFT)
			niceHarpy.moveOrientate(Orientation.LEFT, 0);
		
		if(moveComplete1 && player.getOrientation() == Orientation.RIGHT 
				&& moveComplete2 && niceHarpy.getOrientation() == Orientation.LEFT) {
			advancement = StoryAdvancement.ADD_BAD_HARPY;
			area.registerActor(badHarpy);
		}
	}

	@Override
	public boolean isEnded() {
		return advancement == StoryAdvancement.END;
	}
	
	@Override
	public boolean wantsCellInteraction() {
		return advancement == StoryAdvancement.CLEAR;
	}
	
	@Override
	public List<DiscreteCoordinates> getCurrentCells() {
		List<DiscreteCoordinates> collection = new ArrayList<>();
		for(int i = 0; i < 17; ++i)
			for(int j = 0; j < 15; ++j)
				collection.add(new DiscreteCoordinates(i, j));
		return collection;
	}
	
	@Override
	public void interactWith(Interactable other) {
		other.acceptInteraction(handler);
	}
	
	private class StoryGrotteMewExtHandler implements TWICInteractionVisitor {
		public void interactWith(TWICPlayer twicPlayer) {
			player = twicPlayer;
		}
		
		public void interactWith(CastleKey key) {
			castleKey = key;
		}
	}
	
	private enum StoryAdvancement {
		NOT_START,
		CLEAR,
		MOVE,
		ADD_BAD_HARPY,
		DIALOG_1,
		CHOOSE_YOUR_SIDE,
		MOVE_PLAYER,
		DIALOG_2,
		LEAVE_HARPY,
		END
	}

}

