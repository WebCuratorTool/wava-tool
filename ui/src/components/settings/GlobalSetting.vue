<script setup lang="ts">
import { useSettingsGlobalStore } from '@/stores/settings';
import { type GlobalSetting } from '@/types/deposit';
import { dataTimeObject2String, dateTimeString2Object } from '@/utils/helper';
import { inject, ref } from 'vue';

const dialogRef: any = inject('dialogRef');
const closeDialog = () => {
    dialogRef.value.close();
};

const paused = ref();
const pausedStartTime = ref();
const pausedEndTime = ref();
const delays = ref();
const delayUnit = ref();

const globalStore = useSettingsGlobalStore();
globalStore.queryRow().then((globalSetting: GlobalSetting) => {
    paused.value = globalSetting.paused;
    pausedStartTime.value = dateTimeString2Object(globalSetting.pausedStartTime);
    pausedEndTime.value = dateTimeString2Object(globalSetting.pausedEndTime);
    delays.value = globalSetting.delays;
    delayUnit.value = globalSetting.delayUnit;
});

const onSave = async () => {
    const data = {} as GlobalSetting;
    data.paused = paused.value;
    data.pausedStartTime = dataTimeObject2String(pausedStartTime.value);
    data.pausedEndTime = dataTimeObject2String(pausedEndTime.value);
    data.delays = delays.value;
    data.delayUnit = delayUnit.value;
    const ret = await globalStore.saveRow(Object.assign({}, data));
    if (ret !== undefined) {
        closeDialog();
    }
};
</script>

<template>
    <Fieldset legend="Pause Settings">
        <InputGroup class="mt-2 mb-2">
            <ToggleSwitch v-model="paused" inputId="paused" />
            <label for="paused" class="ml-2"> Enable Paused </label>
        </InputGroup>
        <FlatInputGroup class="mt-2 mb-2">
            <InputGroupAddon>Between</InputGroupAddon>
            <DatePicker v-model="pausedStartTime" dateFormat="dd/mm/yy" showTime hourFormat="12" :disabled="!paused" />
            <InputGroupAddon>and</InputGroupAddon>
            <DatePicker v-model="pausedEndTime" dateFormat="dd/mm/yy" showTime hourFormat="12" :disabled="!paused" />
        </FlatInputGroup>
    </Fieldset>

    <Fieldset legend="On Timer">
        <FlatInputGroup class="mt-2 mb-2">
            <InputGroupAddon>Scan Interval (Seconds)</InputGroupAddon>
            <InputNumber v-model="delays" :min="60" :max="3600" fluid />
        </FlatInputGroup>
    </Fieldset>
    <Toolbar style="border: 0">
        <template #end>
            <Button label="Save" class="mr-4" @click="onSave()" autofocus />
            <Button label="Close" outlined @click="closeDialog()" autofocus />
        </template>
    </Toolbar>
</template>
