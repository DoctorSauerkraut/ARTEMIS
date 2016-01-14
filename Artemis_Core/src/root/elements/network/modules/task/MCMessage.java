package root.elements.network.modules.task;

import java.util.HashMap;
import java.util.Vector;

import logger.GlobalLogger;
import root.elements.network.modules.CriticalityLevel;
import root.util.constants.ConfigParameters;
import root.util.tools.NetworkAddress;

public class MCMessage extends FrameMessage implements ISchedulable, Cloneable{	
	/* Size in bytes */
	/* Each different size corresponds to a criticality level */
	public HashMap<CriticalityLevel, Double> size;
	
	public double wcet;
	
	/* Period of emission */
	public int period;
	
	public MCMessage(String name) {
		super(name);
		size = new HashMap<CriticalityLevel, Double>();
	}
	
	public double getCurrentWcet(CriticalityLevel critLvl) {
		if(wcetTask == -1) {
			if(size.get(critLvl) != null && size.get(critLvl) > 0) {
				wcetTask = size.get(critLvl)/ConfigParameters.FLOW_DATARATE; 
				return wcetTask;
			}
			else {
				return 0.0;
			}
		}
		else {
			return wcetTask;
		}
	}
	
	public void setCurrentWcet(double wcet) {
		this.wcet = wcet;
	}
	
	public int addCriticalityLevel(String critLvlName) {
		return 0;
	}

	@Override
	public int getCurrentPeriod() {
		return this.period;
	}

	@Override
	public void setCurrentPeriod(int period) {
		this.period = period;
		
	}

	@Override
	public int getPeriod() {
		return this.period;
	}

	@Override
	public void setPeriod(int period) {
		this.period = period;
	}

	public Double getSize(CriticalityLevel critLvl) {
		return size.get(critLvl);
	}
	
	public HashMap<CriticalityLevel, Double> getSize() {
		return size;
	}
	
	@Override
	public double getWcet() {
		return size.get(CriticalityLevel.NONCRITICAL)/ConfigParameters.FLOW_DATARATE;
	}
	
	public double getWcet(CriticalityLevel critLvl) {
		if(size.get(critLvl) != -1) {
			wcetTask = size.get(critLvl)/ConfigParameters.FLOW_DATARATE; 
		}
		else {
			wcetTask = 0.0;
		}
			return wcetTask;
	}

	@Override
	public void setWcet(double wcet) {
		size.put(CriticalityLevel.NONCRITICAL, wcet*ConfigParameters.FLOW_DATARATE);
	}
	
	public void setWcet(double wcet, CriticalityLevel critLvl) {
		size.put(critLvl, wcet);
	}

	@Override
	public int getPriority() {
		return priority;
	}

	@Override
	public void setPriority(int priority) {
		this.priority = priority;
	}
	
	
}
