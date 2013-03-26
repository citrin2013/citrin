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
		SymbolTable stab = new SymbolTable("Global");

		SymbolLocation loc  = new SymbolLocation(1, 1); // line number and column number
		String      key  = "foo";
		SymbolData  data = new DebugSymbol("debug");
		Symbol sym	= new Symbol(loc, data);
		stab.insertSymbol( key, sym );

		key  = new String("main");
		data = new DebugSymbol("debug");
		sym	= new Symbol(loc, data);
		stab.insertSymbol( key, sym );

		//
		//      Push
		//
		stab.pushScope("main");

		key  = new String("foo");
		data = new DebugSymbol("debug");
		sym	= new Symbol(loc, data);
		stab.insertSymbol( key, sym );

		//
		//      Push
		//
		stab.pushScope("nakedscope1");

		key  = new String("buzz");
		data = new DebugSymbol("debug");
		sym	= new Symbol(loc, data);
		stab.insertSymbol( key, sym );

		// key  = new String("foo");
		// sym = new DebugSymbol("debug");
		// stab.insertSymbol( key, sym );

		//
		//      Modify Value
		//
		// sym = stab.searchSymbol(new String("foo") );
		data = new Bool( true );
		sym	= new Symbol(loc, data);
		// stab.assignSymbol( key,  sym );
		// stab.assignSymbol(new String("foo"),  sym ); 
		stab.assignSymbol("foo",  data ); 
		
		//
		//      Dump
		//
		stab.dumpTable(stdout);

		//
		//      Pop
		//
		stab.popScope();
		key = new String("bar");
		data = new DebugSymbol("debug");
		sym	= new Symbol(loc, data);
		stab.insertSymbol( key, sym );

		//
		//      Dump
		//
		stab.dumpTable(stdout);


		//
		//      Pop 
		//
		stab.popScope();

		//
		//      Dump
		//
		stab.dumpTable(stdout);
		
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
		SymbolTable stable = new SymbolTable("Global");

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
					stable.dumpTable( stdout );
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
					stable.dumpTable( stdout );
				}
				else if ( token.value.equals("{") ) {
					//
					//      Push Scope
					//
					stdout.print("Pushing a new scope into the symbol table");
					stable.pushScope(""+ (counter++) );
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

				String         name = token.value;			// 
				String         key  = new String(name);

				// Construct SymbolData (Value)
				//
				//		NEVER USE hackish toSymbol() elsewhere
				//
				SymbolData data = token.toSymbol();

				Symbol sym = new Symbol( loc, data );

				// Insert into symbol table
				SymbolDiagnosis d = stable.insertSymbol(key, sym);

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

		SymbolTable stab = new SymbolTable("Global");

		// Symbol
		SymbolLocation loc  = new SymbolLocation(1, 1); 
		String         key  = "foo";                    
		SymbolData     data = new DebugSymbol("foo in global"); 
		Symbol         sym  = new Symbol(loc, data);   

		// Insert symbol foo="foo in global" into Global scope
		stab.insertSymbol( key, sym );
			
		// Insert symbol foo="foo in Function 1" in to the Function1 Scope
		stab.pushScope("Function1");
		data = new DebugSymbol("foo in Function1");
		sym  = new Symbol(loc, data);
		stab.insertSymbol( key, sym );

		// Insert symbol foo="foo in Function2" in to the Function2 Scope
		stab.pushParallelScope("Function2");

		// debug
		stab.dumpTable(stdout);

		// 
		stdout.print("Now the searchSymbol(\"foo\") = ");
		Symbol foo = stab.searchSymbol("foo");
		if ( foo != null ) {
			stdout.println(foo.toData().toString());
		}
		else {
			stdout.println("null");
		}

	}

	public static void main(String[] arg) {

		int path = 3;

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
