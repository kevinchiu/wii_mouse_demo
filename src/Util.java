import java.awt.Point;

import wiiremotej.IRLight;
import wiiremotej.event.WRIREvent;


public class Util {
	public static IRLight toIRLight(Point p) {
		return new IRLight(p.x, p.y);
	}

	public static void print(Object o) {
		System.out.println(o);
	}
}
