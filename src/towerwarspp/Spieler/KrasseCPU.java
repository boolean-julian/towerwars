package towerwarspp.Spieler;

import java.util.Random;
import java.util.LinkedList;
import towerwarspp.preset.*;
import towerwarspp.board.Token;
import towerwarspp.board.TokenType;
import towerwarspp.board.ExtendedBoard;
import towerwarspp.board.Tower;

/**Klasse der verbesserten KI. 
* Spielzuege werden ausgewertet, indem die Situation nach ausfuehren des Zuges bewertet wird. Dazu bekommt unter anderem jeder Token eine Wertung(eigene und gegnerische)
*@author Oskar Wittkamp 
*/
public class KrasseCPU extends Spieler {
	
	/**Diverse Instanzvariablen*/
	//Bestimmt bei gleichwertigen Zuegen zufaellige einen davon
	private Random random;
	private LinkedList<Move> ownMoves = new LinkedList<Move>();
	//Einzelner Spielstein, der zur gegn. Basis losgeschickt wird
	private ExtendedBoard board;
	private boolean hasRunner = false;
	//All non-BASE-Tokens werden hier nach Farbe eingespeichert
	private LinkedList<Token>redTokenList=new LinkedList<Token>();	
	private LinkedList<Token>blueTokenList=new LinkedList<Token>();
	
	//Bewertung, falls ein Pawn geschlagen wird
	private final int capturePawn = 5000;
	//Bewertung, falls ein Turm geschlagen wird(additiv)
	private final int captureTower = 10000;
	//Bewertung, falls ein Turm geschlagen wird(Turmhoehe*captureTowerMult+captureTower)
	private final int captureTowerMult = 0;
	//Bewertung fuer Token, die gefaehrdet sind, d.h. sie koennen von mehr gegnerischen Steinen geschlagen werden, als sie von eigenen gedeckt ist
	private final int isEndangered = 1000;
	//Bewertung fuer jeden PAWN auf dem Brett. Kann Turmbildung justieren.
	private final int pawnValue = 0;
	//Bewertung, falls der Zug Richtung eigene Base geht
	private final int directionOwnBase = 5;
	//Bewertung, wenn ein Zug wiederholt ausgefuehrt werden soll(anzahl der zuletzt wiederholten Zuege * -1 * repeatMoveMalus)
	private final int repeatMoveMalus = 30;
	//Bewertung je Feld, das der PAWN erreichen kann/deckt
	private final int coveredTiles = 1;
	//Ab so viel Tokens vorsprung, wird ein runner losgeschickt(additiv)
	private final int runnerInitFlat = 3;
	//Oder ab so viel Tokens vorsprung, wird ein runner losgeschickt(multiplikativ)
	private final int runnerInitMult = 2;
	//Bewertung, wenn der Zug ein Zug des Runners in Richtung gegnerische Base ist
	private final int runnerBonus = 2000;
	//Bewertung, wenn ein PAWN die Base unmittelbar verteidigt
	private final int protectBase = 250;
	
	//Counter fuer die Anzahl der wiederholten Moves
	private int repeatedMoves;
	
	/**
	Konstruktor fuer die CPU
	*/
	public KrasseCPU(){
		super();
		random = new Random();
	}
	
