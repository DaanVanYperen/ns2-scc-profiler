package net.mostlyoriginal.game.manager;

import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Wire;
import com.artemis.systems.VoidEntitySystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import net.mostlyoriginal.game.component.Routable;
import net.mostlyoriginal.game.component.Traveler;
import net.mostlyoriginal.game.component.ui.RenderMask;

/**
 * Load map layers.
 *
 * @author Daan van Yperen
 */
@Wire
public class LayerLoaderSystem extends VoidEntitySystem {

	private EntityFactoryManager entityFactoryManager;
	private LayerManager layerManager;

	protected ComponentMapper<Traveler> mTraveler;
	protected ComponentMapper<Routable> mRoutable;
	private boolean processed;

	public String mapFile = "ns2_summit.tga";

	@Override
	protected void initialize() {
		super.initialize();
	}

	public void load() {
		layerManager.getLayer("RAW", RenderMask.Mask.BASIC).drawPixmapToFit(new Pixmap(Gdx.files.internal(mapFile)));

		/*

		addNode(100, 170);
		Entity sewerHive = addTechpoint(100, 200);

		for (int i=0;i< G.ALIEN_TEAM_SIZE;i++) {
			Entity alien = entityFactoryManager.createEntity("alien", 130, 395, null);
			Traveler traveler = mTraveler.get(alien);
			traveler.location = mRoutable.get(sewerHive);
		}

		// PURIFICATION
		addNode(260, 160);
		addDuct(195, 290);
		addDuct(185, 265);
		addDuct(335, 248);

		addDuct(360, 325);

		for ( int i=1; i<5;i++) {
			addWall(210 + i * 14, 220);
		}

		// VENTILATION
		addNode(410, 140);
		addTechpoint(465, 125);
		addDuct(410, 181);
		for ( int i=1; i<5;i++) {
			addDuct(416 + i * 14, 156);
		}

		// GENERATOR
		addNode(745, 465);
		addTechpoint(745, 425);
		addDuct(655, 485);

		// ROOM NORTH-WEST OF GENERATOR
		addNode(570, 600);

		// MARINE START
		addNode(160, 600);
		Entity marineSpawn = addTechpoint(130, 560);

		for (int i=0;i< G.MARINE_TEAM_SIZE;i++) {
			Entity marine = entityFactoryManager.createEntity("marine", 165, 175, null);
			Traveler traveler = mTraveler.get(marine);
			traveler.location = mRoutable.get(marineSpawn);
		}

		// SEWER
		addNode(120, 390);

		// LOADING
		addNode(635, 230);

		// CENTER
		addNode(405, 400);
		addDuct(570, 380);

		for ( int i=0; i<5;i++) {
			addWall(350, 400 - i * 14);
		}
		for ( int i=0; i<5;i++) {
			addWall(434, 400 - i * 14);
		}

		// MONITORING
		addNode(355, 520);
		for ( int i=0; i<4;i++) {
			addWall(384, 540 - i * 14);
		}
		for ( int i=0; i<3;i++) {
			addWall(384 + 28, 560 - i * 14);
		}
		for ( int i=0; i<2;i++) {
			addWall(394 + 28 + i * 14, 520);
		}
		for ( int i=1; i<5;i++) {
		 	addWall(394 + 28 + i * 14, 580);
		} */
	}

	private void addDuct(int x, int y) {
		entityFactoryManager.createEntity("duct", x, y, null);
	}
	private void addWall(int x, int y) {
		entityFactoryManager.createEntity("wall", x, y, null);
	}
	private Entity addNode(int x, int y) {
		return entityFactoryManager.createEntity("resourceNode", x, y, null);
	}
	private Entity addTechpoint(int x, int y) {
		return entityFactoryManager.createEntity("techpoint", x, y, null);
	}

	@Override
	protected void processSystem() {
		if ( !processed )
		{
			processed = true;
			load();
		}
	}
}
