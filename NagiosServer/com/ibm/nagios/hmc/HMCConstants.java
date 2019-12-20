
package com.ibm.nagios.hmc;

public class HMCConstants {
	// was /QOpenSys/QIBM/ProdData/OPS/cloudinit
	public final static String CLONE_INFO_JSON_PARENT_PATH = "/QOpenSys/pkgs/lib/cloudinit/cloud/seed/config_drive/openstack/latest/";
	public final static String META_DATA_JSON_PARENT_PATH = "/QOpenSys/pkgs/lib/cloudinit/cloud/seed/config_drive/openstack/latest/";
	public final static String CLONE_INFO_JSON_FILE_PATH = "/QOpenSys/pkgs/lib/cloudinit/cloud/seed/config_drive/openstack/latest/clone_info.json";
	public final static String META_DATA_JSON_FILE_PATH = "/QOpenSys/pkgs/lib/cloudinit/cloud/seed/config_drive/openstack/latest/meta_data.json";
	//public final static String CLONE_INFO_JSON_FILE_PATH = "./clone_info.json";
	public final static String CLONE_INFO_JSON_FILE_PATH_DEBUG = "E:\\DB2Mirror\\startup\\toolkit\\clone_info.json";
	//public final static String CLONE_INFO_JSON_FILE_PATH = "/QIBM/ProdData/OS400/MRDB/Initialization/SetupConfig/clone_info.json";

	public final static String SETUP_TOOLS_USER_DATA_PATH = "/QIBM/UserData/QDB2MIR/MRDB/TOOLS";
	public final static String SETUP_TOOLS_PROD_DATA_PATH = "/QIBM/ProdData/QDB2MIR/MRDB/TOOLS";
	public final static String SETUP_TOOLS_USER_CERT_PATH = "/QIBM/UserData/QDB2MIR/MRDB/TOOLS/CERT";
	
	public final static String DEFAULT_KEYSTORE_FILE = "/QIBM/USERDATA/ICSS/CERT/SERVER/DEFAULT.KDB";
	
	public final static String MRDB_LOG_LEVEL_PROP_NAME = "com.ibm.db2mirror.toolkit.level";//for GUI only, command don't need it

	//for main class parameters
	public final static String PARAM_DSCLI_PATH = "dscli";
	public final static String PARAM_LOGFILE_PATH = "logFile";
	public final static String PARAM_LOGFILE_APPEND = "logAppend";
	public final static String PARAM_LOGFILE_LEVEL = "logLevel";
	public final static String PARAM_ACTION = "action";
	public final static String PARAM_LOCAL_STORAGE_USER = "lclStgUser";
	public final static String PARAM_LOCAL_STORAGE_PASSWORD = "lclStgPass";
	public final static String PARAM_LOCAL_STORAGE_FILE = "lclStgFile";
	public final static String PARAM_REMOTE_STORAGE_USER = "rmtStgUser";
	public final static String PARAM_REMOTE_STORAGE_PASSWORD = "rmtStgPass";
	public final static String PARAM_REMOTE_STORAGE_FILE = "rmtStgFile";
	public final static String PARAM_LOCAL_POWER_USER = "lclPowerHMCUser";
	public final static String PARAM_LOCAL_POWER_PASSWORD = "lclPowerHMCPass";
	public final static String PARAM_REMOTE_POWER_USER = "rmtPowerHMCUser";
	public final static String PARAM_REMOTE_POWER_PASSWORD = "rmtPowerHMCPass";
	public final static String PARAM_COPY_KEYSTORE_FILE = "copyKeyStore";
	public final static String PARAM_KEYSTORE_PASSWORD = "ksPass";
	public final static String PARAM_IBMI_KEYSTORE_PASSWORD = "ibmiksPass";
	public final static String PARAM_CERT_FILE = "certFile";
	public final static String PARAM_CERT_FILE_FORMAT = "certFileFormat";
	public final static String PARAM_POWER_ONOFF_LPAR = "lpar";
	public final static String PARAM_LOCAL_IBMI_USER = "lclIBMiUser";
	public final static String PARAM_LOCAL_IBMI_PASSWORD = "lclIBMiPass";
	public final static String PARAM_REMOTE_IBMI_USER = "rmtIBMiUser";
	public final static String PARAM_REMOTE_IBMI_PASSWORD = "rmtIBMiPass";
	public final static String PARAM_KEY_STRING = "key";
	public final static String PARAM_PLUGIN_PATH = "plugin";
	public final static String PARAM_PRECHECK_ITEM = "item";
	public final static String PARAM_JSON_CONFIG_PATH = "config";
	public final static String PARAM_POWER_OFF_TIMEOUT = "timeout";
	public final static String PARAM_POWER_OFF_OPERATION = "operation";
	public final static String PARAM_STEP_LOG_FILE = "stepLog";
	public final static String PARAM_POWER_OFF_SETUP_COPY = "poweroffCopy";
	public final static String PARAM_PASSWORD_ENCRYPTED = "pwEncrypted";
	public final static String PARAM_SVC_COPY_RATE = "svcCopyRate";
	public final static String PARAM_SVC_CLEAN_RATE = "svcCleanRate";
	public final static String PARAM_SETUP_SOURCE_HOSTNAME = "setupSrcAddress";//where to get the json file? this should be the setup source system
	public final static String PARAM_SETUP_SOURCE_USER = "setupSrcUser";
	public final static String PARAM_SETUP_SOURCE_PASSWORD = "setupSrcPassword";
	public final static String PARAM_SETUP_SOURCE_SYSTEM_NAME = "setupSrcSysName";
	public final static String PARAM_SETUP_COPY_SYSTEM_NAME = "setupCpySysName";
	public final static String PARAM_SETUP_SOURCE_UUID = "setupSrcUUID";
	public final static String PARAM_SETUP_COPY_UUID = "setupCpyUUID";
	public final static String PARAM_DB_IASP_NAME = "dbIaspName";
	public final static String PARAM_DB_IASP_CLONE_STATE = "iaspCloneState";
	public final static String PARAM_DB_IASP_AUTO_VARY_OFF = "dbIaspAutoVaryOff";
	public final static String PARAM_DB_IASP_AUTO_VARY_ON = "dbIaspAutoVaryOn";
	public final static String PARAM_FROM_GUI = "gui";
	public final static String PARAM_INIT_SETUP = "init";
	public final static String PARAM_POWER_HOST= "powerHMCHost";
	public final static String PARAM_POWER_USER = "powerHMCUser";
	public final static String PARAM_POWER_PASSWORD = "powerHMCPass";
	public final static String PARAM_SYSTEM_NAME = "systemName";
	public final static String PARAM_PARTITION_NAME = "partitionName";
	public final static String PARAM_MASTER_KEY= "masterKey";
	public final static String PARAM_SNAPSHOT_COMPARE = "snapshotCompare";
	public final static String PARAM_QUORUM_ADDRESS = "quorumAddress";
	public final static String PARAM_QUORUM_USER = "quorumUser";
	public final static String PARAM_QUORUM_PASSWORD = "quorumPassword";
	public final static String PARAM_ARE = "are";
	public final static String PARAM_ARE_PLUGIN = "arePlugin";
	public final static String PARAM_USE_DS8K_REST = "ds8kRest";
	public final static String PARAM_IASP_NAME = "iasp";

