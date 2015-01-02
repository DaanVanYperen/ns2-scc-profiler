package net.mostlyoriginal.game.manager;

import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.Manager;
import com.artemis.annotations.Wire;
import com.artemis.managers.TagManager;
import com.artemis.utils.EntityBuilder;
import net.mostlyoriginal.game.component.MapMetadata;

/**
 * @author Daan van Yperen
 */
@Wire
public class MapMetadataManager extends Manager {

	protected ComponentMapper<MapMetadata> mMapMetadata;

	public static final String MAP_METADATA = "mapMetadata";
	TagManager tagManager;

	public MapMetadata getMetadata()
	{
		Entity entity = tagManager.getEntity(MAP_METADATA);
		if ( entity == null )
		{
			entity = new EntityBuilder(world).with(new MapMetadata()).tag(MAP_METADATA).build();
		}
		return mMapMetadata.get(entity);
	}
}
