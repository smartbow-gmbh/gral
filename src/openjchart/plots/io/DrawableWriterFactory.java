/* OpenJChart : a free plotting library for the Java(tm) platform
 *
 * (C) Copyright 2009, by Erich Seifert and Michael Seifert.
 *
 * This file is part of OpenJChart.
 *
 * OpenJChart is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OpenJChart is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OpenJChart.  If not, see <http://www.gnu.org/licenses/>.
 */

package openjchart.plots.io;

import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * Class that provides <code>DrawableWriter</code> implementations for
 * different file formats.
 * @see DrawableWriter
 */
public class DrawableWriterFactory {
	private static DrawableWriterFactory instance;
	private final Map<String, Class<? extends DrawableWriter>> writers = new HashMap<String, Class<? extends DrawableWriter>>();

	private DrawableWriterFactory() {
		// Retrieve property-files
		Enumeration<URL> propFiles;
		try {
			propFiles = ClassLoader.getSystemResources("drawablewriters.properties");
			Properties props = new Properties();
			while (propFiles.hasMoreElements()) {
				URL propURL = propFiles.nextElement();
				props.load(new FileReader(propURL.getFile()));
				// Parse property files and register entries as writers
				for (Map.Entry<Object, Object> prop : props.entrySet()) {
					String mimeType = (String) prop.getKey();
					String className = (String) prop.getValue();
					Class<?> clazz = Class.forName(className);
					if (DrawableWriter.class.isAssignableFrom(clazz)) {
						writers.put(mimeType, (Class<? extends DrawableWriter>) clazz);
					}
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public WriterCapabilities getCapabilities(String mimeType) {
		Class<? extends DrawableWriter> clazz = writers.get(mimeType);
		try {
			Method capabilitiesGetter = clazz.getMethod("getCapabilities");
			Set<WriterCapabilities> capabilities = (Set<WriterCapabilities>) capabilitiesGetter.invoke(clazz);
			for (WriterCapabilities c : capabilities) {
				if (c.getMimeType().equals(mimeType)) {
					return c;
				}
			}
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Returns an array of Strings containing all supported output formats.
	 * @return Supported formats.
	 */
	public String[] getSupportedFormats() {
		String[] formats = (String[]) writers.keySet().toArray();
		return formats;
	}

	/**
	 * Returns true if the specified MIME-Type is supported.
	 * @param mimeType MIME-Type.
	 * @return True if supported.
	 */
	public boolean isFormatSupported(String mimeType) {
		return writers.containsKey(mimeType);
	}

	/**
	 * Returns an instance of this DrawableWriterFactory.
	 * @return Instance.
	 */
	public static DrawableWriterFactory getInstance() {
		if (instance == null) {
			instance = new DrawableWriterFactory();
		}
		return instance;
	}

	/**
	 * Returns a DrawableWriter for the specified format.
	 * @param format Output format.
	 * @return DrawableWriter.
	 */
	public DrawableWriter getDrawableWriter(OutputStream destination, String mimeType) {
		DrawableWriter writer = null;
		Class<? extends DrawableWriter> clazz = writers.get(mimeType);
		WriterCapabilities capabilities = getCapabilities(mimeType);
		try {
			Constructor<? extends DrawableWriter> constructor = clazz.getDeclaredConstructor(OutputStream.class, String.class);
			writer = constructor.newInstance(destination, mimeType);
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (writer == null) {
			throw new IllegalArgumentException("Unsupported MIME-Type: "+mimeType);
		}

		return writer;
	}
}