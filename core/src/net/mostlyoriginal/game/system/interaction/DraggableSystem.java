package net.mostlyoriginal.game.system.interaction;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Wire;
import com.artemis.systems.EntityProcessingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector3;
import net.mostlyoriginal.api.component.basic.Bounds;
import net.mostlyoriginal.api.component.basic.Pos;
import net.mostlyoriginal.api.event.common.EventManager;
import net.mostlyoriginal.api.system.camera.CameraSystem;
import net.mostlyoriginal.game.component.ui.Clickable;
import net.mostlyoriginal.game.events.DragEvent;
import net.mostlyoriginal.game.component.ui.Draggable;

/**
 * Draggable components.
 *
 * @author Daan van Yperen
 */
@Wire
public class DraggableSystem extends EntityProcessingSystem {

	protected ComponentMapper<Pos> mPos;
	protected ComponentMapper<Bounds> mBounds;
	protected ComponentMapper<Clickable> mClickable;
	protected ComponentMapper<Draggable> mDraggable;

	private CameraSystem cameraSystem;

	private boolean leftMousePressed = false;
	private boolean rightMousePressed = false;
	private boolean leftWasDown = false;
	private boolean rightWasDown = false;

	private EventManager em;

	@SuppressWarnings("unchecked")
	public DraggableSystem() {
		super(Aspect.getAspectForAll(Pos.class, Clickable.class, Draggable.class));
	}

	@Override
	protected void begin() {
		super.begin();

		leftMousePressed = Gdx.input.isButtonPressed(Input.Buttons.LEFT);
	}

	@Override
	protected void end() {
		super.end();
	}

	private Vector3 aimAtTmp = new Vector3();


	@Override
	protected void process(Entity e) {
		Draggable draggable = mDraggable.get(e);
		Clickable clickable = mClickable.get(e);
		if ( draggable.dragging )
		{
			if ( !leftMousePressed ) {
				draggable.dragging = false;
				em.dispatch(new DragEvent(e));
			} else {
				// drag to mouse cursor.
				final Pos pos = mPos.get(e);

				aimAtTmp.set(Gdx.input.getX(), Gdx.input.getY(), 0);

				final Vector3 unproject = cameraSystem.camera.unproject(aimAtTmp);

				pos.x = unproject.x;
				pos.y = unproject.y;

				if ( mBounds.has(e))
				{
					Bounds bounds = mBounds.get(e);
					pos.x -= bounds.cx();
					pos.y -= bounds.cy();
				}
			}
		} else {
			if ( clickable.state == Clickable.ClickState.CLICKED_LEFT ) {
				draggable.dragging = true;
			}
		}
	}
}
