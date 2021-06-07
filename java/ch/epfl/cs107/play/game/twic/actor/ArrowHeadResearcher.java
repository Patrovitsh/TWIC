package ch.epfl.cs107.play.game.twic.actor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ch.epfl.cs107.play.game.twic.handler.TWICInteractionVisitor;
import ch.epfl.cs107.play.game.twic.utils.DisplacementIA;
import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.Interactable;
import ch.epfl.cs107.play.game.areagame.actor.Interactor;
import ch.epfl.cs107.play.game.areagame.actor.MovableAreaEntity;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.areagame.actor.Sprite;
import ch.epfl.cs107.play.game.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.game.twic.actor.Monster.DommageType;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.RegionOfInterest;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.window.Canvas;
import ch.epfl.cs107.play.window.Keyboard;

public class ArrowHeadResearcher extends MovableAreaEntity implements FlyableEntity, Interactor{

	private final int VITESSE;
	
	private final ArrowHeadResearcherHandler handler;
	private final Sprite[] spriteNoMvt = new Sprite[4];
	private final Sprite[] spriteWithMvt = new Sprite[4];
	
	private final DommageType attackType;
	private DiscreteCoordinates targetCoords;
	private MovableAreaEntity target;
	private DiscreteCoordinates masterCoords;
	private Etat etat;

	private float Timer = 0;

	public ArrowHeadResearcher(Area area, Orientation orientation, DiscreteCoordinates position, int vitesse, DiscreteCoordinates masterCoords) {
		super(area, orientation, position);
		handler = new ArrowHeadResearcherHandler();
		
		VITESSE = vitesse;
		this.masterCoords = masterCoords;
		attackType = DommageType.PHYSIC;
		etat = Etat.IDLE;
		
		String[][] spriteNames = { {"magicArrowUadvanced", "magicArrowRadvanced", "magicArrowDadvanced", "magicArrowLadvanced"},
				{"magicArrowU", "magicArrowR", "magicArrowD", "magicArrowL"} };
		
		for(int i = 0; i < 3; i += 2) {
			spriteNoMvt[i] = new Sprite("personalAdds/" + spriteNames[1][i], 1, 1.66f, this, new RegionOfInterest(0, 0, 96, 160),
					Vector.ZERO, 1, -10);
			spriteNoMvt[i+1] = new Sprite("personalAdds/" + spriteNames[1][i+1], 1.66f, 1, this, new RegionOfInterest(0, 0, 160, 96),
					Vector.ZERO, 1, -10);
			
			spriteWithMvt[i] = new Sprite("personalAdds/" + spriteNames[0][i], 1, 1.66f, this, new RegionOfInterest(0, 0, 96, 160),
					Vector.ZERO, 1, -10);
			spriteWithMvt[i+1] = new Sprite("personalAdds/" + spriteNames[0][i+1], 1.66f, 1, this, new RegionOfInterest(0, 0, 160, 96),
					Vector.ZERO, 1, -10);
		}
		
		Vector anchor = orientation.opposite().toVector().resized(0.6f);
		spriteNoMvt[0].setAnchor(anchor);
		spriteWithMvt[0].setAnchor(anchor);
		spriteNoMvt[1].setAnchor(anchor);
		spriteWithMvt[1].setAnchor(anchor);
		
	}

	@Override
	public List<DiscreteCoordinates> getCurrentCells() {
		return Collections.singletonList(getCurrentMainCellCoordinates());
	}

	@Override
	public List<DiscreteCoordinates> getFieldOfViewCells() {
		List<DiscreteCoordinates> collection = new ArrayList<>();
		
		Vector vectorOrientation = getOrientation().toVector();
		Vector vectorOrientationRight = getOrientation().hisRight().toVector();
		for(int i = -2; i <= 10; ++i) {
			for(int j = -9; j <= 9; ++j) {
				collection.add(getCurrentMainCellCoordinates().jump(vectorOrientation.resized(i)).jump(vectorOrientationRight.resized(j)));
			}
		}
		
		for(int i = -1; i <= 1; ++i) {
			for(int j = -1; j <= 1; ++j) {
				collection.add(masterCoords.jump(i, j));
			}
		}
		
		return collection;
	}

	@Override
	public boolean wantsCellInteraction() {
		return true;
	}

	@Override
	public boolean wantsViewInteraction() {
		return true;
	}

	@Override
	public boolean takeCellSpace() {
		return false;
	}

	@Override
	public boolean isCellInteractable() {
		return etat == Etat.BACK_HOME;
	}

	@Override
	public boolean isViewInteractable() {
		return false;
	}

	@Override
	public void acceptInteraction(AreaInteractionVisitor v) {
		((TWICInteractionVisitor)v).interactWith(this);
	}

