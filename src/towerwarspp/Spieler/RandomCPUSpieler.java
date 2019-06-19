package towerwarspp.Spieler;

import java.util.Random;
import java.util.LinkedList;
import towerwarspp.preset.Move;

/**Klasse der KI, die zufällige Züge ausführt
*/
public class RandomCPUSpieler extends Spieler {
	
	/**Instanzvariablen*/
	private Random random;
	//Konstruktor
	public RandomCPUSpieler(){
		super();
		random = new Random();
	}
	/**	Ermittelt einen nächsten zufälligen Move aus der Liste aller möglichen Moves aus
	@return nächster Move
	*/
	@Override
	protected Move getNextMove(){
		LinkedList<Move> possibleMoves = board.getLegalMoves();
		// Methode zur Behandlung von keinen moeglichen Zuegen
		if(possibleMoves.isEmpty()) {
			return null;
		}
		return possibleMoves.get(random.nextInt(possibleMoves.size()));
	}
}
