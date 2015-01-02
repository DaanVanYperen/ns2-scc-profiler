package net.mostlyoriginal.game.system.logic;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Wire;
import com.artemis.systems.EntityProcessingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import net.mostlyoriginal.api.component.graphics.Invisible;
import net.mostlyoriginal.api.event.common.EventManager;
import net.mostlyoriginal.game.component.Input;
import net.mostlyoriginal.game.component.ui.Clickable;
import net.mostlyoriginal.game.component.ui.Label;
import net.mostlyoriginal.game.events.EditEvent;

/**
 * @author Daan van Yperen
 */
@Wire
public class InputSystem extends EntityProcessingSystem implements InputProcessor {

	protected ComponentMapper<Label> mLabel;
	protected ComponentMapper<Clickable> mClickable;
	protected ComponentMapper<Input> mInput;

	protected Entity selectedInput;
	private String oldText;
	private boolean entered;
	private boolean escaped;
	private Clickable clickable;
	private Input input;

	private EventManager eventManager;

	public InputSystem() {
		super(Aspect.getAspectForAll(Label.class, Clickable.class, Input.class).exclude(Invisible.class));
	}

	StringBuilder typeBuffer = new StringBuilder();

	@Override
	protected void initialize() {
		super.initialize();
		Gdx.input.setInputProcessor(this);
	}

	@Override
	protected void process(Entity e) {

		Clickable clickable = mClickable.get(e);
		if (clickable.state == Clickable.ClickState.CLICKED_LEFT) {
			selectInput(e);
		}

		if (selectedInput != null && selectedInput == e) {
			Label label = mLabel.get(e);

			// temporarily put text in field.
			if (typeBuffer.length() > 0) {
				label.text = typeBuffer.toString() + "<";
			} else label.text = "<";

			// escape changes.
			if (escaped) {
				selectInput(null);
			}

			// apply changes.
			if (entered) {
				entered = false;
				if (typeBuffer.length() >= input.minLength ) {
					label.text = typeBuffer.toString();
					selectedInput = null;
					typeBuffer = new StringBuilder();
					eventManager.dispatch(new EditEvent(e));
				}
			}
		}
	}

	private void selectInput(Entity e) {

		if (selectedInput != e) {

			// restore old input
			if (selectedInput != null) {
				Label label = mLabel.get(selectedInput);
				label.text = oldText;
				oldText = null;
			}

			selectedInput = e;
			if (e != null) {
				input = mInput.get(e);
				Label label = mLabel.get(e);
				oldText = label.text;
			}
		}
	}

	@Override
	public boolean keyDown(int keycode) {
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {

		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		if (selectedInput != null) {
			if (character == 8) {
				if (typeBuffer.length() > 0) {
					typeBuffer.deleteCharAt(typeBuffer.length() - 1);
				}
			} else if (character == 13) {
				entered = true;
			} else if (character == 27) {
				escaped = true;
			} else if (character >= ' ' || character <= '~') {
				// hack hack hack
				if ( typeBuffer.length() < input.maxLength && input.allowedCharacters.contains(character+"") ) {
					typeBuffer.append(character);
				}
			}
		}
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		return false;
	}
}
