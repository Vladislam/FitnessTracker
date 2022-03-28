package com.example.fitnesstracker.di

import com.example.fitnesstracker.data.RunDao
import com.example.fitnesstracker.repositories.MainRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.realm.Realm

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    fun provideDao(realm: Realm): RunDao = RunDao(realm)

    @Provides
    fun provideRealm(): Realm = Realm.getDefaultInstance()

    @Provides
    fun provideMainRepository(dao: RunDao): MainRepository = MainRepository(dao)

}