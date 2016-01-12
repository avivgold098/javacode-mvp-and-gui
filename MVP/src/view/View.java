package view;

import java.util.HashMap;

import presenter.Command;
import presenter.Properties;

public interface View {
	public void printMessage(String str);
	public void notifyMe(String str);
	public void notifyMe(String [] str);
	public void start();
	public void setHashCommand(HashMap<String, Command> hc);
	void notifyMe(Properties properties);

}
