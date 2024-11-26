package bg.zahov.app.data.model.state

/**
 * Data class representing the state of the shutdown process.
 *
 * @property shutDown Boolean flag indicating whether the shutdown process has been initiated.
 * @property navigateToShuttingDown Boolean flag indicating whether the app needs to navigate to the shutting down screen.
 */
data class ShutDownData(
    val shutDown: Boolean = false,
    val navigateToShuttingDown: Boolean = false
)