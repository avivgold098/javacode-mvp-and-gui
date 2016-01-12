 	package model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Observable;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import Demo.SearchableMaze;
import algorithms.mazeGenerators.Maze3d;
import algorithms.mazeGenerators.MyMaze3dGenerator;
import algorithms.mazeGenerators.Position;
import algorithms.mazeGenerators.SimpleMaze3dGenerator;
import algorithms.search.Astar;
import algorithms.search.BFS;
import algorithms.search.CostComparator;
import algorithms.search.MazeAirDistance;
import algorithms.search.MazeManhattenDistance;
import algorithms.search.Solution;
import algorithms.search.State;
import io.MyCompressorOutputStream;
import io.MyDecompressorInputStream;
import presenter.Properties;






public class MyModel extends Observable implements Model {
	ExecutorService threadpool;
	 HashMap<Maze3d,String> mazeFile;
	 HashMap<String, Solution<Position>> hashSolution;
	 HashMap<String, Maze3d> hashMaze;
	 int x;
	 int y;
	 int z;
	 int numberOfThreads;
	 String algorithmForSolution;
	 String algorithmForGenerate;
	 String name;
	 Properties properties;
	 public MyModel(Properties properties) {
			super();
			this.properties = properties;
			setProperties(properties);
			threadpool = Executors.newFixedThreadPool(numberOfThreads);
			mazeFile = new HashMap<Maze3d,String>();
			load();
	 }
		
		

	@Override//
	public void dir(String str) {
		try {
			File file = new File(str);	
			String[] string = file.list();
			String list = "";
			for(int i = 0; i <string.length; i++)
				list += string[i] + '\n';
			this.setChanged();
			this.notifyObservers(list);
		}
		catch (NullPointerException e){
			this.setChanged();
			this.notifyObservers("invalid path");
		}
	}


	 @Override
	 public void createSolution(String name,String Algoname) {
			 threadpool.submit(new Callable<Solution<Position>>() {
					
					@Override
					public Solution<Position> call() throws Exception {
						if(Algoname.equalsIgnoreCase("bfs")){
							Maze3d maze = hashMaze.get(name);
							if(maze != null){
								CostComparator<Position> c1 = new CostComparator<Position>();
								BFS<Position> bfs = new BFS<Position>(c1);
								Solution<Position> bfsSolution = bfs.search(new SearchableMaze(maze));
								hashSolution.put(name, bfsSolution);
								setChanged();
								notifyObservers("Solution for '" + name + "' is ready");
								return bfsSolution;
							}
							else{
								setChanged();
								notifyObservers("Invalid name");
							}
						}
						else if(Algoname.equalsIgnoreCase("MazeManhattanDistance")){
							Maze3d maze = hashMaze.get(name);
							if(maze != null){
								CostComparator<Position> c1 = new CostComparator<Position>();
								Astar<Position> astarManhattanDistance = new Astar<Position>(new MazeManhattenDistance(new State<Position>(maze.getEndPosition())),c1);
								Solution<Position> astarManhattan = astarManhattanDistance.search(new SearchableMaze(maze));
								hashSolution.put(name, astarManhattan);
								setChanged();
								notifyObservers("Solution for '" + name + "' is ready");
								return astarManhattan;
							}
							else{
								
								setChanged();
								notifyObservers("Invalid name");
							}
						}
						else if(Algoname.equalsIgnoreCase("MazeAirDistance")){
							Maze3d maze = hashMaze.get(name);
							if(maze != null){
								CostComparator<Position> c1 = new CostComparator<Position>();
								Astar<Position> astarAirDistance = new Astar<Position>(new MazeAirDistance(new State<Position>(maze.getEndPosition())),c1);
								Solution<Position> astarAir = astarAirDistance.search(new SearchableMaze(maze));
								hashSolution.put(name, astarAir);
								setChanged();
								notifyObservers("Solution for '" + name + "' is ready");
								return astarAir;
							}
							else{
								setChanged();
								notifyObservers("Invalid name");
								
							}
						}
						else{
							setChanged();
							notifyObservers("Invalid algorithm");
					}
						return null;
					}});
			 }
	

	@Override
	public void displaySolution(String name) {
		Solution<Position>	solution = this.getSolution(name);
		if(solution == null)
		{
			this.setChanged();
			this.notifyObservers("Not exist solution for " + name + " maze");
		}
		else
		{
			this.setChanged();
			this.notifyObservers(solution.toString());
		}
	}

	@Override
	public Solution<Position> getSolution(String name) {
		return hashSolution.get(name);
	}

