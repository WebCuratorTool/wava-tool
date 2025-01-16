<script setup lang="ts">
import { onMounted, ref } from 'vue';
const visible = ref(false);
const title = ref();
const visLocation = ref();
const nodes = ref();

const icon = (data: any) => {
    if (data.folder) {
        return 'pi pi-folder';
    } else {
        return 'pi pi-file-word';
    }
};

const onClick = (data: any) => {
    visLocation.value = '/tools/visualization.html?targetInstanceOid=' + data.tiId + '&harvestResultId=1&harvestNumber=1';
    title.value = data.absolutePath;
    visible.value = false;
};

onMounted(() => {
    fetch('/curator/vis/all_hr_results')
        .then((rsp: any) => {
            return rsp.json();
        })
        .then((datasets: any) => {
            nodes.value = datasets;
            console.log(datasets);
        });
});
</script>

<template>
    <Toast position="bottom-left"></Toast>
    <div class="flex items-center justify-between w-full topbar gap-4 p-2">
        <div class="text-lg" style="color: white">{{ title }}</div>
        <Button icon="pi pi-bars" severity="warn" @click="visible = true" />
    </div>
    <div class="row-container">
        <iframe v-if="visLocation" :src="visLocation" class="full-screen"></iframe>
        <div v-else class="flex items-center justify-center" style="background: white; width: 100vw; height: 100vh">
            <div style="font-size: 2rem">Click the &nbsp;<i class="pi pi-bars"></i>&nbsp; button on the top right to select a web harvest</div>
        </div>
    </div>
    <Drawer v-model:visible="visible" header="Click the button to open a web harvest." class="!w-full md:!w-80 lg:!w-[50rem]" position="right">
        <Tree :value="nodes" tableStyle="min-width: 100%">
            <!-- <Column field="label" header="Name" expander style="width: 60%">
                <template #body="slotProps">
                    <span>{{ slotProps }}</span>
                </template>
            </Column>
            <Column field="label" header="Name" expander style="width: 60%"></Column>
            <Column field="label" header="Action" style="width: 30%">
                <template #body>
                    <div class="flex flex-wrap gap-2">
                        <Button type="button" icon="pi pi-search" rounded />
                        <Button type="button" icon="pi pi-pencil" rounded severity="success" />
                    </div>
                </template>
            </Column> -->
            <template #default="slotProps">
                <span><i :class="icon(slotProps.node.data)">&nbsp;</i> {{ slotProps.node.data.label }} </span>
            </template>
            <template #url="slotProps">
                <a href="javascript: void(0)" @click="onClick(slotProps.node.data)">
                    <span> <i :class="icon(slotProps.node.data)">&nbsp;</i> {{ slotProps.node.data.label }} <i class="pi pi-check">&nbsp;</i></span>
                </a>
            </template>
        </Tree>
    </Drawer>
</template>

<style>
/* .p-treetable-table-container {
    height: calc(100vh - 6rem);
} */
.topbar {
    position: fixed;
    width: 55vw;
    top: 0;
    right: 0;
}
.row-container {
    display: flex;
    width: 100vw;
    height: 100vh;
    flex-direction: column;
    overflow: hidden;
}

.full-screen {
    flex-grow: 1;
    border: none;
    margin: 0;
    padding: 0;
}
</style>
