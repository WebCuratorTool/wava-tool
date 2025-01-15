import { type UseFetchApis, useFetch } from '@/utils/rest.api';
import { FilterMatchMode, FilterOperator } from '@primevue/core/api';
import { saveAs } from 'file-saver';
import { defineStore } from 'pinia';
import { reactive, ref } from 'vue';

export const depositJobList = ref([]);
export const keywords = ref();

export const searchConditions = reactive({
    fromDate: undefined as any,
    toDate: undefined as any,
    selectedData: [] as any[],
    selectedStages: [] as any[],
    selectedStates: [] as any[],
    isIncludeDeactiveJobs: true
});

export const jobFilters = ref({
    global: { value: null, matchMode: FilterMatchMode.CONTAINS },
    // 'id': { value: null, matchMode: FilterMatchMode.CONTAINS },
    injectionTitle: {
        operator: FilterOperator.OR,
        constraints: [{ value: null, matchMode: FilterMatchMode.CONTAINS }]
    },
    'appliedFlowSetting.materialFlowName': {
        operator: FilterOperator.OR,
        constraints: [{ value: null, matchMode: FilterMatchMode.CONTAINS }]
    }
    // 'stage': { value: null, matchMode: FilterMatchMode.CONTAINS },
    // 'state': { value: null, matchMode: FilterMatchMode.CONTAINS },
    // 'sipID': { value: null, matchMode: FilterMatchMode.CONTAINS },
});

export const formatDatetimeFromEpochMilliSeconds = (epochMilliSecond: any) => {
    if (!epochMilliSecond || epochMilliSecond === 0) {
        return 'NA';
    }

    var date = new Date(epochMilliSecond);
    return date.toLocaleString();
};

export const formatMaterialFlowGroup = (data: any) => {
    if (!data || !data.appliedFlowSetting || !data.appliedFlowSetting.materialFlowName) {
        return 'Unknown';
    }

    return data.appliedFlowSetting.materialFlowName;
};

export const getProgressBarClass = (data: any) => {
    if (data.stage === 'FINISHED' && data.state === 'CANCELED') {
        return 'abnormal-progressbar';
    } else {
        return '';
    }
};

export const calcProgressPercent = (data: any) => {
    const stage = data.stage;
    const state = data.state;

    let percent = 0;
    if (stage === 'INGEST') {
        percent = 0;
    } else if (stage === 'DEPOSIT') {
        percent = 33.33;
    } else if (stage === 'FINALIZE') {
        percent = 66.77;
    } else {
        percent = 100;
    }

    var percentState = 0.0;
    if (state === 'INITIALED') {
        percentState = 0.1;
    } else if (state === 'RUNNING' || state === 'PAUSED') {
        percentState = 0.5;
    } else {
        percentState = 1.0;
    }

    percent += percentState * 33.33;

    if (percent > 99) {
        percent = 100;
    }

    // return percent.toFixed(0);
    // return parseInt(percent.toFixed(2));
    return Math.round(percent);
};

const isIncludes = (fieldValue: any, keywords: string) => {
    if (!fieldValue) {
        return false;
    }
    if (typeof fieldValue === 'number') {
        return fieldValue.toString().includes(keywords);
    } else if (typeof fieldValue === 'string') {
        return fieldValue.toLowerCase().includes(keywords);
    } else {
        return false;
    }
};

const jobFilter = (job: any, keywords: string) => {
    if (
        isIncludes(job.id, keywords) ||
        isIncludes(job.materialFlowName, keywords) ||
        isIncludes(job.injectionTitle, keywords) ||
        isIncludes(job.stage, keywords) ||
        isIncludes(job.state, keywords) ||
        isIncludes(job.sipID, keywords) ||
        isIncludes(job.sipModule, keywords) ||
        isIncludes(job.sipStage, keywords) ||
        isIncludes(job.sipStatus, keywords)
    ) {
        return true;
    }
};

export const useJobStore = defineStore('JobStore', () => {
    const listJobs = ref([]);
    const listJobsFiltered = ref();
    const selectedJobs = ref([]);
    const subFolder = ref();
    const rest: UseFetchApis = useFetch();

    const fetchAllData = () => {
        rest.get('/restful/deposit-jobs/jobs/active/list')
            .then((data: any) => {
                listJobs.value = data.map((e: any) => ({
                    ...e,
                    progress: calcProgressPercent(e),
                    materialFlowName: formatMaterialFlowGroup(e)
                }));
                filter(keywords.value);
            })
            .catch((err: any) => {
                console.log(err.message);
            });
    };

    const searchData = (searchConditions: any) => {
        rest.post('/restful/deposit-jobs/search', searchConditions)
            .then((data: any) => {
                listJobs.value = data.map((e: any) => ({
                    ...e,
                    progress: calcProgressPercent(e),
                    materialFlowName: formatMaterialFlowGroup(e)
                }));
                filter(keywords.value);
            })
            .catch((err: any) => {
                console.log(err.message);
            });
    };

    const filter = (keywords: string) => {
        let lowerKeywords = '';
        if (keywords) {
            lowerKeywords = keywords.toLowerCase();
        }

        listJobsFiltered.value = listJobs.value.filter((e: any) => jobFilter(e, lowerKeywords));
    };

    const exportSelectedJobs = () => {
        const req: any[] = [];
        selectedJobs.value.forEach((job: any) => {
            req.push(job.id);
        });
        rest.post('/restful/deposit-jobs/export-data', req)
            .then((blob: any) => {
                if (blob) {
                    console.log('Succeed to export the selected jobs');
                    saveAs(blob, 'deposit_jobs.xlsx');
                }
            })
            .catch((err: any) => {
                console.log(err.message);
            });
    };

    const redeposit = () => {
        rest.get('/restful/deposit-jobs/redeposit?subFolder=' + subFolder.value)
            .then((data: any) => {
                console.log('Succeed to redeposit: ' + subFolder.value);
            })
            .catch((err: any) => {
                console.log(err.message);
            });
    };
    return { selectedJobs, listJobsFiltered, subFolder, fetchAllData, searchData, filter, exportSelectedJobs, redeposit };
});
