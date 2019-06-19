package towerwarspp.Spieler;

import towerwarspp.preset.Move;
import java.util.LinkedList;
import java.rmi.RemoteException;
import towerwarspp.board.ExtendedBoard;
import java.util.Random;

/**
CPU -Spieler mit der einfachen KI, die Spielzuge positiv bewertet, wenn der Stein in Richtung gegnerische Base zieht, oder einen Token schlägt. Bei gleichguten Zügen wird zufällig einer ausgewählt.
Erbt von Spieler
@author Oskar Wittkamp
*/
public class CPUSpieler extends Spieler{

	/**Instanzvariablen*/
	private Random random;
	int bestValue;
	
	/**Standard Konstruktor
	*/
	public CPUSpieler(){
		super();
		random = new Random();
	}
	
	/**überschreibt die geerbte getNextMove Methode und ermittelt den nächsten Zug
	@return bestmöglicher Spielzug nach Kriterien der gegebenen einfachen KI, bei mehreren gleichguten Zügen wird ein Zufälliger ausgewählt
	*/
	@Override
	protected Move getNextMove(){
		LinkedList<Move> list = board.getLegalMoves();
		// Abfangen, wenn keine Züge möglich sind, bzw aufgeben der einzige mögliche Zug ist
		if(list.isEmpty()) {
			return null;
		}
		//Linked List zum speichern gleichguter moves
		LinkedList<Move> bestmovelist = new LinkedList<>();
		//Erster Move wird zum initialiseren als bester bisheriger Move angenommen
		bestmovelist.add(list.get(0));
		//initialisiert das Value vom ersten move als bisher angenommen bestes Value
		bestValue = board.evaluateMove(list.get(0));
		/*Iteriert über die Liste aller legalen Moves
		Jeder Move wird ausgewertet, und derbeste/die besten Moves werden in bestmovelist gespeichert
		*/
		for (Move move : list){
			//Wenn der Move genauso gut wie der bisher beste Move ist, wird er auch gespeichert
			if(board.evaluateMove(move) == bestValue)
				bestmovelist.add(move);
			//Wenn der Move der neue  beste Move ist, dann wird die bestmovelist geleert und der move reingespeichert
			if (board.evaluateMove(move) > bestValue){
				bestmovelist.clear();
				bestmovelist.add(move);
				bestValue = board.evaluateMove(bestmovelist.get(0));
			}
		}
		//Gibt einen zufälligen der besten Moves zurück
		return bestmovelist.get(random.nextInt(bestmovelist.size()));
	}	
}




