import { createRouter, createWebHistory } from 'vue-router';

export const routes = {
    history: createWebHistory(import.meta.env.BASE_URL),
    routes: [
        {
            path: '/',
            children: [
                {
                    path: '',
                    name: 'home',
                    component: () => import('@/views/MainView.vue')
                }
            ]
        }
    ]
};

const router = createRouter(routes);

export default router;
