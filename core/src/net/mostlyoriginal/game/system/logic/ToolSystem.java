package net.mostlyoriginal.game.system.logic;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Wire;
import com.artemis.managers.TagManager;
import com.artemis.systems.EntityProcessingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import net.mostlyoriginal.api.component.basic.Pos;
import net.mostlyoriginal.api.component.graphics.Anim;
import net.mostlyoriginal.api.utils.EntityUtil;
import net.mostlyoriginal.game.component.Tool;
import net.mostlyoriginal.game.component.ui.Clickable;
import net.mostlyoriginal.game.manager.AssetSystem;

/**
 * @author Daan van Yperen
 */
@Wire
public class ToolSystem extends EntityProcessingSystem {

	protected ComponentMapper<Clickable> mClickable;
	protected ComponentMapper<Tool> mTool;
	protected ComponentMapper<Anim> mAnim;
	protected ComponentMapper<Pos> mPos;
	private TagManager tagManager;
	private boolean buttonWasDown=true;
	private AssetSystem assetSystem;

	@SuppressWarnings("unchecked")
	public ToolSystem() {
		super(Aspect.getAspectForAll(Tool.class,Pos.class, Anim.class));
	}

	@Override
	protected void process(Entity e) {

		if ( Gdx.input.isButtonPressed(1) )
		{
			reset();
			return;
		}

		final Entity cursor = tagManager.getEntity("cursor");
		if ( cursor != null ) {
			Pos cursorPos = mPos.get(cursor);
			Pos toolPos = mPos.get(e);

			Anim anim = mAnim.get(e);
			TextureRegion icon = assetSystem.get(anim.id).getKeyFrame(0);
			toolPos.x = cursorPos.x - (icon.getRegionWidth()/2 * anim.scale);
			toolPos.y = cursorPos.y - (icon.getRegionHeight()/2 * anim.scale);

			Clickable clickable = mClickable.get(e);
			if ( Gdx.input.isButtonPressed(0) )
			{
				Tool tool = mTool.get(e);
				if ( tool.listener.enabled() && (tool.continuous || !buttonWasDown) ) {
					tool.listener.run();
					buttonWasDown = true;
				}
			} else buttonWasDown=false;
		}
	}

	public void reset() {
		EntityUtil.safeDeleteAll(getActives());
	}
}
