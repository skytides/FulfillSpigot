plugins {
    id("fulfillspigot.conventions")
    `maven-publish`
}

java {
    withSourcesJar()
    withJavadocJar()
}

dependencies {
    api("commons-lang:commons-lang:2.6")
    api("org.avaje:ebean:2.8.1")
    api("com.googlecode.json-simple:json-simple:1.1.1")
    api("org.yaml:snakeyaml:1.15")
    api("net.md-5:bungeecord-chat:1.8-SNAPSHOT")
    compileOnlyApi("net.sf.trove4j:trove4j:3.0.3") // provided by server

    // bundled with Minecraft, should be kept in sync
    api("com.google.guava:guava:17.0")
    api("com.google.code.gson:gson:2.2.4")
    api("org.slf4j:slf4j-api:1.7.35") // PandaSpigot - Add SLF4J Logger

    // testing
    testImplementation("junit:junit:4.12")
    testImplementation("org.hamcrest:hamcrest-library:1.3")
    testImplementation("net.sf.trove4j:trove4j:3.0.3") // required by tests
}

tasks {
    val generateApiVersioningFile by registering {
        inputs.property("version", project.version)
        val pomProps = layout.buildDirectory.file("pom.properties")
        outputs.file(pomProps)
        doLast {
            pomProps.get().asFile.writeText("version=${project.version}")
        }
    }

    jar {
        from(generateApiVersioningFile.map { it.outputs.files.singleFile }) {
            into("META-INF/maven/${project.group}/${project.name.toLowerCase()}")
        }

        manifest {
            attributes(
                "Automatic-Module-Name" to "org.bukkit"
            )
        }
    }

    withType<Javadoc> {
        (options as StandardJavadocDocletOptions).let {
            // hide warnings
            it.addBooleanOption("Xdoclint:none", true)
            it.addStringOption("Xmaxwarns", "1")

            it.links(
                "https://guava.dev/releases/17.0/api/docs/",
                "https://javadoc.io/doc/org.yaml/snakeyaml/1.15/",
                "https://javadoc.io/doc/net.md-5/bungeecord-chat/1.16-R0.4/",
            )
        }
    }
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
            versionMapping {
                usage("java-api") {
                    fromResolutionOf("runtimeClasspath")
                }
            }
        }
    }
}
