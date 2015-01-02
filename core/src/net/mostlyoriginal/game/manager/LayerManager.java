package net.mostlyoriginal.game.manager;

import com.artemis.*;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.graphics.Color;
import net.mostlyoriginal.api.component.basic.Pos;
import net.mostlyoriginal.api.component.graphics.Renderable;
import net.mostlyoriginal.game.G;
import net.mostlyoriginal.game.component.Layer;
import net.mostlyoriginal.game.component.Team;
import net.mostlyoriginal.game.component.ui.RenderMask;
import net.mostlyoriginal.game.system.BlockadeSystem;

import java.util.EnumSet;
import java.util.HashMap;

/**
 * @author Daan van Yperen
 */
@Wire
public class LayerManager extends Manager {

	private BlockadeSystem blockadeSystem;

	protected ComponentMapper<Layer> mLayer;
	protected ComponentMapper<Renderable> mRenderable;
	private HashMap<String, Layer> layers = new HashMap<>();
    private HashMap<Team, Layer> teamNavigationLayer = new HashMap<>();

	public static final int CELL_SIZE = 2;
	public static final int LAYER_HEIGHT = G.CANVAS_HEIGHT / CELL_SIZE;
	public static final int LAYER_WIDTH = G.CANVAS_WIDTH / CELL_SIZE;
	private Archetype layerArchetype;
	private ComponentMapper<RenderMask> mRenderMask;

	@Override
	protected void initialize() {
		layerArchetype = new ArchetypeBuilder().add(Pos.class, Layer.class, Renderable.class, RenderMask.class).build(world);
	}

	public Layer getLayer( String key, RenderMask.Mask mask )
	{
		Layer layer = this.layers.get(key);
		if (layer == null)
		{
			Entity layerEntity = world.createEntity(layerArchetype);
			layer = mLayer.get(layerEntity);

			Renderable renderable = mRenderable.get(layerEntity);
			renderable.layer = -100;

			mRenderMask.get(layerEntity).visible = EnumSet.of(mask);

			layers.put(key, layer);
		}
		return layer;
	}

	public Layer getTeamNavLayer(Team team)
	{
		Layer layer = teamNavigationLayer.get(team);
		if ( layer == null )
		{
			Entity layerEntity = world.createEntity(layerArchetype);
			layer = mLayer.get(layerEntity);

			switch ( team ) {
				case  ALIEN: mRenderMask.get(layerEntity).visible = EnumSet.of(RenderMask.Mask.PATHFIND_ALIEN); break;
				case MARINE: mRenderMask.get(layerEntity).visible = EnumSet.of(RenderMask.Mask.PATHFIND_MARINE); break;
			}

			Renderable renderable = mRenderable.get(layerEntity);
			renderable.layer = -90 + team.ordinal();

			teamNavigationLayer.put(team, layer);

		}

		return layer;
	}

	/** Clear with Map maked by color */
	public void clearWithMap(Layer layer, Color color, float colorTransparancy) {
		layer.clear();

		final Layer rawMapLayer = getLayer("RAW", RenderMask.Mask.BASIC);

		Color tmpColor = new Color();

		for (int x=0;x < rawMapLayer.pixmap.getWidth();x++) {
			for (int y = 0; y < rawMapLayer.pixmap.getHeight(); y++)
			{
				int rawColor = rawMapLayer.pixmap.getPixel(x, rawMapLayer.pixmap.getHeight() - y);
				boolean isWalkable= ((rawColor & 0x000000ff)) / 255f >= 0.5f;

				// generate mask based on blockades.
				tmpColor.set(rawColor);
				if ( isWalkable )
				{
					tmpColor.r = (tmpColor.r * colorTransparancy + color.r * (1- colorTransparancy));
					tmpColor.g = (tmpColor.g * colorTransparancy + color.g * (1- colorTransparancy));
					tmpColor.b = (tmpColor.b * colorTransparancy + color.b * (1- colorTransparancy));
					tmpColor.a = (tmpColor.a * colorTransparancy + color.a * (1- colorTransparancy));
					layer.drawPixel(x, layer.pixmap.getHeight() - y,tmpColor);
				}
			}
		}

		rawMapLayer.invalidateTexture();
	}
}
