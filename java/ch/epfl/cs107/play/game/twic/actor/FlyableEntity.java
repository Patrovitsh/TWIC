package ch.epfl.cs107.play.game.twic.actor;

public interface FlyableEntity {
	
	/**
	 * Can a flyable entity flies ?
	 * @return (boolean), True if it can
	 */
	default boolean canFly() {
		return true;
	}
}

