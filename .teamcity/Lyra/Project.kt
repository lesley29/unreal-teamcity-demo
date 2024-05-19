package Lyra

import jetbrains.buildServer.configs.kotlin.Project
import jetbrains.buildServer.configs.kotlin.buildSteps.UnrealEngine
import jetbrains.buildServer.configs.kotlin.buildSteps.unrealEngine
import jetbrains.buildServer.configs.kotlin.toId
import jetbrains.buildServer.configs.kotlin.vcs.PerforceVcsRoot

object LyraGameVcs : PerforceVcsRoot({
    id("LyraVcs".toId())
    name = "LyraGame"
    port = "ssl:127.0.0.1:1666" // specify the correct address of your P4 server
    mode = stream {
        streamName = "//Lyra/game" // specify the appropriate stream
    }
    // specify your credentials
    userName = "teamcity"
    password = "teamcity"
})

object Project : Project({
    id("Lyra".toId())
    name = "Lyra"

    vcsRoot(LyraGameVcs)

    subProject(BuildGraph)
})

object BuildGraph : Project({
    id("BuildGraph".toId())
    name = "BuildGraph"

    buildType {
        id("CI".toId())
        name = "CI"

        params {
            param("env.UE_SharedDataCachePath", "/mnt/agent-shared-dir/ddc")
            param("env.UE-SharedDataCachePath", "\\\\fs-040b8d6dab476baf1.fsx.eu-west-1.amazonaws.com\\fsx\\ddc")
        }

        vcs {
            root(LyraGameVcs)
        }

        steps {
            unrealEngine {
                name = "Build"
                id = "Unreal_Engine"
                engineDetectionMode = manual {
                    rootDir = "engine"
                }
                command = buildGraph {
                    script = "game/BuildProject.xml"
                    targetNode = "BuildProject"
                    options = """
                        ProjectPath=%teamcity.build.checkoutDir%/game
                        ProjectName=Lyra
                        ClientPlatforms=Linux+Win64
                        ServerPlatforms=Linux
                        EditorPlatforms=Linux+Win64
                        TargetConfigurations=Shipping
                    """.trimIndent()
                    mode = UnrealEngine.BuildGraphMode.Distributed
                }
            }
        }
    }
})