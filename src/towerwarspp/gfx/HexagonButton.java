package towerwarspp.gfx;

import java.awt.Color;
import java.awt.RenderingHints;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Graphics;

import javax.swing.JButton;
import towerwarspp.preset.*;
import towerwarspp.board.Token;
import towerwarspp.board.Tower;

/** 
 *	Klasse zur Darstellung der Spielfelder als JButtons
 *	Erbt von JButton
 *
 *	@author Julian L&uuml;ken
 */
public class HexagonButton extends JButton {
	/** Instanzvariable, die das Hexagon speichert */
	private Hexagon hexagon;
	/** Instanzvariable, die den Spielstein speichert */
	private Token token;
	/** Instanzvariable, die das GUI speichert, auf dem der Button sich befindet */
	private TowerWarsGUI gui;
	/** Instanzvariable, die speichert, ob der JButton ausgew&auml;hlt ist oder nicht */
	private boolean isSelected;
	/** Instanzvariable, die speichert, ob der JButton mit der zweiten Markierung ausgew&auml;hlt ist */
	private boolean isSelected2;
	
	/** Farbe der Umrandung der Spielfelder */
	protected static Color BORDER 		= new Color(0x000000);
	/** Farbe von PlayerColor.RED */
	protected static Color RED 			= new Color(0xff0000);
	/** Farbe von PlayerColor.BLUE */
	protected static Color BLUE 		= new Color(0x0000ff);
	/** Farbe eines leeren Felds */
	protected static Color EMPTY 		= new Color(0xffffff);
	/** Farbe eines ausgew&auml;hlten Felds und der Felder in der Umgebung des ausgew&auml;hlten Felds */
	protected static Color SELECTED 	= new Color(0x00ff00);
	/** Farbe des Rands des ausgew&auml;hlten Felds */
	protected static Color SELECTED2	= new Color(0xffff00);

	
	/** Default-Konstruktor */	
	public HexagonButton() {
		super();
		this.hexagon = null;
		this.token = null;
		this.isSelected = false;
		this.isSelected2 = false;
	}
	
	/** 
	 *	Konstruktor, der ein Hexagon und den dazugeh&ouml;rigen Spielstein entegegennimmt. 
	 *	@param hexagon Hexagon
	 *	@param token Spielstein
	 *	@param gui TowerWarsGUI, auf dem der Spielstein sich befindet
	 */
	protected HexagonButton(Hexagon hexagon, Token token, TowerWarsGUI gui) {
		super();
		this.hexagon = hexagon;
		this.token = token;
		this.gui = gui;
		this.isSelected = false;
		this.isSelected2 = false;
	}

	/** Methode, die den Spielstein f&uuml;r gegebenen Button ausgibt
	 *	@return Spielstein
	 */
	protected Token getToken() {return token;}
	
	/** Methode, die das GUI f&uuml;r gegebenen Button ausgibt
	 *	@return graphical user interface
	 */
	protected TowerWarsGUI getGUI() {return gui;}

	/** Methode, die das zugeh&ouml;rige Hexagon-Objekt ausgibt
	 *	@return Sechseck
	 */
	protected Hexagon getHexagon() {return hexagon;}

	/** Markiert ein Spielfeld */
	protected void select() {isSelected = true; repaint();}
	
	/** Hebt Markierung des Spielfelds auf */
	protected void deSelect() {isSelected = false; repaint();}

	/** Markiert den Rand eines Spielfelds */
	protected void select2() {isSelected2 = true; repaint();}

