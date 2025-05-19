export default {
  content: ['./index.html', './src/**/*.{js,ts,jsx,tsx}'],
// tailwind.config.js
  theme: {
    extend: {
      keyframes: {
        'fade-in-up': {
          '0%': { opacity: '0', transform: 'translateY(40px)' },
          '100%': { opacity: '1', transform: 'translateY(0)' },
        },
      },
      animation: {
        'bounce-in': 'bounce-in 0.4s ease-out'
      },
      animation: {
        'fade-in': 'fadeIn 0.5s ease-out forwards',
      },
    },
  },

}
