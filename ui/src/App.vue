<script setup lang="ts">
import { useThemeStore } from '@/utils/themes';
import { useBrowserLocation } from '@vueuse/core';
import { ref } from 'vue';
import LoginDialog from './components/LoginDialog.vue';
import { useLoginStore } from './utils/rest.api';
import MainView from './views/MainView.vue';
const loginStore = useLoginStore();
const themeStore = useThemeStore();

let location = useBrowserLocation();
let hostname: string = location.value.hostname;
let darkMode = false;
let colorMode = 'indigo';
let hostType = 'DEV';

// hostname = 'wlguatrosiapp01.natlib.govt.nz';

if (hostname && hostname.length > 6) {
    hostname = hostname.toUpperCase();
    hostType = hostname.substring(3, 3 + 3);
    if (hostType === 'PRD') {
        darkMode = true;
        colorMode = 'lime';
    } else if (hostType === 'UAT') {
        darkMode = true;
        colorMode = 'rose';
    } else {
        hostType = 'DEV';
    }
}

const envType = ref(hostType);
themeStore.toggleTheme(darkMode, colorMode);
</script>

<template>
    <LoginDialog v-if="loginStore.visibleLoginWindow" id="login-dialog" />

    <div v-show="!loginStore.visibleLoginWindow">
        <Suspense> <MainView :env-type="envType" /> </Suspense>
    </div>
    <Suspense>
        <DynamicDialog />
    </Suspense>
    <ConfirmDialog></ConfirmDialog>
</template>

<style>
#login-dialog {
    position: fixed;
    z-index: 9999;
}
</style>
