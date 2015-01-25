package net.mostlyoriginal.game.system.logic;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.EntitySystem;
import com.artemis.annotations.Wire;
import com.artemis.utils.ImmutableBag;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.utils.Json;
import net.mostlyoriginal.api.component.basic.Pos;
import net.mostlyoriginal.api.utils.EntityUtil;
import net.mostlyoriginal.game.api.ScreenshotHelper;
import net.mostlyoriginal.game.component.*;
import net.mostlyoriginal.game.component.ui.ButtonListener;
import net.mostlyoriginal.game.component.ui.RenderMask;
import net.mostlyoriginal.game.manager.EntityFactoryManager;
import net.mostlyoriginal.game.system.LayerLoaderSystem;
import net.mostlyoriginal.game.manager.LayerManager;
import net.mostlyoriginal.game.manager.MapMetadataManager;

/**
 * @author Daan van Yperen
 */
@Wire
public class PersistHandlerSystem extends EntitySystem {

	private EntityFactoryManager entityFactoryManager;
	private RefreshHandlerSystem refreshHandlerSystem;
	private MapMetadataManager mapMetadataManager;

	protected ComponentMapper<TeamMember> mTeamMember;
	protected ComponentMapper<Pos> mPos;
	protected ComponentMapper<Persistable> mPersistable;

	LayerLoaderSystem layerLoaderSystem;
	LayerManager layerManager;

	@SuppressWarnings("unchecked")
	public PersistHandlerSystem() {
		super(Aspect.getAspectForAll(Persistable.class));
	}

	@Override
	protected void initialize() {
		super.initialize();

		entityFactoryManager.createBasicButton("save", 50 + 40 * 7, new ButtonListener() {
			@Override
			public void run() {
				save();
			}

			@Override
			public boolean enabled() {
				return true;
			}
		}, 50);
		entityFactoryManager.createBasicButton("load", 50 + 40 * 8, new ButtonListener() {
			@Override
			public void run() {
				load();
			}

			@Override
			public boolean enabled() {
				return true;
			}
		}, 50);

		load();
	}

	private void save() {

		GameState state = new GameState();
		state.mapMetadata.set(mapMetadataManager.getMetadata());

		new ScreenshotHelper().saveMapTexture(state, getRawLayer());

		for (Entity entity : getActives()) {
			if (entity != null) {
				Pos pos = mPos.get(entity);
				Element element = new Element(mPersistable.get(entity).saveId,
						(int) pos.x, (int) pos.y);

				if ( mTeamMember.has(entity))
				{
					element.team=mTeamMember.get(entity).team;
				}

				state.elements.add(element);
			}
		}



		Json json = new Json();
		String jsonString = json.toJson(state);

		Preferences prefs = getPrefs();
		prefs.putString("state", jsonString);
		prefs.flush();
	}

	public Layer getRawLayer() {
		return layerManager.getLayer("RAW", RenderMask.Mask.BASIC);
	}

	private Preferences getPrefs() {
		return Gdx.app.getPreferences(layerLoaderSystem.mapName);
	}

	private void load() {

		String msg = getPrefs().getString("state");

		if (msg != null && !msg.isEmpty() ) {
			EntityUtil.safeDeleteAll(getActives());

			Json json = new Json();
			GameState state = json.fromJson(GameState.class, msg);

			new ScreenshotHelper().loadMapTexture(state, getRawLayer());

			for (Element element : state.elements) {
				Entity entity = entityFactoryManager.createEntity(element.id, element.x, element.y, null);

				if ( mTeamMember.has(entity))
				{
					mTeamMember.get(entity).team = element.team;
				}
			}

			// load metadata.
			if ( state.mapMetadata != null )
			{
				mapMetadataManager.getMetadata().set(state.mapMetadata);
			}

			refreshHandlerSystem.restart();
		}
	}

	@Override
	protected void processEntities(ImmutableBag<Entity> entities) {
	}
}
