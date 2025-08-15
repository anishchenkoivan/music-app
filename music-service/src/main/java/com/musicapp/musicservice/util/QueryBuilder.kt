package com.musicapp.musicservice.util

import co.elastic.clients.elasticsearch._types.query_dsl.Query
import org.springframework.data.elasticsearch.client.elc.NativeQuery
import org.springframework.stereotype.Component

@Component
class QueryBuilder {
    fun singleFieldQuery(field: String, value: String): NativeQuery{
        val query: Query = Query.of { q ->
            q.match { m->
                m.field(field)
                    .query(value)
                    .fuzziness("AUTO")
            }
        }

        return NativeQuery.builder().withQuery(query).build()
    }
}