	//valid actions
	public final static String VALID_ACTION_VALUE_INSTALL_CERTIFICATE = "installCertificate";
	public final static String VALID_ACTION_VALUE_LIST_CERTIFICATE = "listCertificate";
	public final static String VALID_ACTION_VALUE_START = "start";
	public final static String VALID_ACTION_VALUE_HELP = "help";
	public final static String VALID_ACTION_VALUE_POWERON = "poweron";
	public final static String VALID_ACTION_VALUE_POWEROFF = "poweroff";
	public final static String VALID_ACTION_VALUE_SUSPEND = "suspend";
	public final static String VALID_ACTION_VALUE_RESUME = "resume";
	public final static String VALID_ACTION_VALUE_PRECHECK = "precheck";
	public final static String VALID_ACTION_VALUE_FLASHCOPY = "flashcopy";
	public final static String VALID_ACTION_VALUE_GLOBALCOPY = "remotecopy";
	public final static String VALID_ACTION_VALUE_ATTACH = "attach";
	public final static String VALID_ACTION_VALUE_IASP_COPY = "iaspcopy";
	public final static String VALID_ACTION_VALUE_PRE_IASP_COPY = "preiaspcopy";
	public final static String VALID_ACTION_VALUE_POST_IASP_COPY = "postiaspcopy";
	public final static String VALID_ACTION_VALUE_CLEANUP = "cleanup";
	public final static String VALID_ACTION_VALUE_CHECK_RESULT_SYSBAS = "checksysbas";
	public final static String VALID_ACTION_VALUE_START_WARM_CLONE = "startwarmclone";
	public final static String VALID_ACTION_VALUE_START_IASP_WARM_CLONE = "startiaspwarmclone";
	public final static String VALID_ACTION_VALUE_REVERT_CLONE_INFO = "revertcloneinfo";
	public final static String VALID_ACTION_VALUE_REVERSE_CLONE_INFO = "reversecloneinfo";
	public final static String VALID_ACTION_VALUE_RETRIEVE_UUID = "retrieveuuid";
	public final static String VALID_ACTION_VALUE_SNAPSHOT = "snapshot";
	public final static String VALID_ACTION_RUN_CLOUDINIT = "runcloudinit";
	public final static String VALID_ACTION_NRG_READY = "nrgready";
	public final static String VALID_ACTION_CONFIG_COMPLETE = "configcomplete";
	public final static String VALID_ACTION_VALUE_CONFIG_COMPARE = "configcompare";
	public final static String VALID_ACTION_DELETE_IASP = "deleteiasp";
	public final static String VALID_ACTION_LIST_IASP = "listiasp";

	//ds8k
	public final static String DS8K_NONE = " - ";
	public final static String PARAM_DS8K_RESTORE_SCRIPT = "ds8KRestoreScript";
	public final static String VOLUME_STATUS_DEGRADED = "Degraded";
	public final static String VOLUME_STATUS_ONLINE = "Online";

	//for flash copy
	public final static String MSG_NO_FLASHCOPY = "CMUC00234I lsflash: No Flash Copy found.";
	public final static String MSG_SUCCESS_CREATE = "successfully created";
	public final static String MSG_SUCCESS_DELETE = "successfully deleted";
	public final static String MSG_SUCCESS_MODIFY = "successfully modified";

	public final static String MSG_NO_PPRC = "CMUC00234I lspprc: No Remote Mirror and Copy found.";

	//for CLI
	public final static int SVC_RCCONSISTGRP_STATE_CHECKING_INTERVAL = 600;
	public final static String SVC_RCCONSISTGRP_STATE_CONSISTENT_STOPPED = "consistent_stopped";
	public final static String SVC_RCCONSISTGRP_STATE_CONSISTENT_SYNCHRONIZED = "consistent_synchronized";
	public final static String SVC_RCCONSISTGRP_STATE_CONSISTENT_COPYING = "consistent_copying";
	public final static String SVC_RCCONSISTGRP_STATE_INCONSISTENT_COPYING = "inconsistent_copying";
	public final static String SCV_FCCONSISTGRP_STATE_PREPARED = "prepared";
	public final static String SVC_FCMAPPING_STATE_COPYING = "copying";
	public final static String SVC_RCRELATIONSHIP_STATE_IDLING = "idling";
	public final static String SVC_GMCV_CHANGE_VOLUME_SUFFIX = "_cv";

