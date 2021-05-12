package com.example.pokedexwithjetpackcompose.pokemonList

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.intl.Locale
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.palette.graphics.Palette
import com.example.pokedexwithjetpackcompose.data.models.PokedexListEntry
import com.example.pokedexwithjetpackcompose.repository.PokemonRepository
import com.example.pokedexwithjetpackcompose.util.Constants.PAGE_SIZE
import com.example.pokedexwithjetpackcompose.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.http.Query
import javax.inject.Inject

@HiltViewModel
class PokemonListViewModel @Inject constructor(
    private val repository : PokemonRepository
): ViewModel() {


    private var curPage = 0

    var pokemonList = mutableStateOf<List<PokedexListEntry>>(listOf())
    var loadError = mutableStateOf("")
    var isloading = mutableStateOf(false)
    var endReached = mutableStateOf(false)

    private var cachedPokemonList =  listOf<PokedexListEntry>()
    private var isSearchStarting = true
    var isSearching = mutableStateOf(false)

    init {
        loadPokemonPaginated()
    }

    fun searchPokemonList(query: String){
        val listToSearch = if(isSearchStarting){
            pokemonList.value
        }else{
            cachedPokemonList
        }
        viewModelScope.launch(Dispatchers.Default) {
            if(query.isEmpty()){
                pokemonList.value = cachedPokemonList
                isSearching.value = false
                isSearchStarting = true
                return@launch
            }
            val results = listToSearch.filter{
                it.pokemonName.contains(query.trim(),ignoreCase = true ||
                it.number.toString() ==query.trim())
            }
            if(isSearchStarting){
                cachedPokemonList = pokemonList.value
                isSearchStarting = false
            }
            pokemonList.value = results
            isSearching.value = true
        }
    }

    fun loadPokemonPaginated(){
        viewModelScope.launch {
            isloading.value = true
            val result = repository.getPokemonList(PAGE_SIZE,curPage * PAGE_SIZE)
            when(result){
                is Resource.Success -> {
                    endReached.value = curPage * PAGE_SIZE >= result.data!!.count
                    val pokedexEntries = result.data.results.mapIndexed{index,entry ->
                        val number = if(entry.url.endsWith("/")){
                            entry.url.dropLast(1).takeLastWhile { it.isDigit() }
                        }else{
                            entry.url.takeLastWhile { it.isDigit() }
                        }

                        val url = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/${number}.png"
                        PokedexListEntry(entry.name.capitalize(java.util.Locale.ROOT), url, number.toInt())
                    }
                    curPage++
                    loadError.value = ""
                    isloading.value = false
                    pokemonList.value += pokedexEntries
            }
                is Resource.Error -> {
                    loadError.value = result.message!!
                    isloading.value = false
                }
            }
        }
    }

    fun calcDominantColor(drawable : Drawable,onFinish : (Color) -> Unit)
    {
        val bmp = (drawable as BitmapDrawable).bitmap.copy(Bitmap.Config.ARGB_8888,true)

        Palette.from(bmp).generate{pallette->
            pallette?.darkMutedSwatch?.rgb?.let { colorValue ->
                onFinish(Color(colorValue))
            }
        }
    }
}