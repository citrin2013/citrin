
public class SyntaxError extends Exception{
    private String str;
    private int line;
    private int column;
    
    public SyntaxError(String str, int line, int column){
        this.str = new String(str);
        this.line = line;
        this.column = column;
    }
 
    @Override
    public String toString(){
        return str;
    }
    public int getLine(){
        return line;
    }
    public int getColumn(){
    	return column;
    }
    
}
