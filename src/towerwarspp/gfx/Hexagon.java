package towerwarspp.gfx;

import java.awt.Polygon;
import java.awt.Point;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.Graphics2D;

/** 
 *	Klasse zur Darstellung von Sechsecken.
 *	Erbt von Polygon
 *	
 *	@author Julian L&uuml;ken
 */
public class Hexagon extends Polygon {
	/** Anzahl der Seiten */
	public static final int SIDES = 6;
	
	/** Feld, das die Eckpunkte beinhaltet */
	private Point[] points = new Point[SIDES];
	/** Variable f&uuml;r den Mittelpunkt */
	private Point center = new Point(0,0);
	/** Variable f&uuml;r den Radius (H&auml;lfte des l&auml;ngsten Abstands im Sechseck) */
	private int radius;
	
	/** 
	 *	Konstruktor, der einen Mittelpunkt und den Radius entgegennimmt
	 *	@param center Mittelpunkt
	 *	@param radius Radius
	 */
	protected Hexagon(Point center, int radius) {
		// xpoints, npoints und ypoints sind aus Superklasse
		npoints = SIDES;
		xpoints = new int[SIDES];
		ypoints = new int[SIDES];

		this.center = center;
		this.radius = radius;

		updatePoints();
	}

	/**
	 *	Konstruktor, der nur einen Radius entgegennimmt. Der Mittelpunkt ist (radius,radius).
	 *	@param radius Radius
	 */
	protected Hexagon(int radius) {
		this(new Point(radius,radius), radius);
	}
	
	/** 
	 *	Konstruktor, der zwei ganze Zahlen und den Radius entgegennimmt. Die zwei Zahlen werden zum Mittelpunkt umgewandelt
	 *	@param x Mittelpunkt: x-Koordinate
	 *	@param y Mittelpunkt: y-Koordinate
	 *	@param radius Radius
	 */
	protected Hexagon(int x,int y, int radius) {
		this(new Point(x,y), radius);
	}
	/**
	 *	Getter-Methode f&uuml;r den Radius
	 *	@return Radius
	 */
	public int getRadius() {return radius;}
	/** 
	 *	Setter-Methode f&uuml;r den Radius
	 *	@param radius Radius
	 */
	protected void setRadius(int radius) {
		this.radius = radius;
		updatePoints();
	}
	/** 
	 *	Setter-Methode f&uuml;r den Mittelpunkt des Sechsecks
	 *	@param center neuer Mittelpunkt
	 */
	protected void setCenter(Point center) {
		this.center = center;
		updatePoints();
	}
	/**
	 *	Setter-Methode f&uuml;r den Mittelpunkt des Sechsecks
	 *	@param x x-Koordinate des neuen Mittelpunkts
	 *	@param y y-Koordinate des neuen Mittelpunkts
	 */
	protected void setCenter(int x, int y) {
		setCenter(new Point(x,y));
	}
	/** 
	 *	Methode, die den Winkel zwischen Mittelpunkt und zwei Ecken eines regelm&auml;&szlig;igen n-Ecks in Grad liefert
	 *	@param fraction Ecke k-1 geteilt durch die Anzahl der Seiten, 1 kleiner gleich k kleiner gleich n
	 *	@return Winkel zwischen erster und k-ter Ecke in Grad
	 */
	private double findAngle(double fraction) {
		return fraction * Math.PI * 2 + Math.toRadians(180);
	}
	/**
	 *	Methode, die bei gegebenem Winkel mit dem Mittelpunkt und dem Radius des Objekts den n&auml;chsten Punkt eines
	 *	regelm&auml;&szlig;igen n-Ecks berechnet und ausgibt.
	 *	@param angle Winkel in Grad
	 *	@return n&auml;chster Punkt im n-Eck
	 */
	private Point findPoint(double angle) {
		int x = (int) (center.y + Math.sin(angle) * radius);
		int y = (int) (center.x + Math.cos(angle) * radius);
		return new Point(x,y);
	}
	/**
	 *	Bei Aktualisierung der Variablen eines Hexagon-Objekts wird diese Methode ausgef&uuml;hrt,
	 *	um alle Punkte neu zu berechnen.
	 */
	protected void updatePoints() {
		for (int p = 0; p < SIDES; p++) {
			// alle Eckpunkte anhand geaenderter Parameter updatePoints
			double angle = findAngle((double) p / SIDES);
			Point point = findPoint(angle);
			xpoints[p] = point.x;
			ypoints[p] = point.y;
			points[p] = point;
		}
	}

	/** 
	 *	Methode, die das Sechseck-Objekt &uuml;ber Graphics2D darstellt
	 *	@param g 				Graphics2D, in das gezeichnet werden soll
	 *	@param x 				x-Position, ist hier aber irrelevant, da die Methode &uuml;berschrieben wird
	 *	@param y 				y-Position, ist hier aber irrelevant, da die Methode &uuml;berschrieben wird
	 *	@param lineThickness 	dicke der Randlinie in px
	 *	@param colorValue 		Farbe als int
	 *	@param filled 			gefuelltes Polygon oder nicht
	 */
	public void draw(Graphics2D g, int x, int y, int lineThickness, int colorValue, boolean filled) {
		// Kantenglaettung
		g.addRenderingHints( new RenderingHints( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON ));
		g.setRenderingHint( RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON );
		
		// Werte speichern
		Stroke tmpS = g.getStroke();
		Color tmpC = g.getColor();
		
		// "Stift" auswaehlen
		g.setColor(new Color(colorValue));
		g.setStroke(new BasicStroke(lineThickness, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER));

		// Hexagon malen
		if (filled) {
			g.fillPolygon(xpoints,ypoints,npoints);
		}
		else {
			g.drawPolygon(xpoints,ypoints,npoints);
		}
		// auf alte Werte zuruecksetzen
		g.setColor(tmpC);
		g.setStroke(tmpS);
	}
}