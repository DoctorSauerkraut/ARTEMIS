package root.elements.network.modules.machine;

import java.util.ArrayList;
import java.util.Vector;

import logger.GlobalLogger;
import logger.XmlLogger;
import root.elements.network.modules.NetworkModule;
import root.elements.network.modules.task.ISchedulable;
import root.elements.network.modules.task.MCMessage;
import root.elements.network.modules.task.NetworkMessage;
import root.util.constants.ConfigParameters;
import root.util.tools.NetworkAddress;

public abstract class Node extends NetworkModule {
	/**
	 *  Address of the machine
	 */
	public NetworkAddress networkAddress;
	
	
	/**
	 *  Buffering messages in an output buffer 
	 */
	public Vector<ISchedulable> outputBuffer;
	
	/**
	 *  Buffering messages in an input buffer
	 */
	public Vector<ISchedulable> inputBuffer;

	/**
	 *  Time for analysing a packet 
	 */
	public double analyseTime;
	
	/**
	 *  Currently transmitted message
	 */
	public ISchedulable currentlyTransmittedMsg;
	
	/**
	 *  Contains all the messages generated by the machine 
	 */
	public ArrayList<ISchedulable> messageGenerator;
	
	/**
	 * Name of the machine
	 */
	public String name; 
	
	/**
	 * XML LOG FILE
	 */
	public String logXML;
	
	/** 
	 * XML Logger class
	 */
	public XmlLogger xmlLogger;
	
	/**
	 * Get current network address
 	 * @return the network address
	 */
	public NetworkAddress getNetworkAddress() {
		return this.networkAddress;
	}
	
	/**
	 * Sets current network address
	 * @param nAddr The network address
	 */
	public void setNetworkAddress(NetworkAddress nAddr) {
		this.networkAddress = nAddr;
	}
	
	public void setAddress(final NetworkAddress pNetworkAddress) {
		/* Associate a networkAddress for current machine */
		this.networkAddress = pNetworkAddress;
	}
	
	public NetworkAddress getAddress() {
		return this.networkAddress;
	}
	
	public int associateMessage(final ISchedulable msg) {
		messageGenerator.add(msg);
		
		return 0;
	}
	
	public int sendMessage(final ISchedulable msg) {
		if(GlobalLogger.DEBUG_ENABLED) {
			String debug = "PUSHING "+msg.getName()+" MACHINE "+this.name;
			GlobalLogger.debug(debug);
		}
		
		outputBuffer.add(msg);

		return 0;
	}
	
	public int generateMessage(final double currentTime) {
		for(int i=0;i<messageGenerator.size();i++) {
			
			/* We get the message generator content. It includes all the messages
			 * which should be generated by a specified machine
			 */
			ISchedulable currentMsg = messageGenerator.get(i);
			ISchedulable newMsg = null;
			
			if(currentMsg.getNextSend() == currentTime) {
				//GlobalLogger.debug("MSGGENOK:"+currentMsg.getName());
				try {
					//Make a copy for each periodic message
					if(ConfigParameters.MIXED_CRITICALITY) {
						newMsg = (((MCMessage) messageGenerator.get(i)).copy());
					}
					else {
						newMsg = (((NetworkMessage)messageGenerator.get(i)).copy());
					}
					
					newMsg.setCurrentNode(1);
					newMsg.setName(currentMsg.getName() + "_" + currentMsg.getNbExec());
					newMsg.setNextSend(currentTime);
					currentMsg.increaseNbExec();
					
					/* We put the copy in the input buffer of the generating node */
					inputBuffer.add(newMsg);
					if(currentMsg.getPeriod() != 0) {
						/* Periodic sending */
						currentMsg.setNextSend(currentMsg.getNextSend()+currentMsg.getPeriod());
					}
					else {
						/* Sporadic sending */
						messageGenerator.remove(i);
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}		
		}
		return 0;
	}
	
	/* XML Writing functions */
	public int writeLogToFile(final double timer) {
		if(currentlyTransmittedMsg != null) {
			xmlLogger.addChild("timer", xmlLogger.getRoot(), "value:"+timer,
					"message:"+currentlyTransmittedMsg.getName());		
		}
		else {
			xmlLogger.addChild("timer", xmlLogger.getRoot(), "value:"+timer+"");
		}
		return 0;
	}
	
	public XmlLogger createXMLLog() {
		xmlLogger = new XmlLogger(this.name.trim()+".xml");
		xmlLogger.createDocument();
		xmlLogger.createRoot("machine");
		//xmlLogger.getRoot().setAttribute("id", this.networkAddress.value);
		xmlLogger.getRoot().setAttribute("name", this.name);
		
		return xmlLogger;
	}
}
