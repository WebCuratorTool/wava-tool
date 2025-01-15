<script setup lang="ts">
import { searchConditions, useJobStore } from '@/stores/depositjob';
import { useMaterialFlowsStore } from '@/stores/materialflows';
import { computed, ref } from 'vue';

const visible = ref(false);
const jobList = useJobStore();

const stages = ref([
    { name: 'INGEST', code: 'INGEST' },
    { name: 'DEPOSIT', code: 'DEPOSIT' },
    { name: 'FINALIZE', code: 'FINALIZE' },
    { name: 'FINISHED', code: 'FINISHED' }
]);

const states = ref([
    { name: 'INITIALED', code: 'INITIALED' },
    { name: 'RUNNING', code: 'RUNNING' },
    { name: 'PAUSED', code: 'PAUSED' },
    { name: 'SUCCEED', code: 'SUCCEED' },
    { name: 'FAILED', code: 'FAILED' },
    { name: 'CANCELED', code: 'CANCELED' }
]);

const flowStore = useMaterialFlowsStore();
flowStore.fetchAllData();

const onReset = () => {
    searchConditions.fromDate = undefined;
    searchConditions.toDate = undefined;
    searchConditions.selectedData = [];
    searchConditions.selectedStages = [];
    searchConditions.selectedStates = [];
    searchConditions.isIncludeDeactiveJobs = true;
};

const onSearch = () => {
    const req = {
        dtStart: 0,
        dtEnd: Date.now().valueOf(),
        flowIds: [] as string[],
        stages: [] as string[],
        states: [] as string[],
        isIncludeDeactiveJobs: isIncludeDeactiveJobs.value
    };

    if (searchConditions.fromDate) {
        req.dtStart = searchConditions.fromDate.valueOf();
    }

    if (searchConditions.toDate) {
        req.dtEnd = searchConditions.toDate.valueOf();
    }

    searchConditions.selectedStages.forEach((stage: any) => {
        req.stages.push(stage.code);
    });

    searchConditions.selectedStates.forEach((state: any) => {
        req.states.push(state.code);
    });

    for (let flow in searchConditions.selectedData) {
        if (!flow.startsWith('producer')) {
            req.flowIds.push(flow);
        }
    }
    jobList.searchData(req);
    visible.value = false;
};

const isIndeteminate = computed(() => {
    return true;
});
const producersSelect = ref(false);
const isIncludeDeactiveJobs = ref(true);

const show = () => {
    visible.value = true;
};

defineExpose({ show });
</script>

<template>
    <Dialog v-model:visible="visible" modal header="Advanced Filter" :style="{ width: '75rem' }">
        <div class="font-semibold">Latested Update Datetime</div>
        <InputGroup class="mt-2 mb-2">
            <InputGroupAddon>From</InputGroupAddon>
            <DatePicker v-model="searchConditions.fromDate" />
            <InputGroupAddon>To</InputGroupAddon>
            <DatePicker v-model="searchConditions.toDate" />
        </InputGroup>
        <div class="flex flex-wrap gap-4 mt-6">
            <div class="flex flex-col grow basis-0 gap-2">
                <label for="name2">Stage</label>
                <MultiSelect v-model="searchConditions.selectedStages" :options="stages" optionLabel="name" placeholder="Select stages" :filter="false"></MultiSelect>
            </div>
            <div class="flex flex-col grow basis-0 gap-2">
                <label for="email2">State</label>
                <MultiSelect v-model="searchConditions.selectedStates" :options="states" optionLabel="name" placeholder="Select states" :filter="false"></MultiSelect>
            </div>
        </div>

        <div class="mt-6 font-semibold">Material Flows</div>

        <div style="width: 100%; height: 25rem">
            <Tree v-model:selectionKeys="searchConditions.selectedData" :value="flowStore.treeData" selectionMode="checkbox" class="w-full" scrollHeight="flex">
                <template #default="slotProps">
                    <b v-if="slotProps.node.type == 'producer'">{{ slotProps.node.label }}</b>
                    <span v-if="slotProps.node.type == 'materialflow'">{{ slotProps.node.label }}</span>
                    <Badge v-if="slotProps.node.type == 'materialflow' && !slotProps.node.data.enabled" value="Disabled" severity="info" class="ml-4"></Badge>
                </template>
            </Tree>
        </div>
        <template #footer>
            <Toolbar style="width: 100%; border: 0">
                <template #start>
                    <div class="flex justify-center flex-wrap gap-4 mt-4" style="justify-content: flex-start">
                        <Button type="button" label="Reset" @click="onReset()"></Button>
                    </div>
                </template>

                <template #end>
                    <div class="flex justify-center flex-wrap gap-4 mt-4" style="justify-content: flex-end">
                        <!-- <InputGroup class="mt-2 mb-2">
                            <ToggleSwitch v-model="isIncludeDeactiveJobs" inputId="includeDeactiveJobs" />
                            <label for="includeDeactiveJobs" class="ml-2"> Include deactive jobs </label>
                        </InputGroup> -->
                        <Button type="button" label="Search" @click="onSearch()" autofocus></Button>
                        <Button type="button" label="Close" @click="visible = false" severity="secondary"></Button>
                    </div>
                </template>
            </Toolbar>
        </template>
    </Dialog>
</template>
