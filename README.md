# Troubleshooter Editor Back-end

[![Codacy Badge](https://api.codacy.com/project/badge/Grade/34e6614e8ec245c6ab2afb57123520ce)](https://app.codacy.com/gh/Attacktive/troubleshooter-editor-back-end?utm_source=github.com&utm_medium=referral&utm_content=Attacktive/troubleshooter-editor-back-end&utm_campaign=Badge_Grade_Settings)
[![CodeFactor](https://www.codefactor.io/repository/github/attacktive/troubleshooter-editor-back-end/badge)](https://www.codefactor.io/repository/github/attacktive/troubleshooter-editor-back-end)
[![CodeQL](https://github.com/Attacktive/troubleshooter-editor-back-end/actions/workflows/codeql.yaml/badge.svg)](https://github.com/Attacktive/troubleshooter-editor-back-end/actions/workflows/codeql.yaml)
[![Java CI with Gradle](https://github.com/Attacktive/troubleshooter-editor-back-end/actions/workflows/gradle.yaml/badge.svg)](https://github.com/Attacktive/troubleshooter-editor-back-end/actions/workflows/gradle.yaml)
[![Push to Docker Image Registries](https://github.com/Attacktive/troubleshooter-editor-back-end/actions/workflows/push-image.yaml/badge.svg)](https://github.com/Attacktive/troubleshooter-editor-back-end/actions/workflows/push-image.yaml)

It's the back-end of a save file editor of the game Troubleshooter: Abandoned Children.

## ▶️How to Run

### prerequisites

- [Git](https://git-scm.com/downloads)
- [OpenJDK 21](https://jdk.java.net/archive/): Choose the latest `21.x` version
- Firewall settings might be required: whitelist inbound `TCP` traffics via port `8080`

### execution

- Windows Powershell:

```shell

git clone https://github.com/Attacktive/troubleshooter-editor-back-end.git
cd troubleshooter-editor-back-end
.\gradlew bootRun
```

- Windows CMD

```shell

git clone https://github.com/Attacktive/troubleshooter-editor-back-end.git
cd troubleshooter-editor-back-end
gradlew bootRun
```

- Unix-like:

```shell

git clone https://github.com/Attacktive/troubleshooter-editor-back-end.git
cd troubleshooter-editor-back-end
./gradlew bootRun
```

## 🔧 How to Use the Editor

It's just the back-end part of the editor which still requires [the front-end](https://github.com/Attacktive/troubleshooter-editor-front-end-svelte).
So,
1. run this Spring application, then
2. either just use the front-end application at [the GitHub Pages](https://attacktive.github.io/troubleshooter-editor-front-end-svelte), or run it yourself

## 🗒️ Note

The [Vue.js](https://github.com/Attacktive/troubleshooter-editor-front-end-vue) variant and [React](https://github.com/Attacktive/troubleshooter-editor-front-end) version is currently discontinued in favor of the Svelte version.
