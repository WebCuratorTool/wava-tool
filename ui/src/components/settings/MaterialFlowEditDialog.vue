<script setup lang="ts">
import { useSettingsDepositAccountStore, useSettingsMaterialFlowStore } from '@/stores/settings';
import type { MaterialFlow } from '@/types/deposit';
import { inject, reactive, ref } from 'vue';
import DepositAccountSelectForm from './DepositAccountSelectForm.vue';
import RawMaterialFlowSelectForm from './RawMaterialFlowSelectForm.vue';
import RawProducerSelectForm from './RawProducerSelectForm.vue';

const dialogRef: any = inject('dialogRef');
const closeDialog = () => {
    dialogRef.value.close();
};
const params = dialogRef.value.data;

const opDepositAccount = ref();
const opProducer = ref();
const opMaterialFlow = ref();

const depositAccountStore = useSettingsDepositAccountStore();
const materialFlowStore = useSettingsMaterialFlowStore();
const depositAccount = ref();

const weeklyThreads = reactive({
    mon: 1,
    tue: 1,
    wed: 1,
    thu: 1,
    fri: 1,
    sat: 1,
    sun: 1
});
const deletionOptions = ref([
    { name: 'Not delete', code: 'notDelete' },
    { name: 'Delete when exceeds the MAX Storage Days', code: 'deleteExceedMaxStorageDays' },
    { name: 'Delete instantly when finished', code: 'deleteInstantly' }
]);

const backupOptions = ref([
    { name: 'Not backup', code: 'notBackup' },
    { name: 'Backup the contents without the subfolder name', code: 'backupContentsWithoutSubFolderName' },
    { name: 'Backup the structure of the subfolder', code: 'backupSubFolder' }
]);

const onDepositAccountSelected = (data: any) => {
    opDepositAccount.value.hide();
    if (!data || selectedRow.value.depositAccountId == data.id) {
        return;
    }
    selectedRow.value.depositAccountId = data.id;
    depositAccount.value = data.depositUserInstitute + ' | ' + data.depositUserName;
    selectedRow.value.producerId = '';
    selectedRow.value.producerName = '';
    selectedRow.value.materialFlowId = '';
    selectedRow.value.materialFlowName = '';
};

const onProducerSelected = (data: any) => {
    opProducer.value.hide();
    if (!data || selectedRow.value.producerId == data.id) {
        return;
    }
    selectedRow.value.producerId = data.id;
    selectedRow.value.producerName = data.id + '-' + data.name;
    selectedRow.value.materialFlowId = '';
    selectedRow.value.materialFlowName = '';
};

const onMaterialFlowSelected = (data: any) => {
    opMaterialFlow.value.hide();
    if (!data || selectedRow.value.materialFlowId == data.id) {
        return;
    }
    selectedRow.value.materialFlowId = data.id;
    selectedRow.value.materialFlowName = data.id + '-' + data.name;
};

const onSave = async () => {
    selectedRow.value.weeklyMaxConcurrency = [weeklyThreads.mon, weeklyThreads.tue, weeklyThreads.wed, weeklyThreads.thu, weeklyThreads.fri, weeklyThreads.sat, weeklyThreads.sun];
    const ret = await materialFlowStore.saveRow(selectedRow.value);
    if (ret !== undefined) {
        materialFlowStore.queryAllRows();
        closeDialog();
    }
};
const togglePopover = (event: any, target: string) => {
    if (target === 'opDepositAccount' && opDepositAccount.value) {
        opDepositAccount.value.toggle(event);
    } else if (target === 'opProducer' && opProducer.value) {
        opProducer.value.toggle(event);
    } else if (target === 'opMaterialFlow' && opMaterialFlow.value) {
        opMaterialFlow.value.toggle(event);
    }
};
const selectedRow = ref(Object.assign({}, params) as MaterialFlow);
const weeklyMaxConcurrency = selectedRow.value.weeklyMaxConcurrency;
weeklyThreads.mon = weeklyMaxConcurrency[0];
weeklyThreads.tue = weeklyMaxConcurrency[1];
weeklyThreads.wed = weeklyMaxConcurrency[2];
weeklyThreads.thu = weeklyMaxConcurrency[3];
weeklyThreads.fri = weeklyMaxConcurrency[4];
weeklyThreads.sat = weeklyMaxConcurrency[5];
weeklyThreads.sun = weeklyMaxConcurrency[6];
if (selectedRow.value.depositAccountId >= 0) {
    depositAccountStore.queryRow(selectedRow.value.depositAccountId).then((data: any) => {
        depositAccount.value = data.depositUserInstitute + ' | ' + data.depositUserName;
    });
} else {
    depositAccount.value = '';
}
</script>

