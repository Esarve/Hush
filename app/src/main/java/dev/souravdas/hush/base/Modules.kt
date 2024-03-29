package dev.souravdas.hush.base

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.souravdas.hush.HushApp.Companion.context
import dev.souravdas.hush.arch.AppLogDao
import dev.souravdas.hush.arch.AppLogRepository
import dev.souravdas.hush.arch.SelectAppRepository
import dev.souravdas.hush.arch.SelectedAppDao
import dev.souravdas.hush.others.NotificationHelper
import dev.souravdas.hush.others.NotifyUtils
import dev.souravdas.hush.others.Utils
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object Modules {
    @Provides
    @Singleton
    fun provideHushDB(@ApplicationContext context: Context): HushDB {
        return Room.databaseBuilder(
            context,
            HushDB::class.java,
            "hush_db"
        )   .addMigrations(MIGRATION_1_2)
            .build()
    }


    @Provides
    fun provideSelectAppDao(hushDB: HushDB): SelectedAppDao {
        return hushDB.selectAppDao()
    }

    @Provides
    fun provideAppLogDao(hushDB: HushDB): AppLogDao {
        return hushDB.appLogDao()
    }

    @Provides
    fun provideSelectAppRepository(selectedAppDao: SelectedAppDao): SelectAppRepository {
        return SelectAppRepository(selectedAppDao)
    }

    @Provides
    fun providesAppLogRepository(appLogDao: AppLogDao): AppLogRepository {
        return AppLogRepository(appLogDao)
    }

    @Provides
    fun provideUtils(): Utils {
        return Utils()
    }

    @Provides
    fun provideNotificationHelper(): NotificationHelper{
        return NotificationHelper(context)
    }
    @Provides
    fun provideNotifyUtils(helper: NotificationHelper): NotifyUtils{
        return NotifyUtils(helper)
    }
}
