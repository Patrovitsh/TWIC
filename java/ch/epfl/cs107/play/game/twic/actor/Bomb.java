package ch.epfl.cs107.play.game.twic.actor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ch.epfl.cs107.play.game.twic.handler.TWICInteractionVisitor;
import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.Animation;
import ch.epfl.cs107.play.game.areagame.actor.AreaEntity;
import ch.epfl.cs107.play.game.areagame.actor.Interactable;
import ch.epfl.cs107.play.game.areagame.actor.Interactor;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.areagame.actor.Sprite;
import ch.epfl.cs107.play.game.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.game.rpg.actor.RPGSprite;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.RegionOfInterest;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.window.Canvas;

public class Bomb extends AreaEntity implements Interactor {

	private int retardateur;
	private final BombHandler handler;
	private final Animation bombAnim;
	private final Animation bombExplo;
	private final Monster.DommageType attackType = Monster.DommageType.PHYSIC;
	
	/**
	 * Default Bomb Constructor
	 * @param owner (Area), Not null
	 * @param coordinates (DiscreteCoordinates), Not null
	 * @param retardateur (int), The "time" remaining before the bomb explodes
	 */
	public Bomb(Area owner, DiscreteCoordinates coordinates, int retardateur) {
		super(owner, Orientation.UP, coordinates);
		this.retardateur = retardateur;
		this.handler = new BombHandler();

		Sprite[] sprite = new Sprite[2];
		for(int i = 0; i < sprite.length; ++i)
			sprite[i] = new RPGSprite("zelda/bomb", 1, 1, this, new RegionOfInterest(i*16, 0, 16, 16));
		bombAnim = new Animation(6, sprite);

		Sprite[] spriteExplo = new Sprite[7];
		for(int i = 0; i < spriteExplo.length; ++i)
			spriteExplo[i] = new RPGSprite("zelda/explosion", 2.5f, 2.5f, this, new RegionOfInterest(i*32, 0, 32, 32), 
					new Vector(-0.75f, -0.75f));
		bombExplo = new Animation(4, spriteExplo, false);
	}

	@Override
	public boolean takeCellSpace() {
		return (retardateur >= 0);
	}

	@Override
	public boolean isCellInteractable() {
		return retardateur > 0;
	}

	@Override
	public boolean isViewInteractable() {
		return retardateur > 0;
	}

	@Override
	public void acceptInteraction(AreaInteractionVisitor v) {
		((TWICInteractionVisitor)v).interactWith(this);
	}

	@Override
	public void draw(Canvas canvas) {
		if(retardateur > 0) {
			bombAnim.draw(canvas);
		} else if (!bombExplo.isCompleted()) {
			bombExplo.draw(canvas);
		}
	}
	
	@Override
	public void update(float deltaTime) {
		retardateur -= 1;
		if(retardateur > 0) {
			bombAnim.update(deltaTime);
		} else {
			bombExplo.update(deltaTime);
			if(bombExplo.isCompleted())
				getOwnerArea().unregisterActor(this);
		}
	}

	@Override
	public List<DiscreteCoordinates> getCurrentCells() {
		return Collections.singletonList(getCurrentMainCellCoordinates());
	}

	@Override
	public List<DiscreteCoordinates> getFieldOfViewCells() {
		List<DiscreteCoordinates> collection = new ArrayList<>();
		int RAYON = 1;
		for(int i = -RAYON; i <= RAYON; ++i) {
			for(int j = -RAYON; j <= RAYON; ++j) {
				collection.add(getCurrentMainCellCoordinates().jump(i, j));
			}
		}
		return collection;
	}

	@Override
	public boolean wantsCellInteraction() {
		return (retardateur > -2 && retardateur < 1);
	}

	@Override
	public boolean wantsViewInteraction() {
		return (retardateur > -2 && retardateur < 1);
	}

	@Override
	public void interactWith(Interactable other) {
		other.acceptInteraction(handler);
	}
	
	/**
	 * Make a bomb explode
	 */
	protected void toExplode() {
		retardateur = 0;
	}
	
	/**
	 * Handle all the interactions with the others actors
	 */
	private class BombHandler implements TWICInteractionVisitor {
		public void interactWith(Grass grass) {
			grass.cutGrass();
		}
		
		public void interactWith(TWICPlayer TWICPlayer) {
			TWICPlayer.hurted(2f, attackType);
		}
		
		public void interactWith(Monster monster) {
			monster.hurted(2f, attackType);
		}
		
		public void interactWith(Personnage personnage) {
			personnage.hurted(2f, attackType);
		}
		
		public void interactWith(Bomb bomb) {
			bomb.toExplode();
		}
		
		public void interactWith(Shop shop) {
			shop.hurted();
		}
		
	}

}
