plugins {
    kotlin("jvm") version "1.9.10" apply false
    id("org.polyfrost.multi-version.root")
    id("com.github.johnrengelman.shadow") version "7.1.2" apply false
//    id("io.github.juuxel.loom-quiltflower-mini") version "171a6e2e49" apply false
}

preprocess {
    "1.8.9-forge"(10809, "srg") {}
}