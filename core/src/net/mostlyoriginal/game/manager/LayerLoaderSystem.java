package net.mostlyoriginal.game.manager;

import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Wire;
import com.artemis.systems.VoidEntitySystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.RootPanel;
import net.mostlyoriginal.game.api.ScreenshotHelper;
import net.mostlyoriginal.game.component.Layer;
import net.mostlyoriginal.game.component.Routable;
import net.mostlyoriginal.game.component.Traveler;
import net.mostlyoriginal.game.component.ui.RenderMask;
import net.mostlyoriginal.game.system.logic.RefreshHandlerSystem;

/**
 * Load map layers.
 *
 * @author Daan van Yperen
 */
@Wire
public class LayerLoaderSystem extends VoidEntitySystem {

	private EntityFactoryManager entityFactoryManager;
	private LayerManager layerManager;

	public RefreshHandlerSystem refreshHandlerSystem;

	protected ComponentMapper<Traveler> mTraveler;
	protected ComponentMapper<Routable> mRoutable;
	public boolean processed;
	private boolean loading=false;

	public String mapName = "ns2_caged_v3";
	public String mapFile = "data/"+ mapName + ".png";

	public void load() {
		if (!loading && GWT.isClient()) {
			String mapParameter = Window.Location.getParameter("map");
			if (mapParameter != null && !mapParameter.isEmpty()) {
				loading=true;
				mapName = mapParameter;
				mapFile = null;
				final Image img = new Image("img.php?mode=native&url=" + mapParameter);
				img.setVisible(false);
				RootPanel.get().add(img);
				img.addLoadHandler(new LoadHandler() {
					@Override
					public void onLoad(LoadEvent event) {

						Gdx.app.postRunnable(new Runnable() {
							@Override
							public void run() {
								ImageElement imgElement = ImageElement.as(img.getElement());
								RootPanel.get().remove(img);
								Pixmap pixmap = new ScreenshotHelper().asPixmap(imgElement);
								if (pixmap != null) {
									Layer layer = layerManager.getLayer("RAW", RenderMask.Mask.BASIC);
									layer.drawPixmapToFit(pixmap);
									layer.invalidateTexture();
									pixmap.dispose();
									processed = true;
									refreshHandlerSystem.restart();
								}
							}
						});
					}
				});
			}
		}

		if (!loading && mapFile != null) {
			loading = true;
			processed = true;
			layerManager.getLayer("RAW", RenderMask.Mask.BASIC).drawPixmapToFit(new Pixmap(Gdx.files.internal(mapFile)));
		}
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
		if (!processed) {
			load();
		}
	}
}
