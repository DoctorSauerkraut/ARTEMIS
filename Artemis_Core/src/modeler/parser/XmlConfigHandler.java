package modeler.parser;

import logger.GlobalLogger;
import modeler.parser.tags.TriggerCodes;
import modeler.parser.tags.XMLNetworkTags;

import org.xml.sax.Attributes;

import root.elements.criticality.CriticalityLevel;
import root.elements.criticality.CriticalityModel;
import root.elements.criticality.CriticalityProtocol;
import root.elements.criticality.CriticalitySwitch;
import root.elements.network.Network;
import root.util.Utils;
import root.util.constants.ComputationConstants;
import root.util.constants.ConfigParameters;

public class XmlConfigHandler extends XmlDefaultHandler {
	/** 
	 * Criticality switch
	 */
	private CriticalitySwitch currentCritSwitch;
	
	public XmlConfigHandler() {
		super();
		mainNet = new Network();
		
		currentCritSwitch = new CriticalitySwitch();
	}
	
	private void switchTrigger(final String qualif,final boolean trigger) {
		/* XML Tags triggers */
		if(qualif == XMLNetworkTags.TAG_CRIT_SWITCH){triggers.put(TriggerCodes.CRITSWITCH, trigger);}
		if(qualif == XMLNetworkTags.TAG_TIME_LIMIT){triggers.put(TriggerCodes.TIMELIMIT, trigger);}
		if(qualif == XMLNetworkTags.TAG_ELECTRONICAL_LATENCY){triggers.put(TriggerCodes.ELECTRONICALLATENCY, trigger);}
		if(qualif == XMLNetworkTags.TAG_AUTOGEN_TASKS){triggers.put(TriggerCodes.AUTOGENTASKS, trigger);}
		if(qualif == XMLNetworkTags.TAG_HIGH_WCTT){triggers.put(TriggerCodes.HIGHESTWCTT, trigger);}
		if(qualif == XMLNetworkTags.TAG_AUTO_TASKS){triggers.put(TriggerCodes.AUTOGENNUMBER, trigger);}
		if(qualif == XMLNetworkTags.TAG_AUTO_LOAD){triggers.put(TriggerCodes.AUTOLOAD, trigger);}
		if(qualif == XMLNetworkTags.TAG_SPEED_MACHINE){triggers.put(TriggerCodes.SPEEDMACHINE, trigger);}
		if(qualif == XMLNetworkTags.TAG_WCTT_COMPUTE){triggers.put(TriggerCodes.WCTT_COMPUTE, trigger);}
		if(qualif == XMLNetworkTags.TAG_WCTT_RATE){triggers.put(TriggerCodes.WCTT_RATE, trigger);}
		if(qualif == XMLNetworkTags.TAG_MC_PROTO){triggers.put(TriggerCodes.MC_PROTO, trigger);}
		if(qualif == XMLNetworkTags.TAG_MC_MODEL){triggers.put(TriggerCodes.MC_MODEL, trigger);}
		if(qualif == XMLNetworkTags.TAG_WORST_CASE_ANALYSIS){triggers.put(TriggerCodes.WC_ANALYSIS, trigger);}
	}
	
	/**
	 *  Start element 
	 */
	 public void startElement(final String uri, final String name, final String qualif, final Attributes pAttr) {
		switchTrigger(qualif, true);
		
		if(qualif == XMLNetworkTags.TAG_CRIT_SWITCH) {
			for(int cptAttr=0;cptAttr < pAttr.getLength();cptAttr++) {
				
				if(pAttr.getLocalName(cptAttr).compareTo("time") == 0) {
					currentCritSwitch.setTime(Integer.parseInt(pAttr.getValue(cptAttr)));
				}
			}
		}
	}
	 
