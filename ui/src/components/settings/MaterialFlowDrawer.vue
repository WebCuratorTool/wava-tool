<script setup lang="ts">
import { useSettingsMaterialFlowStore } from '@/stores/settings';
import type { MaterialFlow } from '@/types/deposit';
import { useDialog } from 'primevue/usedialog';
import { defineExpose, onMounted, ref } from 'vue';
import MaterialFlowEditDialog from './MaterialFlowEditDialog.vue';

const dialog = useDialog();
const openEditForm = (data: any) => {
    const dialogRef = dialog.open(MaterialFlowEditDialog, {
        props: {
            header: 'MaterialFlow Configuration',
            style: {
                width: '75vw'
            },
            breakpoints: {
                '960px': '75vw',
                '640px': '90vw'
            },
            modal: true
        },
        data: data
    });
};

const materialFlowStore = useSettingsMaterialFlowStore();
const metaKey = ref(true);
const selectedRow = ref();
const initialData = {
    id: null,
    enabled: true,
    depositAccountId: -1,
    materialFlowId: '',
    materialFlowName: '',
    producerId: '',
    producerName: '',
    rootPath: '',
    streamLocation: 'content',
    injectionCompleteFileName: 'ready-for-ingestion-FOLDER-COMPLETED',
    maxActiveDays: 60,
    maxSaveDays: 30,
    delays: null,
    delayUnit: null,
    weeklyMaxConcurrency: [1, 1, 1, 1, 1, 1, 1],
    actualContentDeleteOptions: 'notDelete',
    backupEnabled: false,
    actualContentBackupOptions: 'notBackup',
    backupPath: '',
    backupSubFolders: '',
    auditRst: true,
    auditMsg: 'OK'
};

const onNew = () => {
    openEditForm(initialData);
};
const onEdit = (selectedData: MaterialFlow) => {
    openEditForm(selectedData);
};
const onDelete = (selectedData: MaterialFlow) => {
    materialFlowStore.deleteConfirm(selectedData);
};

onMounted(() => {});

const visible = ref(false);
const toggle = () => {
    materialFlowStore.queryAllRows().then((data: any) => {
        visible.value = true;
    });
};
defineExpose({ toggle });
</script>

<template>
    <Drawer v-model:visible="visible" header="Material Flow Settings" class="!w-full md:!w-80 lg:!w-[50rem]" position="right">
        <DataTable v-model:selection="selectedRow" :value="materialFlowStore.dataList" :metaKeySelection="metaKey" dataKey="id" tableStyle="width:100%" showGridlines sortField="id" :sortOrder="1">
            <Column field="id" header="ID" sortable></Column>
            <Column field="producerName" header="Producer" sortable></Column>
            <Column field="materialFlowName" header="Material Flow" sortable>
                <template #body="slotProps">
                    {{ slotProps.data.materialFlowName }}
                    <!-- <span v-if="!slotProps.data.enabled" style="display: inline-block; background: #6c757d; color: white; padding: 0 5px; border: 0.1px solid; border-radius: 8px">Disabled</span> -->
                    <Badge v-if="!slotProps.data.enabled" value="Disabled" severity="info"></Badge>
                </template>
            </Column>
            <Column field="id" header="Action" alignFrozen="right" style="width: 8rem">
                <template #body="{ data }">
                    <Button icon="pi pi-pen-to-square" @click="onEdit(data)" text />
                    <Button icon="pi pi-trash" severity="danger" @click="onDelete(data)" text />
                </template>
            </Column>
        </DataTable>

        <template #footer>
            <Fluid>
                <Button label="New" icon="pi pi-plus" @click="onNew()" raised />
            </Fluid>
        </template>
    </Drawer>
</template>
