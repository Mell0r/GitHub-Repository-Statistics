package ru.kotlin.senin

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import java.util.*

const val PAGE_SIZE = 100

interface GitHubService {
    @GET("repos/{owner}/{repo}/commits")
    suspend fun getCommits(
        @Path("owner") owner: String,
        @Path("repo") repository: String,
        @Query("since") since: String? = null,
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = PAGE_SIZE
    ): Response<List<Commit>>

    @GET("repos/{owner}/{repo}/commits/{ref}")
    suspend fun getChanges(
        @Path("owner") owner: String,
        @Path("repo") repository: String,
        @Path("ref") commitRef: String
    ): Response<CommitWithChanges>
}

@JsonIgnoreProperties(ignoreUnknown = true)
data class Commit(
    val sha: String,
    val commit: CommitDetails,
    val author: UserInfo?  // may be null if user has been deleted
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class CommitDetails(
    val author: AuthorInfo,
    val message: String
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class AuthorInfo(
    val name: String,
    val date: String
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class CommitWithChanges(
    val sha: String,
    val author: UserInfo?, // may be null if user has been deleted
    val files: List<Change>
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class Change(
    val sha: String,
    val filename: String,
    val additions: Int,
    val deletions: Int,
    val changes: Int
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class UserInfo(
    val login: String,
    val id: Long,
    val type: String
)

data class RequestData(
    val username: String,
    val password: String,
    val owner: String,
    val repository: String
)

fun createGitHubService(username: String, password: String): GitHubService {
    val authToken = "Basic " + Base64.getEncoder().encode("$username:$password".toByteArray()).toString(Charsets.UTF_8)
    val httpClient = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val original = chain.request()
            val builder = original.newBuilder()
                .header("Accept", "application/vnd.github.v3+json")
                .header("Authorization", authToken)
            val request = builder.build()
            chain.proceed(request)
        }
        .build()

    val retrofit = Retrofit.Builder()
        .baseUrl("https://api.github.com")
        .addConverterFactory(JacksonConverterFactory.create(jacksonObjectMapper()))
        .client(httpClient)
        .build()
    return retrofit.create(GitHubService::class.java)
}