	/**	Ermittelt einen naechsten zufaelligen Move
	@return naechster Move
	*/
	@Override
	protected Move getNextMove(){
		board = getBoard();
		int scoreTemp;
		//Liste aller legalen Zuege
		LinkedList<Move> legalMoves = board.getLegalMoves();
		//Abfangen, falls kein Zug moeglich ist
		if (legalMoves.isEmpty()){
			return null;
		}
		//Mehrere gleichgute und beste Zuege werden bestMoves gespeichert
		LinkedList<Move> bestMoves= new LinkedList<Move>();
		//bestMove initialisert mit dem 1. Move der Liste aller moves
		bestMoves.add(legalMoves.get(0));
		int bestValue= evaluateMove(bestMoves.get(0));
		//Iterieriert ueber alle moeglichen Moves und wertet sie aus
		for(Move move : legalMoves){
			scoreTemp = evaluateMove(move);
			if(scoreTemp > bestValue){
				bestMoves.clear();
				bestMoves.add(move);
				bestValue=scoreTemp;
			} else if(scoreTemp == bestValue)
				bestMoves.add(move);
		}
		Move bestMove = bestMoves.get(random.nextInt(bestMoves.size()));
		//Stellt fest, ob der Move ein wiederholter move ist
		if(!isRepeated(bestMove))
			repeatedMoves = 0;
		else
			repeatedMoves++;
		//Speichert den Move, um festzustellen, ob er spaeter wiederholt wird
		ownMoves.add(bestMove);
		return bestMove;
	}
	
	/**fuegt alle Tokens auf dem Board in die jjeweilige Liste ein*/
	private void addTokensToLists(){
		Token[][] tokens = board.getTokens();
		redTokenList.clear();
		blueTokenList.clear();
		//Iteriert durch das ganze Spielfeld der groesse i*j
		for(int i = 0; i < board.getSize(); i++)
			for(int j = 0; j < board.getSize(); j++){
			
				//Wenn ein Token sich auf dem Feld befindet
				if(tokens[i][j] != null && tokens[i][j].getType() != TokenType.BASE){
					
					//Bei roten Token auf dem Feld
					if(tokens[i][j].getColor() == PlayerColor.RED){
						redTokenList.add(tokens[i][j]);
					}
					
					//Bei blauem Token auf dem Feld
					else{
						blueTokenList.add(tokens[i][j]);
					}
				}
			}
	}
	
	/**
	Die Methode vergleicht vorerst die Farbe der Spielsteine und bestimmt dadurch die gegnerischen
	Spielsteine, die den uebergebenen Spielstein zu schlagen gefaehrden.
	Dies geschieht anhand der Bewegungsreichweite(range), die die einzelnen Spielsteine besitzen.
	sollte eine Base uebergeben werden, so wird sie gecatcht und mit einem extremst hohen
	negativen Wert zugewiesen. Das soll verhindern, dass der Zug von der CPU ausgewaehlt wird.
	@param token ist der zubetrachtende Spielstein
	@return gibt die Anzahl der gegnerischen Spielsteine an, die den uebergebenen Token schlagen koennten
	*/
	private int checkIsEndangered(Token token){
		
		//Counter fuer Anzahl gegnerischer Tokens die ihn bedrohen
		int count = 0;
		Position tokenPosi = token.getPosition();
		
			//rote Spielsteine
			if(token.getColor() == PlayerColor.RED){
			
				//Iteriert ueber alle blauen Token und vergleicht Range mit Distance zum Token
				for(int i = 0; i < blueTokenList.size(); i++){
					if ((blueTokenList.get(i).getType()== TokenType.PAWN) && (blueTokenList.get(i).getRange() >= board.getDistance(tokenPosi, blueTokenList.get(i).getPosition()))){
						count++;
					}
				}
			}
			
			//blaue Spielsteine
			if(token.getColor() == PlayerColor.BLUE){
			
				//Iteriert ueber alle roten Token und vergleicht Range mit Distance zum Token
				for(int i = 0; i < redTokenList.size(); i++){
					if ((redTokenList.get(i).getType()==TokenType.PAWN) && (redTokenList.get(i).getRange() >= board.getDistance(tokenPosi, redTokenList.get(i).getPosition()))){
						count++;
					}
				}
			}
		return count;
	}
	
	
	/**Zaehlt die Anzahl der Token, die diesen token decken
	@param token zu checkender Token
	@return Anzahl der deckenden Token
	*/
	private int checkCovered(Token token){
		
		//Wie viele eigene Tokens decken den Token
		int count = 0;
		Position tokenPosi = token.getPosition();
		
			//falls blau
			if(token.getColor() == PlayerColor.BLUE){
				//Iteriert ueber alle blauen Token und vergleicht Range mit Distance zum Token
				for(int i = 0; i < blueTokenList.size(); i++){
					if ((blueTokenList.get(i).getType()==TokenType.PAWN) && (blueTokenList.get(i).getRange() >= board.getDistance(tokenPosi, blueTokenList.get(i).getPosition()))){
						count++;
					}
				}
			}
			
			//falls rot
			if(token.getColor() == PlayerColor.RED){
				//Iteriert ueber alle roten Token und vergleicht Range mit Distance zum Token
				for(int i = 0; i < redTokenList.size(); i++){
					if ((redTokenList.get(i).getType()==TokenType.PAWN) && (redTokenList.get(i).getRange() >= board.getDistance(tokenPosi, redTokenList.get(i).getPosition()))){
						count++;
					}
				}
			}
		//-1, da er sich selbst immer deckt
		return count-1;	
	}	
	