<template>
    <div style="width: 100%; height: 70vh; overflow-y: scroll">
        <Fieldset legend="Basic Settings">
            <InputGroup class="mt-2 mb-2">
                <ToggleSwitch v-model="selectedRow.enabled" inputId="enabledMaterialFlow" />
                <label for="enabledMaterialFlow" class="ml-2"> If enable depositing for this material flow. </label>
            </InputGroup>
            <InputGroup class="mt-2 mb-2">
                <span></span>
                <InputGroupAddon>ID</InputGroupAddon>
                <InputNumber v-model="selectedRow.id" disabled />
                <span></span>
            </InputGroup>
            <IconInputGroup :label="'Deposit Account'" :icon="'pi pi-angle-down'">
                <InputText v-model="depositAccount" @click="togglePopover($event, 'opDepositAccount')" readonly style="padding-left: 3px" />
            </IconInputGroup>

            <FlatInputGroup>
                <IconInputGroup label="Producer" icon="pi pi-angle-down">
                    <InputText v-model="selectedRow.producerName" @click="togglePopover($event, 'opProducer')" readonly style="padding-left: 3px" />
                </IconInputGroup>
                <IconInputGroup label="Material Flow" icon="pi pi-angle-down">
                    <InputText v-model="selectedRow.materialFlowName" @click="togglePopover($event, 'opMaterialFlow')" readonly style="padding-left: 3px" />
                </IconInputGroup>
            </FlatInputGroup>

            <FlatInputGroup>
                <InputGroupAddon>Root Location</InputGroupAddon>
                <InputText v-model="selectedRow.rootPath" />
            </FlatInputGroup>
        </Fieldset>

        <Fieldset legend="Advanced Settings">
            <FlatInputGroup>
                <InputGroupAddon>Stream Path</InputGroupAddon>
                <InputText v-model="selectedRow.streamLocation" />
            </FlatInputGroup>
            <FlatInputGroup>
                <InputGroupAddon>Ingestion Completed File Name </InputGroupAddon>
                <InputText v-model="selectedRow.injectionCompleteFileName" />
            </FlatInputGroup>
            <FlatInputGroup>
                <InputGroupAddon>Max Active Days </InputGroupAddon>
                <InputNumber v-model="selectedRow.maxActiveDays" mode="decimal" fluid />
                <InputGroupAddon>Max Storage Days </InputGroupAddon>
                <InputNumber v-model="selectedRow.maxSaveDays" mode="decimal" fluid />
            </FlatInputGroup>
            <FlatInputGroup>
                <InputGroupAddon>Max Threads: </InputGroupAddon>
                <InputGroupAddon>Mon</InputGroupAddon>
                <InputNumber v-model="weeklyThreads.mon" mode="decimal" :min="0" :max="128" fluid />
                <InputGroupAddon>Tue</InputGroupAddon>
                <InputNumber v-model="weeklyThreads.tue" mode="decimal" :min="0" :max="128" fluid />
                <InputGroupAddon>Wed</InputGroupAddon>
                <InputNumber v-model="weeklyThreads.wed" mode="decimal" :min="0" :max="128" fluid />
                <InputGroupAddon>Thu</InputGroupAddon>
                <InputNumber v-model="weeklyThreads.thu" mode="decimal" :min="0" :max="128" fluid />
                <InputGroupAddon>Fri</InputGroupAddon>
                <InputNumber v-model="weeklyThreads.fri" mode="decimal" :min="0" :max="128" fluid />
                <InputGroupAddon>Sat</InputGroupAddon>
                <InputNumber v-model="weeklyThreads.sat" mode="decimal" :min="0" :max="128" fluid />
                <InputGroupAddon>Sun</InputGroupAddon>
                <InputNumber v-model="weeklyThreads.sun" mode="decimal" :min="0" :max="128" fluid />
            </FlatInputGroup>
            <FlatInputGroup>
                <InputGroupAddon>Deletion Options For Actual Content </InputGroupAddon>
                <Select v-model="selectedRow.actualContentDeleteOptions" :options="deletionOptions" optionLabel="name" optionValue="code" />
            </FlatInputGroup>
            <FlatInputGroup>
                <InputGroupAddon>Backup Options For Actual Content </InputGroupAddon>
                <Select v-model="selectedRow.actualContentBackupOptions" :options="backupOptions" optionLabel="name" optionValue="code" />
            </FlatInputGroup>
            <FlatInputGroup>
                <InputGroupAddon>Backup Location </InputGroupAddon>
                <InputText v-model="selectedRow.backupPath" />
            </FlatInputGroup>
            <FlatInputGroup>
                <InputGroupAddon>Sub folders for backup </InputGroupAddon>
                <Textarea v-model="selectedRow.backupSubFolders" rows="5" cols="120" fluid />
            </FlatInputGroup>
        </Fieldset>

        <Fieldset legend="Health Audit">
            <Message v-if="selectedRow.auditRst" severity="success" :closable="false">{{ selectedRow.auditMsg }}</Message>
            <Message v-if="!selectedRow.auditRst" severity="warn" :closable="false">{{ selectedRow.auditMsg }}</Message>
        </Fieldset>
    </div>

    <div class="flex justify-end mt-4 gap-4">
        <Button label="Save" @click="onSave()" autofocus />
        <Button label="Close" severity="secondary" @click="closeDialog()" />
    </div>

    <Popover ref="opDepositAccount" appendTo="body" style="width: 60rem">
        <DepositAccountSelectForm @onSelected="onDepositAccountSelected" />
    </Popover>

    <Popover ref="opProducer" appendTo="body" style="width: 60rem">
        <RawProducerSelectForm @onSelected="onProducerSelected" :account="selectedRow.depositAccountId" />
    </Popover>

    <Popover ref="opMaterialFlow" appendTo="body" style="width: 60rem">
        <RawMaterialFlowSelectForm @onSelected="onMaterialFlowSelected" :account="selectedRow.depositAccountId" :producer="selectedRow.producerId" />
    </Popover>
</template>
