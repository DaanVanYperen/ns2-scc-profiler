package net.mostlyoriginal.api;

/**
 * @author Daan van Yperen
 */

import java.util.LinkedList;

/**
 * @author Daan van Yperen
 */
public class AStar {

	public class Node {
		public final int x;
		public final int y;

		public int totalCost;
		public final int estimatedDestinationCost;

		public Node parent;

		private Node(int x, int y, int totalCost, int estimatedDestinationCost) {
			this.x = x;
			this.y = y;
			this.totalCost = totalCost;
			this.estimatedDestinationCost = estimatedDestinationCost;
		}

		public int totalDistance()
		{
			return totalCost + estimatedDestinationCost;
		}
	}

	LinkedList<Node> open = new LinkedList<Node>();
	LinkedList<Node> closed = new LinkedList<Node>();

	int xOff[] = { -1, 0, 1, 1, 1, 0, -1, -1};
	int yOff[] = { -1,-1,-1, 0, 1, 1,  1,  0};


	public Node findRoute( AStarMap map, int startTX,int startTY, int destTX, int destTY, int maxRouteLength )
	{
		open.clear();
		closed.clear();

		createUpdateNode(startTX, startTY, destTX, destTY, null, false);
		while ( !open.isEmpty() )
		{
			final Node node = open.pollFirst();
			closed.addFirst(node);

			for ( int i=0; i<8; i++)
			{
				final int childX = node.x + xOff[i];
				final int childY = node.y + yOff[i];

				// we don't care if destination or origin are passable.
				if ( !isClosedNode(childX, childY) && (!map.isBlocked(childX, childY) || ( childX == destTX && childY == destTY ) )  )
				{
					final boolean diagonal = xOff[i] != 0 && yOff[i] != 0;

					final Node childNode = createUpdateNode(childX, childY, destTX, destTY, node, diagonal);

					// apply maximum distance for searches to keep things snappy.
					if ( childNode.totalCost > maxRouteLength )
					{
						open.remove(childNode);
						closed.addFirst(childNode);
						continue;
					}

					if ( childX == destTX && childY == destTY )
					{
						return childNode;
					}
				}
			}
		}


		return null;
	}

	private boolean isClosedNode(int x, int y) {
		for ( Node node : closed )
		{
			if ( node.x==x && node.y==y ) return true;
		}

		return false;
	}

	private Node createUpdateNode(int x, int y, int destX, int destY, Node parent, boolean diagonal) {

		final int updatedCost = parent != null ? parent.totalCost + (diagonal ? 14 : 10) : 0;

		for ( Node node : open )
		{
			if ( node.x==x && node.y==y )
			{
				if ( updatedCost < node.totalCost )
				{
					node.totalCost = updatedCost;
					node.parent = parent;
				}
				return node;
			}
		}

		final Node node = new Node(x, y, updatedCost, estimatedDistanceCost(x, y, destX, destY));
		node.parent = parent;

		open.add(node);

		return node;
	}

	private int estimatedDistanceCost(int x1, int y1, int x2, int y2) {
		return (x1-x2) * (x1-x2)  + (y1-y2) * (y1-y2);
	}
}
