package ch.epfl.cs107.play.game.twic.area.story;

import java.util.ArrayList;
import java.util.List;

import ch.epfl.cs107.play.game.twic.handler.TWICInteractionVisitor;
import ch.epfl.cs107.play.game.actor.ImageGraphics;
import ch.epfl.cs107.play.game.areagame.actor.Interactable;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.areagame.io.ResourcePath;
import ch.epfl.cs107.play.game.twic.actor.DarkLord;
import ch.epfl.cs107.play.game.twic.actor.FireSpell;
import ch.epfl.cs107.play.game.twic.actor.Monster;
import ch.epfl.cs107.play.game.twic.actor.StoryPersonnage;
import ch.epfl.cs107.play.game.twic.actor.TWICCollectableAreaEntity;
import ch.epfl.cs107.play.game.twic.actor.TWICPlayer;
import ch.epfl.cs107.play.game.twic.actor.TWICPlayerStatusGUI;
import ch.epfl.cs107.play.game.twic.area.RouteChateau;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.RegionOfInterest;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.window.Canvas;
import ch.epfl.cs107.play.window.Keyboard;

public class StoryRouteChateau extends Story {
	
	private final StoryRouteChateauHandler handler;
	private final RouteChateau area;
	private StoryAdvancement advancement;
	
	private TWICPlayer player;
	private final StoryPersonnage niceHarpy;
	private final StoryPersonnage badHarpy;
	private TWICCollectableAreaEntity collectableEntity;

	private int indexKeyboard = 1;
	
	public StoryRouteChateau(RouteChateau area) {
		super(area);
		this.area = area;
		handler = new StoryRouteChateauHandler();
		advancement = StoryAdvancement.NOT_START;
		
		niceHarpy = new StoryPersonnage(area, Orientation.UP, new DiscreteCoordinates(10, 0), StoryPersonnage.StoryPerso.NICE_HARPY, false);
		
		badHarpy = new StoryPersonnage(area, Orientation.UP, new DiscreteCoordinates(10, 0), StoryPersonnage.StoryPerso.BAD_HARPY, false);
	}
	
	@Override 
	public void draw(Canvas canvas) {
		switch(advancement) {
		case DIALOG_1 :
		case DIALOG_2 :
		case DIALOG_3 :
		case DIALOG_4 :
			break;
		default: return;
		}
		
		float width = canvas.getScaledWidth();
		float height = canvas.getScaledHeight();
		Vector anchor = canvas.getTransform().getOrigin().sub(new Vector(width/2, height/2));

		ImageGraphics dialog = new ImageGraphics(ResourcePath.getSprite("personalAdds/txtRouteChateau" + (char) (indexKeyboard + 48)),
				9, 3, new RegionOfInterest(0, 0, 240, 80), Vector.ZERO, 1, TWICPlayerStatusGUI.DEPTH);
		
		dialog.setAnchor(anchor.add(new Vector(width-9.5f, 0.5f)));
		dialog.draw(canvas);
	}

