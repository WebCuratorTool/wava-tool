<script setup lang="ts">
import { useSettingsWhiteListStore } from '@/stores/settings';
import { type WhiteListUser } from '@/types/deposit';
import { useDialog } from 'primevue/usedialog';
import { defineExpose, onMounted, ref } from 'vue';
import WhiteListEditDialog from './WhiteListEditDialog.vue';

const dialog = useDialog();
const openEditForm = (data: any) => {
    const dialogRef = dialog.open(WhiteListEditDialog, {
        props: {
            header: 'White List Configuration',
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

const whiteListStore = useSettingsWhiteListStore();

const initialData: WhiteListUser = {
    id: undefined,
    whiteUserName: '',
    whiteUserRole: 'normal',
    auditRst: true,
    auditMsg: 'OK'
};
const selectedRow = ref();
const metaKey = ref(true);

const onNew = () => {
    openEditForm(initialData);
};

const onEdit = (selectedData: any) => {
    openEditForm(selectedData);
};

const onDelete = (selectedData: any) => {
    whiteListStore.deleteConfirm(selectedData);
};

onMounted(() => {});

const visible = ref(false);
const toggle = () => {
    whiteListStore.queryAllRows().then((data: any) => {
        visible.value = true;
    });
};
defineExpose({ toggle });
</script>

<template>
    <!-- <ConfirmDialog></ConfirmDialog> -->
    <Drawer v-model:visible="visible" header="Deposit Accounts" class="!w-full md:!w-80 lg:!w-[50rem]" position="right">
        <DataTable v-model:selection="whiteListStore.selectedRow" :value="whiteListStore.dataList" :metaKeySelection="metaKey" dataKey="id" tableStyle="width:100%" showGridlines sortField="id" :sortOrder="1">
            <Column field="id" header="ID" sortable></Column>
            <Column field="whiteUserName" header="User Name" sortable></Column>
            <Column field="whiteUserRole" header="User Role" sortable></Column>
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
