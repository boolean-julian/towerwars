package towerwarspp.gfx;

import javax.swing.*;
import java.awt.Color;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import towerwarspp.preset.*;
import towerwarspp.board.Token;
import towerwarspp.board.BasicBoard;
import java.util.Random;
import java.lang.reflect.InvocationTargetException;

/**
 *	Klasse zur Darstellung des Spielbretts<br><br>
 *
 *	Konstruktion:<br>
 *	{@code TowerWarsGUI gui = new TowerWarsGUI(new BasicBoard(15));}<br>
 *	ruft den Konstruktor auf. Wenn man als zweiten Parameter des Konstruktors noch einen Integer hinzuf&uuml;gt,
 *	so kann man die Gr&ouml;sse der einzelnen Spielfelder modifizieren.<br><br>
 *
 *	Nutzung:<br>
 *	Um das erzeugte GUI anzeigen zu lassen, muss
 *	{@code gui.createAndShowGUI()} aufgerufen werden. Nach jedem vollbrachten Zug, der angezeigt werden soll,
 *	muss ein Aufruf von {@code gui.update()} erfolgen, um die Anzeige zu aktualisieren.<br><br>
 *
 *	@author Julian L&uuml;ken
 */
public class TowerWarsGUI implements Viewer, Requestable {
	/** darzustellendes Board */
	private BasicBoard board;
	/** Raster aus HexagonButtons bildet das Spielfeld */
	private HexagonButton[][] buttons;	

	/** Startposition, die von der deliver() Methode gespeichert wird, damit der Spieler einen Move produzieren kann */
	private Position start;
	/** Endposition, die von der deliver() Methode gespeichert wird, damit der Spieler einen Move produzieren kann */
	private Position end;
	/** Hier speichert deliver(), ob das erste oder zweite mal geklickt wurde, damit der Spieler einen Move produzieren kann */
	private boolean first;

	/** Fenster */
	private JFrame frame;
	/** Panel f&uuml;r das Spielfeld */
	private JPanel panel;
	/** JLabels, auf denen hilfreiche Daten &uuml;ber das laufende Spiel stehen */
	private JLabel[] statusLabel;
	/** JButton f&uuml;r die Aufgabe des Spiels */
	private JButton surrenderButton;
	/** JButton f&uuml;r ??? */
	private JButton wtfButton;

	/** 
	 *	Konstruktor, der das Spielbrett entegegennimmt. Setzt die Gr&ouml;&szlig;e der Hexagons auf 30px
	 *	@param board anzuzeigendes Spielbrett
	 */
	public TowerWarsGUI(BasicBoard board) {
		this.board = board;
		this.first = true;
	}
	/**
	 *	Konstruktor, der das Spielbrett und die Gr&ouml;&szlig;e der Hexagons entgegennimmt
	 *	@param board Spielbrett
	 *	@param hexaSize Gr&ouml;&szlig;e
	 */
	public TowerWarsGUI(BasicBoard board, int hexaSize) {
		this.board = board;
		this.first = true;
	}
		
	/**
	 *	Getter f&uuml;r das Spielbrett, auf dem das GUI l&auml;ft
	 *	@return Spielbrett
	 */
	protected BasicBoard getBoard()			{return board;}

	/** 
	 *	Getter f&uuml;r das Array aus HexagonButtons auf dem Spielbrett
	 *	@return Spielfelder
	 */
	protected HexagonButton[][] getButtons() 	{return buttons;}

	/** 
	 *	Methode, die die Startposition f&uuml;r deliver() speichert
	 *	@param start Startposition
	 */
	private void setStart(Position start)	{this.start = start;}

	/** 
	 *	Methode, die die Endposition f&uuml;r deliver() speichert
	 *	@param end Endposition
	 */
	private void setEnd	(Position end) 		{this.end = end;}

	/** 
	 *	Hilfsmethode f&uuml;r den ActionListener der HexagonButtons. Unterscheidet ersten vom zweiten Mausklick auf dem Spielbrett
	 *	@param first true, falls erster Mausklick, false, falls zweiter
	 */
	private void setFirst(boolean first)		{this.first = first;}

	private int minSize, hexaSize;
	private boolean isWidth;
	/** Hilfsmethode f&uuml;r update() und updateSize(). Bestimmt die Gr&ouml;&szlig;e der anzuzeigenden Elemente */
	private void updateSpacing() {
		minSize = frame.getHeight();
		isWidth = false;
		// Seitenverhaeltnis des Spielbretts ist 3:2
		if (2*frame.getWidth() < 3*frame.getHeight()) {
			minSize = frame.getWidth();
			isWidth = true;
		}
		if (isWidth) {
			hexaSize = (int)(minSize/(3*board.getSize()));
		}
		else {
			hexaSize = (int)(minSize/(2*board.getSize()));
		}
	}
	