	@Override
	public void display(String str) {
		Maze3d maze = hashMaze.get(str);
		if(maze == null)
		{
			this.setChanged();
			this.notifyObservers("There is no maze with the name:"+str);
		}
		else
		{
			this.setChanged();
			this.notifyObservers(maze.toString());
		}
	}

	@Override
	public void generate(String name, int y, int z, int x) {
		if(hashMaze.containsKey(name))
		{
			setChanged();
			notifyObservers(hashMaze.get(name));
			setChanged();
			notifyObservers("Maze '"+name+"' is already exist");
			return;
		}
		threadpool.submit(new Callable<Maze3d>() {

			@Override
			public Maze3d call() throws Exception {
				Maze3d m = new MyMaze3dGenerator().generate(y, z, x);
				setChanged();
				notifyObservers(hashMaze.get(name));
				hashMaze.put(name,m);
				setChanged();
				notifyObservers("Maze '"+name+"' is ready");
				return m;
				
			}
		});
		
	}
	@Override
	public void generate3dMaze() {
		if(hashMaze.containsKey(name))
		{
			setChanged();
			notifyObservers(hashMaze.get(name));
			setChanged();
			notifyObservers("Maze '"+name+"' is already exist");
			return;
		}
		this.threadpool.submit(new Callable<Maze3d>() {

			@Override
			public Maze3d call() throws Exception {
				Maze3d maze;
				if(algorithmForGenerate.equals("MyMaze3dGenerator"))
					maze = new MyMaze3dGenerator().generate(y, z, x);
				else
					maze = new SimpleMaze3dGenerator().generate(y, z, x);
				hashMaze.put(name,maze);
				setChanged();
				notifyObservers(maze);
				setChanged();
				notifyObservers("Maze '"+name+"' is already exist");
				return maze;
			}
		});
	}


	@Override
	public void crossBy(String by, int index, String name) {
		Maze3d maze = hashMaze.get(name);
		
		String strMaze ="";
		int[][] maze2d = null;
		if(maze == null){
			this.setChanged();
			this.notifyObservers("Maze not exist");
			return;
		}
		
		
		
		try{
			switch(by){
			case "X":
				maze2d = maze.getCrossSectionByX(index);
				break;
			case "x":
				maze2d = maze.getCrossSectionByX(index);
				break;
			case "Y":
				maze2d = maze.getCrossSectionByY(index);
				break;
			case "y":
				maze2d = maze.getCrossSectionByY(index);
				break;
			case "Z":
				maze2d = maze.getCrossSectionByZ(index);
				break;
			case "z":
				maze2d = maze.getCrossSectionByZ(index);
				break;
			default:
				this.setChanged();
				this.notifyObservers("invalid input");
				return;
			}
		}
		catch(IndexOutOfBoundsException e){
			this.setChanged();
			this.notifyObservers("invalid input");
			return;
		}
		
		
		
		for(int i = 0; i < maze2d.length; i++){
			for(int j = 0; j < maze2d[i].length; j++)
				strMaze += String.valueOf(maze2d[i][j]) + " ";
			strMaze += '\n';
		}
		
		this.setChanged();
		this.notifyObservers(strMaze);
	}

	@Override
	public void saveMaze(String Name, String FileName) {
		Maze3d maze = hashMaze.get(Name);
		if(maze == null){
			this.setChanged();
			this.notifyObservers("Maze " + Name + " not exist");
			return;
		}
		
		OutputStream out = null;
		try {
			out = new MyCompressorOutputStream(new FileOutputStream(FileName + ".maz"));
			out.write(maze.toByteArray());	
			mazeFile.put(maze, FileName + ".maz");
		} catch (FileNotFoundException e) {
			this.setChanged();
			this.notifyObservers(e.getMessage());
			return;
		} catch (IOException e) {
			this.setChanged();
			this.notifyObservers(e.getMessage());
			return;
		}
		finally{
			try {
				out.flush();
			} catch (IOException e) {
				this.setChanged();
				this.notifyObservers(e.getMessage());
			}
			try {
				out.close();
			} catch (IOException e) {
				this.setChanged();
				this.notifyObservers(e.getMessage());
			}
		}
		
		
		this.setChanged();
		this.notifyObservers("File "+FileName+" save");
		
	}

