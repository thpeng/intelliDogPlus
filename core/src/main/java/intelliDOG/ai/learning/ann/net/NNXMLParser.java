package intelliDOG.ai.learning.ann.net;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

import org.xml.sax.Attributes;
import org.xml.sax.XMLReader;
import org.xml.sax.InputSource;
import org.xml.sax.helpers.XMLReaderFactory;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Klasse NNXMLParser. Die Klasse liest ein erstelltes XML File aus und speichert die
 * enthaltenen Werte in Listen ab. Mittles diversen Get-Methoden können die entsprechenden
 * Werte geholt werden.
 * 
 * zu Beachten: Die Parserklasse benötigt den SAX Parser!
 * 
 * @author Iseli Andreas, Mauerhofer Ralf
 * @date 28.04.08
 */
public class NNXMLParser extends DefaultHandler {
	//Instanzvariablen
	String state = "";
	int numberOfLayers = 0;
	ArrayList<Integer> inputTypes = new ArrayList<Integer>();
	ArrayList<Integer> typeOfLayers = new ArrayList<Integer>();
	ArrayList<Integer> activationTypeOfLayers = new ArrayList<Integer>();
	ArrayList<Integer> numberOfNeurons = new ArrayList<Integer>();
	ArrayList<Double> weights = new ArrayList<Double>();
	
    /**
     * Default Konstruktor
     */
    public NNXMLParser(File file) throws Exception {
    	super();
    	//Instanzierung des XMLReaders
		XMLReader xr = XMLReaderFactory.createXMLReader();
		xr.setContentHandler(this);
		xr.setErrorHandler(this);
		//Instanzierung des Filereaders
		FileReader r = new FileReader(file);
		xr.parse(new InputSource(r));
    }
    
    /**
     * Event Handler welcher die Startelemente bearbeitet.
     */
    public void startElement (String uri, String name, String qName, Attributes atts) {
    		this.state = name;
    }
    
    /**
     * Event Handler Methode, welche bei den Inhalten von geparsten XML Files aufgrund des "states"
     * die Werte dieser Inhalte in die zum "State" gehörenden Variable speichert.
	 * 
	 * @param ch Array mit den geparsden Characters
	 * @param start Erstes Element
	 * @param length Länge des Arrays
     */
    public void characters (char ch[], int start, int length) {
    	// die Werte werden geparst und zwischengespeichert
    	String temp = "";
    	for (int i = start; i < start + length; i++) {
    		switch (ch[i]) {
    		case '\\':
    			break;
    		case '"':
    			break;
    		case '\n':
    			break;
		    case '\r':
				break;
		    case '\t':
				break;
		    default:
		    	temp = temp + ch[i];
				break;
		    }
    	}
    	// geparste Werte werden in die entsprechenden Variablen/Listen gespeichert
    	if (temp != "") {
    		if (this.state.equals("numberOfLayers")) {
    			this.numberOfLayers = Integer.parseInt(temp);
    		} else if (this.state.equals("inputType")) {
    			this.inputTypes.add(Integer.parseInt(temp));
    		} else if (this.state.equals("layerType")) {
    			this.typeOfLayers.add(Integer.parseInt(temp));
    		} else if (this.state.equals("activationType")) {
    			this.activationTypeOfLayers.add(Integer.parseInt(temp));
    		} else if (this.state.equals("numberOfNeurons")) {
    			this.numberOfNeurons.add(Integer.parseInt(temp));
    		} else if (this.state.equals("weight")) {
    			this.weights.add(Double.parseDouble(temp));
    		}
    	}
    }
    
    /**
     * Methode gibt die ausgelesene Anzahl von Layers zurück.
     * 
     * @return int numberOfLayers
     */
    public int getNumberOfLayers() {
		return numberOfLayers;
    }
    
    /**
     * Methode gibt Liste mit den ausgelesenen Inputtypen zurück.
     * 
     * @return ArrayList<Integer> inputTypes
     */
	public ArrayList<Integer> getInputTypes() {
		return inputTypes;
	}
	
    /**
     * Methode gibt Liste mit den ausgelesenen Layertypen zurück.
     * 
     * @return ArrayList<Integer> typeOfLayers
     */
	public ArrayList<Integer> getTypeOfLayers() {
		return typeOfLayers;
	}
	
    /**
     * Methode gibt Liste mit den ausgelesenen Layeraktivierungstypen zurück.
     * 
     * @return ArrayList<Integer> activationTypeOfLayers
     */
	public ArrayList<Integer> getActivationTypeOfLayers() {
		return activationTypeOfLayers;
	}
	
    /**
     * Methode gibt Liste mit der ausgelesenen Anzahl der Neuronen pro Layer zurück.
     * 
     * @return ArrayList<Integer> numberOfNeurons
     */
	public ArrayList<Integer> getNumberOfNeurons() {
		return numberOfNeurons;
	}
	
    /**
     * Methode gibt Liste mit den ausgelesenen Gewichtungen aller Synapsen zurück.
     * 
     * @return ArrayList<Double> weights
     */
	public ArrayList<Double> getWeights() {
		return weights;
	}
}