import java.util.ArrayList;
import java.util.HashMap;


//Symbol Auxilaries

//If a symbol shadows/conflicts with another symbol
enum SymbolDiagnosis { Healthy, Shadow, Conflict }

//location in the source code
class SymbolLocation{
	
	SymbolLocation(int lnum, int cnum){
		this.lnum = lnum;
		this.cnum = cnum;
	}
	
	int lnum; // line number
	int cnum; // column number
	
}

//Symbol
class Symbol{

	Symbol(SymbolLocation location, var_type data){
		this.location = location;
		this.data = data;
	}

	SymbolLocation getLocation()
	{
		return location;
	}
	
	String getDataAsString()
	{
		System.out.println("implement Symbol::getDataAsString()");
		return data.toString();	
	}
	
	var_type getData()
	{
		return data;
	}
	
	SymbolLocation location;
	var_type data;
}


class Scope{
	Scope(String scopeName, int tableIndex)
	{
		this.parent = null;
		this.name=scopeName;
		lowIndex = highIndex = tableIndex;
	}
	
	Scope(Scope parent, String scopeName, int tableIndex)
	{
		this.parent = parent;
		name=scopeName;
		lowIndex = highIndex = tableIndex;
	}
	
	
	HashMap<String, Symbol> symbols = new HashMap<String, Symbol>();
	String name;
	Scope parent;
	int lowIndex;
	int highIndex;
}

public class SymbolTable {

	public ArrayList<Scope> scopeStack;
	public ArrayList<Symbol> varStack;
	public Scope currentScope;
	public Scope globalScope;
	
	
	public SymbolTable(){
		varStack = new ArrayList<Symbol>();
		scopeStack = new ArrayList<Scope>();
		currentScope = globalScope =  new Scope("Global", 0);
		scopeStack.add(currentScope);
	}
	
	// function to find a variable, calls private function which searches recursively
	public Symbol findVar(String var_name){
		return findVar(var_name, currentScope);
	}
	
	// function which searches current scope and its parent's scope and its grandparent's scope...
	private Symbol findVar(String name, Scope scope){
		
		if(scope.symbols.containsKey(name)){
			return scope.symbols.get(name);
		}
		if(scope.parent==null)
			return null;
		else
			return findVar(name, scope.parent);
	}
	
	//function to get a var at an address
	public Symbol getVar(int address){
		return varStack.get(address);
	}
	
	// push a new scope with the parent set as the global scope
	public void pushFuncScope(String funcName){
		currentScope = new Scope(globalScope, funcName, varStack.size());
		scopeStack.add(currentScope);
	}

	// push a new scope with the parent as the current scope
	public void pushLocalScope(){
		currentScope = new Scope(currentScope, null, varStack.size());
		scopeStack.add(currentScope);
	}
	
	// pops the top element off the scope stack, removes all of its vars form the stack
	public void popScope(){
		for(int i=currentScope.highIndex-1; i>=currentScope.lowIndex; --i){
			varStack.remove(i);
		}
		scopeStack.remove(scopeStack.size()-1);
		currentScope = scopeStack.get( scopeStack.size()-1 );
	}
	
	// pops the top element off the scope stack, removes all of its vars form the stack
	public void clear(){
		while(scopeStack.size() > 1 ){
			for(int i=currentScope.highIndex-1; i>=currentScope.lowIndex; --i){
				varStack.remove(i);
			}
			scopeStack.remove(scopeStack.size()-1);
			currentScope = scopeStack.get( scopeStack.size()-1 );
		}
		for(int i=currentScope.highIndex-1; i>=currentScope.lowIndex; --i){
			varStack.remove(i);
		}
		currentScope.symbols = new HashMap<String, Symbol>();
	}
	
	
	// pushes a variable onto the stack, checks for variables with the same name
	// returns either healthy, shadow or conflict
	public SymbolDiagnosis pushSymbol(Symbol s){
		//check if there is a variable visible with the same name
		boolean nameMatch;
		if(findVar(s.data.var_name)==null)
			nameMatch = false;
		else
			nameMatch = true;
		
		// check if variable is a conflict
		if(nameMatch){
			if(currentScope.symbols.containsKey(s.data.var_name))
				return SymbolDiagnosis.Conflict;
		}
		
		// add variable to varStack
		s.data.address = currentScope.highIndex;
		currentScope.highIndex++;
		varStack.add(s);
		currentScope.symbols.put(s.data.var_name, s);
		
		//return diagnosis
		if(nameMatch)
			return SymbolDiagnosis.Shadow;
		else
			return SymbolDiagnosis.Healthy;
	}
	
	// pushes a variable onto the stack, checks for variables with the same name
	// returns either healthy, shadow or conflict
	public SymbolDiagnosis pushArray(Symbol arrSymbol, ArrayList<Symbol> arrayData){
		//check if there is a variable visible with the same name
		boolean nameMatch;
		if(findVar(arrSymbol.data.var_name)==null)
			nameMatch = false;
		else
			nameMatch = true;
		
		// check if variable is a conflict
		if(nameMatch){
			if(currentScope.symbols.containsKey(arrSymbol.data.var_name))
				return SymbolDiagnosis.Conflict;
		}
		
		// add arrSymbol to map only
		arrSymbol.data.address = currentScope.highIndex;
		arrSymbol.data.value = currentScope.highIndex;
		currentScope.symbols.put(arrSymbol.data.var_name, arrSymbol);
		
		for(int i=0;i<arrayData.size();i++){
			currentScope.highIndex++;
			varStack.add(arrayData.get(i));
			arrayData.get(i).data.address = currentScope.highIndex-1;
		}

		//return diagnosis
		if(nameMatch)
			return SymbolDiagnosis.Shadow;
		else
			return SymbolDiagnosis.Healthy;
	}
	
	Symbol assignVar(String varName, var_type value){
		Symbol s = findVar(varName);
		try {
			s.data.assignVal(value);
		} catch (SyntaxError e) {
			// TODO: this shouldnt happen
			e.printStackTrace();
		}
		return s;
	}
	
	
}
