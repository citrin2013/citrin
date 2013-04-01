import java.io.*;


// TODO
//
//	split lex test and make it receive a file path
//	
//	make symbol test be able to receive an arbitrary file  path from command
//

public class SymbolTableDriver {

	static PrintStream stdout = new PrintStream( new FileOutputStream( FileDescriptor.out ) );

	//
	//      Lex
	//
	//  print all the tokens within a C++ file whose path is currently
	//  hardcoded
	//
	public static void LexTest()
	{
		Lexer lex = new Lexer();
		Lexer.Token token = lex.new Token();
		lex.loadSourceFile("testcpp_good1.cpp");
		while ( token.key != keyword.FINISHED ) {
			System.out.println( token.value + " : " + token.key + " : " + token.type );
			// Get next token
			try { 
				token = lex.get_token();
			} catch (SyntaxError e) {
			}
			continue;
		}
		
	}

	//
	//      	Construct Symbol Table
	//
	//  Build everything manually.
	//
	public static void SymbolTest()
	{
		// PrintStream stdout = new PrintStream( new FileOutputStream( FileDescriptor.out ) );

		//
		//      SymbolData Table Construction
		//
		SymbolTable stab = new SymbolTable();

		SymbolLocation loc  = new SymbolLocation(1, 1); // line number and column number
		var_type  data = new var_type();
		data.v_type = keyword.INT;
		data.var_name = "debug";
		Symbol sym	= new Symbol(loc, data);
		stab.pushSymbol( sym );

		data.var_name  = new String("main");
		sym	= new Symbol(loc, data);
		stab.pushSymbol( sym );

		//
		//      Push main
		//
		stab.pushFuncScope("main");

		data.var_name  = new String("foo");
		sym	= new Symbol(loc, data);
		stab.pushSymbol( sym );

		//
		//      Push
		//
		stab.pushFuncScope("test");

		data.var_name  = new String("buzz");
		sym	= new Symbol(loc, data);
		stab.pushSymbol( sym );

		// data.var_name  = new String("foo");
		// sym = new DebugSymbol("debug");
		// stab.pushSymbol( data.var_name, sym );

		//
		//      Modify Value
		//
		// sym = stab.searchSymbol(new String("foo") );
		data.v_type = keyword.BOOL;
		sym	= new Symbol(loc, data);
		// stab.assignVar( data.var_name,  sym );
		// stab.assignVar(new String("foo"),  sym ); 
		stab.assignVar("foo",  data ); 
		
		//
		//      Dump
		//
//TODO		stab.dumpTable(stdout);

		//
		//      Pop
		//
		stab.popScope();
		data.var_name = new String("bar");
		//data = new DebugSymbol("debug");
		sym	= new Symbol(loc, data);
		stab.pushSymbol( sym );

		//
		//      Dump
		//
//TODO		stab.dumpTable(stdout);


		//
		//      Pop 
		//
		stab.popScope();

		//
		//      Dump
		//
//TODO		stab.dumpTable(stdout);
		
	} 
	
	//
	//      Open A C++ File (Currently Hardcoded) And Construct Symbol Table
	//
	public static void SymbolTest2()
	{
		int counter = 1;

		// set up stdout for output
		PrintStream stdout = new PrintStream( new FileOutputStream( FileDescriptor.out ) );

		//
		//      Construct SymbolData Table
		//
		//
		// When symbol stable is constructed, it creates one scope with the
		// name supplied in the constructor argument. Ie. "Global" will the
		// name of the top scope in this symbol stable
		SymbolTable stable = new SymbolTable();

		//
		//      Construct Lexer 
		//
		Lexer lex = new Lexer();
		lex.loadSourceFile("testcpp_good1.cpp");


		//
		//      Fill In The SymbolData Table
		//

		Lexer.Token token = lex.new Token();

		// init before loop
		try { 
			 token = lex.get_token();
		} catch (SyntaxError e) {
			// lexer should not throw syntax error
		}
		// Loop
		while ( token.key != keyword.FINISHED ) {

			token.print(stdout);

			stdout.print( "On token <"+token.value + "> , ");

			if (token.type == token_type.BLOCK) {

				if ( token.value.equals("}") ) {
					stdout.println("");
					//
					//      Dump before Pop on '}'
					//
					stdout.println("Dumping symbol table before pop on }");
//TODO					stable.dumpTable( stdout );
					stdout.println("");

					//
					//      Pop Scope
					//
					stdout.println("Popping a scope from the symbol table");
					stable.popScope();

					//
					//      Dump after Pop on '}'
					//
					stdout.println("Dumping symbol table after pop on }");
//TODO					stable.dumpTable( stdout );
				}
				else if ( token.value.equals("{") ) {
					//
					//      Push Scope
					//
					stdout.print("Pushing a new scope into the symbol table");
					stable.pushFuncScope(""+ (counter++) );
				}
				else {
					stdout.print("SHOULD NOT HAPPEDN");
				}
			}
			else if (token.type == token_type.IDENTIFIER) {

				stdout.print("Inserting into symbol table");	

				// Construct String
				SymbolLocation loc  = new SymbolLocation(1,1); // line number and column number

				//
				// Not working
				//
				// SymbolLocation loc = new SymbolLocation( lex.getColumnNum(), lex.getLineNum() );
				// System.out.println( loc.lnum );
				// System.out.println( loc.cnum );

				var_type       data = new var_type();
				String         name = token.value;			// 
				data.var_name  = new String(name);

				// Construct var_type (Value)
				data.v_type = token.key;
				data.value = 0;

				Symbol sym = new Symbol( loc, data );

				// Insert into symbol table
				SymbolDiagnosis d = stable.pushSymbol(sym);

				stdout.print("Diagnosis : "+ d);

			}
			else {
				stdout.print("Doing nothing");	
			}

			stdout.println("");


			// Get next token
			try { 
				token = lex.get_token();
			} catch (SyntaxError e) {
			}

		}
	}

	// 
	// Demo for a function calling another function
	// 
	// If 
	static void SymbolTest3()
	{

		SymbolTable stab = new SymbolTable();

		// Symbol
		SymbolLocation loc  = new SymbolLocation(1, 1);            
		var_type     data = new var_type();
		data.var_name = "foo";
		data.value = 0;
		data.v_type = keyword.INT;
		Symbol         sym  = new Symbol(loc, data);   

		// Insert symbol foo="foo in global" into Global scope
		stab.pushSymbol( sym );
			
		// Insert symbol foo="foo in Function 1" in to the Function1 Scope
		stab.pushFuncScope("Function1");
		data.var_name = "foo in Function1";
		sym  = new Symbol(loc, data);
		stab.pushSymbol( sym );

		// debug
//TODO		stab.dumpTable(stdout);

		// 
		stdout.print("Now the searchSymbol(\"foo\") = ");
		Symbol foo = stab.findVar("foo");
		if ( foo != null ) {
			stdout.println(foo.data.value);
		}
		else {
			stdout.println("null");
		}

	}

	public static void main(String[] arg) {

		int path = 2;

		if (path==0) {
			LexTest();
			System.out.println("Exiting...");
			return;
		}
		else if (path==1) {
			SymbolTest();
			return;
		}
		else if (path==2) {
			SymbolTest2();
		}
		else if (path==3) {
			SymbolTest3();
		}

	}

}
