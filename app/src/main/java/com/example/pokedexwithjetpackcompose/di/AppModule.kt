package com.example.pokedexwithjetpackcompose.di

import com.example.pokedexwithjetpackcompose.data.remote.PokeApi
import com.example.pokedexwithjetpackcompose.repository.PokemonRepository
import com.example.pokedexwithjetpackcompose.util.Constants.BASE_URL
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun providePokemonRepository(
        api : PokeApi
    ) = PokemonRepository(api)

    @Singleton
    @Provides
    fun providerPokeApi() : PokeApi{
        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BASE_URL)
            .build()
            .create(PokeApi::class.java)
    }

}