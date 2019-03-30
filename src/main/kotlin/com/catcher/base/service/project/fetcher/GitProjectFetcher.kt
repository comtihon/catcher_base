package com.catcher.base.service.project.fetcher

import org.eclipse.jgit.api.Git
import org.springframework.stereotype.Component
import java.io.File

@Component
class GitProjectFetcher : ProjectFetcher {
    override fun isRemoteSupported(remote: String): Boolean {
        return remote.startsWith("git")
    }

    override fun fetchRemote(remote: String, local: String) {
        // TODO Should I catch all errors here and continue?
        Git.cloneRepository()
                .setRemote(remote)
                .setDirectory(File(local))
                .call()
                .close()
    }

    override fun updateRemote(remote: String, local: String) {
        Git.open(File(local))
                .pull()
                .call() // TODO result?
    }
}