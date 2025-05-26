#!/usr/bin/env kotlin

@file:DependsOn("org.kohsuke:github-api:2.0-rc.3")
@file:DependsOn("com.squareup.okhttp3:okhttp:4.12.0")

import okhttp3.OkHttpClient
import okhttp3.Request
import okio.FileSystem
import okio.Path.Companion.toPath
import okio.use
import org.kohsuke.github.GitHub
import org.kohsuke.github.GitHubBuilder
import org.kohsuke.github.extras.okhttp3.OkHttpGitHubConnector

val httpClient = OkHttpClient()

val github: GitHub = GitHubBuilder()
    .withConnector(OkHttpGitHubConnector(httpClient))
    .build()

val repositories = FileSystem.SYSTEM.read("repositories.txt".toPath()) {
    readUtf8().lines().filter { it.isNotBlank() }
}

for (repoName in repositories) {
    github
        .getRepository(repoName)
        .latestRelease
        .assets
        .forEach { asset ->
            if (asset.name.endsWith(".deb")) {
                val response = httpClient
                    .newCall(
                        Request
                            .Builder()
                            .url(asset.browserDownloadUrl)
                            .build()
                    )
                    .execute()
                if (response.isSuccessful) {
                    println("Downloading ${asset.name} from ${asset.browserDownloadUrl}")
                    httpClient
                        .newCall(
                            Request
                                .Builder()
                                .url(asset.browserDownloadUrl)
                                .build()
                        )
                        .execute()
                        .body
                        ?.source()
                        ?.use { source ->
                            FileSystem.SYSTEM.write(asset.name.toPath()) {
                                writeAll(source)
                            }
                        }
                    println("Downloaded ${asset.name} successfully.")
                } else {
                    println("Failed to download ${asset.name}: ${response.message}")
                }
            }
        }
}