<script setup>
const date = new Date()

const { layoutState } = useLayout()
</script>

<template>
  <Sidebar v-model:visible="layoutState.rightMenuVisible.value" position="right" class="layout-profile-sidebar w-full sm:w-28rem" :class="{ 'layout-rightmenu-active': layoutState.rightMenuActive.value }">
    <div class="layout-rightmenu h-full overflow-y-auto overflow-x-hidden">
      <div class="user-detail-wrapper text-center" style="padding: 4.5rem 0 2rem 0">
        <div class="user-detail-content mb-4">
          <NuxtImg src="/layout/images/avatar/gene.png" alt="atlantis" class="user-image" />
          <span class="user-name text-2xl text-center block mt-4 mb-1">Gene Russell</span>
          <span class="user-number">(406) 555-0120</span>
        </div>
        <div class="user-tasks flex justify-content-between align-items-center py-4 px-3 border-bottom-1 surface-border">
          <div class="user-tasks-item in-progress font-medium">
            <a class="task-number text-red-500 flex justify-content-center align-items-center border-round" style="background: rgba(255, 255, 255, 0.05); padding: 9px; width: 50px; height: 50px; font-size: 30px">23</a>
            <span class="task-name block mt-3">Progress</span>
          </div>
          <div class="user-tasks-item font-medium">
            <a class="task-number flex justify-content-center align-items-center border-round" style="background: rgba(255, 255, 255, 0.05); padding: 9px; width: 50px; height: 50px; font-size: 30px">6</a>
            <span class="task-name block mt-3">Overdue</span>
          </div>
          <div class="user-tasks-item font-medium">
            <a class="task-number flex justify-content-center align-items-center border-round" style="background: rgba(255, 255, 255, 0.05); padding: 9px; width: 50px; height: 50px; font-size: 30px">38</a>
            <span class="task-name block mt-3">All deals</span>
          </div>
        </div>
      </div>
      <div>
        <Calendar v-model="date" inline style-class="w-full p-0" />
      </div>
      <div class="daily-plan-wrapper mt-5">
        <span class="today-date">14 Sunday, Jun 2020</span>
        <ul class="list-none overflow-hidden p-0 m-0">
          <li class="mt-3 border-round py-2 px-3" style="background: rgba(255, 255, 255, 0.05)">
            <span class="event-time block font-semibold text-color-secondary">1:00 PM - 2:00 PM</span>
            <span class="event-topic block mt-2">Meeting with Alfredo Rhiel Madsen</span>
          </li>
          <li class="mt-3 border-round py-2 px-3" style="background: rgba(255, 255, 255, 0.05)">
            <span class="event-time block font-semibold text-color-secondary">2:00 PM - 3:00 PM</span>
            <span class="event-topic block mt-2">Team Sync</span>
          </li>
          <li class="mt-3 border-round py-2 px-3" style="background: rgba(255, 255, 255, 0.05)">
            <span class="event-time block font-semibold text-color-secondary">5:00 PM - 6:00 PM</span>
            <span class="event-topic block mt-2">Team Sync</span>
          </li>
          <li class="mt-3 border-round py-2 px-3" style="background: rgba(255, 255, 255, 0.05)">
            <span class="event-time block font-semibold text-color-secondary">7:00 PM - 7:30 PM</span>
            <span class="event-topic block mt-2">Meeting with Engineering managers</span>
          </li>
        </ul>
      </div>
    </div>
  </Sidebar>
</template>
