package app.kaito_dogi.smopin.legacy

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform
