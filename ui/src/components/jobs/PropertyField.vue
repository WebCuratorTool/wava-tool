<template>
    <div class="grid mb-2 mt-2">
        <div class="col-3" style="padding: 0.5rem; text-align: left;">
            <label>{{ title }}:</label>
        </div>
        <div class="col-9"  style="padding: 0.5rem; background: black;">
             <p>{{ value }}</p>
        </div>
    </div>
</template>

<script setup lang="ts">
defineProps<{
    title: string,
    value: string,
}>()
</script>

<style>
label{
    width: 100%;
    display: inline-block;
    position: relative;
    text-align: left;
    margin-right: 0;
}

p{
    width: 100%;
    display: inline-block;
    position: relative;
    text-align: left;
    margin-right: 0;
}

</style>