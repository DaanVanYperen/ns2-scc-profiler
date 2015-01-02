package net.mostlyoriginal.game.system.render.layer;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Wire;
import com.artemis.utils.ImmutableBag;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Interpolation;
import net.mostlyoriginal.api.component.basic.Pos;
import net.mostlyoriginal.game.G;
import net.mostlyoriginal.game.api.DelayedEntitySystem;
import net.mostlyoriginal.game.component.Layer;
import net.mostlyoriginal.game.component.Routable;
import net.mostlyoriginal.game.component.Team;
import net.mostlyoriginal.game.component.TeamMember;
import net.mostlyoriginal.game.component.buildings.Techpoint;
import net.mostlyoriginal.game.component.ui.RenderMask;
import net.mostlyoriginal.game.manager.LayerManager;
import net.mostlyoriginal.game.system.logic.RenderMaskHandlerSystem;
import net.mostlyoriginal.game.system.logic.analysis.PreferredRouteCalculationSystem;

import java.util.LinkedList;

/**
 * Estimate what techpoints will be able to pressure resource towers
 * most effectively.
 * <p/>
 * Since teams have different speeds it matters who owns techpoints
 * to determine who has the greatest claim to a techpoint.
 *
 * @author Daan van Yperen
 */
@Wire(injectInherited = true)
public class DomainSystem extends DelayedEntitySystem {

	public static final int ORTHO_MOVEMENT = 10;
	public static final int DIAGO_MOVEMENT = 14;
	public static final int MAX_SECONDS_RADIUS = 27;
	protected LayerManager layerManager;
	protected RenderMaskHandlerSystem renderMaskHandlerSystem;

	protected ComponentMapper<Routable> mRoutable;
	protected ComponentMapper<TeamMember> mTeamMember;

	private PreferredRouteCalculationSystem preferredRouteCalculationSystem;

	public DomainSystem() {
		super(Aspect.getAspectForAll(Routable.class, Pos.class, Techpoint.class));
	}

	@Override
	protected void initialize() {
		setPrerequisiteSystems(preferredRouteCalculationSystem);
	}

	@Override
	protected void collectJobs(ImmutableBag<Entity> entities, LinkedList<Runnable> jobs) {
		Layer layer = getDomainsLayer();
		layerManager.clearWithMap(layer, Color.WHITE, 0.3f);

		for (Team team : Team.values()) {
			floodFill(layerManager.getTeamNavLayer(team), layer, entities, team);
		}
	}

	private Layer getDomainsLayer() {
		return layerManager.getLayer("DOMAINS", RenderMask.Mask.TEAM_DOMAINS);
	}

	@Override
	protected void postJobs() {
		getDomainsLayer().invalidateTexture();
	}

	public class Node {
		public final int x;
		public final int y;

		public int totalCost;

		public Node parent;

		private Node(int x, int y, int totalCost) {
			this.x = x;
			this.y = y;
			this.totalCost = totalCost;
		}

		public int totalDistance() {
			return totalCost;
		}
	}

	LinkedList<Node> open = new LinkedList<Node>();

	int xOff[] = {-1, 0, 1, 1, 1, 0, -1, -1};
	int yOff[] = {-1, -1, -1, 0, 1, 1, 1, 0};


	public Node floodFill(Layer layerRaw, Layer layerOut, ImmutableBag<Entity> entities, Team team) {
		open.clear();
		boolean[] closed = new boolean[layerOut.pixmap.getHeight() * layerOut.pixmap.getWidth()];

		for (int i = 0, s = entities.size(); i < s; i++) {
			final Entity e = entities.get(i);
			final Routable routable = mRoutable.get(e);
			if (mTeamMember.has(e)) {
				if (mTeamMember.get(e).team == team) {
					createUpdateNode(routable.getX(), routable.getY(), null, false);
				}
			}
		}

		// calculate max route length, in pixels * 10.
		float maxRouteLength = calculateDistance(team, MAX_SECONDS_RADIUS);

		Color colorNear = new Color(team.getPathColor());
		Color colorFar = new Color(team.getPathColor());
		colorFar.a = 0f;
		colorFar.g = 1f;

		Color vTmp = new Color();

		while (!open.isEmpty()) {
			final Node node = open.pollFirst();
			closed[node.x + node.y * layerOut.pixmap.getWidth()] = true;

			for (int i = 0; i < 8; i++) {
				final int childX = node.x + xOff[i];
				final int childY = node.y + yOff[i];

				// bound check.
				if (childX < 0 || childY < 0 || childX >= layerOut.pixmap.getWidth() || childY >= layerOut.pixmap.getHeight())
					continue;

				int rawColor = layerRaw.pixmap.getPixel(childX, layerOut.pixmap.getHeight() - childY);
				boolean isWalkable = ((rawColor & 0x000000ff)) / 255f >= 0.5f;

				// node not closed yet and location walkable? ONWARD!
				if (!closed[childX + childY * layerOut.pixmap.getWidth()] && isWalkable) {

					final boolean diagonal = xOff[i] != 0 && yOff[i] != 0;

					final Node childNode = createUpdateNode(childX, childY, node, diagonal);

					// apply maximum distance for searches to keep things snappy.
					if (childNode.totalCost > maxRouteLength) {
						open.remove(childNode);
						closed[childNode.x + childNode.y * layerOut.pixmap.getWidth()] = true;
					} else {
						// create crossbar style.
						if ((childX + (team == Team.MARINE ? childY : childY * 7)) % 8 < 4) {

							float tween = childNode.totalCost / maxRouteLength;

							vTmp.r = Interpolation.pow2Out.apply(colorNear.r, colorFar.r, tween);
							vTmp.g = Interpolation.pow2Out.apply(colorNear.g, colorFar.g, tween);
							vTmp.b = Interpolation.pow2Out.apply(colorNear.b, colorFar.b, tween);
							vTmp.a = Interpolation.pow2Out.apply(colorNear.a, colorFar.a, tween);

							layerOut.pixmap.setColor(vTmp);
							layerOut.pixmap.drawPixel(childX, layerOut.pixmap.getHeight() - childY);
						}
					}
				}
			}
		}


		return null;
	}

	private float calculateDistance(Team team, int seconds) {
		return ((Math.round(team.getAvgSpeed() * seconds)) / G.UNITS_PER_PIXEL) * ORTHO_MOVEMENT;
	}

	private Node createUpdateNode(int x, int y, Node parent, boolean diagonal) {

		final int updatedCost = parent != null ? parent.totalCost + (diagonal ? DIAGO_MOVEMENT : ORTHO_MOVEMENT) : 0;

		for (Node node : open) {
			if (node.x == x && node.y == y) {
				if (updatedCost < node.totalCost) {
					node.totalCost = updatedCost;
					node.parent = parent;
				}
				return node;
			}
		}

		final Node node = new Node(x, y, updatedCost);
		node.parent = parent;

		open.add(node);

		return node;
	}
}
