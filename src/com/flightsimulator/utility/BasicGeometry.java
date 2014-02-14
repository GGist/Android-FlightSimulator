package com.flightsimulator.utility;


public class BasicGeometry {
	public static final String TAG = "BasicGeometry";
	
	public static class Point {
		public final float x, y, z;
		
		public Point(float x, float y, float z) {
			this.x = x;
			this.y = y;
			this.z = z;
		}
		
		public float distance(Point point) {
			return (float) Math.sqrt(Math.pow((x - point.x), 2) + 
					Math.pow((y - point.y), 2) + Math.pow((z - point.z), 2));
		}
	}
	
	public static class Rectangle {
		public final Point center;
		public final float height, width;
		
		public Rectangle(Point center, float height, float width) {
			this.center = center;
			this.height = height;
			this.width = width;
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
		public final float x, y, z;
		
		public Vector(float x, float y, float z) {
			this.x = x;
			this.y = y;
			this.z = z;
		}
		
		public float getLength() {
			return (float) Math.sqrt((x * x) + (y * y) + (z * z));
		}
	}
	
	private static class Ray {
		public final Point p;
		public final Vector v;
		
		public Ray(Point p, Vector v) {
			this.p = p;
			this.v = v;
		}
	}
	
}