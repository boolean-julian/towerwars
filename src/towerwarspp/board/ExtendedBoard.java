package towerwarspp.board;

import towerwarspp.preset.Position;
import towerwarspp.preset.Move;
import towerwarspp.preset.PlayerColor;
import java.util.LinkedList;

/**
 *	Klasse, die f&uuml;r TowerWarsPP ein Spielbrett-Objekt erzeugt und von BasicBoard.java erbt.
 *	Dar&uuml;ber hinaus liefert diese Klasse Methoden, die alle m&ouml;glichen Spielz&uuml;ge ausgibt und diese anhand einer
 *	simplen Strategie bewertet.
 *
 *	@author Julian L&uuml;ken
 */
public class ExtendedBoard extends BasicBoard {
	/** 
	 *	Konstruktor, der die Spielfeldgr&ouml;&szlig; entgegennimmt und ein neues Spielbrett instanziert wie BasicBoard.java,
	 *	@param size Gr&ouml;&szlig;e des Spielbretts
	 */
	public ExtendedBoard(int size) {
		super(size);
	}

	/** 
	 *	Copy-Konstruktor
	 *	@param extBoard zu kopierendes ExtendedBoard
	 */
	public ExtendedBoard(ExtendedBoard extBoard) {
		super(extBoard.getSize());
	}

	/** Methode f&uuml;r KI-Gegner, die einen Zug bewertet. Dabei wird bewertet, inwiefern der Zug daf&uuml;r sorgt, dass man einen gegnerischen Spielstein bezwingt
	 *	oder sich in Richtung der gegnerischen Basis bewegt. Z&uuml;ge, die das Spiel gewinnen oder das Verlieren vermeiden, werden dabei bevorzugt
	 *	@param move zu bewertender Spielzug
	 *	@return Punktewertung f&uuml;r den Zug. Dabei steht 100 f&uuml;r einen rettenden Zug und 101 f&uuml;r einen gewinnenden Zug
	 */
	public int evaluateMove(Move move) {
		// Leerer Zug
		if (move == null) {
			return -100;
		}
		
		PlayerColor startColor	= findToken(move.getStart()).getColor();
		PlayerColor opponentColor = PlayerColor.RED;
		if (startColor == opponentColor) {
			opponentColor = PlayerColor.BLUE;
		}
		// siegender Zug	
		if (isWinning(move)) {
			return 101;
		}
		// rettender Zug4
		if (willLose(move)) {
			return -99;
		}
		
		// Punkte fuer das Bewegen in Richtung gegnerischer Basis
		int rbase = 0; 
		// Punkte fuer das Schlagen eines Spielsteins
		int bkick = 0; 
		
		// in Richtung BLUE bewegen
		if (startColor == PlayerColor.RED) {
			rbase = getDistance(new Position(getSize(), getSize()),move.getStart()) - getDistance(new Position(getSize(), getSize()), move.getEnd());
		}
		
		// in Richtung RED bewegen
		else if (startColor == PlayerColor.BLUE) {
			rbase = getDistance(new Position(1, 1),move.getStart()) - getDistance(new Position(1, 1), move.getEnd());
		}

		if (findToken(move.getEnd()) != null) {
			PlayerColor endColor	= findToken(move.getEnd()).getColor();
			TokenType 	type		= findToken(move.getEnd()).getType();
			if (endColor != startColor) {
				// Bauern schlagen

				if (type == TokenType.PAWN) {			
					bkick = 1;
				}
				// Turm schlagen
				else if (type == TokenType.TOWER) {
					bkick = 2;
				}
				// Basis schlagen
				else if (type == TokenType.BASE) {
					return 101;
				}
			}
		}
		return bkick + rbase;
	}

	/**
	 * Diese Methode gibt alle legalen Zuege auf diesem Spielfeld zurueck
	 * @return Alle legalen Zuege dieses Spielbretts
	 */
	public LinkedList<Move> getLegalMoves() {
		return legalMoves(true);
	}

	/** Hilfsmethode f&uuml;r evaluateMove. Berechnet, ob der Zug gewinnen kann durch blocken aller gegnerischen Figuren.
	 *	@param move zu prüfender Move
	 *	@return true, falls es so ist, sonst false.
	 */
	private boolean isWinning(Move move) {
		//Prüfen, ob der Zug überhaupt legal ist
		boolean hasMoves;
		if (isLegal(move)){
			//Wenn der Zug auf der Basis endet, handelt es sich um einen gewinnenden move
			if (findToken(move.getEnd()) != null && findToken(move.getEnd()).getType() == TokenType.BASE)
				return true;
			//Zug ausführen und anschließend checken, ob der Gegner noch einen möglichen Zug hat
			executeMove(move);
			hasMoves = hasLegalMove();
			//Zug rückgängig machen
			undoMove();
			//Wenn Aufgeben der einzige mögliche Zug des Gegners ist, führt der Zug zum Sieg
			if (hasMoves)
				return false;
		}
		return true;
	}
	
	/** Hilfsmethode f&uuml;r evaluateMove. Berechnet, ob der Zug eine Niederlage verhindern kann
	 *	@param move zu prüfender Move
	 *  @return true, falls es so ist, sonst false.
	 */
	private boolean willLose(Move move) {

		//Führt den übergebenen Zug aus, prüft ob der Gegner danach einen gewinnenden move ausführen könnte, und macht den ausgeführten zug rückgängig
		LinkedList<Move> legalMoves;
		executeMove(move);
		legalMoves = getLegalMoves();
		for(Move m : legalMoves){
			if (isWinning(m)){
				undoMove();
				return true;
			}
		}
		undoMove();
		//Wenn kein einziger gegnerischer gewinnnender Move gefunden wurde, wird der Gegner nicht nächste Runde gewinnen

		return false;
			
		 
	}
}
