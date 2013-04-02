// ---------------------------------------------------------------------------
// Workaround for multiple inheritance 
//
//	SymbolTableNotifier cannot subclass from both SymbolTable and Observable
//

interface CitrinObservable {
	void notifyObservers(Object arg);		
}

interface CitrinObserver {
	void update(CitrinObservable o, Object arg);
};
