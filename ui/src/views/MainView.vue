<script setup lang="ts">
import { sleep, useToastStore } from '@/utils/helper';
import { onMounted, ref } from 'vue';

const toast = useToastStore();
const title = ref();
const visLocation = ref();
const nodes = ref();
const selectedNodeData = ref();
const isRunning = ref(false);
const progressValue = ref('0.00');
const icon = (data: any) => {
    if (data.folder) {
        return 'pi pi-folder';
    } else {
        return 'pi pi-file-word';
    }
};

const onInspect = async (data: any) => {
    selectedNodeData.value = data;
    const params = 'targetInstanceOid=' + data.tiId + '&harvestResultId=1&harvestNumber=1';
    const rspGlobalSetting = await fetch('/curator/get/global-settings?' + params);
    if (!rspGlobalSetting.ok) {
        toast.error('Failed to get the global settings.');
        return;
    }

    const globalSettings = await rspGlobalSetting.json();
    const retrieveResult = globalSettings.retrieveResult;
    if (retrieveResult === '9') {
        if (confirm('No index exists for this web harvest, click ok to generate the index.')) {
            const rspInitialIndex = await fetch('/curator/initial-wava-index?' + params);
            if (!rspInitialIndex.ok) {
                toast.error('Failed to initial the index.');
                return;
            }
            isRunning.value = true;
            while (true) {
                const rspProgress = await fetch('/curator/get-wava-progress?' + params);
                if (!rspInitialIndex.ok) {
                    toast.error('Failed to fetch the progress.');
                    return;
                }
                const progress = await rspProgress.json();
                progressValue.value = progress.toFixed(2);
                if (progressValue.value >= 100) {
                    break;
                }
                await sleep(3000);
            }
            isRunning.value = false;
        } else {
            return;
        }
    }
    visLocation.value = '/tools/visualization.html?' + params;
    title.value = data.absolutePath;
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
    <Splitter style="height: 100vh">
        <SplitterPanel class="flex flex-col" :size="25" style="overflow-y: auto">
            <TreeTable :value="nodes" tableStyle="min-width: 100%">
                <template #header>
                    <div class="text-xl font-bold">Web Harvests</div>
                </template>
                <Column header="Name" expander style="width: 100%">
                    <template #body="slotProps">
                        <span><i :class="icon(slotProps.node.data)">&nbsp;</i> {{ slotProps.node.data.label }} </span>
                    </template>
                </Column>
                <Column header="Action" style="width: 30%">
                    <template #body="slotProps">
                        <div class="flex flex-wrap gap-2">
                            <Button v-if="slotProps.node.type === 'url'" severity="secondary" icon="pi pi-eye" @click="onInspect(slotProps.node.data)" />
                        </div>
                    </template>
                </Column>
            </TreeTable>
        </SplitterPanel>
        <SplitterPanel class="flex items-center justify-center" :size="75">
            <div v-if="isRunning" class="flex flex-col items-center justify-center">
                <ProgressSpinner />
                <span class="text-3xl">Indexing: {{ progressValue }}%</span>
            </div>
            <div v-else class="row-container">
                <iframe :src="visLocation" class="full-screen"></iframe>
            </div>
        </SplitterPanel>
    </Splitter>

    <div class="flex items-center justify-end w-full topbar gap-4 p-2">
        <div class="text-lg" style="color: white">{{ title }}</div>
        <!-- <Button icon="pi pi-bars" severity="warn" @click="openDialog" /> -->
    </div>
</template>

<style>
.tab-panel {
    height: 60vh;
}
.topbar {
    position: fixed;
    width: 45vw;
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
