package de.tobiyas.enderdragonsplus.util;

public class MinecraftChatColorUtils {

	/**
	 * Changes the coloring of the passed string to the one minecraft understands.
	 * 
	 * @param string
	 * @return
	 */
	public static String decodeColors(String string){
		return string.replaceAll("(&([a-f0-9]))", "§$2");
	}
}
