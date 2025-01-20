import { createRouter, createWebHistory } from 'vue-router';

export const routes = {
    history: createWebHistory(import.meta.env.BASE_URL),
    routes: [
        {
            path: '/',
            name: 'default',
            component: () => import('@/views/MainView.vue')
        },
        {
            path: '/home',
            name: 'home',
            component: () => import('@/views/MainView.vue')
        },
        {
            path: '/wava',
            name: 'wava',
            component: () => import('@/views/MainView.vue')
        }
    ]
};

const router = createRouter(routes);

export default router;
