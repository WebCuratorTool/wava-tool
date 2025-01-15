<script setup lang="ts">
import { useSettingsDepositAccountStore } from '@/stores/settings';
import { type DepositAccount } from '@/types/deposit';
import { useDialog } from 'primevue/usedialog';
import { defineExpose, onMounted, ref } from 'vue';
import DepositAccountEditDialog from './DepositAccountEditDialog.vue';

const dialog = useDialog();
const openEditForm = (data: any) => {
    const dialogRef = dialog.open(DepositAccountEditDialog, {
        props: {
            header: 'Deposit Account Configuration',
            style: {
                width: '35vw'
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

const depositAccountStore = useSettingsDepositAccountStore();

const initialData: DepositAccount = {
    id: undefined,
    depositUserInstitute: '',
    depositUserName: '',
    depositUserPassword: '',
    auditRst: true,
    auditMsg: 'OK'
};
const metaKey = ref(true);

const onNew = () => {
    openEditForm(initialData);
};
const onEdit = (selectedData: any) => {
    openEditForm(selectedData);
};
const onDelete = (selectedData: any) => {
    depositAccountStore.deleteConfirm(selectedData);
};

onMounted(() => {});

const visible = ref(false);
const toggle = () => {
    depositAccountStore.queryAllRows().then((data: any) => {
        visible.value = true;
    });
};
defineExpose({ toggle });
</script>

<template>
    <!-- <ConfirmDialog></ConfirmDialog> -->
    <Drawer v-model:visible="visible" header="Deposit Accounts" class="!w-full md:!w-80 lg:!w-[50rem]" position="right">
        <DataTable v-model:selection="depositAccountStore.selectedRow" :value="depositAccountStore.dataList" :metaKeySelection="metaKey" dataKey="id" tableStyle="width:100%" showGridlines sortField="id" :sortOrder="1">
            <Column field="id" header="ID" sortable></Column>
            <Column field="depositUserInstitute" header="Institute" sortable></Column>
            <Column field="depositUserName" header="User Name" sortable></Column>
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
