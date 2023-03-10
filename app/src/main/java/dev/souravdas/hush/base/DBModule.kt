package dev.souravdas.hush.base

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.souravdas.hush.arch.MainActivityVM
import dev.souravdas.hush.arch.SelectAppRepository
import dev.souravdas.hush.arch.SelectedAppDao
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DBModule {
    @Provides
    @Singleton
    fun provideHushDB(@ApplicationContext context: Context): HushDB {
        return androidx.room.Room.databaseBuilder(
            context,
            HushDB::class.java,
            "hush_db"
        ).build()
    }


    @Provides
    fun provideSelectAppDao(hushDB: HushDB): SelectedAppDao {
        return hushDB.selectAppDao()
    }

    @Provides
    fun provideSelectAppRepository(selectedAppDao: SelectedAppDao): SelectAppRepository {
        return SelectAppRepository(selectedAppDao)
    }

}
