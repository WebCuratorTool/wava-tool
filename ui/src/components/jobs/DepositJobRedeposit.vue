<script setup lang="ts">
import { useJobStore } from '@/stores/depositjob';
import { inject } from 'vue';

const jobList = useJobStore();

const dialogRef: any = inject('dialogRef');
const closeDialog = () => {
    dialogRef.value.close();
};

const onRedeposit = () => {
    jobList.redeposit();
    closeDialog();
};
</script>

<template>
    <Message severity="warn" :closable="false">
        Please note that the following steps will be executed alongside the redepositing operation:
        <br />
        1. The done file inside the sub folder will be deleted.
        <br />
        2. The running job relevant to this sub folder will be removed.
    </Message>

    <InputGroup class="mt-8 mb-2">
        <InputGroupAddon>Sub Folder</InputGroupAddon>
        <InputText placeholder="" v-model="jobList.subFolder" />
    </InputGroup>

    <Toolbar style="border: 0">
        <template #end>
            <div class="flex justify-center flex-wrap gap-4 mt-4" style="justify-content: flex-end">
                <Button type="button" label="Redeposit" @click="onRedeposit()" autofocus></Button>
                <Button type="button" label="Close" @click="closeDialog()" severity="secondary"></Button>
            </div>
        </template>
    </Toolbar>
</template>

<style scoped></style>
