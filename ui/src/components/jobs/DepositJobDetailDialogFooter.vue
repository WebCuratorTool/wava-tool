<script setup lang="ts">
import { inject } from "vue";

const dialogRef:any = inject("dialogRef");

const closeDialog = (e:any) => {
    dialogRef.value.close(e);
};
</script>

<template>
    <div class="flex justify-center flex-wrap gap-4 mt-4">
        <Button type="button" label="OK" @click="closeDialog({ buttonType: 'Confirm' })" autofocus></Button>
        <Button type="button" label="Close" @click="closeDialog({ buttonType: 'Cancel' })" severity="secondary"></Button>
    </div>
</template>

<style scoped>
</style>