	//svc message
	public final static String MSG_SVC_NO_HOST = "CMMVC5754E The specified object does not exist, or the name supplied does not meet the naming rules.";
	public final static String MSG_SVC_NO_FCRC_CONSISTGRP = "CMMVC5804E The action failed because an object that was specified in the command does not exist.";
	public final static String MSG_SVC_NO_VDISK ="CMMVC5753E The specified object does not exist or is not a suitable candidate.";
	public final static String MSG_SVC_NO_FORCE = "CMMVC6045E The action failed, as the -force flag was not entered";
	public final static String MSG_SVC_ALREADY_MAPPED = "CMMVC5878E The host mapping was not created because this volume is already mapped to this host.";
	//ds8k message
	public final static String MSG_DS8K_NO_VOLUME_GROUP = "CMUC00234I"; //CMUC00234I lsvolgrp: No Volume Group found.
	public final static String MSG_DS8K_NO_VOLUME_FOUND = "CMUC00234I";//CMUC00234I lsfbvol: No FB Volume found.
	//for HMC REST API
	public final static String HTTPS = "https://";
	public static final String PORT = ":" + HMCConstants.HMC_PORT;
	public final static String HMC_PORT = "12443";
	public final static String HMC_REST_ROOT_CONTEXT_PATH = "/rest/api/uom";
	public final static String HMC_REST_SCHEMA = "/rest/api/web/schema";
	public final static String HMC_REST_SEARCH = "/search";
	public final static String HMC_REST_QUICK = "/quick";
	public final static String HMC_REST_QUICK_ALL = "/quick/all";
	public final static String HMC_REST_MANAGED_SYSTEM = "ManagedSystem";
	public final static String HMC_REST_LOGICAL_PARTITION = "LogicalPartition";

	//for DS8K REST API
	public final static String DS8K_HTTPS = "https://";
	public final static String DS8K_PORT = "8452";
	public final static String DS8K_TOKEN_LIVE = "86400000";//millisecond
	public final static String DS8K_TOKEN__IDLE_LIVE = "86400000";
	public final static String DS8K_API_TOKENS = "/api/v1/tokens";
	public final static String DS8K_API_HOSTS = "/api/v1/hosts";
	public final static String DS8K_API_SYSTEMS = "/api/v1/systems";
	public final static String DS8K_API_HOST_VOLUMES = "/api/v1/hosts/{0}/volumes";
	public final static String DS8K_API_LSS = "/api/v1/lss";
	public final static String DS8K_API_LSS_VOLUMES = "/api/v1/lss/{0}/volumes";
	public final static String DS8K_API_VOLUME = "/api/v1/volumes/{0}";
	public final static String DS8K_API_HOST_MAPPING = "/api/v1/hosts/{0}/mappings";
	public final static String DS8K_API_HOST_MAPPING_DELETE = "/api/v1/hosts/{0}/mappings/{1}";
	public final static String DS8K_API_PPRC = "/api/v1/cs/pprcs?volume_id_from={0}&volume_id_to={1}";
	public final static String DS8K_API_PPRC_FROM = "/api/v1/cs/pprcs?volume_id_from={0}";
	public final static String DS8K_API_PPRC_TO = "/api/v1/cs/pprcs?volume_id_to={0}";
	public final static String DS8K_API_PPRCS = "/api/v1/cs/pprcs";
	public final static String DS8K_API_FLASHCOPY = "/api/v1/cs/flashcopies?volume_id_from={0}&volume_id_to={1}";
	public final static String DS8K_API_FLASHCOPY_FROM = "/api/v1/cs/flashcopies?volume_id_from={0}";
	public final static String DS8K_API_FLASHCOPY_TO = "/api/v1/cs/flashcopies?volume_id_to={0}";
	public final static String DS8K_API_FLASHCOPIES = "/api/v1/cs/flashcopies";
	public final static String DS8K_API_FLASHCOPIES_DELETE = "/api/v1/cs/flashcopies/{0}";
	public final static String DS8K_API_FLASHCOPIES_DELETE_MULTI = "/api/v1/cs/flashcopies?ids={0}";
	public final static String DS8K_API_UNFREEZE_FLASHCOPIES = "/api/v1/cs/flashcopies/unfreeze";
	public final static String DS8K_HEADER_AUTHTOKEN = "X-Auth-Token";
	public final static String DS8K_HEADER_CONTENTTYPE = "application/json";
	public final static String DS8K_RESPONSE_SERVER = "server";
	public final static String DS8K_RESPONSE_COUNT = "count";
	public final static String DS8K_RESPONSE_DATA = "data";
	public final static String DS8K_RESPONSE_SERVER_STATUS_OK = "ok";

	//for CSM REST API
	public final static String CSM_PORT = "443";
	public final static String CSM_HEADER_CONTENTTYPE = "application/json";
	public final static String CSM_HEADER_ACCEPT = "Accept";
	public final static String CSM_SESSION = "/sessions/{0}";
	public final static String CSM_SESSIONS = "/sessions";
	public final static String CSM_STORAGE_DEVICES_DS8K = "/storagedevices?type=ESS_DS";
	public final static String CSM_STORAGE_DEVICES = "/storagedevices";
	public final static String CSM_ADD_COPYSETS = "/sessions/{0}/copysets";
	public final static String CSM_REMOVE_COPYSETS = "/sessions/{0}/false/false/copysets";
	public final static String CSM_STGTYPE_DS8000 = "DS8000";
	public final static String CSM_SESSION_STATUS_NORMAL = "Normal";
	public final static String CSM_SESSION_STATUS_INACTIVE = "Inactive";
	public final static String CSM_SESSION_STATUS_WARNING = "Warning";
	public final static String CSM_SESSION_STATUS_SEVERE = "Severe";
	public final static String CSM_SESSION_STATE_DEFINED = "Defined";
	public final static String CSM_SESSION_STATE_PREPARED = "Prepared";
	public final static String CSM_SESSION_STATE_SUSPENDED = "Suspended";
	public final static String CSM_SESSION_STATE_SUSPENDEING = "Suspending";
	public final static int CSM_STATE_CHECKING_INTERVAL = 60;
	public final static String CSM_SYSTEM_HA = "/system/ha";
	public final static String CSM_SYSTEM_HA_RECONNECT = "/system/ha/reconnect";
	public final static String CSM_SYSTEM_HA_TAKEOVER = "/system/ha/takeover";


