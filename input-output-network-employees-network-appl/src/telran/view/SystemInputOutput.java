package telran.view;
import java.io.*;
public class SystemInputOutput implements InputOutput {
    BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
    PrintStream writer = new PrintStream(System.out);
	@Override
	public String readString(String prompt) {
		writeLine(prompt);
		String res = null;
		try {
			res = reader.readLine();
		} catch (IOException e) {
			writeLine(e.getMessage());
		}
		return res;
	}

	@Override
	public void writeString(String string) {
		writer.print(string);

	}

}
