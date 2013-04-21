import javax.swing.*;
import javax.swing.text.*;

public class Console extends JTextArea implements CitrinObserver {

	Document doc; 

	public Console() { 
		super(); 
		this.setFont(UIManager.getDefaults().getFont("TextField.font"));
	}
	

	public void update(CitrinObservable o, Object arg)
	{
		/*
		if ( o instanceof Interpreter ) {
			Interpter interpreter = (Interreter) o;
			if (arg == InterpterEvent.InterpretationReady) {
				Interpter.interpretation	
				for ( )	{
				}
			}
		}
		*/
	}

	public Console(Document doc) { 
		this.doc = doc;
	}

}
