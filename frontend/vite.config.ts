import { fileURLToPath, URL } from 'node:url'
import { defineConfig, loadEnv } from 'vite'
import vue from '@vitejs/plugin-vue'
import Components from 'unplugin-vue-components/vite'
import { ElementPlusResolver } from 'unplugin-vue-components/resolvers'

export default defineConfig(({ mode }) => {
  const env = loadEnv(mode, process.cwd(), '')
  const apiProxyTarget = env.VITE_API_PROXY_TARGET || 'http://localhost:8080'

  return {
    plugins: [
      vue(),
      Components({
        dts: false,
        resolvers: [
          ElementPlusResolver({
            importStyle: 'css'
          })
        ]
      })
    ],
    resolve: {
      alias: {
        '@': fileURLToPath(new URL('./src', import.meta.url))
      }
    },
    css: {
      preprocessorOptions: {
        scss: {
          additionalData: `@use "@/assets/styles/variables.scss" as *;`
        }
      }
    },
    server: {
      host: '0.0.0.0',
      port: 5173,
      open: false,
      proxy: {
        '/api': {
          target: apiProxyTarget,
          changeOrigin: true
        }
      }
    },
    build: {
      chunkSizeWarningLimit: 900,
      rollupOptions: {
        output: {
          manualChunks: {
            vue: ['vue', 'vue-router', 'pinia'],
            element: ['element-plus'],
            vendor: ['axios', 'dayjs'],
            echarts: ['echarts']
          }
        }
      }
    }
  }
})
