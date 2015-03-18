package simulator.managers;

import java.util.Vector;

import logger.GlobalLogger;
import root.elements.network.modules.task.ISchedulable;
import root.elements.network.modules.task.MCMessage;
import root.elements.network.modules.task.NetworkMessage;
import root.util.constants.ConfigParameters;
import simulator.policies.FIFOISolatedSchedulingPolicy;
import simulator.policies.FIFOSchedulingPolicy;
import simulator.policies.FIFOStarSchedulingPolicy;
import simulator.policies.FixedPrioritySchedulingPolicy;
import simulator.policies.ISchedulingPolicy;
import utils.Errors;

public class PriorityManager {
	/* Application of different scheduling policies */
	public PriorityManager() {
		
	}
	
	public ISchedulable getNextMessage(Vector<ISchedulable> buffer) {
		ISchedulable rstMessage = null;
		ISchedulingPolicy policy = null;
		
		/* Picking priority policy */
		switch(ConfigParameters.PRIORITY_POLICY) {
			case FIFO:
				policy = new FIFOSchedulingPolicy();
				break;
			case FIFOISOLATED:
				policy = new FIFOISolatedSchedulingPolicy();
				break;
			case FIXEDPRIORITY:
				policy = new FixedPrioritySchedulingPolicy();
				break;
			case FIFOSTAR:
				policy = new FIFOStarSchedulingPolicy();
				break;
			default:
				policy = null; 
				break;
		}

		try {
			if(ConfigParameters.MIXED_CRITICALITY) {
				rstMessage = (MCMessage) policy.getSchedulingMessage(buffer);
			}
			else {
				rstMessage = (NetworkMessage) policy.getSchedulingMessage(buffer);
			}
			
		}
		catch(Exception e) {
			GlobalLogger.error(Errors.NULL_POLICY, "No scheduling policy defined");
		}
		
		return rstMessage;
	}
}
