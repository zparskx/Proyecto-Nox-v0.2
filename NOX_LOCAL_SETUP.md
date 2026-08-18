# Nox V0.2 - cerebro local

Esta versión elimina la dependencia de Gemini/API para las conversaciones y usa un
modelo LLM ejecutado en el propio Android mediante MediaPipe LLM Inference.

## Modelo

La aplicación espera:

`files/models/nox-model.task`

No se incluye el modelo dentro del APK porque estos archivos son demasiado grandes
para empaquetarlos normalmente.

Google documenta actualmente el uso de Gemma 3 1B cuantizado a 4 bits con la API
MediaPipe LLM Inference. La documentación también indica que esta API está en modo
de mantenimiento y recomienda migrar a LiteRT-LM para proyectos nuevos.

## Instalar el modelo durante desarrollo

1. Obtén un modelo compatible con MediaPipe.
2. En una futura pantalla de administración, usa `ModelManager.copyModelFromUri()`
   para copiar el `.task` al almacenamiento privado de la aplicación.
3. Para pruebas con ADB también puede copiarse al almacenamiento interno de la app,
   siempre respetando el sandbox del paquete.

## Arquitectura

UI -> MainViewModel -> AiStudioRepository -> LocalNoxEngine -> modelo local

Room conserva:
- personas
- personalidad/instrucciones
- base de conocimiento
- historial
- benchmarks

La personalidad de Nox no está "entrenada" en el modelo. Se construye como contexto
de sistema y puede modificarse sin volver a entrenar el modelo.

## Nota de hardware

La documentación oficial de MediaPipe señala que su LLM Inference está optimizada
para teléfonos Android de gama alta. En un Samsung A13 puede funcionar lentamente o
no ser fiable dependiendo del modelo y backend. Por eso esta versión usa un modelo
pequeño y deja el motor aislado para poder cambiarlo posteriormente por LiteRT-LM.
