package presenter;
/**
 * extends the CommonComand
 * manage the display of the cross section by  
 */
public class DisplayCrossSection extends CommonCommand{
	/**
	 * DisplayCrossSectionBy constructor
	 * @param controller - set the controller to work with him
	 */
	public DisplayCrossSection(Presenter presenter) {
		super(presenter);
	}

	@Override
	public void doCommand(String str) {
		String[] parm =str.split(" ");
		if(parm.length != 3)
			presenter.setMessage("Invalid Command");
		else
		{
		  int index=Integer.parseInt(parm[1]);
		  presenter.getModel().displayCrossSectionBy(parm[0], index, parm[2]);
		}
	}
	
}
