package ch.epfl.cs107.play.game.twic.area.deco;

import java.util.Collections;
import java.util.List;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.AreaEntity;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.areagame.actor.Sprite;
import ch.epfl.cs107.play.game.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.RegionOfInterest;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.window.Canvas;

public class GrotteClose extends AreaEntity {
	
	private final Sprite sprite;
	
	public GrotteClose(Area area, Orientation orientation, DiscreteCoordinates position) {
		super(area, orientation, position);
		
		sprite = new Sprite("personalAdds/grotteClose", 3, 3, this, new RegionOfInterest(0, 0, 48, 48), new Vector(-1f, -1f));
		sprite.setDepth(-10000);
	}

	@Override
	public void draw(Canvas canvas) {
		sprite.draw(canvas);
	}
	
	public void drawStory(Canvas canvas) {
		sprite.draw(canvas);
	}

	@Override
	public List<DiscreteCoordinates> getCurrentCells() {
		return Collections.singletonList(new DiscreteCoordinates(25, 18));
	}

	@Override
	public boolean takeCellSpace() {
		return true;
	}

	@Override
	public boolean isCellInteractable() {
		return false;
	}

	@Override
	public boolean isViewInteractable() {
		return false;
	}

	@Override
	public void acceptInteraction(AreaInteractionVisitor v) {
		// TODO Auto-generated method stub
	}
	
}