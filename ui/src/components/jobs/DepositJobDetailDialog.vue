<script setup lang="ts">
import { formatDatetimeFromEpochMilliSeconds } from '@/stores/depositjob';
import { formatContentLength } from '@/utils/helper';
import { inject, reactive, ref } from 'vue';

const dialogRef: any = inject('dialogRef');
const params = dialogRef.value.data;
const job = Object.assign({}, params.data);
job.initialTime = formatDatetimeFromEpochMilliSeconds(job.initialTime);
job.latestTime = formatDatetimeFromEpochMilliSeconds(job.latestTime);
job.depositStartTime = formatDatetimeFromEpochMilliSeconds(job.depositStartTime);
job.depositEndTime = formatDatetimeFromEpochMilliSeconds(job.depositEndTime);
const formatedFileSize = ref();
formatedFileSize.value = formatContentLength(job.fileSize);

const auditMessage = reactive({
    isActive: false,
    content: 'OK'
});

if ((job.resultMessage && job.resultMessage !== '') || job.state === 'FAILED') {
    auditMessage.isActive = true;
    auditMessage.content = job.resultMessage;
} else {
    auditMessage.isActive = false;
}

const ingestProgress = reactive({
    value: 0,
    label: ''
});

const depositProgress = reactive({
    value: 0,
    label: ''
});

const finalizeProgress = reactive({
    value: 0,
    label: ''
});

const getProgressBarStateStyle = (stage: string, state: string) => {
    let value = 0;
    if (state === 'INITIALED') {
        value = 10;
    } else if (state === 'RUNNING' || state === 'PAUSED') {
        value = 60;
    } else if (state === 'SUCCEED' || state === 'FAILED' || state === 'CANCELED') {
        value = 100;
    }
    return {
        value: value,
        label: stage + '(' + state + ')'
    };
};

const setIngestProgressBar = (stage: string, state: string) => {
    const progress = getProgressBarStateStyle(stage, state);
    ingestProgress.value = progress.value;
    ingestProgress.label = progress.label;
};

const setDepositProgressBar = (stage: string, state: string) => {
    const progress = getProgressBarStateStyle(stage, state);
    depositProgress.value = progress.value;
    depositProgress.label = progress.label;
};

const setFinalizeProgressBar = (stage: string, state: string) => {
    const progress = getProgressBarStateStyle(stage, state);
    finalizeProgress.value = progress.value;
    finalizeProgress.label = progress.label;
};

if (job.stage === 'INGEST') {
    setIngestProgressBar(job.stage, job.state);
    setDepositProgressBar('DEPOSIT', '...');
    setFinalizeProgressBar('FINALIZE', '...');
} else if (job.stage === 'DEPOSIT') {
    setIngestProgressBar('INGEST', 'SUCCEED');
    setDepositProgressBar(job.stage, job.state);
    setFinalizeProgressBar('FINALIZE', '...');
} else {
    setIngestProgressBar('INGEST', 'SUCCEED');
    setDepositProgressBar('DEPOSIT', 'SUCCEED');
    setFinalizeProgressBar(job.stage, job.state);
}

const closeDialog = (event: any) => {
    dialogRef.value.close(event);
};
</script>

<template>
    <div>
        <Message v-if="auditMessage.isActive" severity="warn" :closable="false">{{ auditMessage.content }}</Message>
        <Fieldset legend="Basic Properties">
            <InputGroup class="mt-2 mb-2">
                <InputGroupAddon>Job Title</InputGroupAddon>
                <InputText v-model="job.injectionTitle" readonly />
            </InputGroup>
            <InputGroup class="mt-2 mb-2">
                <InputGroupAddon>Original Path</InputGroupAddon>
                <InputText v-model="job.injectionPath" readonly />
            </InputGroup>
            <InputGroup class="mt-2 mb-2">
                <InputGroupAddon>Material Flow</InputGroupAddon>
                <InputText v-model="job.appliedFlowSetting.materialFlowName" readonly />
            </InputGroup>
            <InputGroup class="mt-2 mb-2">
                <InputGroupAddon>Initial Time</InputGroupAddon>
                <InputText v-model="job.initialTime" readonly />
                <InputGroupAddon>Latest Update Time</InputGroupAddon>
                <InputText v-model="job.latestTime" readonly />
            </InputGroup>
            <InputGroup class="mt-2 mb-2">
                <InputGroupAddon>File Count</InputGroupAddon>
                <InputText v-model="job.fileCount" readonly />
                <InputGroupAddon>File Size</InputGroupAddon>
                <InputText v-model="formatedFileSize" readonly />
            </InputGroup>
            <InputGroup class="mt-2 mb-2">
                <InputGroupAddon>Current Stage</InputGroupAddon>
                <InputText v-model="job.stage" readonly />
                <InputGroupAddon>Current State</InputGroupAddon>
                <InputText v-model="job.state" readonly />
            </InputGroup>
            <InputGroup class="mt-2 mb-2">
                <InputGroupAddon>Backup Completed Flag</InputGroupAddon>
                <InputText v-model="job.backupCompleted" readonly />
                <InputGroupAddon>Actual Content Deleted Flag</InputGroupAddon>
                <InputText v-model="job.actualContentDeleted" readonly />
            </InputGroup>
            <InputGroup class="mt-2 mb-2">
                <ProgressBar class="mr-1" :value="ingestProgress.value">{{ ingestProgress.label }}</ProgressBar>
                <ProgressBar :value="depositProgress.value">{{ depositProgress.label }}</ProgressBar>
                <ProgressBar class="ml-1" :value="finalizeProgress.value">{{ finalizeProgress.label }}</ProgressBar>
            </InputGroup>
        </Fieldset>

        <Fieldset legend="SIP Properties">
            <InputGroup class="mt-2 mb-2">
                <InputGroupAddon>Deposit Start Time</InputGroupAddon>
                <InputText v-model="job.depositStartTime" readonly />
                <InputGroupAddon>Deposit End Time</InputGroupAddon>
                <InputText v-model="job.depositEndTime" readonly />
            </InputGroup>
            <InputGroup class="mt-2 mb-2">
                <InputGroupAddon>SIP ID</InputGroupAddon>
                <InputText v-model="job.sipID" readonly />
                <InputGroupAddon>SIP Result</InputGroupAddon>
                <InputText v-model="job.resultMessage" readonly />
            </InputGroup>
            <InputGroup class="mt-2 mb-2">
                <InputGroupAddon>SIP Module</InputGroupAddon>
                <InputText v-model="job.sipModule" readonly />
                <InputGroupAddon>SIP Stage</InputGroupAddon>
                <InputText v-model="job.sipStage" readonly />
                <InputGroupAddon>SIP Status</InputGroupAddon>
                <InputText v-model="job.sipStatus" readonly />
            </InputGroup>
        </Fieldset>
    </div>

    <Divider />

    <div class="flex justify-center flex-wrap gap-4 mt-4" style="justify-content: flex-end">
        <Button type="button" label="OK" @click="closeDialog({ buttonType: 'Confirm' })" autofocus></Button>
        <Button type="button" label="Close" @click="closeDialog({ buttonType: 'Cancel' })" severity="secondary"></Button>
    </div>
</template>

<style scoped>
/* .p-inputgroupaddon,
input:disabled {
    color: black;
    background-color: white;
} */
.p-inputgroupaddon {
    font-weight: bold;
}

.p-progressbar {
    border: 0 none;
    width: 33.333%;
    height: 1.75rem;
    border-radius: 0px;
}
</style>
