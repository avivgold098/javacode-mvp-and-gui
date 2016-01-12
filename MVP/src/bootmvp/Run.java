package bootmvp;

import java.beans.XMLDecoder;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import model.MyModel;
import presenter.Presenter;
import presenter.Properties;
import view.CLI;
import view.MazeWindow;
import view.MyView;
import view.UserInterface;

public class Run {

	public static void main(String[] args) {
		XMLDecoder d;//
		Properties properties = new Properties();
		UserInterface uc = null;
		
		try {
			d = new XMLDecoder(new BufferedInputStream(new FileInputStream("Properties.xml")));
			properties = (Properties) d.readObject();
			d.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return;
		}
		if(properties.getUi().equals("CLI"))
		{
			uc = new CLI(new PrintWriter(System.out), new BufferedReader(new InputStreamReader(System.in)));
			System.out.println("You need to choose one of the following commands every time:\ndir <path>\ngenerate3dMaze <name> <y> <z> <x>\ndisplay <name>\ndisplayCrossSectionBy <X,Y or Z> <index> <name>\nsaveMaze <name> <file name>\nloadMaze <file name> <name>\nmazeSize <name>\nfileSize <name>\nsolve <name> <algorithm>\ndisplaySolution <name>\nexit");
		}else if(properties.getUi().equals("GUI"))
			uc = new MazeWindow("Maze 3D GAME", 1200, 700);
		
		MyModel model = new MyModel(properties);
		MyView view = new MyView(uc);
		Presenter presenter = new Presenter(model,view);
		
		model.addObserver(presenter);
		view.addObserver(presenter);
		
		
		view.start();//jjjjjj

	}

}







