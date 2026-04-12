import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'
import PhotosView from '../views/PhotosView.vue'
import CalendarView from '../views/CalendarView.vue'
import MapView from '../views/MapView.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: HomeView,
    },
    {
      path: '/photos',
      name: 'photos',
      component: PhotosView,
    },
    {
      path: '/calendar',
      name: 'calendar',
      component: CalendarView,
    },
    {
      path: '/map',
      name: 'map',
      component: MapView,
    },
  ],
})

export default router