	public final static String PASSWORD_KEY = "{hmc_password}";
	public final static String USER_ID_KEY = "{hmc_user_id}";
	public final static String IMMEDIATE_KEY = "{immed_or_not}";
	public final static String PARTITION_PROFILE = "{LogicalPartitionProfile}";
	public final static String PROFILE_UUID = "{profile_uuid}";
	public final static String DEFAULT_KEYSTORE_PASSWORD = "changeit";
	public final static String JOB_STATUS_COMPLETED_OK = "COMPLETED_OK";
	public final static String JOB_STATUS_NOT_STARTED = "NOT_STARTED";
	public final static String JOB_STATUS_RUNNING = "RUNNING";
	public final static int DEFAULT_POWEROFF_TIMEOUT = 10800;
	public final static int DEFAULT_POWERON_TIMEOUT = 10800;
	public final static int REFERENCE_CODE_SHOW_INTERVAL = 60;
	public final static int HMC_PARTITION_UUID_LENGTH = 36;

	//xml node id
	public static final String XML_SESSION_ID = "X-API-Session";
	public static final String XML_JOB_ID = "JobID";
	public static final String XML_JOB_STATUS = "Status";
	public static final String XML_PARTITION_UUID = "PartitionUUID";
	public static final String XML_PARTITION_NAME = "PartitionName";
	public static final String XML_PARTITION_PROFILE = "PartitionProfiles";
	public static final String XML_PARTITION_STATE = "PartitionState";
	public final static String XML_LOCATION_CODE = "LocationCode";
	public final static String XML_LINK_REF = "href";
	public final static String XML_LINK = "link";
	public final static String XML_CLIENT_NETWORK_ADAPTERS = "ClientNetworkAdapters";
	public final static String XML_VIRTUAL_FIBRE_CHANNEL_CLIENT_ADAPTER = "VirtualFibreChannelClientAdapters";//virtual? need to update if it's physical adapter
	public final static String XML_WWPNs = "WWPNs";
	public final static String XML_IPL_MODE = "DesignatedIPLSource";
	public final static String XML_KEYLOCK_POSITION = "KeylockPosition";
	public final static String XML_ENTRY = "entry";
	public final static String XML_CONTENT = "content";
	public final static String XML_PORT_VLAN_ID = "PortVLANID";

	/*AssociatedSystemIOConfigurationIOAdapters
	 *  - IOAdapters
	 *    - IOAdapterChoice
	 *      - IOAdapter
	 *  - IOBuses
	 *    - IOBus
	 *      - IOSlots
	 *        - IOSlot
	 *          - IOAdapter
	 *          - IORDevices
	 *            - IORDevice
	 *
	 *  - IOSlots
	 *    - IOSlot
	 *      - IORDevices (adapter)
	 *          - IORDevice(port)
	 */
	public final static String XML_ASSOCIATEDSYSTEMIOCONFIGURATION = "AssociatedSystemIOConfiguration";



	//HMC partition status
	public final static String PARTITION_NOT_ACTIVATED = "not activated";
    public final static String PARTITION_RUNNING = "running";
	public final static String REFERENCE_CODE_00000000 = "00000000";
	public final static String CONTROL_OS_SHUTDOWN = "osshutdown_cntrld";
	public final static String IMMED_OS_SHUTDOWN = "osshutdown_immed";
	public final static String HMC_SHUTDOWN = "shutdown";

    //
	public static final String YES = "yes";
	public static final String NO = "no";
	public static final String HEX_PREFIX = "0x";
	public static final String ALL = "ALL";

	public static final String SSL_TLS_VERSION = "TLSv1.2";

	public final static String DELIMITER_COMMA = ",";
	public final static String DELIMITER = "@@";
	public final static String INFORMATIONAL = "Informational:";
	public final static String DIAGNOSTIC = "Diagnostic:";
	public final static String HOST_MAP_COMMAND_RECORDER_FILE_NAME_SUFFIX = "_hostmap";

	//for precheck
	public static enum CHECK_ITEMS {
		IBMI,

		HMC,

		ONLINE,
	    SIZE,
	    VOLGRP,
	    FCMAPPING,
	    RCRELATIONSHIP,
	    HOSTCONNECT,
	    HOST,
	    FCCONSISTGRP,
	    RCCONSISTGRP,
	    EXISTINGRELATIONSHIP,//check whether the source or target volumes are already in existing relationships which will fail the copy process
	    ALL

	}
	public static String HMC_VERSION = "V8R8.4.0";
	public static String SVC_VERSION = "";
	public static String DSCLI_VERSION = "";
	public static String DS_REST_VERSION = "";
	public static String DEFAULT_HOST_NAME = "unassigned";

