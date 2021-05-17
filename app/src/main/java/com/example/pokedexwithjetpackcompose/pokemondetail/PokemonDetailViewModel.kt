package com.example.pokedexwithjetpackcompose.pokemondetail

import androidx.lifecycle.ViewModel
import com.example.pokedexwithjetpackcompose.data.remote.response.Pokemon
import com.example.pokedexwithjetpackcompose.repository.PokemonRepository
import com.example.pokedexwithjetpackcompose.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PokemonDetailViewModel @Inject constructor(
    private val repository: PokemonRepository
)  : ViewModel()
{

    suspend fun getPokemonInfo(pokemonName:String) : Resource<Pokemon>{
        return repository.getPokemonInfo(pokemonName)
    }

}