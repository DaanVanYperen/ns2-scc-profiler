package net.mostlyoriginal.game.system.render;

/**
 * @author Daan van Yperen
 */

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import net.mostlyoriginal.api.component.basic.Pos;
import net.mostlyoriginal.api.component.graphics.Color;
import net.mostlyoriginal.api.component.graphics.Invisible;
import net.mostlyoriginal.api.component.graphics.Renderable;
import net.mostlyoriginal.api.system.camera.CameraSystem;
import net.mostlyoriginal.api.system.delegate.DeferredEntityProcessingSystem;
import net.mostlyoriginal.api.system.delegate.EntityProcessPrincipal;
import net.mostlyoriginal.game.component.ui.Label;
import net.mostlyoriginal.game.manager.FontManager;

/**
 * Render and progress animations.
 *
 * @author Daan van Yperen
 * @see net.mostlyoriginal.api.component.graphics.Anim
 */
@Wire
public class LabelRenderSystem extends DeferredEntityProcessingSystem {

    protected ComponentMapper<Pos> mPos;
    protected ComponentMapper<Label> mLabel;
    protected ComponentMapper<Color> mColor;

    protected CameraSystem cameraSystem;
    protected FontManager fontManager;

    protected SpriteBatch batch;

    public LabelRenderSystem(EntityProcessPrincipal principal) {
        super(Aspect.getAspectForAll(Pos.class, Label.class, Renderable.class).exclude(Invisible.class), principal);
        batch = new SpriteBatch(1000);
    }

    @Override
    protected void begin() {
        batch.setProjectionMatrix(cameraSystem.camera.combined);
        batch.begin();
        batch.setColor(0f, 0f, 0f, 1f);
    }

    @Override
    protected void end() {
        batch.end();
    }

    @Override
    protected boolean checkProcessing() {
        return true;
    }

    protected void process(final Entity entity) {

        final Label label = mLabel.get(entity);
        final Pos pos = mPos.get(entity);

        if (label.text != null) {

            final BitmapFont font = label.scale == 2f ? fontManager.bigFont : fontManager.font;

            if ( mColor.has(entity) )
            {
                final Color color = mColor.get(entity);
                font.setColor(color.r, color.g, color.b, color.a);
            } else {
                font.setColor(0f,0f,0f,1f);
            }

            switch ( label.align ) {
                case LEFT:
                    font.draw(batch, label.text, pos.x, pos.y);
                    break;
                case CENTER:
                    font.draw(batch, label.text, (int)(pos.x - font.getBounds(label.text).width / 2), (int)(pos.y + font.getBounds(label.text).height / 2));
                    break;
                case RIGHT:
                    font.draw(batch, label.text, pos.x - font.getBounds(label.text).width, pos.y);
                    break;
            }
        }
    }
}
