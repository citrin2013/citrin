JC=javac
JI=java
JFLAGS = -g
CLASSES = $(shell ls *.java)

.SUFFIXES: .java .class

# ---------------------------------------------------------------------------
# Default Rule

default: classes

# ---------------------------------------------------------------------------
# Run 

rungui : guiPanel.class Interpreter.class
	$(JI) guiPanel

runInterpreter : Interpreter.class
	$(JI) Interpreter

# ----------------------------------------------------------------------------
# Test

# ---------------------------------------------------------------------------
# Compile

SymbolTableNotifier.class : SymbolTable.class CitrinObserver.class

DataDisplay.class : SymbolTableNotifier.class CitrinObserver.class

classes: $(CLASSES:.java=.class)

.java.class:
	$(JC) $(JFLAGS) $*.java

# ---------------------------------------------------------------------------
# misc

clean :
	rm *.class tags

tags : $(shell ls *.java)
	ctags -R .
