package presenter;

public class Solve extends CommonCommand {

	public Solve(Presenter presenter) {
		super(presenter);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void doCommand(String str) {
		String[] parm = str.split(" ");
		
		if(parm.length != 2)
		{
			if(parm.length == 1)
				pr.getM().solve();
			else
				pr.setMessage("Invalid Command");
		}
		else
			pr.getM().solve(parm[0],parm[1]);
	}

}
