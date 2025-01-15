import { useUserProfileStore } from '@/stores/users';
import { defineStore } from 'pinia';
import { useToast } from 'primevue/usetoast';
import { computed, reactive, ref } from 'vue';

const RootContextPath = '/deposit-dashboard';
const ToastLife = 6 * 1000;
const RetryDelay = 3 * 1000;

type HttpMethod = 'GET' | 'POST' | 'PUT' | 'DELETE' | 'PATCH' | 'HEAD' | 'OPTIONS';

const sleep = (ms: number) => new Promise((r) => setTimeout(r, ms));

export const useLoginStore = defineStore('LoginStore', () => {
    const feedback = reactive({
        ok: true,
        title: '',
        detail: ''
    });
    const username = ref('');
    const password = ref('');
    const userProfile = useUserProfileStore();
    const isAuthenticating = ref(false);
    const isInitialed = ref(false);
    const startLogin = () => {
        if (!isAuthenticating.value) {
            isAuthenticating.value = true;
        }
    };

    const logout = () => {
        username.value = '';
        password.value = '';
        userProfile.clear();
        startLogin();
    };

    const authenticate = async () => {
        feedback.ok = true;
        feedback.title = '';
        feedback.detail = '';

        const credentials = JSON.stringify({
            username: username.value,
            password: password.value
        });
        const rsp = await fetch(RootContextPath + '/auth/login.json', {
            method: 'POST',
            redirect: 'error',
            headers: {
                'Content-Type': 'application/json'
            },
            body: credentials
        }).catch((err: any) => {
            feedback.ok = false;
            feedback.title = 'Error';
            feedback.detail = err.message;
        });

        //Exception has happened
        if (!rsp) {
            console.log(feedback);
            return;
        }

        if (!rsp.ok) {
            let status = rsp.status;
            let statusText = rsp.statusText;
            if (!statusText || statusText.length === 0) {
                if (status === 401) {
                    statusText = 'Unknown username or password, please try again.';
                } else {
                    statusText = 'Unknown error.';
                }
            }
            feedback.ok = false;
            feedback.title = 'Error: ' + status;
            feedback.detail = statusText;
            return;
        }

        const token = await rsp.text();
        userProfile.setToken(username, token);

        isAuthenticating.value = false;
        isInitialed.value = true;
    };

    const visibleLoginWindow = computed(() => {
        if (!isInitialed.value) {
            return true;
        }
        return isAuthenticating.value;
    });

    return { startLogin, authenticate, logout, visibleLoginWindow, isAuthenticating, isInitialed, feedback, username, password };
});

export interface UseFetchApis {
    // methods
    get: (path: string) => any;
    post: (path: string, payload: any) => any;
    put: (path: string, payload: any) => any;
    delete: (path: string, payload: any) => any;
    patch: (path: string, payload: any) => any;
    head: (path: string) => any;
    options: (path: string, payload: any) => any;
}

// by convention, composable function names start with "use"
export function useFetch() {
    // state encapsulated and managed by the composable
    // const dialog = useDialog();
    const toast = useToast();

    const shell: UseFetchApis = {
        // method
        get: setMethod('GET'),
        put: setMethod('PUT'),
        post: setMethod('POST'),
        delete: setMethod('DELETE'),
        patch: setMethod('PATCH'),
        head: setMethod('HEAD'),
        options: setMethod('OPTIONS')
    };

    function setMethod(methodValue: HttpMethod) {
        return async (path: string, payload: any = null) => {
            const loginStore = useLoginStore();
            const userProfile = useUserProfileStore();

            let ret = null;

            const isFinished = ref(false);

            //Retry until it's finished. If the login session is expired, it can be run 2 rounds
            while (!isFinished.value) {
                // Waiting until the authentication is finished
                while (loginStore.isAuthenticating) {
                    await sleep(1000);
                }

                const reqOptions: RequestInit = {
                    method: methodValue,
                    credentials: 'same-origin',
                    redirect: 'error',
                    headers: {
                        'Content-Type': 'application/json',
                        Authorization: userProfile.token
                    }
                };
                if (payload !== null && payload !== undefined) {
                    reqOptions.body = JSON.stringify(payload);
                }

                const rsp = await fetch(RootContextPath + path, reqOptions).catch((err: any) => {
                    toast.removeAllGroups();
                    toast.add({ severity: 'warn', summary: 'Warning! ', detail: err.message, life: ToastLife });
                });

                //Exception has happened
                if (!rsp) {
                    // await sleep(RetryDelay);
                    // continue;
                    break;
                }

                //Need authentication, forward to login page
                if (rsp.status === 401) {
                    loginStore.startLogin();
                    continue;
                }

                //Upstream error
                if (rsp.status >= 500 && rsp.status <= 599) {
                    toast.removeAllGroups();
                    toast.add({ severity: 'warn', summary: 'Warning!', detail: '[' + rsp.status + '] System Error! Will retry in ' + RetryDelay / 1000 + ' seconds.', life: ToastLife });
                    await sleep(RetryDelay);
                    continue;
                }

                // const headerNames = rsp.headers.keys();
                // while (true) {
                //     const headerName = headerNames.next();
                //     if (headerName.done) {
                //         break;
                //     }
                //     console.log(headerName.value);
                // }

                if (rsp.ok) {
                    const contentType = rsp.headers.get('content-type') || '';
                    const contentLength = parseInt(rsp.headers.get('content-length') || '-1');

                    if (contentType.length === 0 && contentLength <= 0) {
                        ret = null;
                    } else if (contentType.startsWith('application/json')) {
                        ret = await rsp.json();
                    } else if (contentType.startsWith('application') || contentType.startsWith('image') || contentType.startsWith('video') || contentType.startsWith('audio')) {
                        ret = await rsp.blob();
                    } else {
                        ret = await rsp.text();
                    }
                } else {
                    let error = '';
                    let e = await rsp.text();
                    if (!e) {
                        e = 'Unknown error';
                    }
                    error = '[' + path + '] ' + e + ' StatusCode: ' + rsp.status;
                    toast.removeAllGroups();
                    toast.add({ severity: 'error', summary: 'Error!', detail: error, life: ToastLife });
                    ret = undefined;
                }
                isFinished.value = true;
            }
            return ret;
        };
    }

    // expose managed state as return value
    return shell;
}
