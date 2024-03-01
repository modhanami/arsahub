import { defineConfig } from "orval";

export default defineConfig({
  "arsahub-file": {
    input: "./api-docs.yaml",
    output: {
      target: "./lib/generated/arsahub.ts",
      client: "react-query",
      mode: "split",
      mock: true,
    },
  },
});
