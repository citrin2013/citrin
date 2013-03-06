# ---------------------------------------------------------------------------
# Default Rule
all : 
	javac *.java

# ---------------------------------------------------------------------------
# Run / Test

rungui : guiPanel.class Interpreter.class
	java guiPanel

runInterpreter : Interpreter.class
	java Interpreter

testScanner1 :
	java Scanner ../cppsrc/scanner_test_1.cpp

# ---------------------------------------------------------------------------
# Compile

Interpreter.class : Interpreter.java
	javac $^

guiPanel.class : guiPanel.java Console.class Editor.class
	javac guiPanel.java

Console.class : Console.java
	javac $^

Editor.class : Editor.java
	javac $^

# ---------------------------------------------------------------------------
# misc

clean :
	rm *.class tags

tags : 
	ctags -R .
