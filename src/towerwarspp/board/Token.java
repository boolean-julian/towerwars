package towerwarspp.board;

import towerwarspp.preset.Position;
import towerwarspp.preset.PlayerColor;
/**
 * Die Klasse Token stellt die Implementation einer Spielfigur aus Towerwars da
 * Die Funktion der Spielfigur wird hierbei durch den TokenType und die PlayerColor bestimmt
 * @author Marcel Hellwig und Julian L&uuml;ken
 */
public abstract class Token {
	// Instanzvariablen
    /**
     * Position an der sich das Token befindet
     */
	private Position position;
    /**
     * color = Die Farbe dieses Tokens
     */
	private PlayerColor color;
    /**
     * type = Der Typ dieses Tokens (Pawn, Base, Tower)
     */
	private TokenType type;
	/**
	 * board = Board, auf dem sich dieses Token befindet
	 */
	private BasicBoard board;

	// Konstruktoren
    /**
     * Konstruktor mit uebergebener Position, Farbe und und Typ
     * @param position = Position an der sich dieses Token befindet
     * @param color = Farbe dieses Tokens
     * @param type = Typ dieses Token
	 * @param board = Board auf dem sich dieses Token befindet
     */
	protected Token(Position position, PlayerColor color, TokenType type, BasicBoard board) {
		this.position = position;
		this.color = color;
		this.type = type;
		this.board = board;
	}

	// Getter/Setter
    /**
     * Getter fuer position
     * @return Position an der sich dieses Token befindet
     */
	public Position getPosition() {return position;}
    /**
     * Getter fuer color
     * @return Farbe dieses Tokens
     */
	public PlayerColor getColor() {return color;}
    /**
     * Getter fuer type
     * @return Typ dieses Tokens
     */
	public TokenType getType() {return type;}
	/**
	 * Getter fuer board
	 * @return board auf dem sich dieses Token befindet
	 */
	public BasicBoard getBoard() {return board;}
    /**
     * Setter fuer cell
     * @param position = Neue Position in der sich dieses Token befindet
     */
	protected void setPosition(Position position) {this.position = position;}
    /**
     * Setter fuer color
     * @param color = Neue Farbe dieses Tokens
     */
	protected void setColor(PlayerColor color) {this.color = color;}
    /**
     * Setter fuer type
     * @param type = Neuer Typ dieses Tokens
     */
	protected void setType(TokenType type) {this.type = type;}
	/**
	 * Setter fuer board
	 * @param board = Neues Board auf dem sich dieses Token befindet
	 */
	protected void setBoard(BasicBoard board) {this.board = board;}

	// Methoden
	public abstract int getRange();
    
}
