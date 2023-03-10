package dev.souravdas.hush.arch

import dev.souravdas.hush.models.SelectedApp
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by Sourav
 * On 3/10/2023 6:34 PM
 * For Hush!
 */
@Singleton
class SelectAppCache @Inject constructor(private val repository: SelectAppRepository) {
    private val selectedAppsFlow = repository.getSelectedAppsWithFlow()
    private val databaseUpdatesFlow = repository.getDBUpdatesWithFlow()

    fun getSelectedApps(): Flow<List<SelectedApp>> = combine(selectedAppsFlow, databaseUpdatesFlow) { selectedApps, _ ->
        selectedApps
    }
}