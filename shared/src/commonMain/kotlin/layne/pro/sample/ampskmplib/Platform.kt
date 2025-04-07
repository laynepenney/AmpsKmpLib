package layne.pro.sample.ampskmplib

public interface Platform {
    public val name: String
}

public expect fun getPlatform(): Platform
