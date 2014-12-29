package net.mostlyoriginal.game.manager;

import com.artemis.Manager;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import net.mostlyoriginal.game.component.Layer;

/**
 * Load map layers.
 *
 * @author Daan van Yperen
 */
@Wire
public class MapLoaderManager extends Manager {

	private EntityFactoryManager entityFactoryManager;
	private LayerManager layerManager;

	@Override
	protected void initialize() {
		super.initialize();

		layerManager.getRawLayer().drawPixmapToFit(new Pixmap(Gdx.files.internal("ns2_caged.tga")));

		addNode(100, 170);
		addTechpoint(100, 200);

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
		addTechpoint(130, 560);

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
		}
	}

	private void addDuct(int x, int y) {
		entityFactoryManager.createEntity("duct", x, y, null);
	}
	private void addWall(int x, int y) {
		entityFactoryManager.createEntity("wall", x, y, null);
	}
	private void addNode(int x, int y) {
		entityFactoryManager.createEntity("resourceNode", x, y, null);
	}
	private void addTechpoint(int x, int y) {
		entityFactoryManager.createEntity("techpoint", x, y, null);
	}
}
