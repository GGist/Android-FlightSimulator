package com.andrewmiller.flightsimulator.utility;

public class BasicGeometry {
	public static class Point {
		public final float x, y, z;
		
		public Point(float x, float y, float z) {
			this.x = x;
			this.y = y;
			this.z = z;
		}

	}
	
	public static class Circle {
		public final Point center;
		public final float radius;
		
		public Circle(Point center, float radius) {
			this.center = center;
			this.radius = radius;
		}
	}
	
	public static class Cylinder {
		public final Circle base;
		public final float height;
		
		public Cylinder(Circle base, float height) {
			this.base = base;
			this.height = height;
		}
	}
	
	private static class Vector {
		
	}
	
	private static class Ray {
		
	}
	
}