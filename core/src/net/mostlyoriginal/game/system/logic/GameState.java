package net.mostlyoriginal.game.system.logic;

import com.badlogic.gdx.utils.Array;
import net.mostlyoriginal.game.component.MapMetadata;

/**
* @author Daan van Yperen
*/
public class GameState {

	public GameState() {
	}

	public MapMetadata mapMetadata = new MapMetadata();
	public byte[] layer;
	public Array<Element> elements = new Array<>();
}
