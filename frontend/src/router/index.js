import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'
import PhotosView from '../views/photos/PhotosView.vue'
import CalendarView from '../views/schedule/CalendarView.vue'
import MapView from '../views/map/MapView.vue'
import ScheduleRegisterView from '../views/schedule/ScheduleRegisterView.vue'
import ScheduleDetailView from '../views/schedule/ScheduleDetailView.vue'

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
    {
      path: '/schedule/register',
      name: 'schedule-register',
      component: ScheduleRegisterView,
    },
    {
      path: '/schedule/:id',
      name: 'schedule-detail',
      component: ScheduleDetailView,
    },
  ],
})

export default router