	@Override
	public void draw(Canvas canvas) {
		if(isDisplacementOccurs()) 
			spriteWithMvt[getOrientation().ordinal()].draw(canvas);
		else 
			spriteNoMvt[getOrientation().ordinal()].draw(canvas);
	}
	
	@Override
	public void update(float deltaTime) {
		Area area = getOwnerArea();
		Keyboard keyboard = area.getKeyboard();
		if(keyboard.get(Keyboard.G).isDown() && !keyboard.get(Keyboard.G).wasDown()) {
			if(etat == Etat.GUIDED)
				etat = Etat.BACK_HOME;
			else 
				etat = Etat.GUIDED;
		}
		
		Orientation orientation = null;
		DiscreteCoordinates myCoords = getCurrentMainCellCoordinates();
		
		switch(etat) {
		case IDLE : orientation = getOrientation();
			break;
		case HUNTING : orientation = DisplacementIA.searchingDisplacement(this, targetCoords, myCoords, area, 0);
			break;
		case BACK_HOME : orientation = DisplacementIA.searchingDisplacement(this, masterCoords, myCoords, area, 0);
			break;
		case GUIDED : orientation = DisplacementIA.searchingDisplacement(this, area.getRelativeMouseCoordinates(), myCoords, area, 0);
		    break;
		}
		
		if(orientation != null)
			moveOrientate(orientation);
		
		if(!isDisplacementOccurs() && etat != Etat.GUIDED) {
			Timer += deltaTime;
			if(Timer >= deltaTime + 1./2*deltaTime)
				etat = Etat.BACK_HOME;
		} else Timer = 0;
		
		super.update(deltaTime);
	}
	
	private void moveOrientate(Orientation orientation){
	    
	    if(getOrientation() == orientation) move(100/VITESSE);
	    else orientate(orientation);
	}
	
	/**
	 * Collect the arrow head researcher
	 * @return (ARPGItem) the Arrow head researcher
	 */
	protected TWICItem collect() {
		getOwnerArea().unregisterActor(this);
		return TWICItem.ARROW_HEAD_RESEARCHER;
	}
	
	@Override
	public void interactWith(Interactable other) {
		other.acceptInteraction(handler);
	}
	
	/**
	 * Handle all the interactions with the others actors
	 */
	private class ArrowHeadResearcherHandler implements TWICInteractionVisitor {
		public void interactWith(Monster monster) {
			DiscreteCoordinates monsterCoords = monster.getCurrentCells().get(0);
			DiscreteCoordinates coords = getCurrentMainCellCoordinates();
			
			if(targetCoords == null) {
				targetCoords = monster.getCurrentCells().get(0);
				target = monster;
				etat = Etat.HUNTING;
			}
			
			// Mise a jour de la cible si une cible est plus proche
			if(DiscreteCoordinates.distanceBetween(coords, monsterCoords) 
					< DiscreteCoordinates.distanceBetween(coords, targetCoords)) {
				targetCoords = monsterCoords;
				target = monster;
			}
			
			if(monsterCoords.equals(getCurrentMainCellCoordinates())) {
				monster.hurted(2.f, monster.getVulnerability()[0]);
				if(etat != Etat.GUIDED) 
					etat = Etat.BACK_HOME;
			}
			
			// Mise a jour les coordonnees de la cible
			if(!monster.equals(target)) return;
			targetCoords = monsterCoords;
		}
		
		public void interactWith(TWICPlayer twicPlayer) {
			masterCoords = twicPlayer.getCurrentCells().get(0);
		}
		
		public void interactWith(FireSpell fireSpell) {
			if(fireSpell.getCurrentCells().get(0).equals(getCurrentMainCellCoordinates()))
				fireSpell.dispawn(attackType);
		}
		
		public void interactWith(DragonSpell dragonSpell) {
			if(dragonSpell.getCurrentCells().get(0).equals(getCurrentMainCellCoordinates()))
				dragonSpell.dispawn(attackType);
		}
		
		public void interactWith(Grass grass) {
			if(grass.getCurrentCells().get(0).equals(getCurrentMainCellCoordinates()))
				grass.cutGrass();
		}
		
		public void interactWith(Orb orb) {
			orb.activation();
		}
		
		public void interactWith(Personnage personnage) {
			if(personnage.getCurrentCells().get(0).equals(getCurrentMainCellCoordinates()))
				personnage.hurted(2.f, attackType);
		}
		
		public void interactWith(Bomb bomb) {
			if(bomb.getCurrentCells().get(0).equals(getCurrentMainCellCoordinates())) 
				bomb.toExplode();
		}
		
		public void interactWith(Shop shop) {
			if(shop.getCurrentCells().get(0).equals(getCurrentMainCellCoordinates())) 
				shop.hurted();
		}
		
	}
	
	private enum Etat {
		IDLE,
		HUNTING,
		BACK_HOME,
		GUIDED
	}

}
