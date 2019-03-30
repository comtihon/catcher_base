package com.catcher.base.service.project.fetcher

interface ProjectFetcher {

    fun isRemoteSupported(remote: String): Boolean

    fun fetchRemote(remote: String, local: String)

    fun updateRemote(remote: String, local: String)
}