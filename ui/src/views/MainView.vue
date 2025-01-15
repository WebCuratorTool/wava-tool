<script setup lang="ts">
import DepositJobListDataTable from '@/components/jobs/DepositJobListDataTable.vue';
import DepositJobSearchDialog from '@/components/jobs/DepositJobSearchDialog.vue';
import DepositAccountDrawer from '@/components/settings/DepositAccountDrawer.vue';
import GlobalSetting from '@/components/settings/GlobalSetting.vue';
import MaterialFlowDrawer from '@/components/settings/MaterialFlowDrawer.vue';
import WhiteListDrawer from '@/components/settings/WhiteListDrawer.vue';
import { useLayout } from '@/layout/composables/layout';
import { useJobStore } from '@/stores/depositjob';
import { useTopbarActions } from '@/stores/depositjobTopbarActions';
import { useUserProfileStore } from '@/stores/users';
import { useLoginStore } from '@/utils/rest.api';
import Menu from 'primevue/menu';
import { useDialog } from 'primevue/usedialog';
import { computed, ref } from 'vue';
import { useRouter } from 'vue-router';

defineProps<{
    envType?: string;
}>();

const dialog = useDialog();

const router = useRouter();
const { onMenuToggle, toggleDarkMode, isDarkTheme } = useLayout();

const keywords = ref();

const jobList = useJobStore();
const loginStore = useLoginStore();
const topbarMenuActive = ref(false);
const topbarMenuClasses = computed(() => {
    return {
        'layout-topbar-menu-mobile-active': topbarMenuActive.value
    };
});
/*  */
const dlgSearch = ref();
const drawerDepositAccount = ref();
const drawerMaterialFlow = ref();
const drawerWhiteList = ref();

const menu = ref();
const settingsMenuItems = ref([
    {
        label: 'Deposit Account Settings',
        icon: 'pi pi-users',
        command: () => {
            // router.push('/setting/deposit-account');
            drawerDepositAccount.value.toggle();
        }
    },
    {
        label: 'Material Flow Settings',
        icon: 'pi pi-objects-column',
        command: () => {
            drawerMaterialFlow.value.toggle();
        }
    },
    {
        label: 'User White List',
        icon: 'pi pi-list-check',
        command: () => {
            drawerWhiteList.value.toggle();
        }
    },
    {
        separator: true
    },
    {
        label: 'Global Setting',
        icon: 'pi pi-globe',
        command: () => {
            const dialogRef = dialog.open(GlobalSetting, {
                props: {
                    header: 'Global Settings',
                    closable: true,
                    style: {
                        width: '50rem'
                    },
                    modal: true
                }
            });
        }
    },
    {
        separator: true
    },
    {
        label: 'Sign out',
        icon: 'pi pi-power-off',
        command: () => {
            loginStore.logout();
        }
    }
]);

const toggleMenu = (event: any) => {
    menu.value.toggle(event);
};

const token = useUserProfileStore();
const logout = () => {
    token.clear();
};

const topbarActions = useTopbarActions();
</script>

<template>
    <Toast position="bottom-left"></Toast>
    <!-- <div class="layout-topbar" style="position: relative; background: linear-gradient(to right, #212529, #32cd32, #212529)"> -->
    <div class="layout-topbar" style="position: relative">
        <div class="layout-topbar-logo-container">
            <router-link to="/" class="layout-topbar-logo">
                <img src="@/assets/natlib-logo-red.png" width="50" height="50" alt="logo" />
                <OverlayBadge :value="envType"> Deposit Dashboard </OverlayBadge>
            </router-link>
        </div>

        <div style="width: 1rem"></div>

        <Button label="Search" icon="pi pi-search" class="mr-2" @click="dlgSearch.show()"></Button>
        <Button @click="topbarActions.onReload()" label="Reload" icon="pi pi-refresh" class="mr-2"></Button>
        <Button @click="topbarActions.onExportSelectedJobs()" label="Export Selected Jobs" icon="pi pi-download" class="mr-2"></Button>
        <Button @click="topbarActions.openRedepositDialog()" label="Redeposit" icon="pi pi-pen-to-square" class="mr-2"></Button>

        <div class="layout-topbar-actions">
            <IconField class="mr-2">
                <InputIcon class="pi pi-filter" />
                <InputText v-model="keywords" type="text" placeholder="Filter" />
            </IconField>
            <Button type="button" raised icon="pi pi-cog" severity="contrast" @click="toggleMenu" aria-haspopup="true" aria-controls="overlay_menu" class="mr-2" />
        </div>
    </div>
    <Menu ref="menu" id="overlay_menu" :model="settingsMenuItems" :popup="true">
        <template #item="{ item, props }">
            <a v-ripple :href="item.url" :target="item.target" v-bind="props.action" aria-hidden="false">
                <span :class="item.icon" />
                <span class="ml-2 p-1">{{ item.label }}</span>
            </a>
        </template>
    </Menu>

    <div style="width: 100vw; height: calc(100vh - 60px)">
        <DepositJobListDataTable />
    </div>

    <DepositJobSearchDialog ref="dlgSearch" />

    <DepositAccountDrawer ref="drawerDepositAccount" />
    <MaterialFlowDrawer ref="drawerMaterialFlow" />
    <WhiteListDrawer ref="drawerWhiteList" />
</template>
