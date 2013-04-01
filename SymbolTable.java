import java.util.ArrayList;


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
		for(int i=scope.lowIndex; i<scope.highIndex; i++){
			var_type v = varStack.get(i).data;
			if(v.var_name.equals(name))
				return varStack.get(i);
		}
		
		if(scope.parent==null)
			return null;
		else
			return findVar(name, scope.parent);
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
			for(int i=currentScope.lowIndex; i<currentScope.highIndex; ++i){
				var_type v = varStack.get(i).data;
				if(v.var_name.equals(s.data.var_name))
					return SymbolDiagnosis.Conflict;
			}
		}
		
		// add variable to varStack
		currentScope.highIndex++;
		varStack.add(s);
		
		//return diagnosis
		if(nameMatch)
			return SymbolDiagnosis.Shadow;
		else
			return SymbolDiagnosis.Healthy;
	}
	
	void assignVar(String varName, var_type value){
		Symbol s = findVar(varName);
		s.data.assignVal(value);
	}
	
	
}
