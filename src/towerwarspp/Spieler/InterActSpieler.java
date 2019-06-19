package towerwarspp.Spieler;

import towerwarspp.io.EinAusgabe;
import towerwarspp.preset.Move;

/**Klasse der interaktiven Spielers, der über die Kommandozeile oder Grafikeingabe gesteuert werden kann. Erbt von Spieler
*/
public class InterActSpieler extends Spieler{
	
	/**Instanzvariablen*/
	private EinAusgabe einAusgabe;
	
	/** Konstruktor
	@param einAusgabe ein EinAusgabe-Objekt
	*/
	public InterActSpieler(EinAusgabe einAusgabe){
		super();
		if (einAusgabe == null) {
			throw new IllegalArgumentException("Kann keinen HUMAN-Spieler ohne Eingabe initialisieren. Bitte --graphic oder --debug zum Aufruf hinzufuegen");
		}
		this.einAusgabe = einAusgabe;
	}

	/**
	 * Setter fuer einAusgabe
	 * @param einAusgabe = neues einAusgabe-Objekt
	 */
	public void setEinAusgabe(EinAusgabe einAusgabe) {this.einAusgabe = einAusgabe;}

	/** Überschreibt von Spieler geerbte Methode getNextmove()
	@return von der EinAusgabe eingelesener Zug
	*/
	@Override
	protected Move getNextMove(){
		return einAusgabe.deliver();
	}
}
