package movie;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.LinkedList;
import java.util.List;

public class ActionHistory {
	private PropertyChangeSupport pcs;
	private Present present;
	private List<PlayerAction> commands;
	private static ActionHistory instance;
	public static String CAN_UNDO = "Can_Undo";
	public static String CAN_REDO = "Can_Redo";
	
	private ActionHistory() {
		commands = new LinkedList<PlayerAction>();
		present = new Present(commands);
		pcs = new PropertyChangeSupport(this);
	}
	public static ActionHistory getInstance() {
		if (instance == null) {
			instance = new ActionHistory();
		}
		return instance;
	}
	public void add(PlayerAction cmd) {
		if (cmd.isReversible()) {
			boolean oldCanUndo = canUndo();
			boolean oldCanRedo = canRedo();
			present.clearFuture();
			commands.add(cmd);
			present.moveToEnd();
			pcs.firePropertyChange(CAN_UNDO, oldCanUndo, true);
			pcs.firePropertyChange(CAN_REDO, oldCanRedo, false);
		}
	}
	public void undo() {
		boolean oldCanRedo = canRedo();
		present.getPrevious().unExecute();
		present.stepBack();
		pcs.firePropertyChange(CAN_UNDO, true, canUndo());
		pcs.firePropertyChange(CAN_REDO, oldCanRedo, true);
	}
	public void redo() {
		boolean oldCanUndo = canUndo();
		present.getNext().execute();
		present.stepForward();
		pcs.firePropertyChange(CAN_REDO, true, canRedo());
		pcs.firePropertyChange(CAN_UNDO, oldCanUndo, true);
	}
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		pcs.addPropertyChangeListener(listener);
	}
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		pcs.removePropertyChangeListener(listener);
	}
	private boolean canUndo() {
		return (present.isAtStart() == false);
	}
	private boolean canRedo() {
		return (present.isAtEnd() == false);
	}
}
class Present {
	private List<PlayerAction> commands;
	int iPosn = 0;
	
	public Present(List<PlayerAction> commands) {
		this.commands = commands;
		moveToEnd();
	}
	public void clearFuture() {
		commands.subList(iPosn, commands.size()).clear();
	}
	public void stepForward() {
		iPosn++;
	}
	public void stepBack() {
		iPosn--;
	}
	public void moveToEnd() {
		iPosn = commands.size();
	}
	public boolean isAtEnd() {
		return iPosn == commands.size();
	}
	public boolean isAtStart() {
		return iPosn == 0;
	}
	public PlayerAction getPrevious() {
		return iPosn > 0 ? commands.get(iPosn-1) : null;
	}
	public PlayerAction getNext() {
		PlayerAction cmd = null;
		if (iPosn < commands.size()) {
			cmd = commands.get(iPosn);
		}
		return cmd;
	}
}