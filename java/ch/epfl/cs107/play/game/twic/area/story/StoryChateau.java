package ch.epfl.cs107.play.game.twic.area.story;

import java.util.ArrayList;
import java.util.List;

import ch.epfl.cs107.play.game.twic.actor.StoryPersonnage;
import ch.epfl.cs107.play.game.twic.actor.TWICPlayer;
import ch.epfl.cs107.play.game.twic.actor.TWICPlayerStatusGUI;
import ch.epfl.cs107.play.game.twic.handler.TWICInteractionVisitor;
import ch.epfl.cs107.play.game.actor.ImageGraphics;
import ch.epfl.cs107.play.game.areagame.actor.Interactable;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.areagame.io.ResourcePath;
import ch.epfl.cs107.play.game.twic.area.Chateau;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.RegionOfInterest;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.window.Canvas;
import ch.epfl.cs107.play.window.Keyboard;

public class StoryChateau extends Story {

	private final StoryChateauHandler handler;
	private final Chateau area;
	private StoryAdvancement advancement;
	
	private static StoryPersonnage.StoryPerso typePerso;
	private StoryPersonnage harpy;
	private final StoryPersonnage king;
	private TWICPlayer player;
	
	private DiscreteCoordinates coordinates;
	private Orientation orientation;

	private int indexKeyboard = 1;
	
	public StoryChateau(Chateau area) {
		super(area);
		this.area = area;
		handler = new StoryChateauHandler();
		advancement = StoryAdvancement.NOT_START;
		
		king = new StoryPersonnage(area, Orientation.DOWN, new DiscreteCoordinates(7, 12), StoryPersonnage.StoryPerso.KING);
		
	}
	
	protected static void setPerso(StoryPersonnage.StoryPerso perso) {
		typePerso = perso;
		
	}
	
	@Override 
	public void draw(Canvas canvas) {
		switch(advancement) {
		case KILL_KING :
		case BAD_DIALOG :
		case KING_DIALOG :
			break;
		default: return;
		}
		
		float width = canvas.getScaledWidth();
		float height = canvas.getScaledHeight();
		Vector anchor = canvas.getTransform().getOrigin().sub(new Vector(width/2, height/2));

		ImageGraphics dialog = new ImageGraphics(ResourcePath.getSprite("personalAdds/txtChateau" + (char) (indexKeyboard + 48)),
				9, 3, new RegionOfInterest(0, 0, 240, 80), Vector.ZERO, 1, TWICPlayerStatusGUI.DEPTH);
		
		dialog.setAnchor(anchor.add(new Vector(width-9.5f, 0.5f)));
		dialog.draw(canvas);
	}

	@Override
	public void updateStory(float deltaTime) {
		if(typePerso == null) return;
		
		if(harpy != null)
			harpy.updateStory(deltaTime);
		king.updateStory(deltaTime);
		
		if(player != null && (advancement == StoryAdvancement.GO_TO_KING || advancement == StoryAdvancement.GO_TO_THRONE))
			player.updateStory(deltaTime);
		
		switch(advancement) {
		case NOT_START : 
			harpy = new StoryPersonnage(area, Orientation.UP, new DiscreteCoordinates(8, 1), typePerso);
			if(typePerso == StoryPersonnage.StoryPerso.NICE_HARPY) {
				advancement = StoryAdvancement.GO_TO_KING;
				coordinates = new DiscreteCoordinates(9, 10);
				orientation = Orientation.UP;
				++indexKeyboard;
			}
			else {
				coordinates = new DiscreteCoordinates(8, 12);
				orientation = Orientation.DOWN;
				advancement = StoryAdvancement.KILL_KING;
			}
			area.registerActor(king);
			break;
		case KILL_KING :
			if(!king.isLiving()) {
				area.suspend();
				indexKeyboard += 2;
				advancement = StoryAdvancement.GO_TO_THRONE;
			}
			break;
		case GO_TO_THRONE : if(player != null) goToThrone();
			break;
		case GO_TO_KING : if(player != null) goToKing();
			break;
		case MOVE_HARPY : moveHarpy();
			break;
		case BAD_DIALOG :
			if(dialog(3))
				area.resume();
			break;
		case KING_DIALOG: 
			if(dialog(2))
				area.resume();
			break;
		case END :
			break;
		}
	}
	
	private void goToThrone() {
		boolean moveComplete = !move(player, new DiscreteCoordinates(7, 12), area);
		
		if(moveComplete && player.getOrientation() != Orientation.DOWN)
			player.moveOrientate(Orientation.DOWN, 0);
		
		if(moveComplete && player.getOrientation() == Orientation.DOWN) {
			player.setMeKing();
			area.registerActor(harpy);
			advancement = StoryAdvancement.MOVE_HARPY;
		}
	}
	
	private void goToKing() {
		boolean moveComplete = !move(player, new DiscreteCoordinates(7, 10), area);
		
		if(moveComplete && player.getOrientation() != Orientation.UP)
			player.moveOrientate(Orientation.UP, 0);
		
		if(moveComplete && player.getOrientation() == Orientation.UP) {
			area.registerActor(harpy);
			advancement = StoryAdvancement.MOVE_HARPY;
		}
	}
	
	private void moveHarpy() {
		boolean moveComplete = !move(harpy, coordinates, area, 6);
		
		if(moveComplete && harpy.getOrientation() != orientation)
			harpy.moveOrientate(orientation, 0);
		
		if(moveComplete && harpy.getOrientation() == orientation) {
			if(typePerso == StoryPersonnage.StoryPerso.NICE_HARPY)
				advancement = StoryAdvancement.KING_DIALOG;
			else if(typePerso == StoryPersonnage.StoryPerso.BAD_HARPY)
				advancement = StoryAdvancement.BAD_DIALOG;
		}
	}
	
	private boolean dialog(int index) {
		Keyboard keyboard = area.getKeyboard();
		
		if(keyboard.get(Keyboard.ENTER).isDown() && !keyboard.get(Keyboard.ENTER).wasDown())
			++indexKeyboard;
		if(indexKeyboard > index) {
			advancement = StoryAdvancement.END;
			return true;
		}
		
		return false;
	}

	@Override
	public boolean isEnded() {
		return advancement == StoryAdvancement.END;
	}
	
	@Override
	public boolean wantsCellInteraction() {
		return advancement == StoryAdvancement.KILL_KING || advancement == StoryAdvancement.GO_TO_KING;
	}
	
	@Override
	public List<DiscreteCoordinates> getCurrentCells() {
		List<DiscreteCoordinates> collection = new ArrayList<>();
		for(int i = 1; i < 15; ++i)
			for(int j = 6; j < 13; ++j)
				collection.add(new DiscreteCoordinates(i, j));
		return collection;
	}
	
	@Override
	public void interactWith(Interactable other) {
		other.acceptInteraction(handler);
	}
	
	private class StoryChateauHandler implements TWICInteractionVisitor {
		public void interactWith(TWICPlayer twicPlayer) {
			if(player == null) {
				player = twicPlayer;
				if(advancement == StoryAdvancement.GO_TO_KING) 
					area.suspend();
			}	
		}
		
	}
	
	private enum StoryAdvancement {
		NOT_START,
		KILL_KING,
		GO_TO_THRONE,
		GO_TO_KING,
		MOVE_HARPY,
		BAD_DIALOG,
		KING_DIALOG,
		END
	}

}
