import type { MaterialFlow } from '@/types/deposit';
import { type UseFetchApis, useFetch } from '@/utils/rest.api';
import { defineStore } from 'pinia';
import { computed, ref } from 'vue';

export const useMaterialFlowsStore = defineStore('MaterialFlows', () => {
    const data = ref([] as MaterialFlow[]);
    const filteredData = ref();
    const selectedData = ref([]);
    const rest: UseFetchApis = useFetch();

    const fetchAllData = () => {
        rest.get('/restful/setting/flow/all/get')
            .then((rsp: any) => {
                data.value = rsp;
            })
            .catch((err: any) => {
                console.log(err.message);
            });
    };

    const treeData = computed(() => {
        if (!data) {
            return;
        }
        const map = new Map();
        for (let flow of data.value) {
            let producerNode;
            if (!map.has(flow.producerId)) {
                producerNode = {
                    key: 'producer@' + flow.producerId,
                    label: flow.producerName,
                    data: flow.producerId,
                    icon: 'pi pi-folder',
                    type: 'producer',
                    children: []
                };
                map.set(flow.producerId, producerNode);
            } else {
                producerNode = map.get(flow.producerId);
            }
            const materialFlowNode = {
                key: flow.id,
                label: flow.materialFlowName,
                data: flow,
                icon: 'pi pi-file',
                type: 'materialflow'
            };
            producerNode.children.push(materialFlowNode);
        }

        const nodes = [] as any[];
        map.forEach((node: any, key: string) => {
            nodes.push(node);
        });

        return nodes;
    });

    return { data, filteredData, selectedData, fetchAllData, treeData };
});
