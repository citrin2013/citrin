import java.util.ArrayList;


//Symbol Auxilaries

//If a symbol shadows/conflicts with another symbol
enum SymbolDiagnosis { Healthy, Shadow, Conflict }

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
	public ArrayList<var_type> varStack;
	public Scope currentScope;
	public Scope globalScope;
	
	
	
	public SymbolTable(){
		varStack = new ArrayList<var_type>();
		scopeStack = new ArrayList<Scope>();
		currentScope = globalScope =  new Scope("Global", 0);
		scopeStack.add(currentScope);
	}
	
	// function to find a variable, calls private function which searches recursively
	public var_type findVar(String var_name){
		return findVar(var_name, currentScope);
	}
	
	// function which searches current scope and its parent's scope and its grandparent's scope...
	private var_type findVar(String name, Scope scope){
		for(int i=scope.lowIndex; i<scope.highIndex; i++){
			if(varStack.get(i).var_name.equals(name))
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
	public SymbolDiagnosis pushVar(var_type v){
		//check if there is a variable visible with the same name
		boolean nameMatch;
		if(findVar(v.var_name)==null)
			nameMatch = false;
		else
			nameMatch = true;
		
		// check if variable is a conflict
		if(nameMatch){
			for(int i=currentScope.lowIndex; i<currentScope.highIndex; ++i){
				if(varStack.get(i).var_name.equals(v.var_name))
					return SymbolDiagnosis.Conflict;	
			}
		}
		
		// add variable to varStack
		currentScope.highIndex++;
		varStack.add(new var_type(v));
		
		//return diagnosis
		if(nameMatch)
			return SymbolDiagnosis.Shadow;
		else
			return SymbolDiagnosis.Healthy;
	}
	
	
}
