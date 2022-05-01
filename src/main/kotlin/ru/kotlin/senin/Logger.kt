package ru.kotlin.senin

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import retrofit2.Response

val serviceLog: Logger = LoggerFactory.getLogger("GitHubService")

fun logCommits(req: RequestData, response: Response<List<Commit>>) {
    val commits = response.body()
    if (!response.isSuccessful || commits == null) {
        serviceLog.error("Failed loading commits for owner ${req.owner} and repository " +
                "${req.repository} with response: '${response.code()}: ${response.message()}'")
    }
    else {
        serviceLog.info("owner ${req.owner}, repository ${req.repository}: " +
                "loaded ${commits.size} commits")
    }
}

fun logChanges(commit: Commit, response: Response<CommitWithChanges>) {
    val changes = response.body()
    if (!response.isSuccessful || changes == null) {
        serviceLog.error("Failed loading changes for commit ${commit.sha} " +
                "with response '${response.code()}: ${response.message()}'")
    }
    else {
        serviceLog.info("${commit.sha}: loaded $changes")
    }
}