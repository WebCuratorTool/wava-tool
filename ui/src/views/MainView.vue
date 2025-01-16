<script setup lang="ts">
import { ref } from 'vue';
const visible = ref(false);
const visLocation = ref('/tools/visualization.html');
</script>

<template>
    <Toast position="bottom-left"></Toast>
    <div class="topbar p-2">
        <Button icon="pi pi-bars" severity="warn" @click="visible = true" />
    </div>
    <div class="row-container">
        <iframe :src="visLocation" class="full-screen"></iframe>
    </div>
    <Drawer v-model:visible="visible" header="Click the button to open a web harvest." class="!w-full md:!w-80 lg:!w-[50rem]" position="right">
        <TreeTable :value="nodes" tableStyle="min-width: 50rem">
            <Column field="name" header="Name" expander style="width: 34%"></Column>
            <Column field="size" header="Size" style="width: 33%"></Column>
            <Column field="type" header="Type" style="width: 33%"></Column>
        </TreeTable>
    </Drawer>
</template>

<style>
.topbar {
    position: fixed;
    top: 0;
    right: 0;
}
.row-container {
    display: flex;
    width: 100vw;
    height: 100vh;
    flex-direction: column;
    background-color: blue;
    overflow: hidden;
}

.full-screen {
    flex-grow: 1;
    border: none;
    margin: 0;
    padding: 0;
}
</style>
