/*
 * GRAL: GRAphing Library for Java(R)
 *
 * (C) Copyright 2009-2010 Erich Seifert <info[at]erichseifert.de>, Michael Seifert <michael.seifert[at]gmx.net>
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

package de.erichseifert.gral.io.data;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import de.erichseifert.gral.data.DataSource;
import de.erichseifert.gral.data.DataTable;
import de.erichseifert.gral.io.IOCapabilities;


/**
 * Class that reads a DataSource from a CSV-file. By default the semicolon
 * character will be used for separating columns.
 * @see <a href="http://tools.ietf.org/html/rfc4180">RFC 4180</a>
 */
public class CSVReader extends AbstractDataReader {
	static {
		addCapabilities(new IOCapabilities(
			"CSV",
			"Comma separated values",
			"text/csv",
			"csv", "txt"
		));

		addCapabilities(new IOCapabilities(
			"TSV",
			"Tab separated values",
			"text/tab-separated-values",
			"tsv", "txt"
		));
	}

	/**
	 * Creates a new CSVReader with the specified MIME type.
	 * @param mimeType MIME type of the file format to be read.
	 */
	public CSVReader(String mimeType) {
		super(mimeType);
		setDefault("separator", ";");
	}

	@Override
	public DataSource read(InputStream input, Class<? extends Number>... types)
			throws IOException {
		Map<Class<? extends Number>, Method> parseMethods =
			new HashMap<Class<? extends Number>, Method>();
		for (Class<? extends Number> type : types) {
			if (parseMethods.containsKey(type)) {
				continue;
			}
			Method parseMethod = getParseMethod(type);
			if (parseMethod != null) {
				parseMethods.put(type, parseMethod);
			}
		}

		String separatorPattern = getSetting("separator");
		DataTable data = new DataTable(types);
		BufferedReader reader = new BufferedReader(new InputStreamReader(input));
		String line = null;
		for (int lineNo = 0; (line = reader.readLine()) != null; lineNo++) {
			String[] cols = line.split(separatorPattern);
			if (cols.length < types.length) {
				throw new IllegalArgumentException(
						"Column count in file doesn't match; got " + cols.length +
						", but expected " + types.length + ".");
			}
			Number[] row = new Number[types.length];
			for (int i = 0; i < types.length; i++) {
				Method parseMethod = parseMethods.get(types[i]);
				try {
					row[i] = (Number)parseMethod.invoke(null, cols[i]);
				} catch (IllegalArgumentException e) {
				} catch (IllegalAccessException e) {
					throw new RuntimeException(
							"Couldn't access method for parsing data type " +
							types[i].getSimpleName() + " in column " + i);
				} catch (InvocationTargetException e) {
					throw new IOException(
							"Type mismatch in column " + i + ": got \"" +
							cols[i] + "\", but expected " +
							types[i].getSimpleName() + " value.");
				}
			}
			data.add(row);
		}
		return data;
	}

	/**
	 * Returns a Method that returns a parsed value in the specified type.
	 * @param c Desired type.
	 * @return Method that parses a data type.
	 */
	private static Method getParseMethod(Class<?> c) {
		Method parse = null;
		for (Method m : c.getMethods()) {
			boolean isStatic = m.toString().indexOf("static") >= 0;
			if (!isStatic) {
				continue;
			}
			Class<?>[] types = m.getParameterTypes();
			boolean hasStringParameter =
				(types.length == 1) && String.class.equals(types[0]);
			if (!hasStringParameter) {
				continue;
			}
			boolean parseName = m.getName().startsWith("parse");
			if (!parseName) {
				continue;
			}
			parse = m;
		}
		return parse;
	}

}
