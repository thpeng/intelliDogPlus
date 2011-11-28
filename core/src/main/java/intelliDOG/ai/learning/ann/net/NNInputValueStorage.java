package intelliDOG.ai.learning.ann.net;

import java.util.Map;
import java.util.HashMap;

import java.lang.reflect.Field;

import intelliDOG.ai.learning.ann.net.exceptions.*;
import intelliDOG.ai.learning.rl.StandardState;

/**
 * Speichert und validiert Eingabewerte, welche einem NN übergeben werden. Standardmässig haben alle
 * Eingabewerte den Wert Null.
 * 
 * Verwendet intern ein HashMap zum speichern der Elemente mit Integer key sowie Double value. Entsprechende
 * Methoden setzen values anhand eines übergebenen keys oder geben diese zurück.
 * 
 * Der Vorteil dieser Klasse liegt im Managment der überhaupt vorhandenen und gültigen Inputwerten eines
 * NN's und wirft eine Exception, falls ein nicht vorhandener Wert angefordert wird. Zudem garantiert diese
 * Klasse, dass das NN jederzeit Zugriff auf alle Werte hat, welche notfalls einfach mit 0 initialisiert sind.
 * 
 * @author Iseli Andreas, Mauerhofer Ralf
 * @date 25.04.2008
 */
public class NNInputValueStorage {

	//HashMap zum speichern der Werte
	private Map<Integer, Integer> inputValues;
	
	/**
	 * Standardkonstruktor.
	 * 
	 * Lädt alle NNInputTypes aus den Konstanten in eine Map.
	 */
	public NNInputValueStorage() {
		this.inputValues = new HashMap<Integer, Integer>();
		//HashMap mit allen möglichen NN Inputwerten füllen und mit 0.0 initialisieren
		Field[] fields = Constants.class.getDeclaredFields();
		try {
			for(int i=0; i<fields.length; i++) {
				//dies passiert automatisch indem alle felder mit NNInputType_ herausgeparst werden
				if(fields[i].getName().startsWith("NNInputType_")) {
					this.inputValues.put(fields[i].getInt(fields[i]), 0);
				}
			}
		} catch (IllegalAccessException e) {}
	}
	
