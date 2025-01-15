<script setup lang="ts">
import { useLoginStore } from '@/utils/rest.api';

const loginStore = useLoginStore();

// const checked = ref(false);

const login = () => {
    loginStore.authenticate();
};
</script>

<template>
    <div class="bg-surface-50 dark:bg-surface-950 flex items-center justify-center min-h-screen min-w-[100vw] overflow-hidden">
        <div class="flex flex-col items-center justify-center">
            <div style="border-radius: 56px; padding: 1px; background: linear-gradient(180deg, var(--primary-color) 10%, rgba(33, 150, 243, 0) 30%)">
                <div class="w-full bg-surface-0 dark:bg-surface-900 py-20 px-8 sm:px-20" style="border-radius: 53px">
                    <div class="tems-center text-center mb-8">
                        <img src="@/assets/natlib-logo-red.png" width="80" style="display: inline" />
                        <div class="text-surface-900 dark:text-surface-0 text-3xl font-medium mb-4">Welcome to Deposit Dashboard!</div>
                        <span class="text-muted-color font-medium">Sign in to continue</span>
                    </div>

                    <Message v-if="!loginStore.feedback.ok" severity="error" icon="pi pi-exclamation-triangle" :life="5000">
                        {{ loginStore.feedback.title + ':' + loginStore.feedback.detail }}
                    </Message>

                    <div>
                        <label for="username" class="block text-surface-900 dark:text-surface-0 text-xl font-medium mb-2">Username</label>
                        <InputText v-model="loginStore.username" id="username" type="text" placeholder="User name" class="w-full md:w-[30rem] mb-8" autocomplete="off" />

                        <label for="password1" class="block text-surface-900 dark:text-surface-0 font-medium text-xl mb-2">Password</label>
                        <Password v-model="loginStore.password" id="password1" placeholder="Password" :toggleMask="true" class="mb-4" fluid :feedback="false" autocomplete="off" />

                        <!-- <div class="flex items-center justify-between mt-2 mb-8 gap-8">
                            <div class="flex items-center">
                                <Checkbox v-model="checked" input-id="rememberme1" binary class="mr-2"></Checkbox>
                                <label for="rememberme1">Remember me</label>
                            </div>
                            <span class="font-medium no-underline ml-2 text-right cursor-pointer text-primary">Forgot password?</span>
                        </div> -->

                        <Button label="Sign In" class="mt-8 w-full" @click="login"></Button>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<style scoped>
.pi-eye {
    transform: scale(1.6);
    margin-right: 1rem;
}

.pi-eye-slash {
    transform: scale(1.6);
    margin-right: 1rem;
}
</style>
