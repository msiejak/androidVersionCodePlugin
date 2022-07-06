package versionCodePlugin

import com.android.build.gradle.internal.dsl.BaseAppModuleExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import java.net.URI
import java.net.URLEncoder
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

class VersionCodePlugin : Plugin<Project> {

    private val client = HttpClient.newHttpClient()

    fun setRemoteVersionCode(packageName: String, versionCode: Int, host: String) {
        val pName = URLEncoder.encode(packageName, Charsets.UTF_8.name())
        val request = HttpRequest.newBuilder(
            URI.create("$host/set?package=$pName&versionCode=$versionCode")
        )
            .header("accept", "text/plain")
            .build()
        val response = client.send(request, HttpResponse.BodyHandlers.ofString())
        println(response)
    }

    private fun getVersionCodeFromRemote(packageName: String, host: String): Int {
        val pName = URLEncoder.encode(packageName, Charsets.UTF_8.name())
        val request = HttpRequest.newBuilder(
            URI.create("$host/get?package=$pName")
        )
            .header("accept", "text/plain")
            .build()
        val response = client.send(request, HttpResponse.BodyHandlers.ofString())
        val formattedVersion = response.body().trim()
        return (formattedVersion.toInt())
    }

    override fun apply(target: Project) {
        with(target) {
            extensions.configure<BaseAppModuleExtension> {
                val host = property("versionCodePlugin.host") as String
                println(host)
                val packageName = property("versionCodePlugin.packageName") as String
                println(packageName)
                var versionCode = getVersionCodeFromRemote(packageName, host)
                project.gradle.taskGraph.whenReady {
                    if (project.gradle.taskGraph.hasTask(":app:minifyReleaseWithR8") || project.gradle.taskGraph.hasTask(":app:minifyDogfoodWithR8")) {
                        setRemoteVersionCode(packageName, versionCode + 2, host)
                        versionCode = getVersionCodeFromRemote(packageName, host)
                    }
                }
                defaultConfig.versionCode = versionCode
            }
        }
    }
}
