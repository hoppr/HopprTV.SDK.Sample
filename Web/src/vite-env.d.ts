/// <reference types="vite/client" />

interface ImportMetaEnv {
  readonly VITE_HOPPR_SDK_VERSION: string
}

interface ImportMeta {
  readonly env: ImportMetaEnv
}
