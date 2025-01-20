<script setup lang="ts">
import { useThemeStore } from '@/utils/themes';
import { RouterView } from 'vue-router';
const themeStore = useThemeStore();

let darkMode = false;
let colorMode = 'indigo';

themeStore.toggleTheme(darkMode, colorMode);
</script>

<template>
    <Suspense>
        <RouterView />
    </Suspense>
    <Suspense>
        <DynamicDialog />
    </Suspense>
    <Suspense>
        <ConfirmDialog />
    </Suspense>
</template>
