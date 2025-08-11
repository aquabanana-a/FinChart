package com.banana.finchart.model.di

import com.banana.finchart.model.snapshot.SnapshotManager
import com.banana.finchart.model.snapshot.SnapshotManagerImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(ViewModelComponent::class)
abstract class ModelModule {

    @Binds
    abstract fun bindSnapshotManager(
        impl: SnapshotManagerImpl
    ): SnapshotManager
}