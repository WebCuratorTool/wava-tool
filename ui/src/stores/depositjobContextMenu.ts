import DepositJobAvailableActionsDialog from '@/components/jobs/DepositJobAvailableActionsDialog.vue';
import DepositJobDetailDialog from '@/components/jobs/DepositJobDetailDialog.vue';
import { useJobStore } from '@/stores/depositjob';
import { type UseFetchApis, useFetch } from '@/utils/rest.api';
import { defineStore } from 'pinia';
import { useDialog } from 'primevue/usedialog';
import { useToast } from 'primevue/usetoast';
import { ref } from 'vue';

export const useContextMenu = defineStore('ContextMenu', () => {
    const toast = useToast();
    const dialog = useDialog();
    const jobList = useJobStore();
    const rest: UseFetchApis = useFetch();
    const selectedContextRow = ref();

    const isRowDataValidForAction = (action: string, rowData: any) => {
        action = action.toUpperCase();
        var stage = rowData.stage,
            state = rowData.state;
        switch (action) {
            case 'PAUSE':
                return ((stage === 'INGEST' || stage === 'FINALIZE') && state === 'RUNNING') || (stage === 'DEPOSIT' && state === 'INITIALED');
            case 'RESUME':
                return state === 'PAUSED';
            case 'RETRY':
                return stage === 'DEPOSIT' && state === 'FAILED';
            case 'TERMINATE':
                return (stage === 'FINISHED' && state === 'SUCCEED') || state === 'FAILED' || state === 'CANCELED';
            case 'CANCEL':
                return state !== 'RUNNING' && state !== 'CANCELED';
            default:
                return false;
        }
    };

    const viewJob = () => {
        if (!selectedContextRow.value) {
            console.log('There is no row selected.');
            toast.add({ severity: 'error', summary: 'Error Message', detail: 'There is no row selected.', life: 3000 });
            return;
        }

        const dialogRef = dialog.open(DepositJobDetailDialog, {
            props: {
                header: 'Deposit Job: ' + selectedContextRow.value.id,
                closable: true,
                style: {
                    width: '75rem'
                },
                modal: true
            },
            data: {
                data: selectedContextRow.value
            }
        });
    };

    const continueProcessDepositJobAction = (action: string, req: any[]) => {
        if (!req || req.length === 0) {
            return;
        }

        if (action.toUpperCase() === 'TERMINATE' && !confirm('The selected jobs and the related actual contents will be forced to be terminated and purged. Would you like to continue?')) {
            return;
        }

        rest.post('/restful/deposit-jobs/update' + '?action=' + action, req)
            .then((data: any) => {
                jobList.fetchAllData();
                toast.add({ severity: 'success', summary: 'Success Message', detail: 'Succeed to ' + action + ' the jobs.', life: 3000 });
            })
            .catch((err: any) => {
                console.log(err.message);
                toast.add({ severity: 'error', summary: 'Error Message', detail: err.message, life: 30000 });
            });
    };

    const editJob = (action: string) => {
        let selectedRows = [];

        if (jobList.selectedJobs && jobList.selectedJobs.length > 0) {
            selectedRows = jobList.selectedJobs;
        } else if (selectedContextRow.value) {
            selectedRows.push(selectedContextRow.value);
        } else {
            console.log('There is no row selected.');
            toast.add({ severity: 'error', summary: 'Error Message', detail: 'There is no row selected.', life: 3000 });
            return;
        }

        action = action.toUpperCase();

        const req: any[] = [];
        for (let rowData of selectedRows) {
            if (!isRowDataValidForAction(action, rowData)) {
                continue;
            }
            req.push(rowData);
        }

        let isValid = true;
        let needConfirm = false;
        let errMsg = '';
        if (selectedRows.length > 1) {
            if (req.length == 0) {
                isValid = false;
                errMsg = 'The selected jobs are NOT allowed to apply the ' + action + ' action. The available status for ' + action + ' are shown below:';
            } else if (req.length < selectedRows.length) {
                isValid = false;
                needConfirm = true;
                errMsg = 'Some of the selected jobs are NOT allowed to apply the ' + action + " action. Click 'Confirm' to continue. The available status for " + action + ' are shown below:';
            }
        } else if (selectedRows.length == 1 && req.length < selectedRows.length) {
            isValid = false;
            errMsg = 'The job is NOT allowed to apply the ' + action + ' action. The available status for ' + action + ' are shown below:';
        }

        if (isValid) {
            continueProcessDepositJobAction(action, req);
        } else {
            const dialogRef = dialog.open(DepositJobAvailableActionsDialog, {
                props: {
                    header: 'Warning',
                    closable: true,
                    style: {
                        width: '50rem'
                    },
                    modal: true
                },
                data: {
                    action: action,
                    needConfirm: needConfirm,
                    errMsg: errMsg
                },
                onClose: (options: any) => {
                    if (options.data.buttonType === 'confirm') {
                        continueProcessDepositJobAction(action, req);
                    }
                }
            });
        }
    };

    const contextMenuModel = ref([
        { label: 'Detail', icon: 'pi pi-fw pi-info-circle', command: () => viewJob() },
        { separator: true },
        { label: 'Retry', icon: 'pi pi-fw pi-refresh', command: () => editJob('retry') },
        { label: 'Cancel', icon: 'pi pi-fw pi-times-circle', command: () => editJob('cancel') },
        { separator: true },
        { label: 'Pause', icon: 'pi pi-fw pi-pause-circle', command: () => editJob('pause') },
        { label: 'Resume', icon: 'pi pi-fw pi-play-circle', command: () => editJob('resume') },
        { separator: true },
        { label: 'Terminate and Purge', icon: 'pi pi-fw pi-stop-circle', command: () => editJob('terminate') }
    ]);

    return { selectedContextRow, contextMenuModel };
});
