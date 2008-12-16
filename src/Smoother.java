import java.awt.Point;
import java.util.LinkedList;


public class Smoother {
	private static final int N = 11;
	private LinkedList<Point> points = new LinkedList<Point>();
	private LinkedList<Double> rotations = new LinkedList<Double>();
	
	public void reset() {
		points.clear();
	}
	
	public void addPoint(Point p){
		points.addLast(p);
		if (points.size() > N) points.removeFirst();
	}
	
	public void addRotation(Double d){
		rotations.addLast(d);
		if (rotations.size() > N) rotations.removeFirst();
	}

	public Point getAverage() {
		int x = 0, y = 0;
		for (Point o : points) {
			x += o.x;
			y += o.y;
		}
		return new Point(Math.round((float)x / (float)points.size()), Math.round((float)y / (float)points.size()));
	}
	
	public Double getRotationAverage() {
		double total = 0;
		for (Double d : rotations) {
			total += d;
		}
		return total/rotations.size();
	}
}
