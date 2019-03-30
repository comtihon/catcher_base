package com.catcher.base.service.project.fetcher

import org.springframework.stereotype.Component

@Component
class HttpProjectFetcher : ProjectFetcher {
    override fun isRemoteSupported(remote: String): Boolean {
        return remote.startsWith("http")
    }

    override fun fetchRemote(remote: String, local: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun updateRemote(remote: String, local: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
