/**
 * Precompiled [gradlebuild.build-environment.settings.gradle.kts][Gradlebuild_build_environment_settings_gradle] script plugin.
 *
 * @see Gradlebuild_build_environment_settings_gradle
 */
public
class Gradlebuild_buildEnvironmentPlugin : org.gradle.api.Plugin<org.gradle.api.initialization.Settings> {
    override fun apply(target: org.gradle.api.initialization.Settings) {
        try {
            Class
                .forName("Gradlebuild_build_environment_settings_gradle")
                .getDeclaredConstructor(org.gradle.api.initialization.Settings::class.java, org.gradle.api.initialization.Settings::class.java)
                .newInstance(target, target)
        } catch (e: java.lang.reflect.InvocationTargetException) {
            throw e.targetException
        }
    }
}