	public NNInputValueStorage(StandardState s){
		this.inputValues = new HashMap<Integer, Integer>();
		
		int[] input = s.getInputVector();
		
		this.inputValues.put(Constants.NNInputType_POS00ACTUALBOARD, input[0]);
		this.inputValues.put(Constants.NNInputType_POS01ACTUALBOARD, input[1]);
		this.inputValues.put(Constants.NNInputType_POS02ACTUALBOARD, input[2]);
		this.inputValues.put(Constants.NNInputType_POS03ACTUALBOARD, input[3]);
		this.inputValues.put(Constants.NNInputType_POS04ACTUALBOARD, input[4]);
		this.inputValues.put(Constants.NNInputType_POS05ACTUALBOARD, input[5]);
		this.inputValues.put(Constants.NNInputType_POS06ACTUALBOARD, input[6]);
		this.inputValues.put(Constants.NNInputType_POS07ACTUALBOARD, input[7]);
		this.inputValues.put(Constants.NNInputType_POS08ACTUALBOARD, input[8]);
		this.inputValues.put(Constants.NNInputType_POS09ACTUALBOARD, input[9]);
		this.inputValues.put(Constants.NNInputType_POS10ACTUALBOARD, input[10]);
		this.inputValues.put(Constants.NNInputType_POS11ACTUALBOARD, input[11]);
		this.inputValues.put(Constants.NNInputType_POS12ACTUALBOARD, input[12]);
		this.inputValues.put(Constants.NNInputType_POS13ACTUALBOARD, input[13]);
		this.inputValues.put(Constants.NNInputType_POS14ACTUALBOARD, input[14]);
		this.inputValues.put(Constants.NNInputType_POS15ACTUALBOARD, input[15]);
		this.inputValues.put(Constants.NNInputType_POS16ACTUALBOARD, input[16]);
		this.inputValues.put(Constants.NNInputType_POS17ACTUALBOARD, input[17]);
		this.inputValues.put(Constants.NNInputType_POS18ACTUALBOARD, input[18]);
		this.inputValues.put(Constants.NNInputType_POS19ACTUALBOARD, input[19]);
		this.inputValues.put(Constants.NNInputType_POS20ACTUALBOARD, input[20]);
		this.inputValues.put(Constants.NNInputType_POS21ACTUALBOARD, input[21]);
		this.inputValues.put(Constants.NNInputType_POS22ACTUALBOARD, input[22]);
		this.inputValues.put(Constants.NNInputType_POS23ACTUALBOARD, input[23]);
		this.inputValues.put(Constants.NNInputType_POS24ACTUALBOARD, input[24]);
		this.inputValues.put(Constants.NNInputType_POS25ACTUALBOARD, input[25]);
		this.inputValues.put(Constants.NNInputType_POS26ACTUALBOARD, input[26]);
		this.inputValues.put(Constants.NNInputType_POS27ACTUALBOARD, input[27]);
		this.inputValues.put(Constants.NNInputType_POS28ACTUALBOARD, input[28]);
		this.inputValues.put(Constants.NNInputType_POS29ACTUALBOARD, input[29]);
		this.inputValues.put(Constants.NNInputType_POS30ACTUALBOARD, input[30]);
		this.inputValues.put(Constants.NNInputType_POS31ACTUALBOARD, input[31]);
		this.inputValues.put(Constants.NNInputType_POS32ACTUALBOARD, input[32]);
		this.inputValues.put(Constants.NNInputType_POS33ACTUALBOARD, input[33]);
		this.inputValues.put(Constants.NNInputType_POS34ACTUALBOARD, input[34]);
		this.inputValues.put(Constants.NNInputType_POS35ACTUALBOARD, input[35]);
		this.inputValues.put(Constants.NNInputType_POS36ACTUALBOARD, input[36]);
		this.inputValues.put(Constants.NNInputType_POS37ACTUALBOARD, input[37]);
		this.inputValues.put(Constants.NNInputType_POS38ACTUALBOARD, input[38]);
		this.inputValues.put(Constants.NNInputType_POS39ACTUALBOARD, input[39]);
		this.inputValues.put(Constants.NNInputType_POS40ACTUALBOARD, input[40]);
		this.inputValues.put(Constants.NNInputType_POS41ACTUALBOARD, input[41]);
		this.inputValues.put(Constants.NNInputType_POS42ACTUALBOARD, input[42]);
		this.inputValues.put(Constants.NNInputType_POS43ACTUALBOARD, input[43]);
		this.inputValues.put(Constants.NNInputType_POS44ACTUALBOARD, input[44]);
		this.inputValues.put(Constants.NNInputType_POS45ACTUALBOARD, input[45]);
		this.inputValues.put(Constants.NNInputType_POS46ACTUALBOARD, input[46]);
		this.inputValues.put(Constants.NNInputType_POS47ACTUALBOARD, input[47]);
		this.inputValues.put(Constants.NNInputType_POS48ACTUALBOARD, input[48]);
		this.inputValues.put(Constants.NNInputType_POS49ACTUALBOARD, input[49]);
		this.inputValues.put(Constants.NNInputType_POS50ACTUALBOARD, input[50]);
		this.inputValues.put(Constants.NNInputType_POS51ACTUALBOARD, input[51]);
		this.inputValues.put(Constants.NNInputType_POS52ACTUALBOARD, input[52]);
		this.inputValues.put(Constants.NNInputType_POS53ACTUALBOARD, input[53]);
		this.inputValues.put(Constants.NNInputType_POS54ACTUALBOARD, input[54]);
		this.inputValues.put(Constants.NNInputType_POS55ACTUALBOARD, input[55]);
		this.inputValues.put(Constants.NNInputType_POS56ACTUALBOARD, input[56]);
		this.inputValues.put(Constants.NNInputType_POS57ACTUALBOARD, input[57]);
		this.inputValues.put(Constants.NNInputType_POS58ACTUALBOARD, input[58]);
		this.inputValues.put(Constants.NNInputType_POS59ACTUALBOARD, input[59]);
		this.inputValues.put(Constants.NNInputType_POS60ACTUALBOARD, input[60]);
		this.inputValues.put(Constants.NNInputType_POS61ACTUALBOARD, input[61]);
		this.inputValues.put(Constants.NNInputType_POS62ACTUALBOARD, input[62]);
		this.inputValues.put(Constants.NNInputType_POS63ACTUALBOARD, input[63]);
		this.inputValues.put(Constants.NNInputType_POS64ACTUALBOARD, input[64]);
		this.inputValues.put(Constants.NNInputType_POS65ACTUALBOARD, input[65]);
		this.inputValues.put(Constants.NNInputType_POS66ACTUALBOARD, input[66]);
		this.inputValues.put(Constants.NNInputType_POS67ACTUALBOARD, input[67]);
		this.inputValues.put(Constants.NNInputType_POS68ACTUALBOARD, input[68]);
		this.inputValues.put(Constants.NNInputType_POS69ACTUALBOARD, input[69]);
		this.inputValues.put(Constants.NNInputType_POS70ACTUALBOARD, input[70]);
		this.inputValues.put(Constants.NNInputType_POS71ACTUALBOARD, input[71]);
		this.inputValues.put(Constants.NNInputType_POS72ACTUALBOARD, input[72]);
		this.inputValues.put(Constants.NNInputType_POS73ACTUALBOARD, input[73]);
		this.inputValues.put(Constants.NNInputType_POS74ACTUALBOARD, input[74]);
		this.inputValues.put(Constants.NNInputType_POS75ACTUALBOARD, input[75]);
		this.inputValues.put(Constants.NNInputType_POS76ACTUALBOARD, input[76]);
		this.inputValues.put(Constants.NNInputType_POS77ACTUALBOARD, input[77]);
		this.inputValues.put(Constants.NNInputType_POS78ACTUALBOARD, input[78]);
		this.inputValues.put(Constants.NNInputType_POS79ACTUALBOARD, input[79]);
		
		//cards
		this.inputValues.put(Constants.NNInputType_Card01OWN, input[80]);
		this.inputValues.put(Constants.NNInputType_Card02OWN, input[81]);
		this.inputValues.put(Constants.NNInputType_Card03OWN, input[82]);
		this.inputValues.put(Constants.NNInputType_Card04OWN, input[83]);
		this.inputValues.put(Constants.NNInputType_Card05OWN, input[84]);
		this.inputValues.put(Constants.NNInputType_Card06OWN, input[85]);
		
		//player
		this.inputValues.put(Constants.NNInputType_PlayerToValidate, input[86]);
		
		

		
	}
	
//	/**
//	 * Normalerweise verwendeter Konstruktor, welcher anhand der übergebenen Spielbretter (vor dem Zug sowie
//	 * nach dem Zug) die InputValues direkt extrahiert und fix speichert. Dadurch gewinnt man einen enormen
//	 * Geschnwindigkeitsvorteil.
//	 * 
//	 * @param befBoard  Spielbrett vor dem Zug
//	 * @param actBoard	Spielbrett nach dem Zug
//	 */
//	public NNInputValueStorage(int[] befBoard, int[]actBoard) {
//		this.inputValues = new HashMap<Integer, Integer>();
//		/*
//		 * Es benoetigt zwar etwas mehr Code, jedoch ist es effizienter und schneller alle Werte direkt zu referenzieren als
//		 * dynamisch über ein reflect Field zu holen; Bsp mit reflect:
//		 * 		Integer pos = new Integer(2);
//		 * 		Field actField = Constants.class.getDeclaredField("NNInputType_POS0" + pos.toString() + "ACTUALBOARD");
//		 * 		int fieldValue = actField.getInt(actField);
//		 */
//		
//		//Alle Spielpositionen des Spielbretts vor ausfuehren des Zuges
//		this.inputValues.put(Constants.NNInputType_POS00BEFOREBOARD, befBoard[0]);
//		this.inputValues.put(Constants.NNInputType_POS01BEFOREBOARD, befBoard[1]);
//		this.inputValues.put(Constants.NNInputType_POS02BEFOREBOARD, befBoard[2]);
//		this.inputValues.put(Constants.NNInputType_POS03BEFOREBOARD, befBoard[3]);
//		this.inputValues.put(Constants.NNInputType_POS04BEFOREBOARD, befBoard[4]);
//		this.inputValues.put(Constants.NNInputType_POS05BEFOREBOARD, befBoard[5]);
//		this.inputValues.put(Constants.NNInputType_POS06BEFOREBOARD, befBoard[6]);
//		this.inputValues.put(Constants.NNInputType_POS07BEFOREBOARD, befBoard[7]);
//		this.inputValues.put(Constants.NNInputType_POS08BEFOREBOARD, befBoard[8]);
//		this.inputValues.put(Constants.NNInputType_POS09BEFOREBOARD, befBoard[9]);
//		this.inputValues.put(Constants.NNInputType_POS10BEFOREBOARD, befBoard[10]);
//		this.inputValues.put(Constants.NNInputType_POS11BEFOREBOARD, befBoard[11]);
//		this.inputValues.put(Constants.NNInputType_POS12BEFOREBOARD, befBoard[12]);
//		this.inputValues.put(Constants.NNInputType_POS13BEFOREBOARD, befBoard[13]);
//		this.inputValues.put(Constants.NNInputType_POS14BEFOREBOARD, befBoard[14]);
//		this.inputValues.put(Constants.NNInputType_POS15BEFOREBOARD, befBoard[15]);
//		this.inputValues.put(Constants.NNInputType_POS16BEFOREBOARD, befBoard[16]);
//		this.inputValues.put(Constants.NNInputType_POS17BEFOREBOARD, befBoard[17]);
//		this.inputValues.put(Constants.NNInputType_POS18BEFOREBOARD, befBoard[18]);
//		this.inputValues.put(Constants.NNInputType_POS19BEFOREBOARD, befBoard[19]);
//		this.inputValues.put(Constants.NNInputType_POS20BEFOREBOARD, befBoard[20]);
//		this.inputValues.put(Constants.NNInputType_POS21BEFOREBOARD, befBoard[21]);
//		this.inputValues.put(Constants.NNInputType_POS22BEFOREBOARD, befBoard[22]);
//		this.inputValues.put(Constants.NNInputType_POS23BEFOREBOARD, befBoard[23]);
//		
//		//Alle Spielpositionen des Spielbretts nach ausfuehren des Zuges
//		this.inputValues.put(Constants.NNInputType_POS00ACTUALBOARD, actBoard[0]);
//		this.inputValues.put(Constants.NNInputType_POS01ACTUALBOARD, actBoard[1]);
//		this.inputValues.put(Constants.NNInputType_POS02ACTUALBOARD, actBoard[2]);
//		this.inputValues.put(Constants.NNInputType_POS03ACTUALBOARD, actBoard[3]);
//		this.inputValues.put(Constants.NNInputType_POS04ACTUALBOARD, actBoard[4]);
//		this.inputValues.put(Constants.NNInputType_POS05ACTUALBOARD, actBoard[5]);
//		this.inputValues.put(Constants.NNInputType_POS06ACTUALBOARD, actBoard[6]);
//		this.inputValues.put(Constants.NNInputType_POS07ACTUALBOARD, actBoard[7]);
//		this.inputValues.put(Constants.NNInputType_POS08ACTUALBOARD, actBoard[8]);
//		this.inputValues.put(Constants.NNInputType_POS09ACTUALBOARD, actBoard[9]);
//		this.inputValues.put(Constants.NNInputType_POS10ACTUALBOARD, actBoard[10]);
//		this.inputValues.put(Constants.NNInputType_POS11ACTUALBOARD, actBoard[11]);
//		this.inputValues.put(Constants.NNInputType_POS12ACTUALBOARD, actBoard[12]);
//		this.inputValues.put(Constants.NNInputType_POS13ACTUALBOARD, actBoard[13]);
//		this.inputValues.put(Constants.NNInputType_POS14ACTUALBOARD, actBoard[14]);
//		this.inputValues.put(Constants.NNInputType_POS15ACTUALBOARD, actBoard[15]);
//		this.inputValues.put(Constants.NNInputType_POS16ACTUALBOARD, actBoard[16]);
//		this.inputValues.put(Constants.NNInputType_POS17ACTUALBOARD, actBoard[17]);
//		this.inputValues.put(Constants.NNInputType_POS18ACTUALBOARD, actBoard[18]);
//		this.inputValues.put(Constants.NNInputType_POS19ACTUALBOARD, actBoard[19]);
//		this.inputValues.put(Constants.NNInputType_POS20ACTUALBOARD, actBoard[20]);
//		this.inputValues.put(Constants.NNInputType_POS21ACTUALBOARD, actBoard[21]);
//		this.inputValues.put(Constants.NNInputType_POS22ACTUALBOARD, actBoard[22]);
//		this.inputValues.put(Constants.NNInputType_POS23ACTUALBOARD, actBoard[23]);
//		/*
//	    //Anzahl Spielsteine
//		this.inputValues.put(Constants.NNInputType_NUMBEROFWHITESTONES, this.myHelpers.countStones(actBoard, Constants.WHITE));
//		this.inputValues.put(Constants.NNInputType_NUMBEROFBLACKSTONES, this.myHelpers.countStones(actBoard, Constants.BLACK));
//	    //Anzahl geschlossenen Muehlen
//	    this.inputValues.put(Constants.NNInputType_NUMBEROFCLOSEDMILLESWHITE, this.myHelpers.getCountClosedMills(actBoard, Constants.WHITE));
//	    this.inputValues.put(Constants.NNInputType_NUMBEROFCLOSEDMILLESBLACK, this.myHelpers.getCountClosedMills(actBoard, Constants.BLACK));
//	    //Anzahl offenen Muehlen
//	    this.inputValues.put(Constants.NNInputType_NUMBEROFOPENMILLESWHITE, this.myHelpers.getCountOpenMills(actBoard, Constants.WHITE));
//	    this.inputValues.put(Constants.NNInputType_NUMBEROFOPENMILLESBLACK, this.myHelpers.getCountOpenMills(actBoard, Constants.BLACK));
//	    //Anzahl moegliche Zuege
//	    this.inputValues.put(Constants.NNInputType_NUMBEROFPOSMOVESWHITE, this.myHelpers.getCountPossibleMoves(actBoard, Constants.WHITE));
//	    this.inputValues.put(Constants.NNInputType_NUMBEROFPOSMOVESBLACK, this.myHelpers.getCountPossibleMoves(actBoard, Constants.BLACK));
//	    //Anzahl bewegbare Stein
//	    this.inputValues.put(Constants.NNInputType_NUMBEROFMOVABLESTONESWHITE, this.myHelpers.getCountMoveableStones(actBoard, Constants.WHITE));
//	    this.inputValues.put(Constants.NNInputType_NUMBEROFMOVABLESTONESBLACK, this.myHelpers.getCountMoveableStones(actBoard, Constants.BLACK));
//		*/
//	}
	
