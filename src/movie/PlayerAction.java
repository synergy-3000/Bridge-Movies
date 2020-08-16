package movie;

public interface PlayerAction {
	public boolean execute();
	public void unExecute();
	public boolean isReversible();
	//public String getLastErrorMsg();
	//public void setLastErrorMsg(String msg);
}
