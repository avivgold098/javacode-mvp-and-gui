package presenter;

public class Generate3dMaze extends CommonCommand {

	public Generate3dMaze(Presenter presenter) {
		super(presenter);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void doCommand(String str) {
		String[] parm = str.split(" ");
		
		if(parm.length != 4)
		{
			if(parm.length == 1)
				pr.getM().generate3dMaze();
			else
				pr.setMessage("Invalid Command");
		}
		else{
			int x = 0,y = 0,z = 0;
			try{
				y = Integer.parseInt(parm[1]);
				z = Integer.parseInt(parm[2]);
				x = Integer.parseInt(parm[3]);
			}
			catch (NumberFormatException e){
				pr.setMessage("Invalid Command");
			}
			
			pr.getM().generate(parm[0],y, z, x);
		}
	}


}