	/**
	 * Setzt den Wert des spezifischen InputTypes.
	 * Vorsicht, Methode ist langsam!
	 * 
	 * @param key Inputtype
	 * @param value Inputvalue
	 * @throws NeuralNetStorageValueNotExistException
	 */
	public void setValueByKey(int key, int value) throws NeuralNetStorageValueNotExistException {
		if(this.isValidKey(key)) {
			//alten wert entfernen
			this.inputValues.remove(key);
			//neu hinzufügen
			this.inputValues.put(key, value);
		} else {
			throw new NeuralNetStorageValueNotExistException();
		}
	}
	
	/**
	 * Gibt den Wert des spezifischen InputTypes zurück.
	 * 
	 * @param key Inputtype
	 * @return double Inputvalue
	 * @throws NeuralNetStorageValueNotExistException
	 */
	public int getValueByKey(int key) throws NeuralNetStorageValueNotExistException {
		if(this.isValidKey(key)) {
			return this.inputValues.get(key);
		} else {
			throw new NeuralNetStorageValueNotExistException();
		}
	}
	
	/**
	 * Testet ob der übergebene Inputwert überhaupt existiert.
	 * 
	 * @param key zu Testender Inputwert
	 * @return boolean exists true / not exists false
	 */
	public boolean isValidKey(int key) {
		if(this.inputValues.containsKey(key)) 
			return true;
		else
			return false;
	}
	
	/**
	 * Setzt alle Inputwerte zurück auf den Wert Null.
	 */
	public void resetValues() {
		//Inhalt löschen und neu füllen
		Field[] fields = Constants.class.getDeclaredFields();
		try {
			for(int i=0; i<fields.length; i++) {
				//dies passiert automatisch indem alle felder mit NNInputType_ herausgeparst werden
				if(fields[i].getName().startsWith("NNInputType_")) {
					this.inputValues.remove(fields[i].getInt(fields[i]));
					this.inputValues.put(fields[i].getInt(fields[i]), 0);
				}
			}
		} catch (IllegalAccessException e) {}
	}
	
}
