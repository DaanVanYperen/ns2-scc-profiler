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
import net.mostlyoriginal.game.component.Persistable;
import net.mostlyoriginal.game.component.Team;
import net.mostlyoriginal.game.component.TeamMember;
import net.mostlyoriginal.game.component.ui.ButtonListener;
import net.mostlyoriginal.game.manager.EntityFactoryManager;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Daan van Yperen
 */
@Wire
public class PersistHandlerSystem extends EntitySystem {

	private EntityFactoryManager entityFactoryManager;
	private RefreshHandlerSystem refreshHandlerSystem;

	protected ComponentMapper<TeamMember> mTeamMember;
	protected ComponentMapper<Pos> mPos;
	protected ComponentMapper<Persistable> mPersistable;

	private static final class GameState {

		public GameState() {
		}

		List<Element> elements = new ArrayList<Element>();
	}

	private static final class Element {
		public String id;
		public int x;
		public int y;
		public Team team;

		public Element() {
		}

		public Element(String id, int x, int y) {
			this.id = id;
			this.x = x;
			this.y = y;
		}
	}

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
		});
		entityFactoryManager.createBasicButton("load", 50 + 40 * 8, new ButtonListener() {
			@Override
			public void run() {
				load();
			}

			@Override
			public boolean enabled() {
				return true;
			}
		});

		load();
	}

	private void save() {

		GameState state = new GameState();

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

	private Preferences getPrefs() {
		return Gdx.app.getPreferences("default");
	}

	private void load() {

		String msg = getPrefs().getString("state");

		if (msg != null && !msg.isEmpty() ) {
			EntityUtil.safeDeleteAll(getActives());

			Json json = new Json();
			GameState state = json.fromJson(GameState.class, msg);

			for (Element element : state.elements) {
				Entity entity = entityFactoryManager.createEntity(element.id, element.x, element.y, null);

				if ( mTeamMember.has(entity))
				{
					mTeamMember.get(entity).team = element.team;
				}
			}

			refreshHandlerSystem.restart();
		}
	}

	@Override
	protected void processEntities(ImmutableBag<Entity> entities) {
	}
}
