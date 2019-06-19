package towerwarspp.Spieler;

import java.rmi.*;
import java.rmi.server.UnicastRemoteObject;
import java.net.*;
import java.util.Scanner;
import towerwarspp.io.EinAusgabe;
import towerwarspp.Spieler.InterActSpieler;
import towerwarspp.preset.*;

/**
 *	Klasse des Netzwerkspielers. Wandelt ein Player-Objekt in einen Netzwerkspieler um.
 *	@author Oskar Wittkamp
 */
public class NetPlayer extends UnicastRemoteObject implements Player{
	
	/**Instanzvariablen*/
	private boolean isGraphic;
	private boolean isDebug;
	private Scanner scanner;
	private Spieler spieler;
	private EinAusgabe einAusgabe;
	
	/**
	 *	Konstruktor
	 *	@param spieler umzuwandelnder Spieler
	 *	@param isGraphic true wenn eine grafische Ausgabe erfolgen soll, sonst false
	 *	@param isDebug true wenn eine Konsolenausgabe erfolgen soll, sonst false
	 *	@throws java.rmi.RemoteException falls noetig
	 */
	public NetPlayer(Spieler spieler, boolean isGraphic, boolean isDebug) throws RemoteException{
		this.spieler = spieler;
		this.isGraphic = isGraphic;
		this.isDebug = isDebug;
		scanner = new Scanner(System.in);
	}
	
	/**	
	 *	generiert einen nächsten Spielzug
	 *	@return nächster Spielzug
	 */
	@Override
	public Move request() throws Exception, RemoteException{
		Move m = spieler.request();
		return m;
	}
	
	/** 
	 *	updatet das spielereigene Board, indem der gegnerische Zug ausgeführt wird
	 *	@param opponentMove gegnerischer neuer Zug
	 *	@param boardStatus Status des Host-Boards, nach dem gegnerischen Zug
	 */
	@Override
	synchronized public void update(Move opponentMove, Status boardStatus) throws Exception, RemoteException{
		spieler.update(opponentMove, boardStatus);
		if(isGraphic){
			einAusgabe.updateGUI();
		}
		if(isDebug){
			System.out.println((char)27 + "[0m");
			System.out.println(einAusgabe);
		}
		//Benachrichtigt die main-Klasse, wenn das Spiel vorbei ist
		if(boardStatus == Status.RED_WIN || boardStatus == Status.BLUE_WIN)
			notify();
	}
	
	/**
	 *	prüft, ob der eigene Zug auf dem Server ausgeführt werden konnte
	 *	@param boardStatus Status des Server-Boards nach ausführen des eigenen Moves
	 */
	@Override
	synchronized public void confirm(Status boardStatus) throws Exception, RemoteException{
		spieler.confirm(boardStatus);
		if(isGraphic){
			einAusgabe.updateGUI();
		}
		if(isDebug){
			System.out.println((char)27 + "[0m");
			System.out.println(einAusgabe);
		}
		//Benachrichtigt die main-Klasse, wenn das Spiel vorbei ist
		if(boardStatus == Status.RED_WIN || boardStatus == Status.BLUE_WIN)
			notify();
	}
	
	/**
	 *	initialisiert den NetPlayer
	 *	@param size Spielbrettgröße
	 *	@param color Spielerfarbe
	 */
	@Override
	public void init (int size, PlayerColor color) throws Exception, RemoteException{
		spieler.init(size, color);
		einAusgabe = new EinAusgabe(spieler.getBoard(), scanner);
		if(spieler instanceof InterActSpieler) {
			((InterActSpieler)spieler).setEinAusgabe(einAusgabe);
		}
		//Wenn True, dann erzeuge eine GUI
		if(isGraphic){			
			einAusgabe.showGUI();
			einAusgabe.setGuiInput(true);
		}
		//Wenn true, dann erzeuge eine Text-Ein-Ausgabe
		if(isDebug){
			System.out.println((char)27 + "[0m");
			System.out.println(einAusgabe);
		}	
	}
}
