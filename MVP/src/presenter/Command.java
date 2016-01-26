package presenter;
/**
 * interface of all the commands
 *
 */
public interface Command {
	/**
	 * the command function
	 * @param str the parameter to the Model function
	 */
	void doCommand(String str);
}
