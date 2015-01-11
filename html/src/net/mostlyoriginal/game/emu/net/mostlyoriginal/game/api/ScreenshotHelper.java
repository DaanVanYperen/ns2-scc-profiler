package net.mostlyoriginal.game.api;

/**
 * @author Daan van Yperen
 */

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.gwt.GwtApplication;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.google.gwt.dom.client.CanvasElement;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.user.client.Window;
import net.mostlyoriginal.game.component.Layer;
import net.mostlyoriginal.game.system.logic.GameState;

/**
 * @author Daan van Yperen
 */
public class ScreenshotHelper {

	public void screenshot(String filename) {
		CanvasElement canvas = ((GwtApplication) Gdx.app).getCanvasElement();
		// base64 encoded PNG file using data URL encoding! send this to your server
		String dataUrl = canvas.toDataUrl("image/png");
		// just opening it in a new window, replace this with whatever you want to do with the data
		Window.open(dataUrl, "_blank", "");
	}

	public void loadMapTexture(GameState state, Layer layer) {
	}

	public void saveMapTexture(GameState state, Layer layer) {
	}

	public void beforeScreenshotFrame() {

	}

	public Pixmap asPixmap(ImageElement imgElement) {
		return new Pixmap(imgElement);
	}
}
