import java.util.Vector;
import java.io.PrintStream;
import java.util.*;

// ---------------------------------------------------------------------------
// Overview
//
//		Variables within a program are often sorted out by scope. Each scope
//		limits a visibility within a program. The class SymbolTable in this
//		package offers a control to create such scopes. 
//
//		SymbolTable consists of Scopes 
//
//		Scope consists of 
//			
//			Symbols in the scope within HashMap of <String, Symbol>
//
//			1 parent (Scope)
//
//			0..* childrens (Scopes)
//
//			This is like a tree structure. The visibility of a scope is the
//			symbols within itself and symbols in its parent scope, grandparent
//			scope, and so on.
//			
//
//		Symbol consists of 
//
//			SymbolLocation 
//
//				location in C++ source code (line number and column number)
//
//			SymbolData
//
//				data for the symbol such as int,bool,...
//			
//
//	See Also 
//
//		SymbolTableDriver
//

// ---------------------------------------------------------------------------
// Manifest
//
//		Symbol Name/Key = String
//
//		Symbol 
//
//			SymbolData : abstract
//
//			SymbolLocation
//
//		Scope
//
//		SymbolTable
//

// ---------------------------------------------------------------------------
// Known Bugs
//
// [ ] insertSymbol() does not return SymbolDiagnosis.Conflict properly
//

// ---------------------------------------------------------------------------
// TODO
//
//	[ ] Name SymbolLocation just Location ?
//	
//	[ ] level of symbol
//
//	[ ] symbol diagnosis
//
//	[ ] SymbolDiagnosis diagnozeSymbol(SymbolData sym);
//
//	[ ] SymbolDiagnosis insertSymbol(String key, SymbolLocation loc, SymbolData data); 
//
//	[ ] dumpVisibleScope()
//
//	[ ] typecheck on assignSymbol()
//
//	[ ] make assignSymbol this throw exception
//
//	[ ] In which class should SymbolLocation be contained ?
//
//	[ ] accomodate user defined types 
//
//		class and struct
//		
//	[ ] accomodate all the c++ predefined types including long and other weird
//	    stuff
//
//	[ ] accoomidate composite types 
//
//		array
//	
//  [ ] contain address to achive reference
//
//	[ ] rvalue and lvalue
//
//	[ ] abstract Scope<T> for 
//
//		Scope<ClassOrStruct> 
//
//		Scope<SymbolData> : current one
//
//	[ ] Do not pop on popScope() but redirect currentScope to the parent of the
//	currentScope so that the symbol table can preserve static variable and
//	stuff.
//
//	[ ] make typestack
//
//	[ ] Add diagnosis to the dumpTale() result
//

// ---------------------------------------------------------------------------
// Symbol Auxilaries

// If a symbol shadows/conflicts with another symbol
enum SymbolDiagnosis { Healthy, Shadow, Conflict }

// Location in the source code
class SymbolLocation {

	SymbolLocation(int lnum, int cnum)
	{
		this.lnum = lnum;
		this.cnum = cnum;
	}

	int lnum; // line number
	int cnum; // column number
}

// ---------------------------------------------------------------------------
// SymbolData

abstract class SymbolData {

	enum TypeTag { 
		// builtins
		Bool,
		Char,
		Short,
		Int,
		Double,
		Float, 
		// indirection
		Reference,
		Pointer,
		// user defined types
		Class,
		Struct
	}

	SymbolData() 
	{
	}

	public void print(PrintStream ps)
	{
		ps.print("SymbolData::print()");
	}
	
	// abstract boolean equalByType(SymbolData rhs);

	// abstract boolean equalByValue(SymbolData rhs);

	// abstract boolean equalByAddress(SymbolData rhs); 
	
	// static typeAsString();
	
	// valueAsString();

	// private boolean staticType;
	// private TypeTag tag;
}

class DebugSymbol extends SymbolData {
	DebugSymbol(String debug) 
	{
		this.debug = debug;
	}

