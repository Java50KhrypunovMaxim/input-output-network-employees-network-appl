package telran.view;
import java.util.*;
import java.util.stream.IntStream;
public class Menu implements Item {
private static final String SYMBOL = "*";
private static final int N_SYMBOLS = 20;
private String name;
private ArrayList<Item> items;

	public Menu(String name, ArrayList<Item> items) {
	this.name = name;
	this.items = items;
}
	public Menu(String name, Item ...item) {
		this(name, new ArrayList<Item>(List.of(item)));
		
	}

	@Override
	public String displayName() {
		
		return name;
	}

	@Override
	public void perform(InputOutput io) {
		boolean running = true;
		do {
			displayTitle(io);
			displayItems(io);
			int itemNumber = io.readInt("Enter item number", "Wrong item number", 1, items.size());
		    Item item = items.get(itemNumber - 1);
		    try {
				item.perform(io);
				if (item.isExit()) {
					running = false;
				}
			} catch (RuntimeException  e) {
				io.writeLine(e.getMessage());
			}
			
		} while(running);

	}

	private void displayItems(InputOutput io) {
		IntStream.range(0, items.size()).forEach(i -> io.writeLine(String.format("%d. %s", i + 1, items.get(i).displayName())));
		
	}
	private void displayTitle(InputOutput io) {
		io.writeLine(SYMBOL.repeat(N_SYMBOLS));
		io.writeLine(String.format("%s%s", " ".repeat(N_SYMBOLS / 4), name));
		io.writeLine(SYMBOL.repeat(N_SYMBOLS));
		
	}
	@Override
	public boolean isExit() {
		return false;
	}

}
