<script setup lang="ts">
import { useToast } from 'primevue/usetoast';
import { ref, watch } from 'vue';
// import { FilterMatchMode, FilterOperator } from "primevue/api";
// import { MaterialFlow, DepositJob } from "@/types/deposit";
import { formatDatetimeFromEpochMilliSeconds, getProgressBarClass, keywords, useJobStore } from '@/stores/depositjob';
import { useContextMenu } from '@/stores/depositjobContextMenu';
import { formatContentLength } from '@/utils/helper';

const cm = useContextMenu();

const jobStore = useJobStore();
const expandedRowGroups = ref([]);

watch(keywords, async (newValue, oldValue) => {
    jobStore.filter(keywords.value);
});

const rowContextMenu = ref();
const toast = useToast();

const onRowContextMenu = (event: any) => {
    rowContextMenu.value.show(event.originalEvent);
};

jobStore.fetchAllData();
</script>
<template>
    <ContextMenu ref="rowContextMenu" :model="cm.contextMenuModel" @hide="cm.selectedContextRow = null" />
    <DataTable
        id="main-table"
        v-model:expandedRowGroups="expandedRowGroups"
        :value="jobStore.listJobsFiltered"
        v-model:selection="jobStore.selectedJobs"
        contextMenu
        v-model:contextMenuSelection="cm.selectedContextRow"
        @rowContextmenu="onRowContextMenu"
        dataKey="id"
        :rowHover="true"
        size="small"
        rowGroupMode="subheader"
        groupRowsBy="materialFlowName"
        sortMode="single"
        sortField="materialFlowName"
        :sortOrder="1"
        paginator
        :alwaysShowPaginator="true"
        :rows="50"
        :rowsPerPageOptions="[20, 50, 100, 200]"
        scrollable
        scrollHeight="flex"
        showGridlines
        stripedRows
        resizableColumns
        columnResizeMode="expand"
        style="border-top: 1px solid; border-top-color: lightslategray"
    >
        <Column field="materialFlowName" header="MaterialFlow" frozen class="font-bold"></Column>
        <Column selectionMode="multiple" headerStyle="width: 3rem" class="font-bold"></Column>
        <Column field="id" header="ID" sortable></Column>
        <Column field="injectionTitle" header="JobTitle" style="min-width: 400px" sortable></Column>
        <Column field="stage" header="Stage" sortable></Column>
        <Column field="state" header="State" sortable></Column>
        <Column field="initialTime" header="JobInitialTime" style="min-width: 200px" sortable>
            <template #body="{ data }">
                {{ formatDatetimeFromEpochMilliSeconds(data.initialTime) }}
            </template>
        </Column>
        <Column field="latestTime" header="LatestUpdateTime" style="min-width: 200px" sortable>
            <template #body="{ data }">
                {{ formatDatetimeFromEpochMilliSeconds(data.latestTime) }}
            </template>
        </Column>
        <Column field="progress" header="Progress" sortable>
            <template #body="{ data }">
                <ProgressBar :value="data.progress" v-bind:class="getProgressBarClass(data)"></ProgressBar>
            </template>
        </Column>
        <Column field="fileCount" header="NumOfFiles" sortable></Column>
        <Column field="fileSize" header="SizeOfFiles" sortable>
            <template #body="{ data }">
                {{ formatContentLength(data.fileSize) }}
            </template>
        </Column>
        <Column field="depositStartTime" header="DepositStartTime" style="min-width: 200px" sortable>
            <template #body="{ data }">
                {{ formatDatetimeFromEpochMilliSeconds(data.depositStartTime) }}
            </template>
        </Column>
        <Column field="depositEndTime" header="DepositEndTime" style="min-width: 200px" sortable>
            <template #body="{ data }">
                {{ formatDatetimeFromEpochMilliSeconds(data.depositEndTime) }}
            </template>
        </Column>
        <Column field="sipID" header="SipId" sortable></Column>
        <Column field="sipModule" header="SipModule" sortable></Column>
        <Column field="sipStage" header="SipStage" sortable></Column>
        <Column field="sipStatus" header="SipStatus" sortable></Column>

        <template #groupheader="slotProps">
            <div class="flex align-items-center gap-2 font-bold">
                <i class="pi pi-folder-open"></i>
                <span>{{ slotProps.data.materialFlowName }}</span>
            </div>
        </template>
    </DataTable>
</template>

<style>
#main-table .abnormal-progressbar .p-progressbar-value {
    background: gray;
}
</style>
