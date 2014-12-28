package net.mostlyoriginal.api;

import org.xguzm.pathfinding.grid.GridCell;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Daan van Yperen
 */
public class ListUtils {

	/** flip list around */
	public static <T> List<T> flip(List<T> source) {
		final LinkedList<T> list = new LinkedList<>();
		for (T t : source) {
			list.add(t);
		}
		return list;
	}
}
