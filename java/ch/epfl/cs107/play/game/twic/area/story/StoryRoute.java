package ch.epfl.cs107.play.game.twic.area.story;

import java.util.ArrayList;
import java.util.List;

import ch.epfl.cs107.play.game.twic.actor.Monster;
import ch.epfl.cs107.play.game.twic.actor.Personnage;
import ch.epfl.cs107.play.game.twic.actor.TWICPlayer;
import ch.epfl.cs107.play.game.twic.actor.TWICPlayerStatusGUI;
import ch.epfl.cs107.play.game.twic.handler.TWICInteractionVisitor;
import ch.epfl.cs107.play.game.actor.ImageGraphics;
import ch.epfl.cs107.play.game.areagame.actor.Interactable;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.areagame.io.ResourcePath;
import ch.epfl.cs107.play.game.twic.area.Route;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.RegionOfInterest;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.window.Canvas;
import ch.epfl.cs107.play.window.Keyboard;

public class StoryRoute extends Story {

	private final StoryRouteHandler handler;
	private final Route area;
	private StoryAdvancement advancement;
	private static boolean isStarted = false;
	
	private TWICPlayer player;
	private final Personnage personnage;
	
	private final DiscreteCoordinates[] coords = {new DiscreteCoordinates(13, 12), new DiscreteCoordinates(13, 16),
			new DiscreteCoordinates(9, 16), new DiscreteCoordinates(9, 12)};
	private int indexCoords = 0;

	private int indexKeyboard = 1;
	
	public StoryRoute(Route area) {
		super(area);
		this.area = area;
		handler = new StoryRouteHandler();
		advancement = StoryAdvancement.NOT_START;
		
		personnage = new Personnage(area, Orientation.UP, new DiscreteCoordinates(11, 2), true);
		
	}
	
	protected static void setStart() {
		isStarted = true;
	}
	
	@Override
	public void draw(Canvas canvas) {
		
		switch(advancement) {
		case MOVE_PERSONNAGE :
		case DIALOG :
			break;
		default: return;
		}
		
		float width = canvas.getScaledWidth();
		float height = canvas.getScaledHeight();
		Vector anchor = canvas.getTransform().getOrigin().sub(new Vector(width/2, height/2));

		ImageGraphics dialog = new ImageGraphics(ResourcePath.getSprite("personalAdds/txtRoute" + (char) (indexKeyboard + 48)),
				9, 3, new RegionOfInterest(0, 0, 240, 80), Vector.ZERO, 1, TWICPlayerStatusGUI.DEPTH);
		
		dialog.setAnchor(anchor.add(new Vector(width-9.5f, 0.5f)));
		dialog.draw(canvas);
	}

	@Override
	public void updateStory(float deltaTime) {
		personnage.updateStory(deltaTime);
		
		if(advancement != StoryAdvancement.NOT_START && advancement != StoryAdvancement.PASSIF)
			player.updateStory(deltaTime);
		
		switch(advancement) {
		case NOT_START : if(isStarted) advancement = StoryAdvancement.PASSIF;
			break;
		case PASSIF: 
			break;
		case CLEAR_AND_MOVE : 
			boolean moveComplete = !move(player, new DiscreteCoordinates(11, 14), area);
			if(moveComplete && player.getOrientation() != Orientation.DOWN)
				player.moveOrientate(Orientation.DOWN, 0);
			if(moveComplete && player.getOrientation() == Orientation.DOWN) {
				advancement = StoryAdvancement.MOVE_PERSONNAGE;
				area.registerActor(personnage);
			}
			break;
		case MOVE_PERSONNAGE : 
			if(!move(personnage, coords[indexCoords], area, 2)) {
				++indexCoords;
				if(indexCoords >= coords.length)
					indexCoords = 0;
			}
			dialog(2, StoryAdvancement.PERSONNAGE_INFRONT);
			break;
		case PERSONNAGE_INFRONT : 
			if(!move(personnage, new DiscreteCoordinates(11, 12), area, 2)) {
				if(personnage.getOrientation() != Orientation.UP)
					personnage.moveOrientate(Orientation.UP, 0);
				else 
					advancement = StoryAdvancement.DIALOG;
			}
			break;
		case DIALOG : dialog(3, StoryAdvancement.LEAVE_PERSONNAGE);
			break;
		case LEAVE_PERSONNAGE :
			if(!move(personnage, new DiscreteCoordinates(11, 2), area, 2)) {
				area.unregisterActor(personnage);
				area.resume();
				advancement = StoryAdvancement.END;
				StoryVillage.setStart();
			}
			break;
		case END :
			break;
		}
		
	}
	
	private void dialog(int index, StoryAdvancement nextAdvancement) {
		Keyboard keyboard = area.getKeyboard();
		
		if(keyboard.get(Keyboard.ENTER).isDown() && !keyboard.get(Keyboard.ENTER).wasDown())
			++indexKeyboard;
		if(indexKeyboard > index) {
			advancement = nextAdvancement;
		}

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
		collection.add(new DiscreteCoordinates(9, 17));
		collection.add(new DiscreteCoordinates(10, 17));
		return collection;
	}
	
	@Override
	public boolean wantsViewInteraction() {
		return advancement == StoryAdvancement.CLEAR_AND_MOVE;
	}
	
	@Override
	public List<DiscreteCoordinates> getFieldOfViewCells() {
		List<DiscreteCoordinates> collection = new ArrayList<>();
		for(int i = 7; i < 16; ++i)
			for(int j = 2; j < 19; ++j)
				collection.add(new DiscreteCoordinates(i, j));
		return collection;
	}
	
	@Override
	public void interactWith(Interactable other) {
		other.acceptInteraction(handler);
	}
	
	private class StoryRouteHandler implements TWICInteractionVisitor {
		public void interactWith(TWICPlayer twicPlayer) {
			if(advancement != StoryAdvancement.PASSIF)
				return;
			
			player = twicPlayer;
			area.suspend();
			advancement = StoryAdvancement.CLEAR_AND_MOVE;
		}
		
		public void interactWith(Monster monster) {
			monster.forceDeath();
			monster.hurted(100, Monster.DommageType.PHYSIC);
		}
	}
	
	private enum StoryAdvancement {
		NOT_START,
		PASSIF,
		CLEAR_AND_MOVE,
		MOVE_PERSONNAGE, 
		PERSONNAGE_INFRONT,
		DIALOG,
		LEAVE_PERSONNAGE,
		END
	}

}