	/** Methode, die das Fenster initialisiert und das Spielfeld darstellt */
	public void createAndShowGUI() {
		// Fenster mit JPanel oeffnen
		frame = new JFrame("TowerWarsPP");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		panel = new JPanel();
		panel.setLayout(null);

		frame.add(panel);
		frame.setSize(900,600);
		frame.setVisible(true);

		buttons = new HexagonButton[board.getSize()][board.getSize()];

		update();

		// Listener fuer resize
		frame.addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent componentEvent) {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						updateSize();
					}
				});
			}
		});
	}

	/** 
	 *	Methode, die das JPanel, in dem das Spielfeld liegt, anhand des zugrunde liegenden Spielbrett-Objekts aktualisiert und anzeigt,
	 *	falls createAndShowGUI() vorher aufgerufen wurde
	 */
	public void update() {
		// swing ist (bis auf repaint()) nicht thread safe, darum muss gewartet werden, bis alles vom panel geloescht wurde, bis weiter gedrawt werden kann
		try {
			SwingUtilities.invokeAndWait(new Runnable() {
				public void run() {
					panel.removeAll();
				}
			});
		} catch (InterruptedException ie) {
			System.out.println("Could not wait for panel.removeAll() in TowerWarsGUI.update()");
		} catch (InvocationTargetException ite) {
			System.out.println("Could not invoke panel.removeAll() in TowerWarsGUI.update()");
		}

		// gucken, wie gross die einzelnen Elemente sein muessen und wo sie platziert werden muessen
		updateSpacing();

		// alle JButtons hinmalen
		for (int i = 0; i < getSize(); i++) {
			for (int j = 0; j < getSize(); j++) {
				buttons[i][j] = new HexagonButton(new Hexagon(hexaSize),board.getTokens()[i][j],this);
				buttons[i][j].setBounds(5+j*2*hexaSize+i*hexaSize, 10+i*2*hexaSize-i*hexaSize/4, 2*hexaSize+2, 2*hexaSize+2);
				panel.add(buttons[i][j]);
			}
		}	
		// Labels auf denen der Status und der jetzige Spieler steht
		statusLabel = new JLabel[2];
		statusLabel[0] = new JLabel("Status: " + board.getStatus());
		statusLabel[1] = new JLabel(board.getCurrentPlayer() + " ist dran");
		statusLabel[0].setBounds(10,frame.getHeight()-120, 200, 20);
		statusLabel[1].setBounds(10,frame.getHeight()-100, 200, 20);
		panel.add(statusLabel[0]);
		panel.add(statusLabel[1]);

		// Button zum Aufgeben
		surrenderButton = new JButton("Aufgeben");
		surrenderButton.setBounds(10, frame.getHeight()-70, 150, 20);
		panel.add(surrenderButton);

		// Button zum randomisierten Farbwechsel
		wtfButton = new JButton ("???");
		wtfButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Random rnd = new Random();
				HexagonButton.BORDER 	= new Color(rnd.nextInt(0xffffff));
				HexagonButton.RED 		= new Color(rnd.nextInt(0xffffff));
				HexagonButton.BLUE 		= new Color(rnd.nextInt(0xffffff));
				HexagonButton.EMPTY 	= new Color(rnd.nextInt(0xffffff));
				HexagonButton.SELECTED 	= new Color(rnd.nextInt(0xffffff));
				HexagonButton.SELECTED2	= new Color(rnd.nextInt(0xffffff));
				panel.repaint();
			}
		});
		wtfButton.setBounds(frame.getWidth()-120, 10, 100, 20);
		panel.add(wtfButton);

		frame.add(panel);

		frame.revalidate();
		panel.repaint();
	}


	/**
	 *	Methode, die das JPanel, in dem das Spielfeld liegt, vergroessert oder verkleinert
	 *	falls createAndShowGUI() vorher aufgerufen wurde
	 */
	private void updateSize() {
		updateSpacing();
		for (int i = 0; i < getSize(); i++) {
			for (int j = 0; j < getSize(); j++) {
				buttons[i][j].getHexagon().setRadius(hexaSize);
				buttons[i][j].getHexagon().setCenter(new Point(hexaSize,hexaSize));
				buttons[i][j].setBounds(5+j*2*hexaSize+i*hexaSize, 10+i*2*hexaSize-i*hexaSize/4, 2*hexaSize+2, 2*hexaSize+2);
			}
		}
		statusLabel[0].setBounds(10,frame.getHeight()-120, 200, 20);
		statusLabel[1].setBounds(10,frame.getHeight()-100, 200, 20);
		surrenderButton.setBounds(10, frame.getHeight()-70, 150, 20);
		wtfButton.setBounds(frame.getWidth()-120, 10, 100, 20);
		
		// Evtl. muss man auf update() warten. Dieser Aufruf verhindert, dass der Swing Thread Operationen in der falschen Reihenfolge aufruft
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				panel.repaint();
			}
		});
	}


	/**
	 *	Liefert den Status des Spiels zur&uuml;ck. &Uuml;berschreibt Methode aus der Schnittstelle towerwarspp.preset.Viewer
	 *	@return 	OK, falls das Spiel weiterl&auml;ft<br>
	 *				BLUE_WIN, falls der blaue Spieler das Spiel gewonnen hat<br>
	 *				RED_WIN, falls der rote Spieler das Spiel gewonnen hat<br>
	 *				ILLEGAL, falls eine von den Spielregeln verbotene Aktion durchgef&uuml;hrt wurde
	 */
	@Override
	public Status getStatus() {return board.getStatus();}

	/**
	 *	Liefert die Runde, in dem sich das Spiel befindet, als ganze Zahl. &Uuml;berschreibt Methode aus der Schnittstelle towerwarspp.preset.Viewer
	 *	@return Runde
	 */
	@Override
	public int getTurn() {return board.getTurn();}

	/**
	 *	Liefert die Spielfeldgr&ouml;&szlig;e. &Uuml;berschreibt Methode aus der Schnittstelle towerwarspp.preset.Viewer
	 *	@return Spielfeldgr&ouml;&szlig;e
	 */
	@Override
	public int getSize() {return board.getSize();}

	/**
	 *	F&uuml;gt mithilfe der Hilfsmethode addButtonListeners den HexagonalButtons auf dem Spielfeld ActionListener hinzu,
	 *	die diese Methode wiederum mithilfe von notify() benachrichtigen, wenn ein menschlicher Spieler einen Zug t&auml;tigt.
	 *	@return menschlicher Zug
	 */
	@Override
	public synchronized Move deliver() {
		addButtonListeners();
		if (getStatus() == Status.OK) {			
			SurrenderActionListener sal = new SurrenderActionListener(this);
			surrenderButton.addActionListener(sal);
			
			// Auf den ActionListener von addButtonListeners() warten
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			if (sal.hasSurrendered()) {
				return null;
			}
			
			Move move = new Move(start,end);
			setStart(null);
			setEnd(null);
			// Markierung aufheben, nachdem Zug beendet
			for (int i = 0; i < getSize(); i++) {
				for (int j = 0; j < getSize(); j++) {
					buttons[i][j].deSelect();
					buttons[i][j].deSelect2();
				}
			}
			return move;
		}
		return null;
	}

	/** Hilfsmethode f&uuml;r deliver(). F&uuml;gt dem Spielfeld ActionListener hinzu, um Klicks auf die HexagonButtons auszuwerten */
	private void addButtonListeners() {
		for (int i = 0; i < board.getSize(); i++) {
			for (int j = 0; j < board.getSize(); j++) {

				// erst alte listener entfernen
				for (ActionListener a : buttons[i][j].getActionListeners() ) {
					buttons[i][j].removeActionListener(a);
				}
				// dann neue listener drauf
				buttons[i][j].addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						for (int i = 0; i < getSize(); i++) {
							for (int j = 0; j < getSize(); j++) {
								if (e.getSource() == buttons[i][j]) {
									synchronized(buttons[i][j].getGUI()) {
										// erster klick
										if (first && buttons[i][j].getToken() != null && buttons[i][j].getToken().getColor() == board.getCurrentPlayer()) {
											Position start = new Position(i+1,j+1);
											setStart(start);
											for (Position position : board.getNeighborhood(start, buttons[i][j].getToken().getRange())) {
												buttons[position.getLetter()-1][position.getNumber()-1].select();
											}
											buttons[i][j].select2();
											setFirst(false);
										}
										// zweiter klick
										else if (!first) {
											Position end = new Position(i+1,j+1);
											setEnd(end);
											buttons[i][j].getGUI().notify();
											setFirst(true);
										}
									}
								}
							}
						}
					}	
				});
			}
		}
	}
}

