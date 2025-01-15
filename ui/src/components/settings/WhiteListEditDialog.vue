<script setup lang="ts">
import { useSettingsWhiteListStore } from '@/stores/settings';
import { type WhiteListUser } from '@/types/deposit';
import { getSelectOption } from '@/utils/helper';
import { inject, ref } from 'vue';
import FlatInputGroup from '../FlatInputGroup.vue';

const dialogRef: any = inject('dialogRef');
const closeDialog = () => {
    dialogRef.value.close();
};
const params = dialogRef.value.data;

const userRoleOptions = ref([
    { name: 'Admin', code: 'admin' },
    { name: 'Normal', code: 'normal' }
]);
const selectedUserRole = ref({ name: 'Normal', code: 'normal' });

const whiteListStore = useSettingsWhiteListStore();

const selectedRow = ref(Object.assign({}, params) as WhiteListUser);
selectedUserRole.value = getSelectOption(userRoleOptions.value, selectedRow.value.whiteUserRole);

const onSave = async () => {
    const ret = await whiteListStore.saveRow(selectedRow.value);
    if (ret !== undefined) {
        whiteListStore.queryAllRows();
        closeDialog();
    }
};
</script>

<template>
    <div style="width: 100%">
        <Fieldset legend="Settings">
            <InputGroup class="mt-2 mb-2">
                <span></span>
                <InputGroupAddon>ID</InputGroupAddon>
                <InputNumber v-model="selectedRow.id" disabled />
                <span></span>
            </InputGroup>
            <FlatInputGroup class="mt-2 mb-2">
                <InputGroupAddon>User Name</InputGroupAddon>
                <InputText v-model="selectedRow.whiteUserName" />
            </FlatInputGroup>
            <FlatInputGroup class="mt-2 mb-2">
                <InputGroupAddon>User Role</InputGroupAddon>
                <Select v-model="selectedUserRole" :options="userRoleOptions" optionLabel="name" placeholder="Select a role" />
            </FlatInputGroup>
        </Fieldset>

        <!-- <Fieldset legend="Health Audit">
            <Message v-if="selectedRow.auditRst" severity="success" :closable="false">{{ selectedRow.auditMsg }}</Message>
            <Message v-if="!selectedRow.auditRst" severity="warn" :closable="false">{{ selectedRow.auditMsg }}</Message>
        </Fieldset> -->
    </div>
    <div class="flex justify-end mt-4 gap-4">
        <Button label="Save" @click="onSave()" autofocus />
        <Button label="Close" severity="secondary" @click="closeDialog()" />
    </div>
</template>
