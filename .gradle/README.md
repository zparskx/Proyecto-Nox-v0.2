# Nox Stage 2 - Gradle bootstrap files

Sube estos archivos a la raíz de `zparskx/Proyecto-Nox-v0.2`:

- `gradlew`
- `gradlew.bat`
- `gradle/wrapper/gradle-wrapper.properties`

Este paquete usa un bootstrap de Gradle para evitar depender de un `gradle-wrapper.jar` que no podemos adjuntar desde este entorno. El script descarga Gradle 9.3.1 desde los servidores oficiales de Gradle y verifica el SHA-256 de la distribución en Linux.

Después de subirlos, conserva el workflow `Android.yml` que usa:

    chmod +x gradlew
    ./gradlew assembleDebug

La compilación de GitHub Actions debería entonces poder avanzar hasta la primera comprobación real del proyecto Android.
