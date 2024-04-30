package Cropout

import jetbrains.buildServer.configs.kotlin.Project
import jetbrains.buildServer.configs.kotlin.buildSteps.unrealEngine
import jetbrains.buildServer.configs.kotlin.toId
import jetbrains.buildServer.configs.kotlin.vcs.GitVcsRoot

object CropoutVcs : GitVcsRoot({
    id("CropoutVcs".toId())
    name = "Cropout"
    url = "https://github.com/lesley29/unreal-teamcity-demo.git"
    branch = "master"
})

object Project : Project({
    id("Cropout".toId())
    name = "Cropout"

    vcsRoot(CropoutVcs)

    buildType {
        id("Build".toId())
        name = "Build"

        vcs {
            root(CropoutVcs, "+:cropout")
        }

        val archiveDirectory = "./archived"
        steps {
            unrealEngine {
                name = "Build"
                engineDetectionMode = automatic {
                    identifier = "5.4"
                }
                command = buildCookRun {
                    project = "cropout/CropoutSampleProject.uproject"
                    buildConfiguration = standaloneGame {
                        configurations = "Development+Shipping"
                        platforms = "Mac"
                    }
                    cook = cookConfiguration {
                        maps = "Village+MainMenu"
                        cultures = "en"
                        unversionedContent = true
                    }
                    stage = stageConfiguration {
                        directory = "./staged"
                    }
                    packageProject = true
                    pak = true
                    compressed = true
                    prerequisites = true
                    archive = archiveConfiguration {
                        directory = archiveDirectory
                    }
                }
                additionalArguments = "-utf8output -buildmachine -unattended -noP4 -nosplash -stdout -NoCodeSign"
            }
            unrealEngine {
                name = "Run Tests"
                engineDetectionMode = automatic {
                    identifier = "5.4"
                }
                command = runAutomation {
                    project = "cropout/CropoutSampleProject.uproject"
                    execCommand = runTests {
                        tests = """
                        StartsWith:JsonConfig
                        Input.Triggers.Released
                    """.trimIndent()
                    }
                    nullRHI = true
                }
                additionalArguments = "-utf8output -buildmachine -unattended -noP4 -nosplash -stdout -NoCodeSign"
            }
        }

        artifactRules = "+:${archiveDirectory}=>Cropout.zip"
    }
})