	@Override
	public void updateStory(float deltaTime) {
		
		niceHarpy.updateStory(deltaTime);
		badHarpy.updateStory(deltaTime);
		
		if(advancement != StoryAdvancement.NOT_START && advancement != StoryAdvancement.CLEAR)
			player.updateStory(deltaTime);
		
		switch(advancement) {
		case NOT_START : DarkLord darkLord = (DarkLord) area.getActors()[0];
			if(!darkLord.isLiving()) 
				advancement = StoryAdvancement.CLEAR;
			break;
		case CLEAR :
			if(collectableEntity != null && collectableEntity.isCollect()) {
				advancement = StoryAdvancement.MOVE;
				area.registerActor(niceHarpy);
				area.suspend();
			}
			break;
		case MOVE : faceToFace();
			break;
		case DIALOG_1 : dialog(1, StoryAdvancement.MOVE_UP_HARPY);
		    break;
		case MOVE_UP_HARPY : goToCastleDoor();
			break;
		case DIALOG_2 : 
			if(dialog(2, StoryAdvancement.ADD_BAD_HARPY))
				area.registerActor(badHarpy);
			break;
		case ADD_BAD_HARPY : badHarpyAgainstOthers();
			break;
		case DIALOG_3 : dialog(7, StoryAdvancement.LEAVE_BAD_HARPY);
		    break;
		case LEAVE_BAD_HARPY : 
			if(!move(badHarpy, new DiscreteCoordinates(9, 1), area, 6)) {
				area.unregisterActor(badHarpy);
				advancement = StoryAdvancement.DIALOG_4;
				player.moveOrientate(Orientation.RIGHT, 0);
				niceHarpy.moveOrientate(Orientation.LEFT, 0);
			}
			break;
		case DIALOG_4 : 
			if(dialog(8, StoryAdvancement.LEAVE_NICE_HARPY))
				player.moveOrientate(Orientation.DOWN, 0);
			break;
		case LEAVE_NICE_HARPY:
			if(!move(niceHarpy, new DiscreteCoordinates(10, 0), area, 6)) {
				area.unregisterActor(niceHarpy);
				advancement = StoryAdvancement.END;
				area.resume();
				StoryRoute.setStart();
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
		boolean moveComplete1 = !move(niceHarpy, new DiscreteCoordinates(10, 9), area, 6);
		boolean moveComplete2 = !move(badHarpy, new DiscreteCoordinates(9, 7), area, 6);
		if(player.getOrientation() != Orientation.DOWN)
			player.moveOrientate(Orientation.DOWN, 0);
		if(moveComplete2 && badHarpy.getOrientation() != Orientation.UP)
			badHarpy.moveOrientate(Orientation.UP, 0);
		if(moveComplete2 && moveComplete1 && badHarpy.getOrientation() == Orientation.UP)
			advancement = StoryAdvancement.DIALOG_3;
	}
	
	private void goToCastleDoor() {
		boolean moveComplete = !move(niceHarpy, new DiscreteCoordinates(10, 12), area, 6);
		if(player.getOrientation() != Orientation.UP)
			player.moveOrientate(Orientation.UP, 0);
		if(moveComplete)
			advancement = StoryAdvancement.DIALOG_2;
	}
	
	private void faceToFace() {
		boolean moveComplete1 = !move(player, new DiscreteCoordinates(8, 9), area);
		boolean moveComplete2 = !move(niceHarpy, new DiscreteCoordinates(10, 9), area, 6);
		
		if(moveComplete1 && player.getOrientation() != Orientation.RIGHT)
			player.moveOrientate(Orientation.RIGHT, 0);
		
		if(moveComplete2 && niceHarpy.getOrientation() != Orientation.LEFT)
			niceHarpy.moveOrientate(Orientation.LEFT, 0);
		
		if(moveComplete1 && player.getOrientation() == Orientation.RIGHT 
				&& moveComplete2 && niceHarpy.getOrientation() == Orientation.LEFT)
			advancement = StoryAdvancement.DIALOG_1;
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
		for(int i = 0; i < 20; ++i)
			for(int j = 0; j < 20; ++j)
				collection.add(new DiscreteCoordinates(i, j));
		return collection;
	}
	
	@Override
	public void interactWith(Interactable other) {
		other.acceptInteraction(handler);
	}
	
	private class StoryRouteChateauHandler implements TWICInteractionVisitor {
		public void interactWith(TWICPlayer twicPlayer) {
			player = twicPlayer;
		}
		
		public void interactWith(Monster monster) {
			monster.hurted(1000, Monster.DommageType.PHYSIC);
		}
		
		public void interactWith(FireSpell fireSpell) {
			fireSpell.dispawn(Monster.DommageType.PHYSIC);
		}
		
		public void interactWith(TWICCollectableAreaEntity collectableAreaEntity) {
			collectableEntity = collectableAreaEntity;
		}
	}
	
	private enum StoryAdvancement {
		NOT_START,
		CLEAR,
		MOVE,
		DIALOG_1,
		MOVE_UP_HARPY,
		DIALOG_2,
		ADD_BAD_HARPY,
		DIALOG_3,
		LEAVE_BAD_HARPY,
		DIALOG_4,
		LEAVE_NICE_HARPY,
		END
	}

}
