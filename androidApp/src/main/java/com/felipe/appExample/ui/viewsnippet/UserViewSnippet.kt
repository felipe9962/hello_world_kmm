package com.felipe.appExample.ui.viewsnippet

import com.felipe.appExample.ui.DefaultPage
import com.felipe.appExample.ui.DefaultViewSnippet
import com.felipe.db.Database_initializerQueries
import java.util.*

class UserViewSnippet(
    itemsOnPage: Int,
    pages: Int,
    private val userDatabase: Database_initializerQueries
) : DefaultViewSnippet<Int, Int, DefaultPage<Int, Int>>(itemsOnPage * pages, itemsOnPage) {

    private var nameLike: String? = null

    override fun loadPage(num: Int): DefaultPage<Int, Int>? {
        var numPage = num
        if (numPage < 0)
            numPage = 0
        val offset: Int = pageMaxSize * numPage

        val users: List<Long>
        if (nameLike == null || nameLike!!.isEmpty())
            users = try {
                userDatabase.getUserViewSnippet(pageMaxSize.toLong(), offset.toLong())
                    .executeAsList()
            } catch (e: Exception) {
                throw RuntimeException(e)
            }
        else
            users = try {
                userDatabase.getUserLikeNamePaged(
                    nameLike!!,
                    pageMaxSize.toLong(),
                    offset.toLong()
                ).executeAsList()
            } catch (e: Exception) {
                throw RuntimeException(e)
            }

        if (users.isEmpty())
            return null

        val array = Array(users.size) { i -> users[i].toInt() }
        val page: DefaultPage<Int, Int> = DefaultPage(pageMaxSize, offset, array, array)
        PAGES[numPage] = page
        return page
    }

    override val countFromDataSource: Int
        get() = if (nameLike == null || nameLike!!.isEmpty()) userDatabase.getUserCount()
            .executeAsOne().toInt() else userDatabase.getUserLikeNameCount(nameLike!!)
            .executeAsOne()
            .toInt()

    fun setNameLike(nameLike: String?) {
        var finalNameLike = nameLike
        if (finalNameLike != null && finalNameLike.isNotEmpty())
            finalNameLike = "%$nameLike%"
        this.nameLike = finalNameLike
    }
}