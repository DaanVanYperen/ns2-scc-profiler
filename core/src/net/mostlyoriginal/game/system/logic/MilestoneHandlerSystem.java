package net.mostlyoriginal.game.system.logic;

import com.artemis.ComponentMapper;
import com.artemis.annotations.Wire;
import com.artemis.systems.VoidEntitySystem;
import com.badlogic.gdx.math.MathUtils;
import net.mostlyoriginal.api.event.common.Subscribe;
import net.mostlyoriginal.game.component.DistanceIndicator;
import net.mostlyoriginal.game.component.MapMetadata;
import net.mostlyoriginal.game.component.ui.Label;
import net.mostlyoriginal.game.events.EditEvent;
import net.mostlyoriginal.game.manager.MapMetadataManager;

/**
 * @author Daan van Yperen
 */
@Wire
public class MilestoneHandlerSystem extends VoidEntitySystem {

	protected ComponentMapper<DistanceIndicator> mDistanceIndicator;
	protected ComponentMapper<Label> mLabel;

	RefreshHandlerSystem refreshHandlerSystem;
	MapMetadataManager mapMetadataManager;

	@Override
	protected void processSystem() {
	}

	@Subscribe
	protected void distanceChanged( EditEvent event )
	{
		if ( mDistanceIndicator.has(event.entity) && mLabel.has(event.entity))
		{
			Label label = mLabel.get(event.entity);
			DistanceIndicator distanceIndicator = mDistanceIndicator.get(event.entity);

			try {
				int distanceTraveled = Integer.valueOf(label.text);

				if ( distanceIndicator.travelTime > 0 && distanceTraveled > 0 ) {
					float scale = distanceTraveled / distanceIndicator.travelTime;
					if ( scale != 1) {
						MapMetadata metadata = mapMetadataManager.getMetadata();
						metadata.unitsPerPixel = MathUtils.clamp(metadata.unitsPerPixel*scale,0.01f,10f);
						refreshHandlerSystem.restart();
					}
				}
			} catch ( NumberFormatException e ) {
			}
		}
	}
}
