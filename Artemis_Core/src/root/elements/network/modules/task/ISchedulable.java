package root.elements.network.modules.task;

import java.util.Vector;

import root.elements.network.modules.CriticalityLevel;
import root.util.tools.NetworkAddress;

/* Interface for schedulable elements
 * 
 * @author Olivier Cros
 */
public interface ISchedulable {
	
	/* Add node to the current path */
	int addNodeToPath(NetworkAddress addr);
	
	/* Automatically build network path */
	int buildNetworkPath(NetworkAddress source);
	
	/* Display the path of the schedulable element(message) */
	int displayPath();
	
	/* Get wcet corresponding to current criticality in case of MC-mode */
	/* Wcet getter */
	double getCurrentWcet();
	
	/* Wcet setter */
	void setCurrentWcet(double wcet);
	
	/* Period getter */
	int getCurrentPeriod();
	
	/* Period setter */
	void setCurrentPeriod(int period);
	
	/* Next send getter */
	int getNextSend();
	
	/* Next send setter */
	void setNextSend(int nextSend);
	
	/* Network path getter */
	Vector<NetworkAddress> getNetworkPath();
	
	/* Network path setter */
	void setNetworkPath(Vector<NetworkAddress> path);
	
	/* message id getter */
	int getId();
	/*message id setter */
	void setId(int id);
	
	/* name getter */
	String getName();
	/* name setter */
	void setName(String name);
	
	/* Current node getter */
	int getCurrentNode();
	/* Current node setter */
	void setCurrentNode(int node);
	
	/* Increase the execution number */
	public void increaseNbExec();
	/* Get the current number of executions */
	public int getNbExec();
	
	/* Get the message period (for periodic messages)*/
	public int getPeriod();
	/* Set the message period */
	public void setPeriod(int period);
	
	/* Get the wcet for current criticality level */
	public double getWcet();
	/* Set the wcet for current criticality level */
	public void setWcet(double wcet);
	
	/*MC Management */
	public double getWcet(CriticalityLevel critLvl);
	/* Set wcet for given criticality level */
	public void setWcet(double wcet, CriticalityLevel critLvl);
	
	/* Get message offset */
	public int getOffset();
	/* Set message offset */
	public void setOffset(int offset);
	
	/* Get next message activation time */
	public double getTimerArrival();
	/* Set next message activation time */
	public void setTimerArrival(double timer);
	
	/* Is message observed(for worst-case computations) */
	public boolean isObserved();
	
	/* Get current priority */
	public int getPriority();
	/* Set current priority */
	public void setPriority(int priority);
}
