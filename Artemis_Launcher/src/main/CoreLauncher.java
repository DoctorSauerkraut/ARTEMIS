package main;

import logger.GlobalLogger;
import logger.XmlLogger;
import modeler.networkbuilder.NetworkBuilder;
import root.util.constants.ConfigParameters;
import simulator.managers.NetworkScheduler;
import utils.ConfigLogger;
import utils.Errors;

/**
 * Artemis launcher
 * @author olivier
 * Centralizes all core java artemis modules to launch them
 */
public class CoreLauncher {
	
	public static void main(String[] args) {
		String simuId = args[0];
		/* Default case */
		if(args[0] == "") {
			simuId = "000";
		}
		
		/* We get the simulation id from interface */
		ConfigParameters.getInstance().setSimuId(simuId);
		
		double startSimulationTime = System.currentTimeMillis();
	
		launchSimulation();
		
		double endSimulationTime = System.currentTimeMillis();
		
		GlobalLogger.log("Simulation done in "+(endSimulationTime-startSimulationTime)+" ms");
	}
	
	public static void launchSimulation(String logFile) {
		/* Create and simulate network */
		try {	
			/* Initalizes scheduler */
			NetworkScheduler nScheduler = null;
			
			String xmlInputFile = ConfigLogger.RESSOURCES_PATH+"/"+
					ConfigParameters.getInstance().getSimuId()+"/";
					
			//GlobalLogger.log("------------ EMPTY XML OUTPUT DIRECTORY ------------");
			XmlLogger.prepareSimulation(xmlInputFile);
			NetworkBuilder nBuilder = new NetworkBuilder(xmlInputFile);
			/* Parse the network input file */
			nBuilder.prepareNetwork();
			
			//GlobalLogger.log("------------ LAUNCHING MODELIZER ------------");
			
			nBuilder.prepareMessages();
		
			//GlobalLogger.log("------------ CRITICALITY SWITCHES ------------");
			nBuilder.getMainNetwork().showCritSwitches();
			
			//GlobalLogger.log("------------ LAUNCHING SCHEDULER ------------");
			
			if(nBuilder.getMainNetwork() != null) {
				nScheduler = new NetworkScheduler(nBuilder.getMainNetwork());
			}
			
			if(nScheduler != null) {
				/* Launch network behavior simulation */
				nScheduler.run();
			}
			else {
				GlobalLogger.error(Errors.NULL_SCHEDULER_AT_LAUNCH, "Scheduler is null, error on network topology");
			}
			
			//GlobalLogger.log("------------ SIMULATION DONE ------------");

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void launchSimulation() {
		launchSimulation(ConfigParameters.SIMULOGFILE);
	}
}
