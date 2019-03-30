package com.catcher.base.service.project.fetcher

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class FetcherFactory(@Autowired val projectFetcherList: List<ProjectFetcher>) {

    fun getProjectFetcher(remote: String): ProjectFetcher? {
        return projectFetcherList.firstOrNull { it.isRemoteSupported(remote) }
    }
}