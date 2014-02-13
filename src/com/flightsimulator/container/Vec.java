package com.flightsimulator.container;

public class Vec {
	public static class Vec2<Type> {
		public Type x;
		public Type y;
		
		public Vec2() {
			x = null;
			y = null;
		}
		
		public Vec2(Vec2<Type> vec) {
			x = vec.x;
			y = vec.y;
		}
		
		public Vec2(Type x, Type y) {
			this.x = x;
			this.y = y;
		}
	}
	
	public static class Vec3<Type> {
		public Type x;
		public Type y;
		public Type z;
		
		public Vec3() {
			x = null;
			y = null;
			z = null;
		}
		
		public Vec3(Vec3<Type> vec) {
			x = vec.x;
			y = vec.y;
			z = vec.z;
		}
		
		public Vec3(Type x, Type y, Type z) {
			this.x = x;
			this.y = y;
			this.z = z;
		}
	}
}
