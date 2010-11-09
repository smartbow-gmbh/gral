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

package de.erichseifert.gral.data;

import de.erichseifert.gral.data.statistics.Orientation;



/**
 * Class for storing a row of a data source.
 */
public class Row extends DataAccessor {
	/**
	 * Initializes a new instances with the specified data source and row index.
	 * @param source Data source.
	 * @param row Row index.
	 */
	public Row(DataSource source, int row) {
		super(source, row);
	}

	@Override
	public Number get(int col) {
		if (getSource() == null) {
			return null;
		}
		return getSource().get(col, getIndex());
	}

	@Override
	public int size() {
		return getSource().getColumnCount();
	}

	@Override
	public double getStatistics(String key) {
		return getSource().getStatistics().get(key, Orientation.HORIZONTAL, getIndex());
	}
}
