<script setup lang="ts">
import { type UseFetchApis, useFetch } from '@/utils/rest.api';
import { defineEmits, ref } from 'vue';

const emit = defineEmits(['onSelected']);
const props = defineProps<{
    account?: number;
    producer?: string;
}>();
const rest: UseFetchApis = useFetch();
const rows = ref(10);
const first = ref(0);
const page = ref(0);
const totalRecords = ref(0);
const filterName = ref();
const curPageRows = ref([]);

const onRowSelect = (event: any) => {
    emit('onSelected', event.data);
};

const _search = () => {
    if (!props.account || props.account < 0) {
        console.error('Invalid account: ' + props.account);
        return;
    }

    if (!props.producer || props.producer.length == 0) {
        console.error('Invalid producer: ' + props.producer);
        return;
    }

    const searchCondition = {
        depositAccountId: props.account,
        producerId: props.producer,
        offset: page.value,
        limit: rows.value,
        name: filterName.value
    };

    rest.post('/restful/raw/materialflows', searchCondition).then((rsp: any) => {
        if (!rsp) {
            console.error('Can not get raw meterial flows: ' + rsp);
            return;
        }

        const datasets = JSON.parse(rsp);

        if (datasets && datasets.total_record_count) {
            totalRecords.value = datasets.total_record_count;
        } else {
            totalRecords.value = 0;
        }
        if (datasets && datasets.profile_material_flow) {
            curPageRows.value = datasets.profile_material_flow;
        } else {
            curPageRows.value = [];
        }
    });
};

const onSearch = () => {
    first.value = 0;
    page.value = 0;
    totalRecords.value = 0;
    curPageRows.value = [];
    _search();
};

const onPage = (event: any) => {
    page.value = event.page;
    _search();
};
_search();
</script>

<template>
    <Toolbar style="border: 0; padding-top: 0; padding-bottom: 0; margin: 0">
        <template #start>
            <InputGroup>
                <InputText v-model="filterName" placeholder="MaterialFlow name" />
                <Button icon="pi pi-search" @click="onSearch" />
            </InputGroup>
        </template>
        <template #center>
            <span>Records: {{ totalRecords }}</span>
        </template>
        <template #end> <Paginator v-model:first="first" :rows="rows" :totalRecords="totalRecords" :pageLinkSize="3" @page="onPage"></Paginator> </template>
    </Toolbar>

    <div class="mt-2">
        <DataTable :value="curPageRows" dataKey="id" selectionMode="single" @row-select="onRowSelect" tableStyle="width:100%;" sortField="id" :sortOrder="1" :rows="rows" scrollable scrollHeight="25rem">
            <Column field="id" header="ID"></Column>
            <Column field="name" header="MaterialFlow Name"></Column>
            <!-- <Column field="active" header="Active">
                <template #body="{ data }">
                    <span v-if="data.active" class="pi pi-check"></span>
                    <span v-else class="pi pi-times"></span>
                </template>
            </Column> -->
        </DataTable>
    </div>
</template>