	//IBM i
	public final static int DEFAULT_MONITOR_MIRROR_STATE_TIMEOUT = 10800;
	public static final int MAX_ASP_NUMBER = 255;
	public static final int MAX_DISK_NUMBER = 4800;
	public static final int MAX_NRG_PAIR_COUNT = 16;
	public static final String KSH = "/QOpenSys/usr/bin/ksh ";
	public static final String KSH_WITHOUT_SPACE = "/QOpenSys/usr/bin/ksh";
	public static final String OS_400_NAME = "OS/400";
	public static final String DSCLI_LIBRARY = "QDSCLI";
	public static final String DEFAULT_DSCLI_PATH = "/ibm/dscli/dscli";
	//public static final String DEFAULT_LOG_FILE = "/QIBM/UserData/OS/ADMININST/admin3/wlp/usr/servers/admin3/logs/toolkit.setup.log";
	public static final String DEFAULT_LOG_FILE = "/QIBM/UserData/QDB2MIR/MRDB/TOOLS/toolkit.setup.log";
	public static final String DEFAULT_LOG_FILE_WITHOUT_PATH = "toolkit.setup.log";
	public static final String DEFAULT_LOGGER_KEY = "defaultLoggerKey";
	public static final String DEFAULT_MSG_QUEUE_KEY = "defaultMessageQueueKey";
	public static final String DEFAULT_STEP_FILE_NAME_SUFFIX = "defaultStepFileNameSuffix";
	public static final String IBMiPCMLPath = "com.ibm.db2mirror.startup.ibmi.IBMiCall";
	public static final String CLONE_INFO = "clone_info";
	public static final String META_DATA = "meta_data";
	public static final String SYSBAS_WITH_STAR = "*SYSBAS";
	public static final String SYSBAS = "SYSBAS";
	public static final String MIRROR_STATE_NOT_MIRRORED = "NOT MIRRORED";
	public static final String MIRROR_STATE_ACTIVE = "ACTIVE";
	public static final String MIRROR_STATE_TRACKING = "TRACKING";
	public static final String MIRROR_STATE_BLOCKED = "BLOCKED";
	public static final String MIRROR_STATE_DETAILS_MAINTENANCE = "MAINTENANCE";
	public static final String MIRROR_STATE_DETAILS_REPLICATING = "REPLICATING";
	public static final String MIRROR_STATE_DETAILS_RESUMING = "RESUMING";
	public static final String MIRROR_STATE_DETAILS_SUSPENDED = "SUSPENDED";
	public static final String MIRROR_STATE_DETAILS_SUSPENDING = "SUSPENDING";
	public static final String MIRROR_STATE_DETAILS_SYNCHRONIZING = "SYNCHRONIZING";
	public static final String MIRROR_STATE_DETAILS_STARTING = "STARTING";
	public static final String CONFIG_STATE_NOT_CONFIGURED = "NOT CONFIGURED";
	public static final String CONFIG_STATE_INITIALIZING= "INITIALIZING";
	public static final String CONFIG_STATE_COMPLETE = "COMPLETE";
	public static final String CONFIG_STATE_FAILED = "FAILED";
	public static final String INCLUSION_STATE_INCLUDE = "INCLUDE";
	public static final String INCLUSION_STATE_EXCLUDE = "EXCLUDE";
	public static final int IBMI_QUIESCE_TIMEOUT = 300;
	public static final int IBMI_QYASPCHGAA_REINSTRUCT_TIMEOUT = 3600;
	
	public static enum MRDB_CONFIG_STATE {
		CFG,
		CLUSTER,
		NRG
	}
	public static final String REST_StartDB2Mirror = "startDB2Mirror";
	public static final String REST_StopDB2Mirror = "stopDB2Mirror";
	public static final String PCML_StopDB2MirrorPGM = "QmrdbStopEngine";
	public static final String PCML_StartDB2MirrorPGM = "QmrdbStartEngine";
	public static final String PCML_RetrieveSysStatePGM = "QmrdbRtvSysState";
	public static final String PCML_StartWarmClonePGM = "QmrdbStartWarmClone";
	public static final String PCML_IaspCloneCompletePGM = "QmrdbIaspCloneComplete";
	public static final String PCML_IaspRtvCfgStatePGM = "QmrdbRtvIaspCfgState";
	public static final int CLUSTER_NODE_STATUS_NODE_NOT_FOUND = 999;
	public static final int CLUSTER_NODE_STATUS_NEW = 1;
	public static final int CLUSTER_NODE_STATUS_ACTIVE = 2;
	public static final int CLUSTER_NODE_STATUS_REMOVE_PENDING = 3;
	public static final int CLUSTER_NODE_STATUS_ACTIVE_PENDING = 4;
	public static final int CLUSTER_NODE_STATUS_INACTIVE_PENDING = 5;
	public static final int CLUSTER_NODE_STATUS_INACTIVE = 6;
	public static final int CLUSTER_NODE_STATUS_FAILED = 7;
	public static final int CLUSTER_NODE_STATUS_PARTITION = 8;
	public static final String DEFAULT_VLAN_IP = "*NONE";//1-4094
	public static final String DEFAULT_ADDRESS_PREFIX_LENGTH = "64"; //1-128
	public static final String NONE_WITH_STAR = "*NONE";

	//prefix
	public static final String VOLUME_GROUP_NAME_PREFIX = "mrdbvolgrp";
	public static final String HOST_CONNECT_NAME_PREFIX = "mrdbhostconn";
	public static final String HOST_NAME_PREFIX = "mrdbhost";
	public static final String FC_MAPPING_NAME_PREFIX = "mrdbfc";
	public static final String RC_RELATIONSHIP_NAME_PREFIX = "mrdbrc";//15 is max length mrdbrcxxx-xxxi
	public static final String FC_CONSIST_GROUP_NAME_PREFIX = "mrdbfccgrp";
	public static final String RC_CONSIST_GROUP_NAME_PREFIX = "mrdbcgrp";//15 is max length mrdbcgrpxxxi
	public static final int MAX_DS_CLI_VOLUME_NUMBER= 236;

	//valid storage type
	public static final String VALID_STORAGE_TYPE_DS8000 = "ds8000";
	public static final String VALID_STORAGE_TYPE_SVC = "svc";
	public static final String VALID_STORAGE_TYPE_CUSTOM = "custom";

	//valid copy type
	public static final String VALID_COPY_TYPE_FLASHCOPY = "fc";
	public static final String VALID_COPY_TYPE_METROMIRROR = "mm";
	public static final String VALID_COPY_TYPE_GLOBALCOPY= "gc";
	public static final String VALID_COPY_TYPE_GLOBALMIRROR = "gm";
	public static final String VALID_COPY_TYPE_REMOTECOPY= "rc";
	public static final String VALID_COPY_TYPE_SAVERESTORE = "sr";

