<script setup lang="ts">
import { useSettingsDepositAccountStore } from '@/stores/settings';
import { type DepositAccount } from '@/types/deposit';
import { inject, ref } from 'vue';
import FlatInputGroup from '../FlatInputGroup.vue';

const dialogRef: any = inject('dialogRef');
const closeDialog = () => {
    dialogRef.value.close();
};
const params = dialogRef.value.data;

const depositAccountStore = useSettingsDepositAccountStore();

const selectedRow = ref(Object.assign({}, params) as DepositAccount);

const onSave = async () => {
    const ret = await depositAccountStore.saveRow(selectedRow.value);
    if (ret !== undefined) {
        depositAccountStore.queryAllRows();
        closeDialog();
    }
};
</script>

<template>
    <div style="width: 100%">
        <Fluid>
            <Fieldset legend="Settings">
                <InputGroup class="mt-2 mb-2">
                    <span></span>
                    <InputGroupAddon>ID</InputGroupAddon>
                    <InputNumber v-model="selectedRow.id" disabled />
                    <span></span>
                </InputGroup>
                <FlatInputGroup class="mt-2 mb-2">
                    <InputGroupAddon>Institute</InputGroupAddon>
                    <InputText v-model="selectedRow.depositUserInstitute" />
                </FlatInputGroup>
                <FlatInputGroup class="mt-2 mb-2">
                    <InputGroupAddon>User Name</InputGroupAddon>
                    <InputText v-model="selectedRow.depositUserName" />
                </FlatInputGroup>
                <FlatInputGroup class="mt-2 mb-2">
                    <InputGroupAddon>Password</InputGroupAddon>
                    <Password v-model="selectedRow.depositUserPassword" :feedback="false" />
                </FlatInputGroup>
            </Fieldset>
        </Fluid>
        <Fieldset legend="Health Audit">
            <Message v-if="selectedRow.auditRst" severity="success" :closable="false">{{ selectedRow.auditMsg }}</Message>
            <Message v-if="!selectedRow.auditRst" severity="warn" :closable="false">{{ selectedRow.auditMsg }}</Message>
        </Fieldset>
    </div>
    <div class="flex justify-end mt-4 gap-4">
        <Button label="Save" @click="onSave()" autofocus />
        <Button label="Close" severity="secondary" @click="closeDialog()" />
    </div>
</template>
