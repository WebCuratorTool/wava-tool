<script setup lang="ts">
import { ref } from 'vue';

const visLocation = ref('/home.html');
</script>

<template>
    <Toast position="bottom-left"></Toast>
    <div class="row-container">
        <iframe :src="visLocation" class="full-screen"></iframe>
    </div>
</template>

<style>
.row-container {
    display: flex;
    width: 100vw;
    height: 100vh;
    flex-direction: column;
    background-color: blue;
    overflow: hidden;
}

.full-screen {
    flex-grow: 1;
    border: none;
    margin: 0;
    padding: 0;
}
</style>
