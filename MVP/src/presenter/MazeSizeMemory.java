package presenter;
/**
 * this class extends CommonCommand
 * Get the maze size (byte)
 *
 */
public class MazeSizeMemory extends CommonCommand {
	/**
	 * MazeSize constructor
	 * @param controller - get the Controller to work with him
	 */
	public MazeSizeMemory(Presenter presenter) {
		super(presenter);
	}

	@Override
	public void doCommand(String str) {
		presenter.getModel().mazeSize(str);
	}

}
