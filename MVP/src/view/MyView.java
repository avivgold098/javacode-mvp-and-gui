package view;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Observable;

import presenter.Command;
import presenter.Properties;

public class MyView extends Observable implements View {
	CLI cli;
	HashMap<String,Command> hc;
	BufferedReader in;
	PrintWriter out;
	UserInterface uc;
	public MyView(BufferedReader in,PrintWriter out)
	{
		this.in = in;
		this.out = out;
		cli = new CLI(out,in);
		cli.setView(this);
	}
	@Override
	public void printMessage(String str) {
		cli.printMessage(str);
	}
	public MyView(UserInterface uc)
	{
		this.uc = uc;
		uc.setView(this);
	}
	@Override
	public void start() {
		cli.start();
	}
	@Override
	public void setHashCommand(HashMap<String, Command> hc) {
		cli.setHashCommand(hc);
		this.hc = hc;
	}
	@Override
	public void notifyMe(String[] str)
	{
		setChanged();
		notifyObservers(str);
	}
	@Override
	public void notifyMe(String str) {
		setChanged();
		notifyObservers(str);
	}
	
	@Override
	public void notifyMe(Properties properties)  {
		setChanged();
		notifyObservers(properties);
	}


}

	