	//message id
	public static final String INFO_GERNERAL_MRDBSP01 = "MRDBSP01I";

	public static final String WARNING_GERNERAL_MRDBSP01 = "MRDBSP01W";
	public static final String WARNING_COPY_RUNNING_MRDBSP02 = "MRDBSP02W";
	public static final String WARNING_ACTIVE_IP_MRDBSP03 = "MRDBSP03W";
	public static final String WARNING_FC_MAPPING_EXIST_MRDBSP04 = "MRDBSP04W";
	public static final String WARNING_RC_RELATIONSHIP_EXIST_MRDBSP05 = "MRDBSP05W";

	public static final String ERROR_GERNERAL_MRDBSP01 = "MRDBSP01E";


	/***************************************************************************************************************/
	//there some messages for each id, these messages are for toolkit developer only, other will not see them
	//message id for clone process. The number in message id does not mean the order.
	//precheck
	public static final String INFO_PRECHECK_SVC_MRDBSPC01 = "MRDBSPC01I";//svc precheck
	public static final String INFO_PRECHECK_DS8K_MRDBDPC01 = "MRDBDPC01I";//ds8k precheck
	public static final String INFO_PRECHECK_IBMI_COPY_MRDBIPC01 = "MRDBIPC01I";//setup copy check
	public static final String INFO_PRECHECK_IBMI_PRODUCT_MRDBIPC02 = "MRDBIPC02I";//ibmi product check
	public static final String INFO_PRECHECK_IBMI_MIRROR_STATE_MRDBIPC03 = "MRDBIPC03I";//ibmi mirror state check
	public static final String INFO_PRECHECK_IBMI_JSON_MRDBIPC04 = "MRDBIPC04I";//ibmi JSON file check
	public static final String INFO_PRECHECK_IBMI_SYSTEM_NAME_MRDBIPC05 = "MRDBIPC05I";//ibmi system name check
	public static final String INFO_PRECHECK_IBMI_NETWORK_MRDBIPC06 = "MRDBIPC06I";//ibmi network check
	public static final String INFO_PRECHECK_IBMI_NRG_MRDBIPC07 = "MRDBIPC07I";//ibmi nrg check
	public static final String INFO_PRECHECK_IBMI_CLUSTER_MRDBIPC08 = "MRDBIPC08I";//ibmi cluster check
	public static final String INFO_PRECHECK_IBMI_IASP_MRDBIPC09 = "MRDBIPC09I";//ibmi iasp check
	public static final String INFO_PRECHECK_IBMI_SYSBAS_MRDBIPC10 = "MRDBIPC10I";//ibmi sysbas check
	public static final String INFO_PRECHECK_IBMI_NTP_MRDBIPC11 = "MRDBIPC11I";//ibmi ntp check
	public static final String INFO_PRECHECK_IBMI_SYSVAL_MRDBIPC12 = "MRDBIPC12I";//ibmi system value check
	public static final String INFO_PRECHECK_HMC_MRDBHPC01 = "MRDBHPC01I";//hmc precheck
	
	//post check
//	public static final String INFO_POST_IBMI_REFERENCE_CODE_MRDBPOC01 = "MRDBPOC01I";//reference code is 00000000
//	public static final String INFO_POST_IBMI_MIRROR_INFO_MRDBPOC02 = "MRDBPOC02I";//mirror info check
//	public static final String INFO_POST_IBMI_TCPIP_MRDBPOC03 = "MRDBPOC03I";//tcp/ip check
//	public static final String INFO_POST_IBMI_CLUSTER_MRDBPOC04 = "MRDBPOC04I";//cluster check
//	public static final String INFO_POST_IBMI_CONFIG_STATE_MRDBPOC05 = "MRDBPOC05I";//config state check
//	public static final String INFO_POST_IBMI_CLEANUP_MRDBPOC06 = "MRDBPOC06I";//cleanup
	public static final String INFO_POST_IBMI_CHECK_MRDBPOC01 = "MRDBPOC01I";//IBM i post check. tcp/ip, cluster
	public static final String INFO_POST_IBMI_CLUSTER_MONITOR_MRDBPOC07 = "MRDBPOC07I";//cluster monitor creation
	
	
	public static final String INFO_POWEROFF_SOURCE_MRDBPFS01 = "MRDBPFS01I";//Power off setup source
	public static final String INFO_POWEROFF_SOURCE_MRDBPFS02 = "MRDBPFS02I";
	public static final String INFO_POWEROFF_SOURCE_MRDBPFS03 = "MRDBPFS03I";
	public static final String INFO_POWEROFF_SOURCE_MRDBPFS04 = "MRDBPFS04I";
	public static final String INFO_POWEROFF_SOURCE_MRDBPFS05 = "MRDBPFS05I";
	public static final String INFO_POWEROFF_SOURCE_MRDBPFS06 = "MRDBPFS06I";

	public static final String INFO_POWEROFF_COPY_MRDBPFC01 = "MRDBPFC01I";//Power off setup copy
	public static final String INFO_POWEROFF_COPY_MRDBPFC02 = "MRDBPFC02I";
	public static final String INFO_POWEROFF_COPY_MRDBPFC03 = "MRDBPFC03I";
	public static final String INFO_POWEROFF_COPY_MRDBPFC04 = "MRDBPFC04I";
	public static final String INFO_POWEROFF_COPY_MRDBPFC05 = "MRDBPFC05I";
	public static final String INFO_POWEROFF_COPY_MRDBPFC06 = "MRDBPFC06I";

