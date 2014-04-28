package root.elements.network.modules.machine;

import java.util.ArrayList;
import java.util.Vector;

import logger.GlobalLogger;
import logger.XmlLogger;

import org.w3c.dom.Element;

import root.elements.network.modules.NetworkModule;
import root.elements.network.modules.link.Link;
import root.elements.network.modules.task.Message;
import root.util.constants.ConfigConstants;
import root.util.tools.NetworkAddress;

public class Machine extends NetworkModule {
	public NetworkAddress networkAddress;
	public Link[] portsOutput;
	public Link[] portsInput;
	
	/* Time for analysing a packet */
	public int analyseTime;
	/* Currently transmitted message */
	public Message currentlyTransmittedMsg;
	
	/* Contains all the messages generated by the machine */
	public ArrayList<Message> messageGenerator;
	
	public int ports_number;
	
	/* Buffering messages in an output buffer */
	public Vector<Message> outputBuffer;
	/* Buffering messages in an input buffer */
	public Vector<Message> inputBuffer;
	
	public String name; 
	
	/*XML LOG FILE*/
	public String logXML;
	public XmlLogger xmlLogger;
	
	
	public boolean needReload;
	
	public Machine(String name_, NetworkAddress addr_) throws Exception {
		super();
		name = name_;
		openPorts(ConfigConstants.CONST_PORT_NUMBER);
		networkAddress = addr_;
		outputBuffer = new Vector<Message>();
		inputBuffer  = new Vector<Message>();
		messageGenerator = new ArrayList<Message>();
		analyseTime = 0;
		createXMLLog();
		needReload = true;
	}
	
	public Machine(NetworkAddress addr_) throws Exception {
		this(""+addr_.value, addr_);
	}
	
	public void openPorts(int ports_) {
		portsOutput = new Link[ports_];
		portsInput  = new Link[ports_];
		ports_number = ports_;
	}
	
	public void setAddress(NetworkAddress networkAddress_) {
		/* Associate a networkAddress for current machine */
		this.networkAddress = networkAddress_;
	}
	
	public NetworkAddress getAddress() {
		return this.networkAddress;
	}
	
	public int connectOutput(Link link_) {
		/* Searching for next free port */
		int i = 0;
		while(portsOutput[i] != null){i++;}
		if(ports_number <= i) {
			GlobalLogger.warning("Can't connect machine : no free output port found");
			return 1;
		}
		
		portsOutput[i] = link_;
		
		return 0;
	}
	public int connectInput(Link link_) {
		/* Searching for next free port */
		int i = 0;
		while(portsInput[i] != null){i++;}
		if(ports_number <= i) {
			GlobalLogger.warning("Can't connect machine : no free input port found");
			return 1;
		}
		
		portsInput[i] = link_;
		
		return 0;
	}

	public int associateMessage(Message msg) {
		messageGenerator.add(msg);
		
		return 0;
	}
	
	public int sendMessage(Message msg) {
		GlobalLogger.debug("PUSHING "+msg.name+" MACHINE "+this.name);
		outputBuffer.add(msg);

		return 0;
	}
	
	public int generateMessage(int currentTime) {
		for(int i=0;i<messageGenerator.size();i++) {
			
			/* We get the message generator content. It includes all the messages
			 * which should be generated by a specified machine
			 */
			Message currentMsg = messageGenerator.get(i);
			Message newMsg = null;
						
			if(currentMsg.nextSend == currentTime) {
				try {
					//Make a copy for each periodic message
					newMsg = ((Message)messageGenerator.get(i).copy());
					newMsg.currentNode = 1;
					newMsg.name = currentMsg.name + "_" + currentMsg.nbExec;
					currentMsg.nbExec++;
					
					/* We put the copy in the input buffer of the generating node */
					inputBuffer.add(newMsg);
					if(currentMsg.period.get(0) != 0) {
						/* Periodic sending */
						currentMsg.nextSend += currentMsg.period.get(0);
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
	
	
	
	/* Display infos functions */
	
	public int displayCurrentMessage() {
		if(currentlyTransmittedMsg != null) {
			GlobalLogger.log("MACHINE "+networkAddress.value+":CURRENTLY TREATING :"+currentlyTransmittedMsg.name);			
		}
		else {
			GlobalLogger.log("MACHINE "+networkAddress.value+":NOTHING TO ANALYSE");
		}
		return 0;
	}
	
	public int displayOutputBuffer() {
		String message = "OuputBuffer de la machine "+networkAddress.value+"|";
		
		for(int cptMsgOutput = 0; cptMsgOutput < outputBuffer.size(); cptMsgOutput++) {
			Message currentMsg = outputBuffer.elementAt(cptMsgOutput);
			message += currentMsg.name;
		}
		message += "|";
		GlobalLogger.log(message);
		return 0;
	}
	
	public int displayInputBuffer() {
		String message = "InputBuffer de la machine "+networkAddress.value+"|";
		
		for(int cptMsgInput = 0; cptMsgInput < inputBuffer.size(); cptMsgInput++) {
			Message currentMsg = inputBuffer.elementAt(cptMsgInput);
			message += currentMsg.name;
		}
		message += "|";
		GlobalLogger.log(message);
		return 0;
	}
	
	/* XML Writing functions */
	public int writeLogToFile(int timer) {
		if(currentlyTransmittedMsg != null) {
			xmlLogger.addChild("timer", xmlLogger.getRoot(), "value:"+timer,
					"message:"+currentlyTransmittedMsg.name);		
		}
		else {
			xmlLogger.addChild("timer", xmlLogger.getRoot(), "value:"+timer+"");
		}
		return 0;
	}
	
	public int createXMLLog() {
		xmlLogger = new XmlLogger("Mac"+name+".xml");
		xmlLogger.createDocument();
		xmlLogger.createRoot("machine");
		xmlLogger.getRoot().setAttribute("id", this.name);
		return 0;
	}
}
