package ch.epfl.cs107.play.game.twic;

import ch.epfl.cs107.play.game.twic.actor.FlyableEntity;
import ch.epfl.cs107.play.game.twic.actor.StoryPersonnage;
import ch.epfl.cs107.play.game.twic.actor.TWICPlayer;
import ch.epfl.cs107.play.game.twic.area.story.Story;
import ch.epfl.cs107.play.game.areagame.AreaBehavior;
import ch.epfl.cs107.play.game.areagame.actor.Interactable;
import ch.epfl.cs107.play.game.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.window.Window;

public class TWICBehavior extends AreaBehavior {
	public enum TWICCellType{
		//https://stackoverflow.com/questions/25761438/understanding-bufferedimage-getrgb-output-values
		NULL(0, false, false, false),
		WALL(-16777216, false, false, false),
		IMPASSABLE(-8750470, false, true, false),
		INTERACT(-256, true, true, false),
		DOOR(-195580, true, true, false),
		WALKABLE(-1, true, true, true),
		NO_SPAWNABLE(-8336444, true, true, false),
		APPARITION_CELL(-616056, true, true, false);

		final int type;
		final boolean isWalkable;
		final boolean isFlyable;
		final boolean isSpawnable;

		TWICCellType(int type, boolean isWalkable, boolean isFlyable, boolean isSpawnable){
			this.type = type;
			this.isWalkable = isWalkable;
			this.isFlyable = isFlyable;
			this.isSpawnable = isSpawnable;
		}

		public static TWICCellType toType(int type){
			for(TWICCellType ict : TWICCellType.values()){
				if(ict.type == type)
					return ict;
			}
			// When you add a new color, you can print the int value here before assign it to a type
			System.out.println(type);
			return NULL;
		}
	}

	/**
	 * Default TWICBehavior Constructor
	 * @param window (Window), not null
	 * @param name (String): Name of the Behavior, not null
	 */
	public TWICBehavior(Window window, String name){
		super(window, name);
		int height = getHeight();
		int width = getWidth();
		for(int y = 0; y < height; y++) {
			for (int x = 0; x < width ; x++) {
				TWICCellType color = TWICCellType.toType(getRGB(height-1-y, x));
				setCell(x, y, new TWICCell(x, y, color));
			}
		}
	}
	
	public boolean isDoor(DiscreteCoordinates coord) {
		return (((TWICCell)getCell(coord.x, coord.y)).isDoor());
	}
	
	/**
	 * Cell adapted to the TWIC game
	 */
	public class TWICCell extends AreaBehavior.Cell {
		/// Type of the cell following the enum
		private final TWICCellType type;
		
		/**
		 * Default TWICCell Constructor
		 * @param x (int): x coordinate of the cell
		 * @param y (int): y coordinate of the cell
		 * @param type (EnigmeCellType), not null
		 */
		public  TWICCell(int x, int y, TWICCellType type){
			super(x, y);
			this.type = type;
		}
		public boolean isDoor() {
			return type == TWICCellType.DOOR;
		}
		@Override
		protected boolean canLeave(Interactable entity) {
			return true;
		}

		@Override
		protected boolean canEnter(Interactable entity) {
			if(entity instanceof Story)
				return true;
			
			if(type == TWICCellType.APPARITION_CELL && (!(entity instanceof TWICPlayer) && !(entity instanceof StoryPersonnage)
					&& entity.takeCellSpace()))
				return false;
			
			if(entity instanceof FlyableEntity) {
				FlyableEntity flyableEntity = (FlyableEntity) entity;
				if(flyableEntity.canFly()) 
					return (!hasNonTraversableContent() && type.isWalkable) || (flyableEntity.canFly() && type.isFlyable);
				else 
					return (type.isWalkable);			
			}
			
			return (!hasNonTraversableContent() && type.isWalkable) || (!entity.takeCellSpace() && type.isWalkable);
		}
	    
		@Override
		public boolean isCellInteractable() {
			return true;
		}

		@Override
		public boolean isViewInteractable() {
			return false;
		}

		@Override
		public void acceptInteraction(AreaInteractionVisitor v) {
		}
		
		@Override
		public boolean canSpawn() {
			return type.isSpawnable && !hasNonTraversableContent();
		}

	}
}
