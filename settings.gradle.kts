rootProject.name = "fulfillspigot"

includeBuild("build-logic")

this.setupSubproject("fulfillspigot-server", "FulfillSpigot-Server")
this.setupSubproject("fulfillspigot-api", "FulfillSpigot-API")

fun setupSubproject(name: String, dir: String) {
    include(":$name")
    project(":$name").projectDir = file(dir)
}
