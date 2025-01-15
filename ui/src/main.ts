import { createApp } from 'vue';
import App from './App.vue';
import router from './router';

import { createPinia } from 'pinia';

import Aura from '@primevue/themes/aura';
import PrimeVue from 'primevue/config';
import ConfirmationService from 'primevue/confirmationservice';
import DialogService from 'primevue/dialogservice';
import ToastService from 'primevue/toastservice';

import '@/assets/styles.scss';
import '@/assets/tailwind.css';

const app = createApp(App);

app.use(createPinia());
app.use(router);
app.use(PrimeVue, {
    theme: {
        preset: Aura,
        options: {
            // darkModeSelector: '.app-dark'
            prefix: 'p',
            darkModeSelector: '.app-dark',
            cssLayer: false
        }
    },
    locale: {
        accept: 'Aceptar',
        reject: 'Rechazar'
        //...
    }
});
app.use(ToastService);
app.use(ConfirmationService);
app.use(DialogService);

app.mount('#app');
