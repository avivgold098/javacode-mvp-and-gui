package view;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.MenuItem;

import algorithms.mazeGenerators.Maze3d;
import algorithms.mazeGenerators.Position;
import algorithms.search.Solution;

public class Maze3dDisplayer extends MazeDisplayer{


	Maze3d maze;
	int characterX;
	int characterY;
	int characterZ;
	int exitX;
	int exitY;
	int exitZ;
	ExecutorService threadSolve;
	Thread solve;
	Thread run;
	boolean running;
	MenuItem mi;
	 /**
     * DisplayMaze3D constructor
     */
	public Maze3dDisplayer(Composite parent, int style,MenuItem mi) {
		super(parent, style);
		threadSolve = Executors.newFixedThreadPool(1);
		this.mi = mi;

	}	
	/**
     * get the maze
     * @return maze-the maze
     */
	public Maze3d getMaze() {
		return maze;
	}
	/**
     * set the maze
     * @param maze-the maze
     */
	public void setMaze(Maze3d maze) {
		this.maze = maze;
		characterX = maze.getStartPosition().getX();
		characterY = maze.getStartPosition().getY();
		characterZ = maze.getStartPosition().getZ();
		
		exitX = maze.getGoalPosition().getX();
		exitY = maze.getGoalPosition().getY();
		exitZ = maze.getGoalPosition().getZ();
	}

     /**
      * draw the maze 3d and the character
      */
	public void draw(){
		Image image = new Image(getDisplay(), "resources/walls.jpg");
		Image charachter = new Image(getDisplay(), "resources/runne.jpg");
		Image endGame = new Image(getDisplay(), "resources/EndGame.jpg");
		Image theEnd = new Image(getDisplay(), "resources/theend.jpg");
		Image up = new Image(getDisplay(), "resources/up.png");
		Image down = new Image(getDisplay(), "resources/down.png");
		Image upAndDown = new Image(getDisplay(), "resources/upanddown.jpg");
		setBackground(new Color(null, 192, 192, 192));
		
		setBackgroundImage(image);
		
    	addPaintListener(new PaintListener() {
			
			@Override
			public void paintControl(PaintEvent e) {
				   e.gc.setForeground(new Color(null,255,255,255));
				   e.gc.setBackground(new Color(null,255,255,255));

				   int width=getSize().x;
				   int height=getSize().y;

				   int w=width/maze.getMaze()[0][0].length;
				   int h=height/maze.getMaze()[0].length;
				   if(characterX == exitX && characterZ == exitZ && characterY == exitY)
					   e.gc.drawImage(endGame, 0, 0, 1900, 900, 0, 0, getSize().x, getSize().y);

				   else
					   for(int i=0;i<maze.getMaze()[0].length;i++)
						      for(int j=0;j<maze.getMaze()[0][0].length;j++){
						    	  mi.setText("floor number :"+(characterY+1));
						          int x=j*w;
						          int y=i*h;
						          if(maze.getMaze()[characterY][i][j] == 0)
						              e.gc.fillRectangle(x,y,w,h);  
						          if(characterY+1 < maze.getMaze().length)
						        	  if(maze.getMaze()[characterY][i][j] == 0 && maze.getMaze()[characterY+1][i][j] == 0)
						        		  e.gc.drawImage(up, 0, 0, 500,500 , x, y, w, h);
						          if(characterY-1>= 0)
						        	  	if(maze.getMaze()[characterY][i][j] == 0 && maze.getMaze()[characterY-1][i][j] == 0)
						        	  		e.gc.drawImage(down, 0, 0, 225,225 , x, y, w, h);
						          if(characterY +1< maze.getMaze().length && characterY-1 >= 0)
						        	  if(maze.getMaze()[characterY][i][j] == 0 && maze.getMaze()[characterY+1][i][j] == 0 && maze.getMaze()[characterY-1][i][j] == 0)
						        		  e.gc.drawImage(upAndDown, 0, 0, 380,314 , x, y, w, h);
						          if(j == characterX && i == characterZ){
						        	  e.gc.drawImage(charachter, 0, 0, 1200,768 , x, y, w, h);
						          }
						          if(characterY == exitY)
						          {
						        	  e.gc.drawImage(theEnd, 0, 0, 182,276 , exitX*w, exitZ*h, w, h);
						          }
						      }		
			}
				  
		});
	}
	/**
	 * move the character
	 * @param x
	 * @param y
	 * @param z
	 */
	private void moveCharacter(int y,int z, int x){
		if(characterX == exitX && characterZ == exitZ && characterY == exitY)
      	  return;
		
		if(y>=0 && y < maze.getMaze().length && z>=0 && z<maze.getMaze()[0].length && x>=0 && x<maze.getMaze()[0][0].length && maze.getMaze()[y][z][x] == 0){
			characterX=x;
			characterY=y;
			characterZ=z;

			run=new Thread(new Runnable() {
				
				@Override
				public void run() {
					redraw();
				}
			});
			getDisplay().syncExec(run);
		
		}
	}

	@Override
	public void moveUp() {
		
		moveCharacter(this.characterY+1, this.characterZ, this.characterX);
	}

	@Override
	public void moveDown() {
		
		moveCharacter(this.characterY-1, this.characterZ, this.characterX);
	}

	@Override
	public void moveLeft() {
		
		moveCharacter(this.characterY, this.characterZ, this.characterX-1);
	}

	@Override
	public void moveRight() {
		
		moveCharacter(this.characterY, this.characterZ, this.characterX+1);
	}

	@Override
	public void moveForward() {
		
		moveCharacter(this.characterY, this.characterZ-1, this.characterX);
	}

	@Override
	public void moveBack() {
		
		moveCharacter(this.characterY, this.characterZ+1, this.characterX);
	}
	/**
	 * display the solution
	 * @param sol-the Solution for display
	 */
	public void displaySolution(Solution<Position> sol)
	{
		this.running = true;
		solve=new Thread(new Runnable() {
			
			@Override
			public void run() {
				while(running)
				{
				String[] position=sol.toString().split("\n");
				int x,y,z;
				for(int i=position.length-1; running && i>=1; i--)
				{
					String[] numbers = position[i].split(" ");
					y=Integer.parseInt(numbers[0]);
					z=Integer.parseInt(numbers[1]);
					x=Integer.parseInt(numbers[2]);
					  moveCharacter(y,z,x);
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {}
				}
				}
			}
		});
		threadSolve.execute(solve);
	}
	
	/**
	 * set the running
	 * @param running-the running
	 */
	public void setRunning(boolean running){ 
		
		if(!running)
		{
			this.running=running;
			if(run != null)
				while(run.isAlive());
			threadSolve.shutdown();
		}
	}
	/**
	 * set that new solve is occur
	 * @param running
	 */
	public void newSolve(boolean running){ 
		
		if(!running)
			this.running=running;
	}
	/**
	 * get the x of character
	 * @return characterX-the x
	 */
	public int getCharacterX() {
		return characterX;
	}
	/**
	 * get the y of character
	 * @return characterY-the y
	 */
	public int getCharacterY() {
		return characterY;
	}
	/**
	 * get the z of character
	 * @return characterZ-the z
	 */
	public int getCharacterZ() {
		return characterZ;
	}
	/**
	 * return if thread for solve is in progress
	 * @return boolean running
	 */
	public boolean isSolving() { return running; }
	


}
