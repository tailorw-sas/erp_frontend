<script lang="ts" setup>
import { cards } from '~/components/cards/cards-data'

useHead({
  link: [
    {
      rel: 'stylesheet',
      href: 'https://fonts.googleapis.com/css2?family=Sansita+One&display=swap',
    },
  ],
})

const objTitle = ref({
  title: 'The best ally to',
  title2: 'transform your data into strategies!',
  slogan: 'We drive your growth quickly and safely!',
})
</script>

<template>
  <div style="height: 85vh;" class="flex flex-column align-items-center justify-content-center">
    <!-- TITLE -->
    <div class="text-center font-text container-home-title">
      <div class="title-home text-900 mb-1 font-bold">
        {{ objTitle.title }}
      </div>
      <div class="font-bold container-title">
        <div class="blue-line bar-top" />
        <div class="blue-line bar-center" />
        <div class="blue-line bar-bottom" />
        <div class="custom-title w-full">
          {{ objTitle.title2 }}
        </div>
      </div>

      <div class="title-home text-900 mb-1 font-bold">
        {{ objTitle.slogan }}
      </div>
    </div>

    <!-- CARDS -->
    <div class="grid grid-nogutter">
      <div v-for="card in cards" :key="card.title" class="col-12 p-col-2 sm:col-4 flex align-items-start justify-content-center" style="margin-bottom: 30px;">
        <span>
          <HomeCard :card="card" />
        </span>
      </div>
    </div>
  </div>
</template>

  <style lang="scss">
  .font-text {
    font-family: 'Sansita One', sans-serif;
  }
  .p-col-2 {
    flex: 0 0 calc(20% - 10px); /* Adjust width to 20% and account for gaps */
    max-width: calc(20% - 10px); /* Ensure max width doesn't exceed 20% */
    box-sizing: border-box;
  }

  .blue-line {
    height: 20px;
    border-radius: 50px;
    width: 90%;
    background-color: #0F8BFD;
  }

  .container-title {
      position: relative;
      text-align: center;
      color: #000;
      font-weight: bold;
      margin-bottom: 40px;
      font-size: 1.8rem;
    }

    .bar-top{
      position: relative;
      margin-left: 5%;
    }
    .bar-center{
      position: relative;
      margin-left: 10%;
    }
    .bar-bottom{
      position: relative;
      margin-left: 0;
    }
    .custom-title {
      position: absolute;
      top: 50%;
      left: 50%;
      transform: translate(-50%, -50%);
      color: white;
      padding: 0 10px;
      z-index: 10;

    }

    .container-home-title {
      margin-bottom: 80px;
      width: 58%;
    }

    /* Media Queries */
@media (max-width: 640px) { /* sm */
  .container-home-title {
    width: 90%;
  }

  .bar-center {
    display: none;
  }

  .bar-top {
    justify-self: center;
    width: 100%;
  }

  .bar-bottom {
    margin-left: 0%;
    width: 100%;
  }

  .title-home {
    font-size: 1.75rem;
    text-align: center;
  }

  .text-5xl {
    font-size: 1.75rem; /* Ajusta el tamaño del texto */
  }

  .container-title {
    font-size: 1.55rem; /* Ajusta el tamaño del texto */
  }
}

@media (min-width: 641px) and (max-width: 768px) { /* md */
  container-home-title {
    width: 100%;
  }

  .title-home {
    font-size: 1.75rem; /* Ajusta el tamaño del texto */
  }

  .text-5xl {
    font-size: 1.75rem; /* Ajusta el tamaño del texto */
  }

  .container-title {
    font-size: 1.75rem; /* Ajusta el tamaño del texto */
  }
}

@media (min-width: 769px) and (max-width: 1024px) { /* lg */
  container-home-title {
    width: 100%;
  }

  .bar-center {
    width: 100%;
    margin-left: 0%;
  }
  .bar-bottom {
    margin-left: 5%;
    width: 90%;
  }

  .title-home {
    font-size: 1.7rem; /* Ajusta el tamaño del texto */
  }

  .text-5xl {
    font-size: 1.7rem; /* Ajusta el tamaño del texto */
  }

  .container-title {
    font-size: 1.7rem; /* Ajusta el tamaño del texto */
  }
}

@media (min-width: 1025px) and (max-width: 1280px) { /* xl */
  .title-home {
    font-size: 1.7rem; /* Ajusta el tamaño del texto */
  }

  .text-5xl {
    font-size: 1.7rem; /* Ajusta el tamaño del texto */
  }

  .container-title {
    font-size: 1.7rem; /* Ajusta el tamaño del texto */
  }
}

@media (min-width: 1281px) { /* xxl */
  .title-home {
    font-size: 2rem; /* Ajusta el tamaño del texto */
  }

  .text-5xl {
    font-size: 2.5rem; /* Ajusta el tamaño del texto */
  }

  .container-title {
    font-size: 2rem; /* Ajusta el tamaño del texto */
  }
}
</style>
