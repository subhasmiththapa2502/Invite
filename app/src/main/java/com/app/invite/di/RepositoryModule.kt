package com.app.invite.di


import com.app.invite.network.InviteServiceAPI
import com.app.invite.repository.MainRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object RepositoryModule {

    @Provides
    @Singleton
    fun provideMainRepo(
        servicesApi: InviteServiceAPI,
        dispatcher: CoroutineDispatcher
    ): MainRepository {
        return MainRepository(servicesApi)
    }

}