/**
 *	Klasse f&uuml;r den ActionListener des Surrender Buttons.
 *	Erbt von ActionListener
 */
class SurrenderActionListener implements ActionListener {
	/** GUI als Referenz f&uuml;r notify() */
	private TowerWarsGUI gui;
	/** true: es wurde aufgegeben, false: es wurde nicht aufgegeben */
	private boolean surrendered;
	
	/**
	 *	Getter f&uuml;r boolean surrendered
	 *	@return true falls Aufgabe gew&uuml;nscht, false sonst
	 */
	public boolean hasSurrendered() {return surrendered;}
	/**
	 *	Getter f&uuml;r GUI
	 *	@return GUI
	 */
	public TowerWarsGUI getGUI() {return gui;}
	
	/**
	 *	Konstruktor
	 *	@param gui GUI
	 */
	protected SurrenderActionListener(TowerWarsGUI gui) {
		this.gui = gui;
		surrendered = false;
	}
	
	/**
	 *	actionPerformed: setzt bei dr&uuml;cken surrendered auf true und setzt
	 *	die Methode deliver() aus TowerWarsGUI in Kenntnis
	 *	@param e ActionEvent
	 */
	public void actionPerformed(ActionEvent e) {
		synchronized(gui) {
			surrendered = true;
			gui.notify();
		}		
	}
}
