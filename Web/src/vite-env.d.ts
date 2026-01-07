/// <reference types="vite/client" />

interface ImportMetaEnv {
  readonly HOPPR_SDK_VERSION: string
}

interface ImportMeta {
  readonly env: ImportMetaEnv
}
