package ch.epfl.cs107.play.game.twic.area.story;

import java.util.ArrayList;
import java.util.List;

import ch.epfl.cs107.play.game.twic.actor.TWICPlayer;
import ch.epfl.cs107.play.game.twic.actor.TWICPlayerStatusGUI;
import ch.epfl.cs107.play.game.twic.handler.TWICInteractionVisitor;
import ch.epfl.cs107.play.game.actor.ImageGraphics;
import ch.epfl.cs107.play.game.areagame.actor.Interactable;
import ch.epfl.cs107.play.game.areagame.io.ResourcePath;
import ch.epfl.cs107.play.game.twic.area.Ferme;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.RegionOfInterest;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.window.Canvas;
import ch.epfl.cs107.play.window.Keyboard;

public class StoryFermeBis extends Story {
	
	private final StoryFermeBisHandler handler;
	private final Ferme area;
	private StoryAdvancement advancement;
	private static boolean end = false;
	
	private TWICPlayer player;
	private int indexKeyboard = 1;

	public StoryFermeBis(Ferme area) {
		super(area);
		// TODO Auto-generated constructor stub
		this.area = area;
		handler = new StoryFermeBisHandler();
		advancement = StoryAdvancement.PASSIF;
	}
	
	@Override
	public boolean isEnded() {
		return end;
	}
	
	public static void setEnd() {
		end = true;
	}
	
	@Override 
	public void draw(Canvas canvas) {
		if(advancement != StoryAdvancement.DIALOG)
			return;
		
		float width = canvas.getScaledWidth();
		float height = canvas.getScaledHeight();
		Vector anchor = canvas.getTransform().getOrigin().sub(new Vector(width/2, height/2));

		ImageGraphics dialog = new ImageGraphics(ResourcePath.getSprite("personalAdds/txtFermeBis" + (char) (indexKeyboard + 48)),
				9, 3, new RegionOfInterest(0, 0, 240, 80), Vector.ZERO, 1, TWICPlayerStatusGUI.DEPTH);
		
		dialog.setAnchor(anchor.add(new Vector(width-9.5f, 0.5f)));
		dialog.draw(canvas);
	}

	@Override
	public void updateStory(float deltaTime) {
		switch(advancement) {
		case PASSIF: if(end) area.unregisterActor(this);
			break;
		case DIALOG: area.suspend();
			Keyboard keyboard = area.getKeyboard();
			if(keyboard.get(Keyboard.ENTER).isDown() && !keyboard.get(Keyboard.ENTER).wasDown())
				++indexKeyboard;
			if(indexKeyboard > 1) {
				indexKeyboard = 1;
				area.resume();
				advancement = StoryAdvancement.PASSIF;
			}
			break;
		}
	}
	
	@Override
	public boolean takeCellSpace() {
		if(end) return false;
		if(player != null && getFieldOfViewCells().contains(player.getCurrentCells().get(0)))
			advancement = StoryAdvancement.DIALOG;
		return true;
	}
	
	@Override
	public List<DiscreteCoordinates> getCurrentCells() {
		List<DiscreteCoordinates> collection = new ArrayList<>();
		collection.add(new DiscreteCoordinates(3, 18));
		collection.add(new DiscreteCoordinates(4, 18));
		return collection;
	}
	
	@Override
	public boolean wantsViewInteraction() {
		return true;
	}
	
	@Override
	public List<DiscreteCoordinates> getFieldOfViewCells() {
		List<DiscreteCoordinates> collection = new ArrayList<>();
		collection.add(new DiscreteCoordinates(3, 17));
		collection.add(new DiscreteCoordinates(4, 17));
		return collection;
	}
	
	@Override
	public void interactWith(Interactable other) {
		other.acceptInteraction(handler);
	}
	
	private class StoryFermeBisHandler implements TWICInteractionVisitor {
		public void interactWith(TWICPlayer twicPlayer) {
			player = twicPlayer;
		}
	}
	
	private enum StoryAdvancement {
		PASSIF,
		DIALOG
	}

}
