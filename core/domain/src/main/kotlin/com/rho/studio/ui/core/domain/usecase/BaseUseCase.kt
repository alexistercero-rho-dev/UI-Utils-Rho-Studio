/**
 * ██████╗ ██╗  ██╗ ██████╗     ███████╗████████╗██╗   ██╗██████╗ ██╗ ██████╗
 * ██╔══██╗██║  ██║██╔═══██╗    ██╔════╝╚══██╔══╝██║   ██║██╔══██╗██║██╔═══██╗
 * ██████╔╝███████║██║   ██║    ███████╗   ██║   ██║   ██║██║  ██║██║██║   ██║
 * ██╔══██╗██╔══██║██║   ██║    ╚════██║   ██║   ██║   ██║██║  ██║██║██║   ██║
 * ██║  ██║██║  ██║╚██████╔╝    ███████║   ██║   ╚██████╔╝██████╔╝██║╚██████╔╝
 * ╚═╝  ╚═╝╚═╝  ╚═╝ ╚═════╝     ╚══════╝   ╚═╝    ╚═════╝ ╚═════╝ ╚═╝ ╚═════╝
 *
 * ==============================================================================================
 * File:         BaseUseCase.kt
 * Author:       Alexis Tercero
 * Email:        alexis.tercero@rho.studio
 * Date:         2026-08-05
 * ==============================================================================================
 * Description: Architectural foundation for all business logic components (Use Cases/Interactors).
 *              Provides a standardized execution pattern for domain transactions, ensuring
 *              thread safety, consistent error handling, and result wrapping.
 * ==============================================================================================
 */
package com.rho.studio.ui.core.domain.usecase

import com.rho.studio.ui.core.domain.model.Result
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * # BaseUseCase
 *
 * Base class for all Use Cases in the Domain layer. A Use Case (or Interactor) represents
 * a single business transaction or user action.
 *
 * ### Key Responsibilities:
 * 1. **Thread Management**: Automatically offloads execution to a background dispatcher
 *    (defaults to [Dispatchers.IO]) using `withContext`.
 * 2. **Standardized Execution**: Uses the `invoke` operator to allow Use Cases to be
 *    called as functions (e.g., `loginUseCase(credentials)`).
 * 3. **Consistent Error Handling**: Wraps the execution in a `try-catch` block, catching
 *    any [Exception] and transforming it into a [Result.Error].
 * 4. **Result Wrapping**: Automatically wraps successful execution into a [Result.Success].
 *
 * ### Generics:
 * @param P The Input Parameter type required by the Use Case. Use `Unit` if no input is needed.
 * @param R The Output Result type. Must be a non-nullable type ([Any]).
 *
 * ### Implementation:
 * Subclasses must implement the [execute] method to define the specific business logic.
 *
 * @property coroutineDispatcher The dispatcher where the logic will run. Defaults to [Dispatchers.IO].
 */
abstract class BaseUseCase<in P, out R : Any>(private val coroutineDispatcher: CoroutineDispatcher = Dispatchers.IO) {

    suspend operator fun invoke(parameters: P): Result<R> {
        return try {
            withContext(coroutineDispatcher) {
                execute(parameters).let {
                    Result.Success(it)
                }
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    @Throws(RuntimeException::class)
    protected abstract suspend fun execute(parameters: P): R
}
