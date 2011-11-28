package intelliDOG.ai.learning.ann.net;

/**
 * Mini-Interface um die Möglichkeit zu geben, dass man sich an den Trainer hängen und die
 * Trainigsnachrichten (NetError nach jeder Epoche) abfangen und ausgeben kann.
 * 
 * @author Iseli Andreas, Mauerhofer Ralf
 * @date 08.06.2008
 */
public interface ITeacherListener {
	
	/** Gibt dem Listener eine Trainingsnachricht zum ausgeben */
	public void notifyMessage(String msg);

}
