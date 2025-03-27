/// <reference types="vite/client" />

interface ImportMetaEnv {
  readonly VITE_API_BASE_URL: string;
  readonly VITE_SHOPIFY_APP_URL: string;
  readonly VITE_APP_HOST: string;
}

interface ImportMeta {
  readonly env: ImportMetaEnv;
}