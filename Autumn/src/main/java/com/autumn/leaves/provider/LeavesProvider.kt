package com.autumn.leaves.provider

import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri
import com.autumn.leaves.WindHelper

/**
 * Date：2024/7/5
 * Describe:
 */
class LeavesProvider : ContentProvider() {

    private val myLeaves = arrayOf(
        "accountName",
        "accountType",
        "displayName",
        "typeResourceId",
        "exportSupport",
        "shortcutSupport",
        "photoSupport"
    )


    override fun onCreate(): Boolean {
        return true
    }

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor? {
        return WindHelper.queryInfo(uri, context?.packageName ?: "", myLeaves)
    }

    override fun getType(uri: Uri): String? {
        return null
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        return null
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {
        return 0
    }

    override fun update(
        uri: Uri, values: ContentValues?, selection: String?, selectionArgs: Array<out String>?
    ): Int {
        return 0
    }
}