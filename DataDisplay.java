
//
// This class is like an observer in Observer Pattern
//
//TODO 
//
//	make this implement CitrinObservable
//
//	make overview 
//
//	Integrate this to CITRIN
//

import javax.swing.JTable;
import java.util.Observer;

public class DataDisplay extends JTable implements CitrinObserver { // implements Observer {
	//
	// two options for notify() functiosn 
	// If this DataDisplay holds SymbolTable, and SymbolTable offers public
	// functions, that lets this DataDisplay pull out data, then no need to
	// have Objct data argument.
	//
	// Should be called from Symbol Table
	//
	
	public void update(CitrinObservable notifier, Object arg) 
	{
		if ( notifier instanceof SymbolTable ) {
			//
			// look up in arg and do updte according to what notifier is
			//
			// SymbolTable should have a public method from which this class
			// can pull out data for display
			//
			// arg would be an event like 
			//	new symbol inserted ( with SymbolDiagnosis )
			//	existing symbol assigned new value
			//	scope popped
			//	scope pushed (as a child of current scope. ie, naked block or global scope) 
			//	scope pushed 2 (as a child of global scope. ie, function scope) 
			// 
			;
		}
		else if ( notifier instanceof Interpreter ) {
			// look up in arg and do something accordingly 
			;
		}
		else {
			;
		}
	}

	// private function to reflect changes on the GUI component 
	private void updateDisplay()
	{
	}


}
