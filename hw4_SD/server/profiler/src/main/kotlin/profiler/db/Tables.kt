package profiler.db

import db.connection.TableHolder
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Table

object Tables : TableHolder {
    object ProfilerMethods : IntIdTable() {
        val packageName = varchar("packageName", 64)
        val declaringClassName = varchar("declaringClassName", 64)
        val methodName = varchar("methodName", 64)

        init {
            uniqueIndex("profiler_methods_pk", packageName, declaringClassName, methodName)
        }
    }

    object MethodInvocations : Table() {
        val methodId = reference("methodId", ProfilerMethods)
        val durationMillis = double("durationMillis")
    }
}
