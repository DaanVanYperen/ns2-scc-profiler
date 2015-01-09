package net.mostlyoriginal.game.api;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.utils.ScreenUtils;
import net.mostlyoriginal.game.G;
import net.mostlyoriginal.game.component.Layer;
import net.mostlyoriginal.game.system.logic.GameState;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @author Daan van Yperen
 */
public class ScreenshotHelper {

	public void screenshot(FileHandle local) {
		try {
			FileHandle fh;
			do {
				fh = local;
			} while (fh.exists());
			byte[] frameBufferPixels = ScreenUtils.getFrameBufferPixels(0, 0, G.CANVAS_WIDTH, G.CANVAS_HEIGHT, true);
			Pixmap pixmap = new Pixmap(G.CANVAS_WIDTH, G.CANVAS_HEIGHT, Pixmap.Format.RGBA8888);
			pixmap.getPixels().put(frameBufferPixels);
			PixmapIO.writePNG(fh, pixmap);
			pixmap.dispose();
		} catch (Exception e) {
		}
	}

	public void loadMapTexture(GameState state, Layer layer) {
		if ( state.layer != null )
		{
			if ( layer.pixmap != null ) {
				layer.pixmap.dispose();
				layer.pixmap=null;
			}
			layer.pixmap = new Pixmap(state.layer,0, state.layer.length);
			layer.invalidateTexture();
		}
	}

	public void saveMapTexture(GameState state, Layer layer) {
		Pixmap pixmap = layer.pixmap;
		try {
			PixmapIO.PNG writer = new PixmapIO.PNG((int)(pixmap.getWidth() * pixmap.getHeight() * 1.5f)); // Guess at deflated size.
			try {
				writer.setFlipY(false);
				ByteArrayOutputStream pngStream = new ByteArrayOutputStream();
				writer.write(pngStream, pixmap);
				state.layer = pngStream.toByteArray();
			} finally {
				writer.dispose();
			}
		} catch (IOException ex) {
			throw new RuntimeException("Could not save map as texture.");
		}
	}

}
