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

		// VENTILATION
		addNode(410, 140);
		addTechpoint(465, 125);
		addDuct(410, 181);

		// GENERATOR
		addNode(745, 465);
		addTechpoint(745, 425);

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

		// MONITORING
		addNode(355, 520);
	}

	private void addDuct(int x, int y) {
		entityFactoryManager.createEntity("duct", x, y, null);
	}
	private void addNode(int x, int y) {
		entityFactoryManager.createEntity("resourceNode", x, y, null);
	}
	private void addTechpoint(int x, int y) {
		entityFactoryManager.createEntity("techpoint", x, y, null);
	}
}
