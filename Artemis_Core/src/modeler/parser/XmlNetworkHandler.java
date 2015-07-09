package modeler.parser;

import java.util.HashMap;
import java.util.Vector;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import logger.GlobalLogger;
import modeler.parser.tags.TriggerCodes;
import modeler.parser.tags.XMLNetworkTags;
import root.elements.network.Network;
import root.elements.network.modules.CriticalityLevel;
import root.elements.network.modules.CriticalitySwitch;
import root.elements.network.modules.machine.Machine;
import root.elements.network.modules.task.ISchedulable;
import root.elements.network.modules.task.MCMessage;
import root.elements.network.modules.task.NetworkMessage;
import root.util.Utils;
import root.util.constants.ComputationConstants;
import root.util.constants.ConfigParameters;
import root.util.tools.NetworkAddress;
import utils.Errors;


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
	
	/**
	 *  Triggers for XML Parsing
	 *  Associated with TriggerCodes, and XMLNetworkTags
	 */
	final private HashMap<TriggerCodes, Boolean>triggers;
	
	/** 
	 * Currently parsed criticality level name
	 */
	private String currCriticality;
	
	/**
	 * Currently computed criticality levels
	 */
	final private Vector<String> criticalities;
		
	/**
	 * Created messages
	 */
	final public HashMap<String, String>currMsgProp;
	
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
		triggers = new HashMap<TriggerCodes, Boolean>();
		
		triggers.put(TriggerCodes.MESSAGE, false);
		currMsgProp = new HashMap<String, String>();
		currCriticality = "NONCRITICAL";
		criticalities = new Vector<String>();

		for(TriggerCodes code : TriggerCodes.values()) {
			triggers.put(code, false);
		}
	}
	
	private void switchTrigger(final String qualif,final boolean trigger) {
		/* XML Tags triggers */
		if(qualif == XMLNetworkTags.TAG_MACHINE) {triggers.put(TriggerCodes.MACHINE, trigger);}
		if(qualif == XMLNetworkTags.TAG_WCET) {triggers.put(TriggerCodes.WCET, trigger);}
		if(qualif == XMLNetworkTags.TAG_CRITICALITY) {triggers.put(TriggerCodes.CRITICALITY, trigger);	}
		if(qualif == XMLNetworkTags.TAG_PRIORITY) {triggers.put(TriggerCodes.PRIORITY, trigger);}
		if(qualif == XMLNetworkTags.TAG_PERIOD) {triggers.put(TriggerCodes.PERIOD, trigger);}
		if(qualif == XMLNetworkTags.TAG_OFFSET) {triggers.put(TriggerCodes.OFFSET, trigger);}
		if(qualif == XMLNetworkTags.TAG_MESSAGE) {triggers.put(TriggerCodes.MESSAGE, trigger);}
		if(qualif == XMLNetworkTags.TAG_LINKS) {triggers.put(TriggerCodes.LINKS, trigger);}
		if(qualif == XMLNetworkTags.TAG_MACHINELINK) {triggers.put(TriggerCodes.MACHINELINK, trigger);}
		if(qualif == XMLNetworkTags.TAG_PATH) {triggers.put(TriggerCodes.PATH, trigger);}
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
			}
			
			/* We check if machine has already been created in the network */
			if(mainNet == null)
				GlobalLogger.debug("::NULL");
			currentMachine = mainNet.findMachine(idAddr, currMachName);
			
			/* We set the name of this new machine */
			currentMachine.name = currMachName;
			currentMachine.setSpeed(speed);
			
			if(GlobalLogger.DEBUG_ENABLED) {
				final String debug = "NAME"+currMachName;
				GlobalLogger.debug(debug);
			}
			
			return 1;
		}
		if(qualif == XMLNetworkTags.TAG_MACHINELINK) {
			/* If finding a tag for, machine link, 
			 * we search for the corresponding machines to bind them */
			final String idMachineToLink = pAttr.getValue(0);
			mainNet.linkMachines(currentMachine, mainNet.findMachine(Integer.parseInt(idMachineToLink), currentMachine.name));
			
			if(GlobalLogger.DEBUG_ENABLED) {
				final String debug = "link between "+currMachName +" and "+mainNet.findMachine(Integer.parseInt(idMachineToLink), currentMachine.name).name;
				GlobalLogger.debug(debug);
			}
			
			return 2; 
		}
		/* If new message, we just get its id */
		if(qualif == XMLNetworkTags.TAG_MESSAGE) { 
			final String idMsg = pAttr.getValue(0);
			currMsgProp.put("ID", idMsg);	
			
			final String dest = pAttr.getValue(1);
			currMsgProp.put("DEST", dest);	
			return 3;
		}
		if(qualif == XMLNetworkTags.TAG_CRITICALITY) {
			currCriticality = pAttr.getValue(0);
			
			if(!criticalities.contains(currCriticality)) {
				criticalities.addElement(currCriticality);
			}
			
			return 4;
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
		 
		 //End of message markup : creating a message
		 if(qName == XMLNetworkTags.TAG_MESSAGE) {
			 try {
				 ISchedulable newMsg;
				 
				 if(ConfigParameters.MIXED_CRITICALITY) {
					newMsg = new MCMessage(""); 
					for(int cptCrit=0;cptCrit < criticalities.size();cptCrit++) {
						/* Associating a wcet to each criticality level */
						final String rawWcet = currMsgProp.get(criticalities.get(cptCrit));
						double wcet;
						if(rawWcet == null) {
							wcet = 0;
						}
						else {
							wcet = Integer.parseInt(rawWcet);
						}
 
						final CriticalityLevel critLvl = Utils.convertToCritLevel(criticalities.get(cptCrit));
						newMsg.setWcet(wcet, critLvl);
						
						newMsg.setName("MSG"+currMsgProp.get("ID"));
						
					}
				 }
				 else {
					 GlobalLogger.debug(currMsgProp.get("WCET"));
					newMsg = new NetworkMessage(Integer.parseInt(currMsgProp.get("WCET")),
							"MSG"+currMsgProp.get("ID")); 
					
				 }
				
				if(currMsgProp.containsKey("PERI")) {
					GlobalLogger.debug(currMsgProp.get("PERI"));
					newMsg.setPeriod(Integer.parseInt(currMsgProp.get("PERI")));
				}
				
				if(currMsgProp.containsKey("OFFS")) {
					GlobalLogger.debug(currMsgProp.get("OFFS"));
					newMsg.setOffset(Integer.parseInt(currMsgProp.get("OFFS")));
					newMsg.setNextSend(Integer.parseInt(currMsgProp.get("OFFS")));
				}				
				

				if(currMsgProp.containsKey("PATH")) {
					/* We make a loop to build the message path in the network*/
					final String[] path = currMsgProp.get("PATH").split(",");
					for(int i=0; i < path.length ; i++) {
						/* For each node id in the path, we get its corresponding address */
						
						final NetworkAddress currentAddress = mainNet.findMachine(Integer.parseInt(path[i])).getAddress();
						newMsg.addNodeToPath(currentAddress); 
					}
					
				}	
				if(GlobalLogger.DEBUG_ENABLED) {
					final String debug = "ID:"+newMsg.getName();
					GlobalLogger.debug(debug);
				}
				
				currentMachine.associateMessage(newMsg);
				currMsgProp.clear();
			} catch (NumberFormatException e) {
				GlobalLogger.error(Errors.WCET_NOT_AN_INT, "WCET is not an int");
			}  
		 }
	} 
	 
	 /**
	  * Analyzes xml tag values
	  */
	 public void characters(final char[] pCh,final int start,final int length) {  
		String value = new String(pCh);
		value = value.substring(start, start+length);
		
		if(triggers.get(TriggerCodes.CRITICALITY)) {
			if(triggers.get(TriggerCodes.WCET)) {
				 //Save wcet value into a map
				if(ConfigParameters.MIXED_CRITICALITY) {
 					 currMsgProp.put(currCriticality, value);
				}
				else {
					 currMsgProp.put("WCET", value);
				}
				
			 }
			if(triggers.get(TriggerCodes.PATH)) {
				currMsgProp.put("PATH", value);
			}
			if(triggers.get(TriggerCodes.PERIOD)) {
				 currMsgProp.put("PERI", value);
			}
			if(triggers.get(TriggerCodes.PRIORITY)) {
				currMsgProp.put("PRIO", value);
			}
			if(triggers.get(TriggerCodes.OFFSET)) {
				currMsgProp.put("OFFS", value);
			}
		}
	 }
}