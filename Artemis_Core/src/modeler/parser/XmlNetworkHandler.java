package modeler.parser;

import org.xml.sax.Attributes;
import modeler.parser.tags.TriggerCodes;
import modeler.parser.tags.XMLNetworkTags;
import root.elements.criticality.CriticalityLevel;
import root.elements.network.Network;
import root.elements.network.modules.machine.Machine;


/**
 * @author olivier
 *Event-based xml parser
 */

public class XmlNetworkHandler extends XmlDefaultHandler{

	/** 
	 * Currently built machine
	 */
	private Machine currentMachine;
	
	/**
	 * Name of the current machine
	 */
	private String currMachName;
		

	/** Accessors **/
	
	/**
	 * Setter for main network
	 * @param pMainNet
	 */
	public void setMainNet(final Network pMainNet) {
		this.mainNet = pMainNet;
	}
	
	/**
	 * Getter for current machine
	 * @return
	 */
	public Machine getCurrentMachine() {
		return currentMachine;
	}
	
	/**
	 * Getter for current machine
	 * @return
	 */
	public String getCurrentMachineName() {
		return currMachName;
	}
	
	/** 
	 * XML Parser Handler default constructor
	 */
	public XmlNetworkHandler(Network mainNetP) {
		super();
		this.mainNet = mainNetP;
	}
	
	private void switchTrigger(final String qualif,final boolean trigger) {
		/* XML Tags triggers */
		if(qualif == XMLNetworkTags.TAG_MACHINE) {triggers.put(TriggerCodes.MACHINE, trigger);}
		if(qualif == XMLNetworkTags.TAG_LINKS) {triggers.put(TriggerCodes.LINKS, trigger);}
		if(qualif == XMLNetworkTags.TAG_MACHINELINK) {triggers.put(TriggerCodes.MACHINELINK, trigger);}
	}

	/**
	 *  Parse machine-linked tags 
	 */
	public int detectConfMachine(final String uri,final String name,final String qualif,final Attributes pAttr) {
		if(qualif == XMLNetworkTags.TAG_MACHINE) {
			//End of machine markup
			//We get the specific id, then create the machine
			int idAddr = 0;
			currMachName = "";
			int speed = 1;
			String central = "N";
			
			for(int cptAttr=0;cptAttr < pAttr.getLength();cptAttr++) {
				if(pAttr.getLocalName(cptAttr).compareTo("id") == 0) {
					idAddr = Integer.parseInt(pAttr.getValue(cptAttr));
					
					if(currMachName.compareTo("") == 0) {
						currMachName = "Node "+idAddr;
					}
				}		
				if(pAttr.getLocalName(cptAttr).compareTo("name") == 0) {
					currMachName = pAttr.getValue(cptAttr);
				}
				if(pAttr.getLocalName(cptAttr).compareTo("speed") == 0) {
					speed = Integer.parseInt(pAttr.getValue(cptAttr));
				}
				if(pAttr.getLocalName(cptAttr).compareTo("central") == 0) {
					central = pAttr.getValue(cptAttr);
				}
			}
			
			/* We check if machine has already been created in the network */
			currentMachine = mainNet.findMachine(idAddr, currMachName);
			
			/* We set the name of this new machine */
			currentMachine.name = currMachName;
			currentMachine.setSpeed(speed);
			
			/* The basic criticality level of each machine is non-critical */
			currentMachine.setCritLevel(CriticalityLevel.NONCRITICAL);
			
			if(central.equals("Y")) {
				mainNet.setCentralNode(currentMachine);
			}
			return 1;
		}
		if(qualif == XMLNetworkTags.TAG_MACHINELINK) {
			/* If finding a tag for, machine link, 
			 * we search for the corresponding machines to bind them */
			final String idMachineToLink = pAttr.getValue(0);
			Machine destination = mainNet.findMachine(Integer.parseInt(idMachineToLink), currentMachine.name);
			//if(!destination.name.startsWith("ES")) {
				//GlobalLogger.debug("LINK:"+currentMachine.networkAddress.value+"-"+destination.networkAddress.value);
				mainNet.linkMachines(currentMachine, destination);
			//}
			//else {
				/* If destination machine is an end system, it can't have output links */
				/*if(destination.portsOutput == null || destination.portsOutput[0].getBindRightMachine() == null) {
					mainNet.linkMachines(currentMachine, destination);
				}*/
		//	}
			
			return 2; 
		}
		
		return 0;
	}
	
	/**
	 *  Start element 
	 */
	 public void startElement(final String uri, final String name, final String qualif, final Attributes pAttr) {
		switchTrigger(qualif, true);
		
		final int result = this.detectConfMachine(uri, name, qualif, pAttr);
		
	}
	 
	 /**
	  *  Called at each element's end 
	  */
	 public void endElement(final String uri,final String name,final String qName) {
		 switchTrigger(qName, false);	 

	} 
	 
	 /**
	  * Analyzes xml tag values
	  */
	 
}
