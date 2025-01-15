import { useToastStore } from '@/utils/helper';
import { useFetch, type UseFetchApis } from '@/utils/rest.api';
import { defineStore } from 'pinia';
import { useConfirm } from 'primevue/useconfirm';
import { reactive, ref } from 'vue';

export const globalSettings = reactive({
    id: -1,
    paused: false,
    pausedStartTime: 0,
    pausedEndTime: 0,
    delays: 60,
    delayUnit: 'second'
});

export const depositAccount = reactive({
    id: -1
});

export const useSettingsDepositAccountStore = defineStore('SettingsDepositAccountStore', () => {
    const toast = useToastStore();
    const confirm = useConfirm();
    const rest: UseFetchApis = useFetch();

    const dataList = ref();
    const data = ref();
    const selectedRow = ref();

    const queryAllRows = async () => {
        dataList.value = await rest.get('/restful/setting/deposit-account/all/get');
        return dataList.value;
    };

    const queryRow = async (id: number) => {
        data.value = await rest.get('/restful/setting/deposit-account/detail?id=' + id);
        return data.value;
    };

    const deleteConfirm = (rowData: any) => {
        confirm.require({
            message: 'Do you want to delete this record?',
            header: 'Confirmation',
            icon: 'pi pi-exclamation-triangle',
            rejectProps: {
                label: 'Cancel',
                severity: 'secondary',
                outlined: true
            },
            acceptProps: {
                label: 'Delete',
                severity: 'danger'
            },
            accept: () => {
                deleteRow(rowData);
            },
            reject: () => {}
        });
    };
    const deleteRow = async (rowData: any) => {
        if (!rowData) {
            toast.error('The input param can not be null.');
            return;
        }
        const ret = await rest.delete('/restful/setting/deposit-account/delete?id=' + rowData.id, undefined);
        if (ret !== undefined) {
            toast.success('Succeed to delete the deposit account');
            queryAllRows();
        }
    };

    const saveRow = async (rowData: any) => {
        if (!rowData) {
            toast.error('The input param can not be null.');
            return;
        }
        const ret = await rest.post('/restful/setting/deposit-account/save', rowData);
        if (ret !== undefined) {
            toast.success('Succeed to save the deposit account');
            queryAllRows();
        }
        return ret;
    };

    const depositAccountName = async (id: number) => {
        const data: any = queryRow(id);
        if (!data) {
            return '';
        }
        return data.depositUserInstitute + ' | ' + data.depositUserName;
    };

    return { dataList, data, selectedRow, queryAllRows, queryRow, deleteConfirm, deleteRow, saveRow, depositAccountName };
});

export const useSettingsMaterialFlowStore = defineStore('SettingsMaterialFlowStore', () => {
    const toast = useToastStore();
    const confirm = useConfirm();
    const rest: UseFetchApis = useFetch();

    const dataList = ref();
    const data = ref();

    const queryAllRows = async () => {
        dataList.value = await rest.get('/restful/setting/flow/all/get');
        return dataList.value;
    };

    const queryRow = async (id: number) => {
        data.value = await rest.get('/restful/setting/flow/detail?id=' + id);
        return data.value;
    };

    const deleteConfirm = (rowData: any) => {
        confirm.require({
            message: 'Do you want to delete this record?',
            header: 'Confirmation',
            icon: 'pi pi-exclamation-triangle',
            rejectProps: {
                label: 'Cancel',
                severity: 'secondary',
                outlined: true
            },
            acceptProps: {
                label: 'Delete',
                severity: 'danger'
            },
            accept: () => {
                deleteRow(rowData);
            },
            reject: () => {}
        });
    };
    const deleteRow = async (rowData: any) => {
        if (!rowData) {
            const error = 'The input param can not be null.';
            console.error(error);
            toast.error(error);
            return;
        }
        const ret = await rest.delete('/restful/setting/flow/delete?id=' + rowData.id, undefined);
        if (ret !== undefined) {
            toast.success('Succeed to delete the material flow');
            queryAllRows();
        }
    };

    const saveRow = async (rowData: any) => {
        if (!rowData) {
            toast.error('The input param can not be null.');
            return;
        }
        const ret = await rest.post('/restful/setting/flow/save', rowData);
        if (ret !== undefined) {
            toast.success('Succeed to SAVE the material flow');
            queryAllRows();
        }
        return ret;
    };

    return {
        queryAllRows,
        queryRow,
        deleteConfirm,
        deleteRow,
        saveRow,
        dataList,
        data
    };
});

export const useSettingsWhiteListStore = defineStore('SettingsWhiteListStore', () => {
    const toast = useToastStore();
    const confirm = useConfirm();

    const rest: UseFetchApis = useFetch();

    const dataList = ref();
    const data = ref();
    const selectedRow = ref();

    const queryAllRows = async () => {
        dataList.value = await rest.get('/restful/setting/whitelist/all/get');
        return dataList.value;
    };

    const queryRow = async (id: number) => {
        data.value = await rest.get('/restful/setting/whitelist/detail?id=' + id);
        return data.value;
    };

    const deleteConfirm = (rowData: any) => {
        confirm.require({
            message: 'Do you want to delete this record?',
            header: 'Confirmation',
            icon: 'pi pi-exclamation-triangle',
            rejectProps: {
                label: 'Cancel',
                severity: 'secondary',
                outlined: true
            },
            acceptProps: {
                label: 'Delete',
                severity: 'danger'
            },
            accept: () => {
                deleteRow(rowData);
            },
            reject: () => {}
        });
    };
    const deleteRow = async (rowData: any) => {
        if (!rowData) {
            toast.error('The input param can not be null.');
            return;
        }
        const ret = await rest.delete('/restful/setting/whitelist/delete?id=' + rowData.id, undefined);
        if (ret !== undefined) {
            toast.success('Succeed to delete the white list item');
            queryAllRows();
        }
    };

    const saveRow = async (rowData: any) => {
        if (!rowData) {
            toast.error('The input param can not be null.');
            return;
        }
        const ret = await rest.post('/restful/setting/whitelist/save', rowData);
        if (ret !== undefined) {
            toast.success('Succeed to save the white list item');
            queryAllRows();
        }
        return ret;
    };

    return { dataList, data, selectedRow, queryAllRows, queryRow, deleteConfirm, deleteRow, saveRow };
});

export const useSettingsGlobalStore = defineStore('SettingsGlobalStore', () => {
    const toast = useToastStore();
    const rest: UseFetchApis = useFetch();

    const data = ref();
    const selectedRow = ref();

    const queryRow = async () => {
        data.value = await rest.get('/restful/setting/global/get');
        return data.value;
    };

    const saveRow = async (rowData: any) => {
        if (!rowData) {
            toast.error('The input param can not be null.');
            return;
        }
        const ret = await rest.post('/restful/setting/global/save', rowData);
        if (ret !== undefined) {
            toast.success('Succeed to save the global setting');
            queryRow();
        }
        return ret;
    };

    return { data, selectedRow, queryRow, saveRow };
});
