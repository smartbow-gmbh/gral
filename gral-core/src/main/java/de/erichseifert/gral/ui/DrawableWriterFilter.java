/*
 * GRAL: GRAphing Library for Java(R)
 *
 * (C) Copyright 2009-2010 Erich Seifert <dev[at]richseifert.de>, Michael Seifert <michael.seifert[at]gmx.net>
 *
 * This file is part of GRAL.
 *
 * GRAL is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * GRAL is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with GRAL.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.erichseifert.gral.ui;

import java.io.File;

import javax.swing.filechooser.FileFilter;

import de.erichseifert.gral.io.IOCapabilities;

/**
 * File filter that extracts files that can be read with a certain set of {@link IOCapabilities}.
 */
public class DrawableWriterFilter extends FileFilter {
	private final IOCapabilities capabilities;

	/**
	 * Creates a new instance and initializes it with an {@link IOCapabilities} object.
	 * @param capabilities writer capabilities.
	 */
	public DrawableWriterFilter(IOCapabilities capabilities) {
		this.capabilities = capabilities;
	}

	@Override
	public boolean accept(File f) {
		if (f == null) {
			return false;
		}
		if (f.isDirectory()) {
			return true;
		}
		String ext = getExtension(f).toLowerCase();
		for (String extension : capabilities.getExtensions()) {
			if (extension.equals(ext)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public String getDescription() {
		return String.format("%s: %s", capabilities.getFormat(), capabilities.getName());
	}

	/**
	 * Returns the capabilities filtered by this instance.
	 * @return writer capabilities.
	 */
	public IOCapabilities getWriterCapabilities() {
		return capabilities;
	}

	private final static String getExtension(File f) {
		String name = f.getName();
		int lastDot = name.lastIndexOf('.');
		if ((lastDot <= 0) || (lastDot == name.length() - 1)) {
			return "";
		}
		return name.substring(lastDot + 1);
	}
}
