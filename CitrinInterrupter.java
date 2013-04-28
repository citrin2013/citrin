import java.util.List;
import java.util.ArrayList;
import java.lang.Thread;

class CitrinInterrupter {

	private static volatile CitrinInterrupter instance = null;

	private CitrinInterrupter() {}

	private List<Interpreter> is = new ArrayList<Interpreter>();
	private List<Thread> ts = new ArrayList<Thread>();

	public void addThread(Thread t)
	{
		ts.add(t);
	}

	public void addInterpreter(Interpreter i)
	{
		is.add(i);
	}

	public void interrupt()
	{
		for (Interpreter i : is ) {
			i.stop();
		}

		for (Thread t : ts ) {
			t.interrupt();
		}
	}

	public static CitrinInterrupter getInstance() {
		if (instance == null) {
			synchronized (CitrinInterrupter.class){
				if (instance == null) {
						instance = new CitrinInterrupter();
				}
			}
		}
		return instance;
	}

}
