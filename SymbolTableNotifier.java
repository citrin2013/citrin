//
// Overview 
//
//	A little wrapper class that notifies GUI component to reflect changes as
//	symbol table is changed. By adding an observer to an instance of this
//	class lets the observer notified automatically on any change of this
//	symbol table with appropriate event flag through Observer::update().
//
// Purpose of This Class
//
//	Extending SymbolTable lets SymbolTable be as it is without having logic to
//	interact with anything like GUI components. 
//
// Future Extension
//
//	Being an observable can notify other GUI components like Console.
//	But, we cannot do multiple inheritance with Java. Need to make a mixin.
//
// TODO 
//
//	Symbol objects need to convert itself to some form GUI components
//	understand. Probably, List type.
//
//  Think about Observable Observer classes offered by java.util.
//  At least the interfaces since multiple inheritance is not allowed in Java.
//
//  Add Manifest
//  
//	Add to overview how this works
//

import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;


enum SymbolTableEvent {
	scopePushedInParallel,
	scopePushedAsChild,
	scopePopped,
	symbolInserted,
	symbolAssignedNewValue,
	cleared
};

// ---------------------------------------------------------------------------

class SymbolTableNotifier extends SymbolTable implements CitrinObservable {

	private Symbol insertedSymbol = null;
	private SymbolDiagnosis insertedSymbolDiagnosis = null;
	private Symbol assignedSymbol = null;
	private String assignedSymbolName = null;
	private List<CitrinObserver> observers = new ArrayList<CitrinObserver>();
	// private ArrayList<CitrinObserver> observers = new ArrayList<CitrinObserver>();

	// -----------------------------------------------------------------------
	// Constructors

	public SymbolTableNotifier()
	{
		super();
	}

	// -----------------------------------------------------------------------
	// Functions For GUI Components To Pull Out Data From Symbol Table
	
	public Symbol getInsertedSymbol()
	{
		return insertedSymbol;
	}

	public SymbolDiagnosis getInsertedSymbolDiagnosis()
	{
		return insertedSymbolDiagnosis;
	}

	public Symbol getAssignedSymbol()
	{
		return assignedSymbol;
	}

	public String getAssignedSymbolName()
	{
		return assignedSymbolName;
	}

	// -----------------------------------------------------------------------
	// CitrinObserver Pattern Methods (Observable Side)
	//
	
	public void notifyObservers(Object arg)
	{ // Java Observable uses setChanged() and stuff
		Iterator<CitrinObserver> i = observers.iterator();
		CitrinObserver o;
		while (i.hasNext()) {
			o = (CitrinObserver) i.next();
			if (o != null ) // TODO : ugly
				o.update(this, arg); // HERE this requires Observable,Object by Observer::update
		}
	}

	public void addObserver(CitrinObserver o)
	{
		observers.add(o);
	}

	public void removeObserver(CitrinObserver o)
	{
		System.out.println("todo removeObserver()");
	}

	// -----------------------------------------------------------------------
	// Symbol Table Functions That Need to Notify GUI components

	public void pushFuncScope(String funcName){
		super.pushFuncScope(funcName);
		SymbolTableEvent event;
		notifyObservers(SymbolTableEvent.scopePushedInParallel);
	}

	public void pushLocalScope(){
		super.pushLocalScope();
		notifyObservers(SymbolTableEvent.scopePushedAsChild);
	}

	public void popScope(){
		super.popScope();
		notifyObservers(SymbolTableEvent.scopePopped);
	}
	
	public void clear(){
		super.clear();
		notifyObservers(SymbolTableEvent.cleared);
		
	}

	public SymbolDiagnosis pushSymbol(Symbol s){
		insertedSymbol = s;
		insertedSymbolDiagnosis = super.pushSymbol(s);
		
		// TODO : what should be the behavior of conflicted symbol ? The
		// interpreter catches it ?
		//
		// notify SymbolTable if no conflict happens (should be compilation error)
		notifyObservers( SymbolTableEvent.symbolInserted );

		return insertedSymbolDiagnosis;
	}

	Symbol assignVar(String varName, var_type value){
		assignedSymbol = super.assignVar(varName, value);
		assignedSymbolName = varName;
		notifyObservers( SymbolTableEvent.symbolAssignedNewValue );
		return assignedSymbol;
	}
	
	// function to notify when a var is updated
	void updatedVar(int address){
		assignedSymbol = varStack.get(address);
		assignedSymbolName = varStack.get(address).data.var_name;
		notifyObservers( SymbolTableEvent.symbolAssignedNewValue );
	}

}