	/**handelt es sich um einen gewinnenden Move?
	@param move auszuwertender Move
	@return true, falls gewinnender Move, sonst false
	*/
	private boolean isWinning(Move move) {
		//Pruefen, ob der Zug ueberhaupt legal ist
		boolean hasMoves;
		if (board.isLegal(move)){
			//Wenn der Zug auf der Basis endet, handelt es sich um einen gewinnenden move
			if (board.findToken(move.getEnd()) != null && board.findToken(move.getEnd()).getType() == TokenType.BASE)
				return true;
			//Zug ausfuehren und anschlie??end checken, ob der Gegner noch einen moeglichen Zug hat
			board.executeMove(move);
			hasMoves = board.hasLegalMove();
			//Zug rueckgaengig machen
			board.undoMove();
			//Wenn Aufgeben der einzige moegliche Zug des Gegners ist, fuehrt der Zug zum Sieg
			if (hasMoves)
				return false;
		}
		return true;
	}
	
	/** Hilfsmethode f&uuml;r evaluateMove. Berechnet, ob der Zug eine Niederlage verhindern kann
	 *	@param move zu pruefender Move
	 *  @return true, falls es so ist, sonst false.
	 */
	private boolean willLose(Move move) {

		//Fuehrt den uebergebenen Zug aus, prueft ob der Gegner danach einen gewinnenden move ausfuehren koennte, und macht den ausgefuehrten Zug rueckgaengig
		LinkedList<Move> legalMoves;
		board.executeMove(move);
		legalMoves = board.getLegalMoves();
		for(Move m : legalMoves){
			if (isWinning(m)){
				board.undoMove();
				return true;
			}
		}
		board.undoMove();
		//Wenn kein einziger gegnerischer gewinnnender Move gefunden wurde, wird der Gegner nicht naechste Runde gewinnen
		return false;
	}
	
	/**bewertet einen Token anhand mehrere Faktoren: Wie gut ist er gedeckt
	@param token auszuwertender Token
	@return Bewertung
	*/
	private int evaluateToken(Token token){
		int score = 0;
		int cover = checkCovered(token);
		int endangered = checkIsEndangered(token);
		int coveredScore = cover-endangered;
		
		
		//score -isEndangered, wenn der Stein geschlagen werden kann und nicht ausreichend gedeckt ist
		//score +10, bei 1, +20 bei 2, +25 bei 3+ gedeckt.
		if(coveredScore <0)
			score-= isEndangered;
		else if(coveredScore > 0 && coveredScore <= 2)
			score+=10*coveredScore;
		else 
			score+=25;
		if(token.getType() == TokenType.PAWN)
			score += pawnValue;
		LinkedList<Position> list= board.getNeighborhood(token.getPosition(), token.getRange());
		score += list.size() * coveredTiles;
		return score;	
	}
	