	/** Hebt Markierung des Spielfeldrands auf */
	protected void deSelect2() {isSelected2 = false; repaint();}

	
	/** Zeichnet das Hexagon mit dem Spielstein darauf.
	 *	Diese Methode &uuml;berschreibt paintComponent von JButton.
	 *	@param g Graphics-Objekt
	 */
	@Override
	protected void paintComponent(Graphics g) {
		// Kantenglaettung
		Graphics2D g2d = (Graphics2D) g;
		g2d.addRenderingHints(new RenderingHints( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON));
		g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
	
		// zunaechst das Spielfeld selbst zeichnen lassen
		// gucken, ob das Spielfeld ausgewaehlt ist
		if (isSelected) {
			g.setColor(SELECTED);
		}
		else if (isSelected2) {
			g.setColor(SELECTED2);
		}
		else {
			g.setColor(EMPTY);
		}
		g.fillPolygon(hexagon);
	
		boolean blockedIsRed = false;
		if (token != null) {
			// dann die Farbe des Spielsteins waehlen
			switch (token.getColor()) {
				case RED:
					g.setColor(RED);
					blockedIsRed = true;
					break;
				case BLUE:
					g.setColor(BLUE);
					blockedIsRed = false;
					break;
			}
			// dann die Form des Spielsteins waehlen und den Stein zeichnen lassen
			switch (token.getType()) {
				case PAWN:
					g.fillPolygon(new Hexagon(hexagon.getRadius(), hexagon.getRadius(), hexagon.getRadius()*1/3));
					break;
				case BASE:
					g.fillPolygon(new Hexagon(hexagon.getRadius(), hexagon.getRadius(), hexagon.getRadius()*4/5));
					break;
				case TOWER:
					Tower tower = (Tower) token;
					g.fillPolygon(new Hexagon(hexagon.getRadius(), hexagon.getRadius(), hexagon.getRadius()*1/2));
					if (tower.isBlocked()) {
						if (blockedIsRed) {
							g.setColor(BLUE);
						}
						else {
							g.setColor(RED);
						}
						g.fillPolygon(new Hexagon(hexagon.getRadius(), hexagon.getRadius(), hexagon.getRadius()*1/4));
					}
					g.setColor(EMPTY);
					g.drawString(tower.getHeight()+"",hexagon.getRadius()-5, hexagon.getRadius()+5);
					break;
			}
		}
	}
	
	/** Methode, die die Gr&ouml;&szlig;e des HexagonButtons liefert. Diese ist notwendig f&uuml;r das darstellen als HexagonButton, da der HexagonButton sonst zu
	 *	klein oder zu gro&szlig; ist und somit die grafische Ausgabe des Hexagons und der HexagonButton inkompatible Gr&ouml;&szlig;en haben
	 *	@return Gr&ouml;&szlig;e
	 */
	@Override
	public Dimension getPreferredSize() {
		return new Dimension(hexagon.getRadius()*2, hexagon.getRadius()*2);
	}
	
	/** Methode, die den Rand des HexagonButtons in der Farbe BORDER zeichnet
	 *	@param g Graphics-Objekt
	 */
	@Override
	protected void paintBorder(Graphics g) {
		g.setColor(BORDER);
		if (token != null) {
			switch (token.getType()) {
				case PAWN:
					g.drawPolygon(new Hexagon(hexagon.getRadius(), hexagon.getRadius(), hexagon.getRadius()*1/3));
					break;
				case BASE:
					g.drawPolygon(new Hexagon(hexagon.getRadius(), hexagon.getRadius(), hexagon.getRadius()*4/5));
					break;
				case TOWER:
					Tower tower = (Tower) token;
					g.drawPolygon(new Hexagon(hexagon.getRadius(), hexagon.getRadius(), hexagon.getRadius()*1/2));
					if (tower.isBlocked()) {
						g.drawPolygon(new Hexagon(hexagon.getRadius(), hexagon.getRadius(), hexagon.getRadius()*1/4));
					}
					break;
			}
		}
		g.drawPolygon(hexagon);
	}
	
	Shape shape;
	/** Methode, die die Form von JButton &uuml;berschreibt. Diese Methode ist unumg&auml;nglich, da ein JButton standardm&auml;&szlig;ig nur vier Ecken hat
	 *	@param x x-Koordinate
	 *	@param y y-Koordinate
	 *	@return true, falls (x,y) im HexagonButton liegt, sonst false
	 */
	@Override
	public boolean contains(int x, int y) {
		if (shape == null || !shape.getBounds().equals(getBounds())) {
			shape = hexagon;
		}
		return shape.contains(x,y);
	}
}
