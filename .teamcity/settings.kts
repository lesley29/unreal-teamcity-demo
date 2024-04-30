import jetbrains.buildServer.configs.kotlin.project
import jetbrains.buildServer.configs.kotlin.version

version = "2024.03"

project {
    subProject(Cropout.Project)
    subProject(Lyra.Project)
}

