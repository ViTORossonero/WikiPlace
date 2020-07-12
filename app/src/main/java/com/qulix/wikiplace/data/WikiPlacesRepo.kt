package com.qulix.wikiplace.data

import android.location.Location
import com.qulix.wikiplace.domain.entity.WikiPlace
import com.qulix.wikiplace.data.dto.ImagesResponse
import com.qulix.wikiplace.data.dto.NearbyPlacesResponse
import com.qulix.wikiplace.data.dto.imagesCount
import com.qulix.wikiplace.data.dto.nearbyPlaces
import com.qulix.wikiplace.data.dto.nextPageRef
import com.qulix.wikiplace.extensions.create
import kotlinx.coroutines.Deferred
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Query
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WikiPlacesRepo @Inject constructor(
    retrofit: Retrofit
) {

    private val api = retrofit.create<Api>()

    // TODO: Use some caching of results for same/close Locations.
    suspend fun getNearbyPlaces(location: Location, radius: Int = 10000, limit: Int = 50): List<WikiPlace> {
        val nearbyPlacesResponse = api.getNearbyPlacesAsync(location.toCoords(), radius, limit).await()

        return nearbyPlacesResponse.nearbyPlaces.map { it.toWikiPlace() }
    }

    private suspend fun NearbyPlacesResponse.Query.Place.toWikiPlace(): WikiPlace = WikiPlace(pageId,
                                                                                              title,
                                                                                              getImagesCountFor(pageId))

    private suspend fun getImagesCountFor(pageId: Long): Int {

        var nextPageRef: String? = null
        var counter = 0

        do {
            val images = api.getImagesAsync(pageId, nextPageRef).await()

            counter += images.imagesCount
            nextPageRef = images.nextPageRef

        } while (nextPageRef != null)

        return counter
    }

    interface Api {

        @GET("w/api.php?action=query&format=json&list=geosearch")
        fun getNearbyPlacesAsync(@Query("gscoord") coords: String,
                                 @Query("gsradius") radius: Int,
                                 @Query("gslimit") limit: Int): Deferred<NearbyPlacesResponse> // TODO: use Response<> instead

        @GET("w/api.php?action=query&format=json&prop=images&rawcontinue=1")
        fun getImagesAsync(@Query("pageids") pageId: Long,
                           @Query("imcontinue") nextPageRef: String?): Deferred<ImagesResponse> // TODO: use Response<> instead

    }

    companion object {
        private fun Location.toCoords(): String = "$latitude|$longitude"
    }
}