	public void print(PrintStream ps)
	{
		ps.print(debug);
	}

	String debug;
}

class Bool extends SymbolData {
	Bool(boolean value)
	{
		this.value = value;
	}

	public void print(PrintStream ps)
	{
		ps.print(value);
	}
	
	// private TypeTag tag = TypeTag.Bool;
	private boolean value;
}

class Char extends SymbolData {
	Char(char value)
	{
		this.value = value;
	}

	public void print(PrintStream ps)
	{
		ps.print(value);
	}

	// private TypeTag tag = TypeTag.Char;
	private char value;
}

class Short extends SymbolData {
	Short(short value)
	{
		this.value = value;
	}

	public void print(PrintStream ps)
	{
		ps.print(value);
	}

	// private TypeTag tag = TypeTag.Short;
	private short value;
}

class Int extends SymbolData {
	Int (int value)
	{
		this.value = value;
	}

	public void print(PrintStream ps)
	{
		ps.print(value);
	}

	// private TypeTag tag = TypeTag.Int;
	private int value;
}

class Float extends SymbolData {
	Float(float value)
	{
		this.value = value;
	}
	public void print(PrintStream ps)
	{
		ps.print(value);
	}

	// private TypeTag tag = TypeTag.Float;
	private float value;
}

class Double extends SymbolData {
	Double(double value)
	{
		this.value = value;
	}
	public void print(PrintStream ps)
	{
		ps.print(value);
	}

	// private TypeTag tag = TypeTag.Double;
	private double value;
}

class Pointer extends SymbolData {
	Pointer(SymbolData pointee) 
	{
		this.pointee = pointee;
	}
	public void print(PrintStream ps)
	{
		ps.print(pointee);
	}

	// private TypeTag tag = TypeTag.Pointer;
	private SymbolData pointee;
}

class ClassOrStruct extends SymbolData {
	ClassOrStruct(Vector<SymbolData> fields)
	{
		this.fields = fields;
	}
	
	public void print(PrintStream ps)
	{
		ps.print(fields);
	}

	// private TypeTag tag = TypeTag.Class;
	Vector<SymbolData> fields;
}

class Reference extends SymbolData {
	Reference(SymbolData referee)
	{
		this.referee = referee;
	}

	public void print(PrintStream ps)
	{
		ps.print(referee);
	}

	// private TypeTag tag = TypeTag.Reference;
	SymbolData referee;
}

// ---------------------------------------------------------------------------
// Symbol

class Symbol {
	Symbol(SymbolLocation location, SymbolData data) 
	{
		this.location = location;
		this.data = data;
	}

	void print(PrintStream ps) 
	{
		ps.print("To Implement : Symbol.print() ");		
	}

	SymbolLocation location;
	SymbolData data;

}

// ---------------------------------------------------------------------------
// Scope
class Scope {

	Scope(Scope parent, String scopeName)
	{
		this.parent = parent;
		name=scopeName;
	}

	SymbolDiagnosis insertSymbol(String key, Symbol sym)
	// TODO
	{
		SymbolDiagnosis d = SymbolDiagnosis.Healthy;

		// conflict
		if ( this.hasSymbol(key) ) {
			d = SymbolDiagnosis.Conflict;	
		}

		// shadow
		if ( parent != null && parent.searchSymbol(key) != null ) {
			d = SymbolDiagnosis.Shadow;
		}

		// System.out.println("hello");

		symbols.put(key, sym);
		return d;
	}

	boolean assignSymbol( String key, SymbolData data )
	{
		Scope scope = searchScope( key );
		if (scope != null) {
			Symbol sym = scope.symbols.get(key);
			sym = new Symbol( sym.location, data );
			scope.symbols.put(key ,sym);
			return true;
		}
		else {
			return false;
		}
			
	}
	
