package ch.epfl.cs107.play.game.twic.area.deco;

import ch.epfl.cs107.play.game.actor.Entity;
import ch.epfl.cs107.play.game.areagame.actor.Sprite;
import ch.epfl.cs107.play.math.RegionOfInterest;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.window.Canvas;

public class SupportMagicArrow extends Entity {
	
	private final Sprite sprite;

	/**
	 * Default SupportMagicArrow Constructor
	 * @param position (Vector), Position where it's located
	 */
	public SupportMagicArrow(Vector position) {
		super(position);
		
		sprite = new Sprite("personalAdds/support", 2, 2, this, new RegionOfInterest(0, 0, 32, 32), new Vector(-0.5f, -0.1f));
		sprite.setDepth(-10000);
	}

	@Override
	public void draw(Canvas canvas) {
		sprite.draw(canvas);
	}
	
}