	/**Gibt die Bewertung zurueck, fuer das Schlagen eines Tokens (PAWN/TOWER) (0/capturePawn/tower.getHeight()*captureTowerMult + captureTower)
	@param move auszuwertender Zug
	@return Bewertung
	*/
	private int captureToken(Move move){
		TokenType tokenType;
		try{
		tokenType = board.findToken(move.getEnd()).getType();
		}catch(NullPointerException e){
			return 0;
		}
		if (tokenType == null)
				return 0;
		if (tokenType == TokenType.PAWN && (board.findToken(move.getStart()).getColor() != board.findToken(move.getEnd()).getColor()))
				return capturePawn;
		if (tokenType == TokenType.TOWER && (board.findToken(move.getStart()).getColor() != board.findToken(move.getEnd()).getColor())){
				Tower tower = (Tower)board.findToken(move.getEnd());
				return tower.getHeight()*captureTowerMult + captureTower;
		}
		return 0;
	}
	
	/** Wenn true, dann wurde der gleiche Move schonmal ausgefuehrt, sonst false
	@param move auszuwertender Zug
	@return true, falls der Move schonmal ausgefuehrt wurde, sonst false
	*/
	private boolean isRepeated(Move move){
		if(ownMoves.contains(move))
			return true;
		return false;
	}
	
	/**ermittelt die effektive Anzahl der Spielsteine eines Spielers (Pawn = 1, Tower = Height)
	@param list Liste der Tokens, deren Tokens gezaehlt werden sollen
	@return effektive anzahl an moeglichen Pawns
	*/
	private int numberOfTokens(LinkedList<Token> list){
		int count = 0;
		//Iteriert ueber alle Token der uebergebenen Liste und zaehlt zusammen
		for(Token token: list){
			if (token.getType()==TokenType.PAWN)
				count++;
			if (token.getType()==TokenType.TOWER){
				Tower tower = (Tower)token;
				count+= 1+tower.getHeight();
			}
				
		}
		return count;
	}
	
	/**gibt die protectBase zurueck, wenn ein PAWN die BASE unmittelbar covered, sonst 0
	@return Bewertung der Situation baseIsProtected
	*/
	private int baseIsProtected(){
		LinkedList<Token> tokenList;
		Position basePosition = color == PlayerColor.RED ? new Position(1,1) : new Position(board.getSize(), board.getSize());
		tokenList = color == PlayerColor.RED ? redTokenList : blueTokenList;
		//Iteriert ueber alle Token der jew Spielerliste und prueft, ob ein Token den Kriterien entspricht( Pawn und neben der Base)
		for(Token token: tokenList){
			if (board.getDistance(token.getPosition(), basePosition) == 1 && token.getType() == TokenType.PAWN)
				return protectBase;
		}
		return 0;
	}
	
	/**Bewertet einen Move bzgl des runnerBonuses. 
	* Dieser Bonus wird vergeben, wenn ein runner existieren solle (hasrunner) und der Zug ein Zug eines moeglichen Runners in Richtung der gegnerischen Base ist. Ein Token kann ein Runner sein, wenn er der naechste Token zur gegnerischen Base ist
	@param move auszuwertender Zug
	@return RunnerBonus, wenn der Zug ein passender Zug eines Runners ist, sonst 0
	*/
	private int runnerBonus(Move move){
		//Prueft, ob ein Runner initialisiert werden soll
		if  (!hasRunner){
			int ownTokens = color == PlayerColor.RED ? numberOfTokens(redTokenList) : numberOfTokens(blueTokenList);
			int enemyTokens = color == PlayerColor.RED ? numberOfTokens(blueTokenList) : numberOfTokens(redTokenList);
			//hasRunner = true -> Ab sofort wird RunnerBonus vergeben
			if(ownTokens - enemyTokens >= runnerInitFlat)
				hasRunner = true;
			else if (ownTokens - runnerInitMult * enemyTokens >= 0)
				hasRunner = true;
		}
		//wenn kein Runnner initialisiert ist, return 0, sonst pruefe Move, ob er den Kriterien entspricht und return runnerBonus
		if (!hasRunner)
			return 0;
		else{
			if(isRunner(board.findToken(move.getStart()))){
				if (color == PlayerColor.RED){
					if(isDirectionBase(PlayerColor.BLUE, move)){
						return runnerBonus;
					}
				}else{
					if(isDirectionBase(PlayerColor.RED, move)){
						return runnerBonus;
					}
				}
			}
		}
		return 0;	
	}
	
