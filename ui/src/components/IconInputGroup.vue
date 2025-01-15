<script setup lang="ts">
import { defineProps } from 'vue';
defineProps<{
    label?: string;
    icon?: string;
}>();
</script>

<template>
    <div class="p-iconfield p-inputgroup p-inputgroup-fluid mt-2 mb-2" style="border-radius: 0%">
        <InputGroupAddon style="border-radius: 0%">{{ label }}</InputGroupAddon>
        <slot></slot>
        <InputIcon :class="icon" style="z-index: 1000" />
    </div>
</template>
