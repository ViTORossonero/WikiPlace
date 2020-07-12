package com.qulix.wikiplace.data.dto

import com.squareup.moshi.Json

data class NearbyPlacesResponse(val query: Query) {
    data class Query(val geosearch: List<Place>) {
        data class Place(@field:Json(name = "pageid") val pageId: Long,
                         val title: String)
    }
}

val NearbyPlacesResponse.nearbyPlaces: List<NearbyPlacesResponse.Query.Place>
    get() = query.geosearch

data class ImagesResponse(val query: Query,
                          @field:Json(name = "query-continue") val queryContinue: QueryContinue?) {
    data class Query(val pages: Map<String, PageInfo>) {
        data class PageInfo(@field:Json(name = "pageid") val pageId: Int,
                            val title: String,
                            val images: List<Image>?) {
            data class Image(val title: String)
        }
    }

    data class QueryContinue(val images: Images) {
        data class Images(val imcontinue: String)
    }
}

val ImagesResponse.nextPageRef: String?
    get() = queryContinue?.images?.imcontinue

val ImagesResponse.imagesCount: Int
    get() = query.pages.values.sumBy { it.images?.size ?: 0 }