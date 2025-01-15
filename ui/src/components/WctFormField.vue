<template>
    <div class="grid mb-4">
        <div class="col-2" style="padding: 0.5rem; text-align: right">
            <label style="display: inline-block">{{ label }}:</label>
        </div>
        <div class="col-10" style="padding: 0.5rem">
            <IconField>
                <InputText v-model="producer" />
                <InputIcon class="pi pi-angle-down" />
            </IconField>
            <slot></slot>
        </div>
    </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';

defineProps<{
    label?: string;
    checkbox?: boolean;
}>();

const producer = ref();
</script>
