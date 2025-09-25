<script setup lang="ts">
import { togglePreset, toggleThemeMode } from '@/utils/themes';
import { RouterView } from 'vue-router';

toggleThemeMode('light');
togglePreset('indigo');
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
