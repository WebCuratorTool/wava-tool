import DepositJobRedeposit from '@/components/jobs/DepositJobRedeposit.vue';
import DepositJobSearchDialog from '@/components/jobs/DepositJobSearchDialog.vue';
import { useJobStore } from '@/stores/depositjob';
import { type UseFetchApis, useFetch } from '@/utils/rest.api';
import { defineStore } from 'pinia';
import { useDialog } from 'primevue/usedialog';
import { useToast } from 'primevue/usetoast';
import { ref } from 'vue';

export const useTopbarActions = defineStore('TopbarActions', () => {
    const toast = useToast();
    const dialog = useDialog();
    const jobList = useJobStore();
    const rest: UseFetchApis = useFetch();
    const selectedContextRow = ref();

    const onSearch = (searchOptions: any) => {};
    const openSearchDialog = () => {
        const dialogRef = dialog.open(DepositJobSearchDialog, {
            props: {
                header: 'Advanced Filter',
                closable: true,
                style: {
                    width: '75rem'
                },
                modal: true
            }
        });
    };

    const onReload = () => {
        jobList.fetchAllData();
    };

    const onExportSelectedJobs = () => {
        jobList.exportSelectedJobs();
    };

    const openRedepositDialog = () => {
        const dialogRef = dialog.open(DepositJobRedeposit, {
            props: {
                header: 'Redepoit Job',
                closable: true,
                style: {
                    width: '75rem'
                },
                modal: true
            }
        });
    };

    return { openSearchDialog, onReload, onExportSelectedJobs, openRedepositDialog };
});