	@Override
	public void loadMaze(String FileName, String Name) {
		 Maze3d loaded = null;
			boolean isOpen = false;
				
				try{
					@SuppressWarnings("unused")
					File file = new File(FileName + ".maz");
				}
				catch(NullPointerException e){
					this.setChanged();
					this.notifyObservers("File not exist");
					return;
				}
					
				InputStream in=null;
				try {
					in = new MyDecompressorInputStream(new FileInputStream(FileName+ ".maz"));
					isOpen = true;
					byte b[] = new byte[4096];
					in.read(b);
					loaded = new Maze3d(b);
				}
				catch (FileNotFoundException e) {
					this.setChanged();
					this.notifyObservers(e.getMessage());
					return;
				}
				catch (IOException e) {
					this.setChanged();
					this.notifyObservers(e.getMessage());
				}
				catch(NullPointerException e)
				{
					this.setChanged();
					this.notifyObservers(e.getMessage());
					return;
				}
				finally
				{
					try {
						if(isOpen)
							in.close();
					} catch (IOException e) 
					{
						this.setChanged();
						this.notifyObservers("Maze "+ Name+" was unsuccessfully");
					}
				}
					
				hashMaze.put(Name, loaded);
				mazeFile.put(loaded, FileName + ".maz");
				this.setChanged();
				this.notifyObservers("Maze " + Name + " loaded successfully");
	}

	@Override
	public void mazeSizeMemory(String name) {
		Maze3d maze = hashMaze.get(name);
		if(maze == null){
			this.setChanged();
			this.notifyObservers("Maze " + name + " not exist");
			return;
		}
		
		int size = 4*((maze.getmyMaze()[0][0].length*maze.getmyMaze()[0].length*maze.getmyMaze().length)  + 3 + 3);
		this.setChanged();
		this.notifyObservers("Maze " + name + " size in memory: " + size);
	}

	@Override
	public void mazeSizeFile(String name) {
		try{
			String fielPath = mazeFile.get(hashMaze.get(name));
			if(fielPath == null){
				this.setChanged();
				this.notifyObservers("Maze " + name + " not exist in any file");
				return;
			}
			File maze = new File(fielPath);
			this.setChanged();
			this.notifyObservers("Maze file " + name + " size is: " + maze.length());
			
		}
		catch (NullPointerException e){
			this.setChanged();
			this.notifyObservers("Not exist " + name + " file");
			}
	}

	@Override
	public void exit() {
		threadpool.shutdown();
		try {
			while(!(threadpool.awaitTermination(10, TimeUnit.SECONDS)));
		} catch (InterruptedException e) {
			this.setChanged();
			this.notifyObservers(e.getMessage());
			}
		
	}
	@Override
	public void save() {
		FileOutputStream fileSolutions;
		GZIPOutputStream GZIPOutput;
		ObjectOutputStream out;
		try {
			fileSolutions = new FileOutputStream("solution.zip");
			GZIPOutput = new GZIPOutputStream(fileSolutions);
			out = new ObjectOutputStream(GZIPOutput);
			out.writeObject(hashMaze);
			out.writeObject(hashSolution);
			out.writeObject(mazeFile);
			out.flush();
			out.close();
		} catch (FileNotFoundException e) {
			setChanged();
			notifyObservers(e.getMessage());
		}
		catch (IOException e) {
			setChanged();
			notifyObservers(e.getMessage());
		}		
	}
	@SuppressWarnings("unchecked")
	public void load() {
		FileInputStream fileSolutions;
		GZIPInputStream GZIPInput;
		ObjectInputStream in;
		try {
			fileSolutions = new FileInputStream("solution.zip");
			GZIPInput = new GZIPInputStream(fileSolutions);
			in = new ObjectInputStream(GZIPInput);
			hashMaze = (HashMap<String, Maze3d>) in.readObject();
			hashSolution = (HashMap<String, Solution<Position>>) in.readObject();
			mazeFile = (HashMap<Maze3d, String>) in.readObject();
			in.close();
		} catch (FileNotFoundException e) {
			setChanged();
			notifyObservers(e.getMessage());
		}
		catch (IOException e) {
			setChanged();
			notifyObservers(e.getMessage());
		}	
		catch (ClassNotFoundException e) {
		setChanged();
		notifyObservers(e.toString());
		}
	}
	@Override
	public void setProperties(Properties properties) {
		this.properties = properties;
		setChanged();
		setName(properties.getName());		
		setX(properties.getXSize());
		setY(properties.getYSize());
		setZ(properties.getZSize());
		setAlgorithmForGenerate(properties.getAlgorithemForCreate());
		setAlgorithmForSolution(properties.getAlgorithemForSolution());
		setNumberOfThreads(properties.getNumberOfThreads());
		threadpool = Executors.newFixedThreadPool(numberOfThreads);
		notifyObservers("The properties is uploaded successfully");
	}
	
