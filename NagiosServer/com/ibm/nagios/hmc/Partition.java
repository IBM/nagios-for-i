/* begin_generated_IBM_copyright_prolog                             */
/*                                                                  */
/* This is an automatically generated copyright prolog.             */
/* After initializing,  DO NOT MODIFY OR MOVE                       */
/* ---------------------------------------------------------------- */
/* IBM Confidential                                                 */
/*                                                                  */
/* (C)Copyright IBM Corp.  2017, 2017                               */
/*                                                                  */
/* The Source code for this program is not published  or otherwise  */
/* divested of its trade secrets,  irrespective of what has been    */
/* deposited with the U.S. Copyright Office.                        */
/*  --------------------------------------------------------------- */
/*                                                                  */
/* end_generated_IBM_copyright_prolog                               */
/* Change Log ------------------------------------------------------*/
/*                                                                  */
/*  Flag  Reason   Version Userid    Date        Description        */
/*  ----  -------- ------- --------  ----------  -----------        */
/* End Change Log --------------------------------------------------*/
package com.ibm.nagios.hmc;

public class Partition {
	private String ProgressState;
	private String MigrationState;
	private String CurrentProcessors;
	private String PowerManagementMode;
	private String RemoteRestartState;
	private String OperatingSystemVersion;
	private String PartitionID;
	private String PartitionType;
	private String PartitionName;
	private String IsVirtualServiceAttentionLEDOn;
	private String RMCState;
	private String AllocatedVirtualProcessors;
	private String CurrentMemory;
	private String HasDedicatedProcessors;
	private String PartitionState;
	private String AssociatedManagedSystem;
	//private String ResourceMonitoringIPAddress;//ignore this because for OS400, it's null, but for AIX, it's an object, not a string
	private String ReferenceCode;
	private String CurrentProcessingUnits;
	private String SharingMode;
	private String UUID;
	public String getProgressState() {
		return ProgressState;
	}
	public void setProgressState(String progressState) {
		ProgressState = progressState;
	}
	public String getMigrationState() {
		return MigrationState;
	}
	public void setMigrationState(String migrationState) {
		MigrationState = migrationState;
	}
	public String getCurrentProcessors() {
		return CurrentProcessors;
	}
	public void setCurrentProcessors(String currentProcessors) {
		CurrentProcessors = currentProcessors;
	}
	public String getPowerManagementMode() {
		return PowerManagementMode;
	}
	public void setPowerManagementMode(String powerManagementMode) {
		PowerManagementMode = powerManagementMode;
	}
	public String getRemoteRestartState() {
		return RemoteRestartState;
	}
	public void setRemoteRestartState(String remoteRestartState) {
		RemoteRestartState = remoteRestartState;
	}
	public String getOperatingSystemVersion() {
		return OperatingSystemVersion;
	}
	public void setOperatingSystemVersion(String operatingSystemVersion) {
		OperatingSystemVersion = operatingSystemVersion;
	}
	public String getPartitionID() {
		return PartitionID;
	}
	public void setPartitionID(String partitionID) {
		PartitionID = partitionID;
	}
	public String getPartitionType() {
		return PartitionType;
	}
	public void setPartitionType(String partitionType) {
		PartitionType = partitionType;
	}
	public String getPartitionName() {
		return PartitionName;
	}
	public void setPartitionName(String partitionName) {
		PartitionName = partitionName;
	}
	public String getIsVirtualServiceAttentionLEDOn() {
		return IsVirtualServiceAttentionLEDOn;
	}
	public void setIsVirtualServiceAttentionLEDOn(
			String isVirtualServiceAttentionLEDOn) {
		IsVirtualServiceAttentionLEDOn = isVirtualServiceAttentionLEDOn;
	}
	public String getRMCState() {
		return RMCState;
	}
	public void setRMCState(String rMCState) {
		RMCState = rMCState;
	}
	public String getAllocatedVirtualProcessors() {
		return AllocatedVirtualProcessors;
	}
	public void setAllocatedVirtualProcessors(String allocatedVirtualProcessors) {
		AllocatedVirtualProcessors = allocatedVirtualProcessors;
	}
	public String getCurrentMemory() {
		return CurrentMemory;
	}
	public void setCurrentMemory(String currentMemory) {
		CurrentMemory = currentMemory;
	}
	public String getHasDedicatedProcessors() {
		return HasDedicatedProcessors;
	}
	public void setHasDedicatedProcessors(String hasDedicatedProcessors) {
		HasDedicatedProcessors = hasDedicatedProcessors;
	}
	public String getPartitionState() {
		return PartitionState;
	}
	public void setPartitionState(String partitionState) {
		PartitionState = partitionState;
	}
	public String getAssociatedManagedSystem() {
		return AssociatedManagedSystem;
	}
	public void setAssociatedManagedSystem(String associatedManagedSystem) {
		AssociatedManagedSystem = associatedManagedSystem;
	}
//	public String getResourceMonitoringIPAddress() {
//		return ResourceMonitoringIPAddress;
//	}
//	public void setResourceMonitoringIPAddress(String resourceMonitoringIPAddress) {
//		ResourceMonitoringIPAddress = resourceMonitoringIPAddress;
//	}
	public String getReferenceCode() {
		return ReferenceCode;
	}
	public void setReferenceCode(String referenceCode) {
		ReferenceCode = referenceCode;
	}
	public String getCurrentProcessingUnits() {
		return CurrentProcessingUnits;
	}
	public void setCurrentProcessingUnits(String currentProcessingUnits) {
		CurrentProcessingUnits = currentProcessingUnits;
	}
	public String getSharingMode() {
		return SharingMode;
	}
	public void setSharingMode(String sharingMode) {
		SharingMode = sharingMode;
	}
	public String getUUID() {
		return UUID;
	}
	public void setUUID(String uUID) {
		UUID = uUID;
	}
	
	
	

}
