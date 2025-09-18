import com.android.build.api.dsl.Packaging
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlinSerialization)
}

fun getLocalProperty(key: String, projectDir: File): String {
    val properties = Properties()
    val localPropertiesFile = File(projectDir.parent, "local.properties")
    if (localPropertiesFile.exists()) {
        properties.load(localPropertiesFile.inputStream())
    }
    return properties.getProperty(key, "")
}

android {
    namespace = "com.example.deki_automata"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.deki_automata"
        minSdk = 28
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
        val defaultBaseUrl = "http://10.0.2.2:8000/"
        val configuredBaseUrl = getLocalProperty("BASE_URL", rootDir).ifBlank { defaultBaseUrl }
        val normalizedBaseUrl = if (configuredBaseUrl.endsWith("/")) configuredBaseUrl else "$configuredBaseUrl/"
        buildConfigField("String", "BASE_URL", "\"$normalizedBaseUrl\"")

        val defaultApiToken = "local_dev_token"
        val configuredToken = getLocalProperty("API_TOKEN", rootDir).ifBlank { defaultApiToken }
        buildConfigField("String", "API_TOKEN", "\"$configuredToken\"")

        val configuredGemmaUrl = getLocalProperty("GEMMA_MODEL_URL", rootDir)
        buildConfigField("String", "GEMMA_MODEL_URL", "\"${configuredGemmaUrl.replace("\"", "\\\"")}\"")

        val configuredGemmaAuth = getLocalProperty("GEMMA_MODEL_AUTHORIZATION", rootDir)
        buildConfigField(
            "String",
            "GEMMA_MODEL_AUTHORIZATION",
            "\"${configuredGemmaAuth.replace("\"", "\\\"")}\""
        )
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            // TODO update
        }
        debug {
            // TODO update
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.3"
    }
    packaging {

        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    packagingOptions {
        jniLibs.pickFirsts.add("lib/**/libtensorflowlite_jni.so")
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.compose.material.icons.extended)

    // Networking
    implementation(libs.retrofit)
    implementation(libs.retrofit.kotlinx.serialization.converter)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging.interceptor)

    // Local ML
    implementation(libs.media.pipe.tasks.genai)
    implementation(libs.ml.kit.text.recognition)
    implementation(libs.media.pipe.tasks.vision)
    implementation(libs.tensor.flow.lite.task.vision)
    implementation(libs.tensor.flow.lite.support)

    constraints {
        implementation("org.tensorflow:tensorflow-lite:2.13.0") {
            because("Align all TFLite dependencies to a single, compatible version")
        }
        implementation("org.tensorflow:tensorflow-lite-api:2.13.0") {
            because("Align all TFLite dependencies to a single, compatible version")
        }
        implementation("org.tensorflow:tensorflow-lite-gpu:2.13.0") {
            because("Align all TFLite dependencies to a single, compatible version")
        }
    }

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}