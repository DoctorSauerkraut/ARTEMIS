package simulator.policies;

import java.util.Vector;

import root.elements.network.modules.task.ISchedulable;

public class FIFOISolatedSchedulingPolicy implements ISchedulingPolicy {

	/* FIFO Policy to compute worst-case delay for a given packet */
	@Override
	public ISchedulable getSchedulingMessage(Vector<ISchedulable> buffer) {
		ISchedulable rstMessage = null;
		int cptBuffer = 0;
		
		for(cptBuffer=0;cptBuffer<buffer.size();cptBuffer++) {
			if(rstMessage == null || buffer.get(cptBuffer).getTimerArrival() < rstMessage.getTimerArrival()) {
				rstMessage = buffer.get(cptBuffer);
			}
			else if((buffer.get(cptBuffer).getTimerArrival() == rstMessage.getTimerArrival()) && 
				!buffer.get(cptBuffer).isObserved()){
				rstMessage = buffer.get(cptBuffer);
			}
		}
		
		return rstMessage;
	}

}
