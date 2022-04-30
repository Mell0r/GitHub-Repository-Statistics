package ru.kotlin.senin

import kotlinx.coroutines.delay
import retrofit2.Response

object MockGithubService : GitHubService {

    override suspend fun getCommits(
        owner: String,
        repository: String,
        since: String?,
        page: Int,
        perPage: Int
    ): Response<List<Commit>> {
        delay(commitsRequestDelay)
        return Response.success(commits)
    }

    override suspend fun getChanges(
        owner: String,
        repository: String,
        commitRef: String
    ): Response<CommitWithChanges> {
        val testCommit = testCommitsWithChanges.first { it.commit.sha == commitRef }
        delay(testCommit.timeFromStart)
        return Response.success(testCommit.commit)
    }
}