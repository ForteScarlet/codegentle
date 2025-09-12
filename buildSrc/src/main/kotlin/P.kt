object P {
    const val VERSION = "0.0.1"
    const val NEXT_VERSION = "0.0.2"
    const val HOMEPAGE = "https://github.com/ForteScarlet/codegentle"
}

const val IS_SNAPSHOT_PROPERTY = "isSnapshot"
const val IS_SNAPSHOT_ENV = "IS_SNAPSHOT"

const val IS_LOCAL_PROPERTY = "isLocal"
const val IS_LOCAL_ENV = "IS_LOCAL"

fun currentVersion(): String {
    val snapProp = System.getProperty(IS_SNAPSHOT_PROPERTY)?.toBoolean() ?: false
    val snapEnv = System.getenv(IS_SNAPSHOT_ENV)?.toBoolean() ?: false

    return if (snapProp || snapEnv) {
        "${P.NEXT_VERSION}-SNAPSHOT"
    } else {
        P.VERSION
    }
}

fun isLocal(): Boolean {
    val localProp = System.getProperty(IS_LOCAL_PROPERTY)?.toBoolean() ?: false
    val localEnv = System.getenv(IS_LOCAL_ENV)?.toBoolean() ?: false

    return localProp || localEnv
}
