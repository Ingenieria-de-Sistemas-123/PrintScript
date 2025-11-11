# PrintScript2
# 🖨️ PrintScript CLI

CLI oficial para **validar, ejecutar, formatear y analizar** programas escritos en el lenguaje **PrintScript**.  
Compatible con las versiones del lenguaje **1.0** y **1.1**.

---

## 🚀 Introducción

El CLI permite trabajar con archivos `.ps` o `.printscript`, ofreciendo comandos para:

- **Validar** la sintaxis del código (`validate`)
- **Ejecutar** un programa (`execute`)
- **Formatear** código fuente (`format`)
- **Analizar estáticamente** un programa con el *linter* (`analyze`)

Cada comando devuelve **códigos de salida estándar** (`exit code`), lo cual permite usarlo en pipelines de CI/CD.

---

## ⚙️ Instalación

### Opción 1: Compilar desde el proyecto
# PrintScript CLI

## 🚀 Compilación y ejecución

### Opción 1: Construir el JAR

```bash
./gradlew :cli:build
```

El jar resultante estará en:

```bash
cli/build/libs/printscript-cli.jar
```

### Opción 2: Ejecutar directamente desde Gradle

```bash
./gradlew :cli:run --args="validate -f examples/hello.ps"
```

## 📜 Comandos disponibles

### ✅ validate

Verifica si un archivo es sintácticamente válido.

**Ejemplo:**

```bash
./gradlew :cli:run --args="validate --file=samples/hello.ps --version=1.1"
```

**Salida esperada:**

```
OK: examples/hello.ps is valid.
```

**Código de salida:**

- `0` → archivo válido
- `1` → error léxico o sintáctico

### ▶️ execute

Ejecuta un programa PrintScript.

**Ejemplo:**

```bash
./gradlew :cli:run --args="execute -f samples/hello.ps --version=1.0" 
```

**Salida esperada:**

```
Hello World
```

**Código de salida:**

- `0` → ejecución exitosa
- `1` → error en el parseo o durante la ejecución

💡 Si el programa genera errores en tiempo de ejecución, estos se muestran por stderr.

### 🎨 format

Formatea el código según las reglas configuradas.

**Opciones:**

- `--check` → solo verifica si el archivo está bien formateado (no modifica nada)
- `--apply` → sobrescribe el archivo con el formato correcto
- `-c / --config` → ruta a un archivo de configuración JSON personalizado
- `--version` → selecciona versión del lenguaje (afecta el parser)

**Ejemplo:**

```bash
./gradlew :cli:run --args="format --file=samples/hello.ps --version=1.1"

./gradlew :cli:run --args="format --file=samples/hello.ps --version=1.1 --apply"   
```

**Código de salida:**

- `0` → formato correcto o aplicado
- `1` → archivo no está formateado (solo en modo `--check`)
- `2` → error de uso (`--check` y `--apply` simultáneos)

**Archivo de configuración (opcional):**

```json
{
  "spaceAfterColon": true,
  "spaceAroundEquals": true,
  "lineJumpAfterSemicolon": true
}
```

### 🔍 analyze

Ejecuta el linter sobre un archivo y reporta problemas de estilo o semántica.

**Ejemplo:**

```bash
./gradlew :cli:run --args="analyze --file=samples/hello.ps --version=1.1"
```

**Salida esperada:**

```
Issues found (2):
examples/invalid.ps:(1,1) - (1,1): error: [ERROR] Variable 'x' already defined
examples/invalid.ps:(3,1) - (3,1): warning: [WARNING] Identifier should be camelCase
```

**Código de salida:**

- `0` → sin problemas detectados
- `1` → hay issues de lint o error en el análisis

## 🧠 Versiones del lenguaje

PrintScript CLI soporta dos versiones del lenguaje:

| Versión | Descripción breve | Token provider usado |
|---------|-------------------|---------------------|
| 1.0 | versión base, sin `if` ni `readInput` | `PreConfiguredTokens.TOKENS_1_0` |
| 1.1 | agrega `if`, `readInput`, `readEnv` y nuevas reglas léxicas | `PreConfiguredTokens.TOKENS_1_1` |

El flag `--version` se propaga a `FrontendAdapter` para ajustar el lexer y parser, por ejemplo:

```bash
./gradlew :cli:run --args="validate --file=samples/hello.ps --version=1.1"
```

## 🧰 Códigos de salida (Exit Codes)

| Código | Significado | Aplica a comandos |
|--------|-------------|-------------------|
| 0 | Ejecución exitosa | Todos |
| 1 | Error de análisis, ejecución o lint | Todos |
| 2 | Error de uso del CLI (flags incompatibles) | `format` |

Esto permite integrar el CLI en pipelines de CI/CD, por ejemplo:

```yaml
# .github/workflows/ci.yml
- name: Check formatting
  run: java -jar cli/build/libs/printscript-cli.jar format -f src/main.ps --check
```

## 🧩 Estructura interna (breve explicación técnica)

| Clase | Rol |
|-------|-----|
| `FrontendAdapter` | Orquesta lexer y parser según versión; maneja errores léxicos y de parseo. |
| `FormatterAdapter` | Interfaz con el módulo formatter; lee configs JSON y aplica reglas. |
| `LinterAdapter` | Ejecuta reglas del módulo linter y devuelve Issues. |
| `InterpreterAdapter` | Conecta con el interpreter para ejecutar programas. |
| `CliFailure / LanguageError` | Modelos uniformes para reportar errores en consola. |
| `ErrorPrinter` | Imprime errores en stderr. |
| `Root` | Comando raíz que registra los subcomandos de picocli. |
