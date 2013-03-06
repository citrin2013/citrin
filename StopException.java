
public class StopException extends Exception {
    private int num;
    
    public StopException(int num){
        this.num = num;
    }
 
    public String toString(){
        return "Stop Called during expression eval";
    }
}
