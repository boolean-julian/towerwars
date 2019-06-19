package towerwarspp.board;

import towerwarspp.preset.Position;
import towerwarspp.preset.PlayerColor;
/**
 * Diese Klasse ist eine Implementation einer Basis von Towerwars und erbt von Token
 * @author Marcel Hellwig und Julian L&uuml;ken
 */
public class Base extends Token {
	// Konstruktoren
	/**
	 * Konstruktor mit allen Attributen
	 * @param position = Position an der sich dieses Token befindet
     * @param color = Farbe dieses Tokens
     * @param board = Spielbrett auf welchem sich der Token befindet
	 */
	protected Base(Position position, PlayerColor color, BasicBoard board) {
		super(position, color, TokenType.BASE, board);
	}
	/**
	 * Kopierkonstruktor
	 * @param base = zu kopierende Basis
	 */
	protected Base(Base base) {
		this(base.getPosition(), base.getColor(), base.getBoard());
	}
	
	// Methoden
	/**
	 * Die Methode gibt die Bewegungsreichweite dieser Basis zurueck
	 * @return Bewegungsreichweite dieser Basis
	 */
	public int getRange() {return 0;}
}
