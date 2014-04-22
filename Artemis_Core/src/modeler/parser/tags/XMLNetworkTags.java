package modeler.parser.tags;

/**
 * 
 * @author olivier
 * List of tags for the xml network entry file
 * The objective of this parser is to build a java representation
 * of the network, starting from an entry xml file
 */
public class XMLNetworkTags {
	/* Config Tags */
	public static final String TAG_CONFIG			= "Config";
	public static final String TAG_NAME			= "name";
	public static final String TAG_P_INPUT		= "portsinput";
	public static final String TAG_P_OUTPUT		= "portsoutput";
	
	/* Machine tags */
	public static final String TAG_MACHINE		= "machine"; 
	public static final String TAG_LINKS			= "links"; 
	public static final String TAG_MACHINELINK	= "machinel"; 
	
	/* Message tags */
	public static final String TAG_MESSAGE 		= "message"; 
	public static final String TAG_PRIORITY 		= "priority"; 
	public static final String TAG_CRITICALITY 	= "criticality"; 
	public static final String TAG_PERIOD 		= "period"; 
	public static final String TAG_OFFSET			= "offset"; 
	public static final String TAG_WCET			= "wcet"; 
}