	 /**
	  * Analyzes xml tag values
	  */
	 public void characters(final char[] pCh,final int start,final int length) {  
		String value = new String(pCh);
		value = value.substring(start, start+length);
		 
		if(triggers.get(TriggerCodes.WCTT_COMPUTE)) {
			final ConfigParameters config = ConfigParameters.getInstance();			
			config.setWCTTModel(value);
		}
		
		if(triggers.get(TriggerCodes.MC_PROTO)) {
			if(value.equals("Centralized")) {
				ComputationConstants.getInstance().setCritProtocol(
						CriticalityProtocol.CENTRALIZED);
			}
			else if(value.equals("Decentralized")) {
				ComputationConstants.getInstance().setCritProtocol(
						CriticalityProtocol.DECENTRALIZED);
			}else {
				ComputationConstants.getInstance().setCritProtocol(
						CriticalityProtocol.CENTRALIZED);
			}
		}
		
		if(triggers.get(TriggerCodes.WC_ANALYSIS)) {
			if(value.equals("true")) {
				ComputationConstants.getInstance().setWorstCaseAnalysis(true);
			}
			else {
				ComputationConstants.getInstance().setWorstCaseAnalysis(false);
			}
		}
		
		if(triggers.get(TriggerCodes.MC_MODEL)) {		
			if(value.equals("S")) {
				ComputationConstants.getInstance().setCritModel(
						CriticalityModel.STATIC);
			}
			else if(value.equals("D")) {
				ComputationConstants.getInstance().setCritModel(
						CriticalityModel.DYNAMIC);
			}
			else {
				ComputationConstants.getInstance().setCritModel(
						CriticalityModel.STATIC);
			}
		}
		
		if(triggers.get(TriggerCodes.WCTT_RATE)) {
			final ConfigParameters config = ConfigParameters.getInstance();
			config.setWCTTRate(Double.parseDouble(value));
		}
		
		if(triggers.get(TriggerCodes.TIMELIMIT)) {
			final ConfigParameters config = ConfigParameters.getInstance();			
			config.setTimeLimitSimulation(Integer.parseInt(value));
		}
		
		/* Electronical latency management */
		if(triggers.get(TriggerCodes.ELECTRONICALLATENCY)) {
			final ConfigParameters config = ConfigParameters.getInstance();			
			config.setElectronicalLatency(Integer.parseInt(value));
		}
		
		/* Tasks auto-generation activation */
		if(triggers.get(TriggerCodes.AUTOGENTASKS)) {
			final ConfigParameters config = ConfigParameters.getInstance();		
			if(value.compareTo("0") == 0) {
				config.setAutomaticTaskGeneration(true);
			}
			else {
				config.setAutomaticTaskGeneration(false);
			}
		}
	
		if(triggers.get(TriggerCodes.AUTOGENNUMBER)) {
			final ConfigParameters config 			= ConfigParameters.getInstance();	
			final ComputationConstants simuConst 	= ComputationConstants.getInstance();
			
			if(config.getAutomaticTaskGeneration()) {
				simuConst.setGeneratedTasks(Integer.parseInt(value));
			}
		}
		
		if(triggers.get(TriggerCodes.HIGHESTWCTT)) {
			final ConfigParameters config 			= ConfigParameters.getInstance();	
			final ComputationConstants simuConst 	= ComputationConstants.getInstance();
			
			if(config.getAutomaticTaskGeneration()) {
				simuConst.setHighestWCTT(Integer.parseInt(value));
			}
		}
		
		if(triggers.get(TriggerCodes.AUTOLOAD)) {
			final ConfigParameters config 			= ConfigParameters.getInstance();	
			final ComputationConstants simuConst 	= ComputationConstants.getInstance();
			
			if(config.getAutomaticTaskGeneration()) {
				if(simuConst.getAutoLoad() == -1) {
					simuConst.setAutoLoad(Double.parseDouble(value));
				}
			}
		}
		
		if(triggers.get(TriggerCodes.CRITSWITCH)) {
			final CriticalityLevel newLevel = Utils.convertToCritLevel(value);
			currentCritSwitch.setCritLvl(newLevel);
		}
	 }
	 
	 public void endElement(final String uri,final String name,final String qName) {
		 switchTrigger(qName, false);	 
		 
		 /* Criticality managing */
		 if(qName == XMLNetworkTags.TAG_CRIT_SWITCH) {
			 
			 final CriticalitySwitch critSwitch = new CriticalitySwitch();
			 	if(currentCritSwitch.getTime() == 0 || currentCritSwitch.getCritLvl() == null) {
			 		GlobalLogger.warning("WARNING ON CREATING CRITICALITY LEVEL : null parameter");
					
			 	}
			 	else {
			 		critSwitch.setTime(currentCritSwitch.getTime());
					critSwitch.setCritLvl(currentCritSwitch.getCritLvl());
					mainNet.critSwitches.addElement(critSwitch);
					
					currentCritSwitch.setTime(0);
					currentCritSwitch.setCritLvl(null);
		 		}	
		 }
	 }
	 
}
