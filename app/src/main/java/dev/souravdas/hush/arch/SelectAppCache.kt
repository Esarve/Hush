package dev.souravdas.hush.arch

import dev.sourav.base.datastore.DataStoreManager
import dev.souravdas.hush.models.AppLog
import dev.souravdas.hush.models.HushConfig
import dev.souravdas.hush.models.SelectedApp
import dev.souravdas.hush.others.Constants
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
class SelectAppCache @Inject constructor(private val repository: SelectAppRepository, private val logRepository: AppLogRepository, dataStoreManager: DataStoreManager) {
    private val selectedAppsFlow = repository.getSelectedAppsWithFlow()
    private val databaseUpdatesFlow = repository.getDBUpdatesWithFlow()

    private val dsIsDnd = dataStoreManager.getBooleanValueAsFlow(Constants.DS_DND)
    private val dsIsRemoveExpired = dataStoreManager.getBooleanValueAsFlow(Constants.DS_DELETE_EXPIRE)
    private val dsIsNotifyMute = dataStoreManager.getBooleanValueAsFlow(Constants.DS_NOTIFY_MUTE)

    fun getSelectedApps(): Flow<List<SelectedApp>> = combine(selectedAppsFlow, databaseUpdatesFlow) { selectedApps, _ ->
        selectedApps
    }

    fun getConfig()= combine(dsIsDnd, dsIsRemoveExpired,dsIsNotifyMute) {a,b,c ->
        HushConfig(a,b,c)
    }

    suspend fun logNotification(appLog: AppLog){
        logRepository.insertLog(appLog)
    }

}