	private Scope searchScope(String key) 
	{
		Scope scope = this;
		while (scope.parent != null) {
			if ( scope.hasSymbol(key) ) {
				// return symbols.get(key);	
				return scope;
			}
			scope = scope.parent;
		}
		
		return (scope == null) ? null : scope ;
			
	}

	// @Deprecated
	Symbol searchSymbol(String key) 
	{

		Scope scope = this;
		while (scope.parent != null) {
			if ( scope.hasSymbol(key) ) {
				return symbols.get(key);	
			}
			scope = scope.parent;
		}
		
		return (scope == null) ? null : scope.symbols.get(key) ;
						
	}

	boolean hasSymbol(String key) 
	{
		return (symbols.containsKey(key)) ? true : false ;
	}

	void print(PrintStream ps)
	{
		ps.println("");
		ps.println("ScopeName    : "+name);
		ps.print  ("Parent Scope : ");
		if ( parent == null ) {
			ps.println("None");
		}
		else {
			ps.println(parent.name);
		}
		for ( Map.Entry<String,Symbol> e : symbols.entrySet() ) {
			// e.getKey().print(ps);
			ps.print( e.getKey() );
			ps.print(" : ");
			e.getValue().print(ps);
			ps.println("");
		}
	
	}

	void printAll(PrintStream ps)
	{
		this.print(ps);
		if (children.isEmpty()) {
			// ps.println( "no children" );
			;
		}
		else {
			for ( Scope scope : children ) {
				scope.printAll(ps);
			}
		}
	}
	
	// SortedMap<String, SymbolData> symbols = new TreeMap<String, SymbolData>();
	Map<String, Symbol> symbols = new HashMap<String, Symbol>();
	// Map<String, SymbolData> symbols = new ConcurrentHashMap<String, SymbolData>();
	Scope						 parent  = null;
	Vector<Scope>				 children = new Vector<Scope>();
	String						 name;
}


// ---------------------------------------------------------------------------
// SymbolTableImplementation
class SymbolTable {

	SymbolTable(String topScopeName) 
	{
		Scope topScope = new Scope(null, topScopeName);
		currentScope = topScope;
		scopes.add(topScope);
	}

	public SymbolDiagnosis insertSymbol(String key, Symbol sym) 
	{
		return currentScope.insertSymbol(key,sym);
	}

	public Symbol searchSymbol(String key)
	{
		return currentScope.searchSymbol(key);
	}

	public boolean assignSymbol(String key, SymbolData sym)
	{
		return currentScope.assignSymbol(key,sym);
	}

	public void pushScope(String scopeName)
	{
		Scope newScope  = new Scope(currentScope, scopeName);
		currentScope.children.add(newScope);
		scopes.addElement(newScope);
		currentScope = newScope;
	}

	// public void pushScope(String scopeName, int level)

	public void popScope()
	// TODO
	// Should not pop but redirect current scope to parent scope
	{
		if (currentScope == null) {
			System.out.println("popScope() : ERROR");
		}
		// else if (currentScope) {
		// 	System.out.println("popScope() : ERROR");
		// }
		currentScope.parent.children.removeElementAt( currentScope.parent.children.size() -1 );
		currentScope = currentScope.parent;
		scopes.removeElementAt(scopes.size()-1);
	}

	public void dumpTable(PrintStream ps)
	{

		ps.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> SymbolData Table Dump Start");
		ps.println("NUmber of scopes : " + scopes.size());
		ps.println("");
 
		if ( scopes.isEmpty() )  {
			ps.println("dumpTable: SymbolTable is empty.");		
		}
		else {
			Scope topScope = scopes.elementAt(0);
			topScope.printAll(ps);
		}
		ps.println("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< SymbolData Table Dump END");
	}
	
	private Vector<Scope> scopes  = new Vector<Scope>();
	// private Vector<ClassOrStruct> typeStack = new Vector<ClassOrStruct>();
	private Scope currentScope;
}

