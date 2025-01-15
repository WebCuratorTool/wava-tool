interface GlobalSetting {
    id: number | undefined;
    paused: boolean;
    pausedStartTime: string;
    pausedEndTime: string;
    delays: number;
    delayUnit: string;
}
interface DepositAccount {
    id: number | undefined;
    depositUserInstitute: string;
    depositUserName: string;
    depositUserPassword: string;
    auditRst: boolean;
    auditMsg: string;
}

interface WhiteListUser {
    id: number | undefined;
    whiteUserName: string;
    whiteUserRole: string;
    auditRst: boolean;
    auditMsg: string;
}

interface MaterialFlow {
    id: number | undefined;
    actualContentBackupOptions: string;
    actualContentDeleteOptions: string;
    backupEnabled: boolean;
    backupPath: string;
    backupSubFolders: string;
    delayUnit: string | undefined;
    delays: number | undefined;
    depositAccountId: number;
    enabled: boolean;
    injectionCompleteFileName: string;
    materialFlowId: string;
    materialFlowName: string;
    maxActiveDays: number;
    maxSaveDays: number;
    producerId: string;
    producerName: string;
    rootPath: string;
    streamLocation: string;
    weeklyMaxConcurrency: any;
    auditMsg: string;
    auditRst: boolean;
}

interface DepositJob {
    id: number;
    actualContentDeleted: boolean;
    backupCompleted: boolean;
    depositEndTime: number;
    depositSetId: string;
    depositStartTime: number;
    fileCount: number;
    fileSize: number;
    initialTime: number;
    injectionPath: string;
    injectionTitle: string;
    latestTime: number;
    resultMessage: string;
    sipID: string;
    sipModule: string;
    sipStage: string;
    sipStatus: string;
    stage: string;
    state: string;
    successful: boolean;
    appliedFlowSetting: MaterialFlow;
    auditMsg: string;
    auditRst: boolean;
}

interface JobQueryCondition {
    startTime: number;
    endTime: number;
    materialFlows: number[];
    stages: string[];
    states: string[];
}

export type { DepositAccount, DepositJob, GlobalSetting, JobQueryCondition, MaterialFlow, WhiteListUser };