	public static final String INFO_POWERON_SOURCE_MRDBPNS01 = "MRDBPNS01I";//Power on setup source
	public static final String INFO_POWERON_SOURCE_MRDBPNS02 = "MRDBPNS02I";
	public static final String INFO_POWERON_SOURCE_MRDBPNS03 = "MRDBPNS03I";
	public static final String INFO_POWERON_SOURCE_MRDBPNS04 = "MRDBPNS04I";
	public static final String INFO_POWERON_SOURCE_MRDBPNS05 = "MRDBPNS05I";
	public static final String INFO_POWERON_SOURCE_MRDBPNS06 = "MRDBPNS06I";

	public static final String INFO_POWERON_COPY_MRDBPNC01 = "MRDBPNC01I";//Power on setup copy
	public static final String INFO_POWERON_COPY_MRDBPNC02 = "MRDBPNC02I";
	public static final String INFO_POWERON_COPY_MRDBPNC03 = "MRDBPNC03I";
	public static final String INFO_POWERON_COPY_MRDBPNC04 = "MRDBPNC04I";
	public static final String INFO_POWERON_COPY_MRDBPNC05 = "MRDBPNC05I";
	public static final String INFO_POWERON_COPY_MRDBPNC06 = "MRDBPNC06I";
	
	//db iasp
	public static final String INFO_VARYOFF_IASP_SOURCE_MRDBVFS01 = "MRDBVFS01I";//varyoff iasp on source node
	public static final String INFO_VARYOFF_IASP_COPY_MRDBVFC01 = "MRDBVFC01I";//varyoff iasp on copy node
	public static final String INFO_VARYON_IASP_SOURCE_MRDBVNS01 = "MRDBVNS01I";//varyon iasp on source node
	public static final String INFO_VARYON_IASP_COPY_MRDBVNC01 = "MRDBVNC01I";//varyon iasp on copy node
	public static final String INFO_PRECHECK_IASP_MIRROR_STATE_MRDBDIC01 = "MRDBDIC01I";//db iasp mirror state check
	public static final String INFO_PRECHECK_IASP_CLUSTER_MRDBDIC02 = "MRDBDIC02I";//db iasp cluster check
	public static final String INFO_PRECHECK_IASP_SVC_MRDBDIC01 = "MRDBDIC03I";//db iasp svc check
	public static final String INFO_PRECHECK_IASP_DS8K_MRDBDIC02 = "MRDBDIC04I";//db iasp ds8k check

	public static final String INFO_SUSPEND_SOURCE_MRDBSS01 = "MRDBSS01I";//Suspend setup source
	public static final String INFO_RESUME_SOURCE_MRDBRS01 = "MRDBRS01I";//resume setup source
	
	public static final String INFO_TRACKING_SOURCE_MRDBTS01 = "MRDBTS01I";//track source

	public static final String INFO_SYSBASE_CLONE_MRDBSC01 = "MRDBSC01I";//sysbase clone
	public static final String INFO_DB_IASP_CLONE_MRDBIC01 = "MRDBIC01I";//db iasp clone

	public static final String INFO_SVC_FLASHCOPY_CREATE_CONSISTENT_GROUP_MRDBSFC01 = "MRDBSFC01I";// create svc flashcopy consistency group
	public static final String INFO_SVC_FLASHCOPY_CREATE_MAPPING_MRDBSFC02 = "MRDBSFC02I";// create svc flashcopy mappings
	public static final String INFO_SVC_FLASHCOPY_PREPARE_GROUP_MRDBSFC03 = "MRDBSFC03I";// prepare svc flashcopy
	public static final String INFO_SVC_FLASHCOPY_START_MRDBSFC04 = "MRDBSFC04I";// start svc flashcopy

	public static final String INFO_SVC_GLOBAL_MIRROR_CREATE_CONSISTENT_GROUP_MRDBSGM01 = "MRDBSGM01I";// create svc global mirror consistency group
	public static final String INFO_SVC_GLOBAL_MIRROR_CREATE_RELATIONSHIP_MRDBSGM02 = "MRDBSGM02I";// create svc global mirror relationships
	public static final String INFO_SVC_GLOBAL_MIRROR_START_MRDBSGM03 = "MRDBSGM03I";// start svc global mirror
	public static final String INFO_SVC_GLOBAL_MIRROR_WAIT_CONSISTENT_MRDBSGM04 = "MRDBSGM04I";// wait svc global mirror to be consistent synchronized or consistent copying
	public static final String INFO_SVC_GLOBAL_MIRROR_WAIT_FREEZE_MRDBSGM05 = "MRDBSGM05I";//wait for the correct global mirror freeze time
	public static final String INFO_SVC_GLOBAL_MIRROR_CLEANUP_MRDBSGM06 = "MRDBSGM06I";//clean up

	public static final String INFO_DS8K_FLASHCOPY_MRDBDFC01 = "MRDBDFC01I";// create and start ds8k flashcopy
	public static final String INFO_DS8K_GLOBAL_COPY_MRDBDGC01 = "MRDBDGC01I";// create and start ds8k global copy
	public static final String INFO_DS8K_GLOBAL_COPY_COPYING_MRDBDGC02 = "MRDBDGC02I";// wait 97% data copied
	public static final String INFO_DS8K_GLOBAL_COPY_CONVERT_MM_MRDBDGC03 = "MRDBDGC03I";// convert to metro mirror
	public static final String INFO_DS8K_GLOBAL_COPY_WAIT_SINC_MRDBDGC04 = "MRDBDGC04I";// wait the status change to sync
	public static final String INFO_DS8K_GLOBAL_COPY_CLEANUP_MRDBDGC05 = "MRDBDGC05I";// clean up
	//public static final String INFO_DS8K_GLOBAL_MIRROR_MRDBDGM01 = "MRDBDGM01I";// create ds8k global copy
	//public static final String INFO_DS8K_GLOBAL_MIRROR_MRDBDGM02 = "MRDBDGM02I";// create ds8k flashcopy mappings
	//public static final String INFO_DS8K_GLOBAL_MIRROR_MRDBDGM03 = "MRDBDGM03I";// create ds8k global mirror session
	//public static final String INFO_DS8K_GLOBAL_MIRROR_MRDBDGM04 = "MRDBDGM04I";// start ds8k global mirror


