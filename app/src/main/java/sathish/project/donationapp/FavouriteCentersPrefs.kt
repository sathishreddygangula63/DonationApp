package sathish.project.donationapp

import android.content.Context

object FavouriteCentersPrefs {

    private const val PREF_NAME = "favourite_centers"
    private const val KEY_IDS = "fav_ids"

    fun getFavourites(context: Context): Set<String> {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return prefs.getStringSet(KEY_IDS, emptySet()) ?: emptySet()
    }

    fun toggleFavourite(context: Context, centerId: String) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val current = getFavourites(context).toMutableSet()

        if (current.contains(centerId)) current.remove(centerId)
        else current.add(centerId)

        prefs.edit().putStringSet(KEY_IDS, current).apply()
    }

    fun isFavourite(context: Context, centerId: String): Boolean {
        return getFavourites(context).contains(centerId)
    }
}