	@Override
	public void getMazeByName(String name) {
		// TODO Auto-generated method stub
		
	}



	/**
	 * @return the x
	 */
	public int getX() {
		return x;
	}



	/**
	 * @return the y
	 */
	public int getY() {
		return y;
	}



	/**
	 * @return the z
	 */
	public int getZ() {
		return z;
	}



	/**
	 * @return the numberOfThreads
	 */
	public int getNumberOfThreads() {
		return numberOfThreads;
	}



	/**
	 * @return the algorithmForSolution
	 */
	public String getAlgorithmForSolution() {
		return algorithmForSolution;
	}



	/**
	 * @return the algorithmForGenerate
	 */
	public String getAlgorithmForGenerate() {
		return algorithmForGenerate;
	}



	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}



	/**
	 * @param x the x to set
	 */
	public void setX(int x) {
		this.x = x;
	}



	/**
	 * @param y the y to set
	 */
	public void setY(int y) {
		this.y = y;
	}



	/**
	 * @param z the z to set
	 */
	public void setZ(int z) {
		this.z = z;
	}



	/**
	 * @param numberOfThreads the numberOfThreads to set
	 */
	public void setNumberOfThreads(int numberOfThreads) {
		this.numberOfThreads = numberOfThreads;
	}



	/**
	 * @param algorithmForSolution the algorithmForSolution to set
	 */
	public void setAlgorithmForSolution(String algorithmForSolution) {
		this.algorithmForSolution = algorithmForSolution;
	}



	/**
	 * @param algorithmForGenerate the algorithmForGenerate to set
	 */
	public void setAlgorithmForGenerate(String algorithmForGenerate) {
		this.algorithmForGenerate = algorithmForGenerate;
	}



	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	public void solve() {
		if(hashSolution.containsKey(hashMaze.get(name)) == true)
		{
			setChanged();
			notifyObservers(hashSolution.get(hashMaze.get(name)));
			setChanged();
			notifyObservers("Solution for '" + name + "' is ready");
			return;
		}	
		Callable<Solution<Position>> callable = new Callable<Solution<Position>>() {
			@Override
			public Solution<Position> call() throws Exception {
				if(algorithmForSolution.equalsIgnoreCase("bfs")){
					Maze3d maze = hashMaze.get(name);
					if(maze != null){
						CostComparator<Position> c = new CostComparator<Position>();
						BFS<Position> bfs = new BFS<Position>(c);
						Solution<Position> bfsSolution = bfs.search(new SearchableMaze(maze));
						hashSolution.put(maze, bfsSolution);
						setChanged();
						notifyObservers(bfsSolution);
					}
					else{
						setChanged();
						notifyObservers("Invalid name");
					}
				}
				else if(algorithmForSolution.equalsIgnoreCase("MazeManhattanDistance")){
					Maze3d maze = hashMaze.get(name);
					if(maze != null){
						CostComparator<Position> c = new CostComparator<Position>();
						Astar<Position> astarManhattanDistance = new Astar<Position>(new MazeManhattenDistance(new State<Position>(maze.getGoalPosition())),c);
						Solution<Position> astarManhattan = astarManhattanDistance.search(new SearchableMaze(maze));
						hashSolution.put(maze, astarManhattan);
						setChanged();
						notifyObservers(astarManhattan);
					}
					else{
						setChanged();
						notifyObservers("Invalid name");
					}
				}
				else if(algorithmForSolution.equalsIgnoreCase("MazeAirDistance")){
					Maze3d maze = hashMaze.get(name);
					if(maze != null){
						CostComparator<Position> c = new CostComparator<Position>();
						Astar<Position> astarAirDistance = new Astar<Position>(new MazeAirDistance(new State<Position>(maze.getGoalPosition())),c);
						Solution<Position> astarAir = astarAirDistance.search(new SearchableMaze(maze));
						hashSolution.put(maze, astarAir);
						setChanged();
						notifyObservers(astarAir);
					}
					else{
						setChanged();
						notifyObservers("Invalid name");
					}
				}
				else
				{
					setChanged();
					notifyObservers("Invalid algorithm");
				}
				return hashSolution.get(hashMaze.get(name));
			}
		};
		threadpool.submit(callable);
		setChanged();
		notifyObservers("Solution for '" + name + "' is ready");
		
	}

	}
