package lv.semti.morphology.utils;

public abstract class Utils
{
	/**
	 * The same as String.substring(), except that when end is negative,
	 * instead str.length() + end is used. For null returns null. Other than
	 * that will throw the usual IndexOutOfBounds exceptions.
	 */
	public static String substr(String str, int start, int end) {
		if (str == null) return null;
		if (end < 0) end = str.length() + end;
		return str.substring(start, end);
	}
}
