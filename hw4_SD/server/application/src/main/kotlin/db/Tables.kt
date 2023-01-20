package db

import db.connection.TableHolder
import org.jetbrains.exposed.dao.id.IntIdTable


object Tables : TableHolder {
    object Planners : IntIdTable() {
        val name = varchar("name", 64)
        val description = varchar("description", 512).nullable()
        val archived = bool("archived").default(false)
    }

    object Tasks : IntIdTable() {
        val name = varchar("name", 128)
        val description = text("description").nullable()
        val done = bool("done").default(false)

        @Suppress("unused")
        val plannerId = reference("plannerId", Planners)
    }
}
