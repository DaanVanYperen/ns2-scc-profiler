package net.mostlyoriginal.game.manager;

import com.artemis.*;
import com.artemis.annotations.Wire;
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
	private Layer rawMapLayer;
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

	public Layer getRawLayer()
	{
		if ( rawMapLayer == null )
		{
			Entity layerEntity = world.createEntity(layerArchetype);
			rawMapLayer = mLayer.get(layerEntity);

			Renderable renderable = mRenderable.get(layerEntity);
			renderable.layer = -100;

			mRenderMask.get(layerEntity).visible = EnumSet.of(RenderMask.Mask.BASIC);
		}
		return rawMapLayer;
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

}
