package org.lewis.fs;


public class ImagesUtils {


	public static String getImageUrl(String name, long width, long height) {
		return (new StringBuilder().append("/fs/images/thumbnail-")
				.append(width).append("-").append(height).append("/")).append(
				name).toString();
	}

	public static String getImageUrl(String name) {
		return (new StringBuilder().append("/fs/images/").append(name))
				.toString();
	}

}
