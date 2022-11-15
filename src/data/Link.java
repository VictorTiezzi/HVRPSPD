package data;

public class Link {

	public Node s;
	public Node t;
	public double distance;

	public Link(Node sNode, Node tNode, boolean truncate) {
		this.s = sNode;
		this.t = tNode;
		if (truncate) {
			this.distance = distance1();
		} else {
			this.distance = distance2();
		}
	}
	
	private double distance1() {
		return (double) Math.round(100d * Math.sqrt(Math.pow(s.x - t.x, 2) + Math.pow(s.y - t.y, 2))) / 100d; 
	}

	private double distance2() {
		return Math.sqrt(Math.pow(s.x - t.x, 2) + Math.pow(s.y - t.y, 2)); 
	}

}