package ch.epfl.cs107.play.game.twic;

import ch.epfl.cs107.play.game.twic.actor.TWICPlayer;
import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.rpg.RPG;
import ch.epfl.cs107.play.game.twic.area.Chambre;
import ch.epfl.cs107.play.game.twic.area.Chateau;
import ch.epfl.cs107.play.game.twic.area.Ferme;
import ch.epfl.cs107.play.game.twic.area.Grotte;
import ch.epfl.cs107.play.game.twic.area.GrotteMew1;
import ch.epfl.cs107.play.game.twic.area.GrotteMew2;
import ch.epfl.cs107.play.game.twic.area.GrotteMewExt;
import ch.epfl.cs107.play.game.twic.area.Route;
import ch.epfl.cs107.play.game.twic.area.RouteChateau;
import ch.epfl.cs107.play.game.twic.area.RouteTemple;
import ch.epfl.cs107.play.game.twic.area.Temple;
import ch.epfl.cs107.play.game.twic.area.Village;
import ch.epfl.cs107.play.io.FileSystem;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.window.Window;

public class TWIC extends RPG {
	public final static float CAMERA_SCALE_FACTOR = 15f;

	private final DiscreteCoordinates startingPosition = new DiscreteCoordinates(2, 4);
	
	/**
	 * Add all the areas
	 */
	private void createAreas(){

		addArea(new Ferme());
		addArea(new Village());
		addArea(new Route());
		addArea(new RouteChateau());
		addArea(new Chateau());
		addArea(new Chambre());
		addArea(new RouteTemple());
		addArea(new Temple());
		addArea(new Grotte());
		addArea(new GrotteMew1());
		addArea(new GrotteMew2());
		addArea(new GrotteMewExt());

	}

	@Override
	public boolean begin(Window window, FileSystem fileSystem) {


		if (super.begin(window, fileSystem)) {

			createAreas();
			String areaName = "PetalburgTimmy";
			Area area = setCurrentArea(areaName, true);
			TWICPlayer player = new TWICPlayer(area, Orientation.DOWN, startingPosition);
			initPlayer(player); 
			return true;
		}
		return false;
	}

	@Override
	public void update(float deltaTime) {
		
		super.update(deltaTime);

	}

	@Override
	public void end() {
	}

	@Override
	public String getTitle() {
		return "TheWalkingIC";
	}

}