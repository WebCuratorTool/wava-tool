<script setup lang="ts">
import { inject, ref } from 'vue';

const dialogRef: any = inject('dialogRef');
const params = dialogRef.value.data;
const action: string = params.action.toUpperCase();
const errMsg = ref(params.errMsg);
const needConfirm = ref(params.needConfirm);

const STAGES: any = ['INGEST', 'DEPOSIT', 'FINALIZE', 'FINISHED'];
const STATES: any = ['INITIALED', 'RUNNING', 'PAUSED', 'SUCCEED', 'FAILED', 'CANCELED'];
const PAUSE_AVAILABLE_STATUS: any = {
    RUNNING: ['INGEST', 'FINALIZE'],
    INITIALED: ['DEPOSIT']
};

const RESUME_AVAILABLE_STATUS: any = {
    PAUSED: ['INGEST', 'DEPOSIT', 'FINALIZE']
};

const RETRY_AVAILABLE_STATUS: any = {
    FAILED: ['DEPOSIT']
};

const TERMINATE_AVAILABLE_STATUS: any = {
    SUCCEED: ['FINISHED'],
    FAILED: ['INGEST', 'DEPOSIT', 'FINALIZE', 'FINISHED'],
    CANCELED: ['INGEST', 'DEPOSIT', 'FINALIZE', 'FINISHED']
};

const CANCEL_AVAILABLE_STATUS: any = {
    INITIALED: ['INGEST', 'DEPOSIT', 'FINALIZE', 'FINISHED'],
    PAUSED: ['INGEST', 'DEPOSIT', 'FINALIZE', 'FINISHED'],
    SUCCEED: ['INGEST', 'DEPOSIT', 'FINALIZE', 'FINISHED'],
    FAILED: ['INGEST', 'DEPOSIT', 'FINALIZE', 'FINISHED']
};

const AVAILABLE_STATUS_MAP: any = {
    PAUSE: PAUSE_AVAILABLE_STATUS,
    RESUME: RESUME_AVAILABLE_STATUS,
    RETRY: RETRY_AVAILABLE_STATUS,
    TERMINATE: TERMINATE_AVAILABLE_STATUS,
    CANCEL: CANCEL_AVAILABLE_STATUS
};

const availableActioTableData = [];
let availableStatus: any = {};
if (AVAILABLE_STATUS_MAP.hasOwnProperty(action)) {
    availableStatus = AVAILABLE_STATUS_MAP[action];
}

for (let state of STATES) {
    const item: any = {
        NAME: state,
        INGEST: false,
        DEPOSIT: false,
        FINALIZE: false,
        FINISHED: false
    };

    let availableStatusOfTheState: any = [];
    if (availableStatus.hasOwnProperty(state)) {
        availableStatusOfTheState = availableStatus[state];
    }
    for (let stage of STAGES) {
        if (availableStatusOfTheState.includes(stage)) {
            item[stage] = true;
        }
    }
    availableActioTableData.push(item);
}

const tableData = ref(availableActioTableData);

const closeDialog = (event: any) => {
    dialogRef.value.close(event);
};
</script>

<template>
    <Message severity="warn" :closable="false"> {{ errMsg }} </Message>
    <DataTable :value="tableData" size="small" showGridlines>
        <Column field="NAME" header="State\Stage"></Column>
        <Column field="INGEST" header="INGEST">
            <template #body="{ data }">
                <div v-if="data.INGEST" class="bg-primary text-primary" style="width: 100%; text-align: center">Y</div>
                <div v-else style="width: 100%; text-align: center">-</div>
            </template>
        </Column>
        <Column field="DEPOSIT" header="DEPOSIT">
            <template #body="{ data }">
                <div v-if="data.DEPOSIT" class="bg-primary text-primary" style="width: 100%; text-align: center">Y</div>
                <div v-else style="width: 100%; text-align: center">-</div>
            </template>
        </Column>
        <Column field="FINALIZE" header="FINALIZE">
            <template #body="{ data }">
                <div v-if="data.FINALIZE" class="bg-primary text-primary" style="width: 100%; text-align: center">Y</div>
                <div v-else style="width: 100%; text-align: center">-</div>
            </template>
        </Column>
        <Column field="FINISHED" header="FINISHED">
            <template #body="{ data }">
                <div v-if="data.FINISHED" class="bg-primary text-primary" style="width: 100%; text-align: center">Y</div>
                <div v-else style="width: 100%; text-align: center">-</div>
            </template>
        </Column>
    </DataTable>

    <Divider />

    <div class="flex justify-center flex-wrap gap-4 mt-4" style="justify-content: flex-end">
        <Button v-if="needConfirm" type="button" label="Confirm" @click="closeDialog({ buttonType: 'confirm' })" autofocus></Button>
        <Button type="button" label="Close" @click="closeDialog({ buttonType: 'cancel' })" severity="secondary"></Button>
    </div>
</template>

<style scoped></style>
