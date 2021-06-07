package ch.epfl.cs107.play.game.twic.handler;

import ch.epfl.cs107.play.game.rpg.handler.RPGInteractionVisitor;
import ch.epfl.cs107.play.game.twic.TWICBehavior.TWICCell;
import ch.epfl.cs107.play.game.twic.actor.ArrowHeadResearcher;
import ch.epfl.cs107.play.game.twic.actor.Bomb;
import ch.epfl.cs107.play.game.twic.actor.Bridge;
import ch.epfl.cs107.play.game.twic.actor.CastleDoor;
import ch.epfl.cs107.play.game.twic.actor.CastleKey;
import ch.epfl.cs107.play.game.twic.actor.DragonSpell;
import ch.epfl.cs107.play.game.twic.actor.FireSpell;
import ch.epfl.cs107.play.game.twic.actor.Grass;
import ch.epfl.cs107.play.game.twic.actor.StoryPersonnage;
import ch.epfl.cs107.play.game.twic.actor.Monster;
import ch.epfl.cs107.play.game.twic.actor.Orb;
import ch.epfl.cs107.play.game.twic.actor.Personnage;
import ch.epfl.cs107.play.game.twic.actor.Shop;
import ch.epfl.cs107.play.game.twic.actor.TWICCollectableAreaEntity;
import ch.epfl.cs107.play.game.twic.actor.TWICPlayer;

public interface TWICInteractionVisitor extends RPGInteractionVisitor {

	default void interactWith(TWICCell TWICCell) {
        // by default the interaction is empty
    }
	
	default void interactWith(TWICPlayer TWICPlayer) {
        // by default the interaction is empty
    }
	
	default void interactWith(Bomb bomb) {
		// by default the interaction is empty
	}
	
	default void interactWith(TWICCollectableAreaEntity collectableAreaEntity) {
		// by default the interaction is empty
	}
	
    default void interactWith(Grass grass) {
    	// by default the interaction is empty
    }
    
    default void interactWith(CastleDoor castleDoor) {
    	// by default the interaction is empty
    }
    
    default void interactWith(CastleKey castleKey) {
    	// by default the interaction is empty
    }
    
    default void interactWith(Monster monster) {
    	// by default the interaction is empty
    }
    
    default void interactWith(FireSpell fireSpell) {
    	// by default the interaction is empty
    }
    
    default void interactWith(Orb orb) {
    	// by default the interaction is empty
    }
    
    default void interactWith(Bridge bridge) {
    	// by default the interaction is empty
    }
    
    default void interactWith(Personnage personnage) {
    	// by default the interaction is empty
    }
    
    default void interactWith(ArrowHeadResearcher arrowHeadResearcher) {
    	// by default the interaction is empty
    }
    
    default void interactWith(DragonSpell dragonSpell) {
    	// by default the interaction is empty
    }
    
    default void interactWith(StoryPersonnage storyPerso) {
    	// by default the interaction is empty
    }
    
    default void interactWith(Shop shop) {
    	// by default the interaction is empty
	}
	
}
