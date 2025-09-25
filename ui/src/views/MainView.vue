<script setup lang="ts">
import { sleep, useToastStore } from '@/utils/helper';
import { useFullscreen } from '@vueuse/core';
import { onMounted, ref } from 'vue';

const toast = useToastStore();
const inspectArea = ref();

const { isFullscreen, enter, exit, toggle } = useFullscreen(inspectArea);
const visible = ref(true);

const title = ref();
const visLocation = ref();
const nodes = ref();
const rootPath = ref();
const selectedNodeData = ref();
const isRunning = ref(false);
const progressValue = ref(0.0);
const icon = (data: any) => {
    if (data.folder) {
        return 'pi pi-folder';
    } else {
        return 'pi pi-file-word';
    }
};

const labelClass = (node: any) => {
    if (node.type === 'url') {
        return 'text-slate-300';
    } else {
        return 'text-slate-600';
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
            nodes.value = datasets.children;
            rootPath.value = datasets.data.label;
            console.log(datasets);
        });
});
</script>

<template>
    <Toast position="bottom-left"></Toast>
    <div class="flex justify-between w-full h-full" style="overflow: hidden">
        <div class="flex justify-between h-full">
            <Panel v-show="visible" style="height: 100vh; max-width: 35rem">
                <template #header>
                    <span>Web Harvests: {{ rootPath }}</span>
                </template>
                <template #icons>
                    <Button @click="visible = false" icon="pi pi-times" size="small" severity="contrast" rounded />
                </template>
                <div style="width: 100%; height: calc(100vh - 5rem); overflow-y: auto">
                    <TreeTable :value="nodes" tableStyle="width: 100%" class="mt-2">
                        <Column header="Name" expander style="width: 100%">
                            <template #body="slotProps">
                                <span :class="labelClass(slotProps.node)"><i :class="icon(slotProps.node.data)">&nbsp;</i> {{ slotProps.node.data.label }} </span>
                            </template>
                        </Column>
                        <Column header="Action" style="width: 30%">
                            <template #body="slotProps">
                                <div class="flex flex-wrap gap-2">
                                    <Button v-if="slotProps.node.type === 'url'" icon="pi pi-window-maximize" @click="onInspect(slotProps.node.data)" text raised />
                                </div>
                            </template>
                        </Column>
                    </TreeTable>
                </div>
            </Panel>
            <Button v-show="!visible" icon="pi pi-angle-right" @click="visible = true" style="width: 1rem; height: 100vh; border-radius: 0" severity="contrast" />
        </div>
        <!-- <Divider layout="vertical" /> -->

        <div class="flex items-center justify-center w-full" style="height: 100vh; overflow: hidden">
            <div v-if="isRunning" class="flex flex-col items-center justify-center">
                <ProgressSpinner />
                <span class="text-3xl">Indexing: {{ progressValue }}%</span>
            </div>
            <div v-else ref="inspectArea" class="row-container">
                <div v-if="!visLocation" class="flex items-center justify-center w-full h-full">
                    <span class="text-3xl">No harvest selected!</span>
                </div>
                <div v-if="visLocation" class="flex items-center justify-end w-full gap-4 topbar">
                    <div class="text-white">
                        <label class="">{{ title }} </label>
                    </div>

                    <Button icon="pi pi-expand" severity="contrast" @click="toggle" text />
                </div>
                <iframe v-if="visLocation" :src="visLocation" class="full-screen"></iframe>
            </div>
        </div>
    </div>
</template>

<style>
.tab-panel {
    height: 60vh;
}
.topbar {
    position: absolute;
    width: 50vw;
    top: 0;
    right: 0;
    padding: 6px;
}
.row-container {
    display: flex;
    width: 100%;
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