	/** Ermittelt, ob ein Token ein moeglicher Runner ist, bzw ob er einer der Tokens ist, die der gegnerischen Base am naechsten sind
	@param token auszuwertender Token
	@return true, falls der Token ein moeglicher Runner ist, sonst false
	*/
	private boolean isRunner(Token token){
		//Position der gegnerischen Base
		Position enemyBase = color == PlayerColor.BLUE ? new Position(1,1) : new Position(board.getSize(), board.getSize());
		//Distance vom Token zur Base
		int distance = board.getDistance(enemyBase, token.getPosition());
		//Waehlt die Liste der eigenen token aus
		LinkedList<Token> list = color == PlayerColor.RED ? redTokenList : blueTokenList;
		//Iteriert ueber die eigenen Token
		for(Token t: list){
			//Wenn ein anderer eigener token naeher an der gegnerischen Base dran ist, false
			if (board.getDistance(enemyBase, t.getPosition()) < distance)
				return false;
		}
		//sonst true
		return true;
	}
	
	/**Prueft, ob der Move in Richtung der uebergebenen Base geht
	@param colour Farbe der Base
	@param move auszuwertender Zug
	@return true, wenn in Richtung der Base, sonst false
	*/
	private boolean isDirectionBase(PlayerColor colour, Move move){
		int direction = move.getStart().getLetter() + move.getStart().getNumber() - move.getEnd().getLetter() - move.getEnd().getNumber();	
		if (direction > 0 && colour == PlayerColor.RED){
			return true;
		}
			
		if (direction < 0 && colour == PlayerColor.BLUE){
			return true;
		}
		else return false;
			
	}
	
	/**Bewertet einen Zug, indem die jeweiligen Bewertungen zusammenaddiert werden
	@param move zu evaluierender Zug
	@return Bewertung
	*/
	private int evaluateMove(Move move){
		//Absolut entscheidende Abfragen, wenn isWinning/willLose zutrifft, spielen andere Werte keine Rolle mehr -> return sehr hohen/tiefen Wert
		if (isWinning(move))
			return Integer.MAX_VALUE;
		if (willLose(move))
			return Integer.MIN_VALUE;
		
		//Fuehrt den uebergebenen Zug aus und bewertet die Situation danach.
		board.executeMove(move);
		addTokensToLists();
		int score = 0;
		for(Token token : redTokenList)
			score += color == PlayerColor.BLUE ? -1*evaluateToken(token) : evaluateToken(token);
		for(Token token : blueTokenList)
			score += color == PlayerColor.BLUE ? evaluateToken(token) : -1*evaluateToken(token);
		//Macht den Zug rueckgaengig und verrechnet weitere Werte
		board.undoMove();
		score+=captureToken(move);
		score+=runnerBonus(move);
		score+=baseIsProtected();
		//Verrechnet Bonus fuer Bewegungsrichtung eigene Base
		if (isDirectionBase(PlayerColor.BLUE, move))
			score+= color== PlayerColor.RED ? directionOwnBase : -1*directionOwnBase;
		//Bewertet das Wiederholen von Zuegen als negativ
		if(isRepeated(move))
			score -= repeatMoveMalus*(repeatedMoves+1);
		return score;
	}
}

