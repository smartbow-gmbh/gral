/*
 * GRAL: GRAphing Library for Java(R)
 *
 * (C) Copyright 2009-2012 Erich Seifert <dev[at]erichseifert.de>,
 * Michael Seifert <michael[at]erichseifert.de>
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
package de.erichseifert.gral.plots.lines;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import de.erichseifert.gral.TestUtils;
import de.erichseifert.gral.graphics.Drawable;
import de.erichseifert.gral.graphics.DrawableContainer;
import de.erichseifert.gral.plots.DataPoint;
import de.erichseifert.gral.plots.axes.Axis;
import de.erichseifert.gral.plots.axes.AxisRenderer;
import de.erichseifert.gral.plots.axes.LinearRenderer2D;
import de.erichseifert.gral.plots.points.PointData;
import de.erichseifert.gral.util.PointND;

public class AbstractLineRendererTest {
	private PointData data;

	private static class MockLineRenderer extends AbstractLineRenderer2D {
		/** Version id for serialization. */
		private static final long serialVersionUID = 7510746091876293498L;

		private final Shape shape;

		public MockLineRenderer() {
			this.shape = new Rectangle2D.Double();
		}

		public Shape getLineShape(List<DataPoint> points) {
			return shape;
		}

		public Drawable getLine(List<DataPoint> points, Shape shape) {
			return new DrawableContainer();
		}
	}

	@Before
	public void setUp() {
		Axis axisX = new Axis(-5.0, 5.0);
		Axis axisY = new Axis(-5.0, 5.0);
		AxisRenderer axisRendererX = new LinearRenderer2D();
		AxisRenderer axisRendererY = new LinearRenderer2D();
		data = new PointData(
			Arrays.asList(axisX, axisY),
			Arrays.asList(axisRendererX, axisRendererY),
			null, 0);
	}

	@Test
	public void testCreate() {
		LineRenderer r = new MockLineRenderer();
		assertTrue(r.getSetting(LineRenderer.STROKE) instanceof BasicStroke);
		assertEquals(0.0, r.getSetting(LineRenderer.GAP));
		assertEquals(false, r.getSetting(LineRenderer.GAP_ROUNDED));
		assertEquals(Color.BLACK, r.getSetting(LineRenderer.COLOR));
	}

	@Test
	public void testLine() {
		// Get line
		LineRenderer r = new MockLineRenderer();
		List<DataPoint> points = Arrays.asList(
			new DataPoint(data, new PointND<Double>(0.0, 0.0), null, null),
			new DataPoint(data, new PointND<Double>(1.0, 1.0), null, null)
		);
		Shape shape = r.getLineShape(points);
		Drawable line = r.getLine(points, shape);
		assertNotNull(line);
	}

	@Test
	public void testSettings() {
		// Get
		LineRenderer r = new MockLineRenderer();
		assertEquals(Color.BLACK, r.getSetting(LineRenderer.COLOR));
		// Set
		r.setSetting(LineRenderer.COLOR, Color.RED);
		assertEquals(Color.RED, r.getSetting(LineRenderer.COLOR));
		// Remove
		r.removeSetting(LineRenderer.COLOR);
		assertEquals(Color.BLACK, r.getSetting(LineRenderer.COLOR));
	}

	@Test
	public void testPunch() {
		MockLineRenderer r = new MockLineRenderer();

		Shape line = new Line2D.Double(-1.0, -1.0, 2.0, 2.0);
		List<DataPoint> points = Arrays.asList(
			new DataPoint(data, new PointND<Double>(0.0, 0.0), null,
				new Ellipse2D.Double(-0.25, -0.25, 0.50, 0.50)),
			new DataPoint(data, new PointND<Double>(1.0, 1.0), null,
				new Ellipse2D.Double(-0.25, -0.25, 0.50, 0.50))
		);

		Shape punched = r.punch(line, points);
		assertNotSame(line, punched);
	}

	@Test
	public void testPunchNullLine() {
		MockLineRenderer r = new MockLineRenderer();

		List<DataPoint> points = Arrays.asList(
			new DataPoint(data, new PointND<Double>(0.0, 0.0), null,
				new Ellipse2D.Double(-0.25, -0.25, 0.50, 0.50)),
			new DataPoint(data, new PointND<Double>(1.0, 1.0), null,
				new Ellipse2D.Double(-0.25, -0.25, 0.50, 0.50))
		);

		Shape punched = r.punch(null, points);
		assertNull(punched);
	}

	@Test
	public void testSerialization() throws IOException, ClassNotFoundException {
		LineRenderer original = new MockLineRenderer();
		LineRenderer deserialized = TestUtils.serializeAndDeserialize(original);

		TestUtils.assertSettings(original, deserialized);
    }
}