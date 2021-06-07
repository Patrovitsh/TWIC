package ch.epfl.cs107.play.game.twic.area.deco;

import ch.epfl.cs107.play.game.actor.Entity;
import ch.epfl.cs107.play.game.areagame.actor.Animation;
import ch.epfl.cs107.play.game.areagame.actor.Sprite;
import ch.epfl.cs107.play.math.RegionOfInterest;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.window.Canvas;

public class WaterFall extends Entity {

	private final Animation animation;

	/**
	 * Default WaterFall Constructor
	 * @param position (Vector), Where it's located
	 */
	public WaterFall(Vector position) {
		super(position);

		Sprite[] sprite = new Sprite[3];
		for(int i = 0; i < sprite.length; ++i)
			sprite[i] = new Sprite("zelda/waterfall", 4, 4, this, new RegionOfInterest(i*64, 0, 64, 64));
		animation = new Animation(4, sprite);
	}

	@Override
	public void draw(Canvas canvas) {
		animation.draw(canvas);
	}

	@Override
	public void update(float deltaTime) {
		animation.update(deltaTime);
	}
	
}
