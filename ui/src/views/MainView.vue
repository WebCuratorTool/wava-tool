<script setup lang="ts">
import { useToastStore } from '@/utils/helper';
import { onMounted, ref } from 'vue';

const toast = useToastStore();
const visible = ref(false);
const tabNum = ref(1);
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

const onInspect = async (data: any) => {
    const params = 'targetInstanceOid=' + data.tiId + '&harvestResultId=1&harvestNumber=1';
    const rspGlobalSetting = await fetch('/curator/get/global-settings?' + params);
    if (!rspGlobalSetting.ok) {
        toast.error('Failed to get the global settings.');
        return;
    }

    const globalSettings = await rspGlobalSetting.json();
    const currentVersion = globalSettings.currentVersion;
    const globalVersion = globalSettings.globalVersion;
    const retrieveResult = globalSettings.retrieveResult;
    if (retrieveResult === '9') {
        if (confirm('No index exists for this web harvest, click ok to generate the index.')) {
            //If could not get the BDB version or the BDB does not exit, then prompt with users to confim redex.
            const rspInitialIndex = await fetch('/curator/initial-wava-index?' + params);
            if (!rspInitialIndex.ok) {
                toast.error('Failed to initial the index.');
                return;
            }
        } else {
            return;
        }
    }

    visLocation.value = '/tools/visualization.html?' + params;
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
    <Dialog v-model:visible="visible" header="Click the button to open a web harvest." style="width: 60vw; max-height: 70vh">
        <div v-if="tabNum == 1">
            <TreeTable :value="nodes" tableStyle="min-width: 100%">
                <Column header="Name" expander style="width: 100%">
                    <template #body="slotProps">
                        <span><i :class="icon(slotProps.node.data)">&nbsp;</i> {{ slotProps.node.data.label }} </span>
                    </template>
                </Column>
                <Column header="Action" style="width: 30%">
                    <template #body="slotProps">
                        <div class="flex flex-wrap gap-2">
                            <Button v-if="slotProps.node.type === 'url'" type="button" icon="pi pi-eye" text @click="onInspect(slotProps.node.data)" />
                        </div>
                    </template>
                </Column>
            </TreeTable>
        </div>
        <div v-if="tabNum == 2">
            <div class="flex justify-center items-center w-full h-full">
                <span> No index exists for this web harvest, click ok to generate the index. </span>
            </div>
        </div>
        <template #footer>
            <Button v-if="tabNum == 2" label="Reindex" @click="visible = false" autofocus />
            <Button label="Cancel" @click="visible = false" autofocus />
        </template>
    </Dialog>
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