	//reference codes to check
	public static final String INFO_POWEROFF_STEP_1 = "D6000298";
	public static final String INFO_POWEROFF_STEP_2 = "D9002740";
	public static final String INFO_POWEROFF_STEP_3 = "D9002750";
	public static final String INFO_POWEROFF_STEP_4 = "D90027C0";

	public static final String INFO_POWERON_STEP_1 = "C100D009";
	public static final String INFO_POWERON_STEP_2 = "C2001000";
	public static final String INFO_POWERON_STEP_3 = "C3000000";
	public static final String INFO_POWERON_STEP_4 = "C5000000";
	/***************************************************************************************************************/

	/***************************************************************************************************************/
	public static final String EXIT_CHAR = "e";
	public static final String CONTINUE_CHAR = "c";
	public static final String EMPTY_STRING = "";
	/***************************************************************************************************************/

	public static final String MRI_STRING_MISSING = "MRI_STRING_MISSING ";
	public static final String LINE_SEPARATOR = "------------------------------";
	
	/***************************************************************************************************************/
	public static final String MIRROR_INFO_REPLICATION_DETAIL_INFO_WAITING_RDMA_ON_THE_OTHER_NODE = "WAITING FOR RDMA CONNECTIONS WITH THE ENGINE CONTROLLER ON THE OTHER NODE";

	/********************************************hardcode messages*******************************************************************/
	public static final String PRODUCT_NAME = "Db2 Mirror";
	public static final String MESSAGE_PARTITION_NAME_NOT_UNIQUE = "Partition name {0} is not unique on the HMC {1}";
	public static final String MESSAGE_IASP_ACTIVE_JOBS_RUNNING = "Active jobs are: ";
	public static final String MESSAGE_HOST_NAMES = "Volume {0} maps to {1} hosts: ";
	public static final String MESSAGE_ACTION_ON_SOURCE = "ACTION ON SETUP SOURCE ";
	public static final String MESSAGE_ACTION_ON_TARGET = "ACTION ON SETUP TARGET ";
	public static final String MESSAGE_ACTION_ON_MANAGE_NODE = "ACTION ON MANAGE NODE ";
	public static final String MESSAGE_DB_IASP_VOLUME_MAP_BACK_FAIL = "Failed to map DB IASP volumes back to its host on storage. Manually run commands in file {0} on the storage system on which setup copy node runs";
	public static final String MESSAGE_SVC_REMOTE_COPY_PROCESS = "The process of consistency group {0} is {1}%";
	public static final String MESSAGE_NRG_PAIR_COUNT_RANGE = "The valid NRG pair count range is from 1 to {0}. Current value is {1}";
	public static final String MESSAGE_NRG_LOAD_BALANCE_COUNT_RANGE = "The valid NRG load balance link count range is from 1 to {0}. Current value is {1}";
	public static final String MESSAGE_PRECHECK_TARGET_THIN_PROVISION = "Checking if target volumes are thin provisioned volumes";
	public static final String MESSAGE_CMN_LOCATION_NULL = "The value for communication location is null or empty";
	public static final String MESSAGE_MATCHED_JSON_CONFIG_FILE = "Matched JSON configuration files are: ";
	public static final String MESSAGE_CHECK_AE_CLOUDINIT_LOGS = "Check AE/Cloudinit logs under directory /var/log/ on setup copy node";
	public static final String MESSAGE_DB2MIRROR_STATE = "The Db2 Mirror state: ";
	public static final String MESSAGE_CSM_NO_DETAIL_MESSAGE = "No translated message ";
	public static final String MESSAGE_UUID_FROM_I_SOURCE = "The setup source partition UUID is ";
	public static final String MESSAGE_UUID_FROM_I_COPY = "The setup copy partition UUID is ";
	public static final String MESSAGE_UUID_FROM_HMC_SOURCE = "The setup source partition UUID from HMC is ";
	public static final String MESSAGE_UUID_FROM_HMC_COPY = "The setup copy partition UUID from HMC is ";
	public static final String MESSAGE_SYSBAS_DISK_NOT_EXIST_IN_JSON = "SYSBAS disk {0} does not exist in JSON configuration file";
	public static final String MESSAGE_FAIL = "{0} failed";
	public static final String MESSAGE_REST_NOT_WORK = "REST does not work";
	public static final String MESSAGE_SVC_COMMAND_RETURN_NOTHING = "SVC command return nothing: ";
	public static final String MESSAGE_CSM_ONLY_STANDBY_SPECIFIED = "The specified CSM is the standby server, at least one ACTIVE CSM needs to be specified";
	public static final String MESSAGE_SRC_DBIASP_PRE_EXIT_POINT_VARYOFF = "Pre DB IASP exit point is called on setup source node, but DB IASP {0} is varied off";
	public static final String MESSAGE_SRC_DBIASP_POST_EXIT_POINT_NOT_VARYON = "Post DB IASP exit point is called on setup source node, but DB IASP {0} is varied on";
	public static final String MESSAGE_CPY_DBIASP_PRE_EXIT_POINT_VARYOFF = "Pre DB IASP exit point is called on setup copy node, but DB IASP {0} is varied off";
	public static final String MESSAGE_CPY_DBIASP_POST_EXIT_POINT_NOT_VARYON = "Post DB IASP exit point is called on setup copy node, but DB IASP {0} is varied on";
	public static final String MESSAGE_VERSION_NOTES = "IBM Db2 Mirror for i, MrdbToolkit-7.4.0_20190903-1005";
	public static final String MESSAGE_CHAINED_SNTP_STATUS_ON_SOURCE = "Chained time server is configured, the status is:";
	/********************************************hardcode messages*******************************************************************/
}
