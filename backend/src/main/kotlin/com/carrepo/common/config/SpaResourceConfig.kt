package com.carrepo.common.config

import org.springframework.context.annotation.Configuration
import org.springframework.core.io.Resource
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import org.springframework.web.servlet.resource.PathResourceResolver

/**
 * SPA fallback.
 *
 * `/me` 처럼 브라우저가 직접 들어오는 주소는 서버에 파일이 없다. 실제 파일이 있으면 그것을,
 * 없으면 `index.html` 을 돌려줘서 라우팅을 프런트가 이어받게 한다.
 * `/api/` 로 시작하는 경로는 여기서 제외한다 — 없는 API 는 404 여야 한다.
 */
@Configuration
class SpaResourceConfig : WebMvcConfigurer {

    override fun addResourceHandlers(registry: ResourceHandlerRegistry) {
        registry.addResourceHandler("/**")
            .addResourceLocations("classpath:/static/")
            .resourceChain(true)
            .addResolver(object : PathResourceResolver() {
                override fun getResource(resourcePath: String, location: Resource): Resource? {
                    if (resourcePath.startsWith("api/")) return null

                    val requested = location.createRelative(resourcePath)
                    if (requested.exists() && requested.isReadable) return requested

                    return location.createRelative("index.html").takeIf { it.exists() }
                }
            })
    }
}
