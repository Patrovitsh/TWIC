package ch.epfl.cs107.play.game.twic.area.story;

import java.util.Collections;
import java.util.List;

import ch.epfl.cs107.play.game.twic.actor.StoryPersonnage;
import ch.epfl.cs107.play.game.twic.actor.TWICPlayer;
import ch.epfl.cs107.play.game.twic.actor.TWICPlayerStatusGUI;
import ch.epfl.cs107.play.game.twic.handler.TWICInteractionVisitor;
import ch.epfl.cs107.play.game.actor.ImageGraphics;
import ch.epfl.cs107.play.game.areagame.actor.Interactable;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.areagame.io.ResourcePath;
import ch.epfl.cs107.play.game.twic.area.Ferme;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.RegionOfInterest;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.window.Canvas;
import ch.epfl.cs107.play.window.Keyboard;

public class StoryFerme extends Story {
	
	private final StoryFermeHandler handler;
	private final Ferme area;
	private StoryAdvancement advancement;
	
	private final StoryPersonnage niceHarpy;

	private int indexKeyboard = 1;
	

	public StoryFerme(Ferme area) {
		super(area);
		this.area = area;
		handler = new StoryFermeHandler();
		advancement = StoryAdvancement.NOT_START;
		
		niceHarpy = new StoryPersonnage(area, Orientation.LEFT, new DiscreteCoordinates(5, 1), StoryPersonnage.StoryPerso.NICE_HARPY);
		
	}
	
	@Override
	public boolean isEnded() {
		return advancement == StoryAdvancement.END;
	}
	
	@Override 
	public void draw(Canvas canvas) {
		if(advancement != StoryAdvancement.DIALOG) return;
		
		float width = canvas.getScaledWidth();
		float height = canvas.getScaledHeight();
		Vector anchor = canvas.getTransform().getOrigin().sub(new Vector(width/2, height/2));

		ImageGraphics dialog = new ImageGraphics(ResourcePath.getSprite("personalAdds/txtFerme" + (char) (indexKeyboard + 48)),
				9, 3, new RegionOfInterest(0, 0, 240, 80), Vector.ZERO, 1, TWICPlayerStatusGUI.DEPTH);
		
		dialog.setAnchor(anchor.add(new Vector(width-9.5f, 0.5f)));
		dialog.draw(canvas);
	}
	
	@Override
	public void updateStory(float deltaTime) {
		niceHarpy.updateStory(deltaTime);
		
		switch(advancement) {
		case NOT_START:
			break;
		case MOVE_HARPY: moveHarpy();
			break;
		case DIALOG : Keyboard keyboard = area.getKeyboard();
			if(keyboard.get(Keyboard.ENTER).isDown() && !keyboard.get(Keyboard.ENTER).wasDown())
				++indexKeyboard;
			if(indexKeyboard > 8)
				advancement = StoryAdvancement.MOVE_HARPY_2;
			break;
		case MOVE_HARPY_2 : leaveHarpy();
			break;
		case LEAVE_HARPY: area.unregisterActor(niceHarpy);
			area.resume();
			advancement = StoryAdvancement.END;
			break;
		case END : area.unregisterActor(this);
			break;
		}
	}
	
	@Override
	public boolean wantsCellInteraction() {
		return true;
	}
	
	private void moveHarpy() {
		boolean moveComplete = !move(niceHarpy, new DiscreteCoordinates(6, 8), area);
		if(moveComplete) {
			if(niceHarpy.getOrientation() != Orientation.UP)
				niceHarpy.moveOrientate(Orientation.UP, 0);
			else advancement = StoryAdvancement.DIALOG;
		}
	}
	
	private void leaveHarpy() {
		boolean moveComplete = !move(niceHarpy, new DiscreteCoordinates(6, 2), area);
		if(moveComplete)
			advancement = StoryAdvancement.LEAVE_HARPY;
	}
	
	@Override
	public List<DiscreteCoordinates> getCurrentCells() {
		return Collections.singletonList(new DiscreteCoordinates(6, 10));
	}

	@Override
	public void interactWith(Interactable other) {
		other.acceptInteraction(handler);
	}
	
	private class StoryFermeHandler implements TWICInteractionVisitor {
		public void interactWith(TWICPlayer twicPlayer) {
			if(advancement != StoryAdvancement.NOT_START) 
				return;
			
			advancement = StoryAdvancement.MOVE_HARPY;
			area.suspend();
			area.registerActor(niceHarpy);
		}
	}
	
	private enum StoryAdvancement {
		NOT_START,
		MOVE_HARPY, 
		DIALOG,
		MOVE_HARPY_2,
		LEAVE_HARPY,
		END
	}

}
