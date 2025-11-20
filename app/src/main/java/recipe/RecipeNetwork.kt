package recipe

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

class RecipeNetwork {

    val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("https://raw.githubusercontent.com/udemx/hr-resources/refs/heads/master/")
            .client(
                OkHttpClient.Builder()
                    .addInterceptor(HttpLoggingInterceptor().apply {
                        setLevel(HttpLoggingInterceptor.Level.BODY)
                    }).build()
            )
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val recipeService: RecipeService by lazy {
        retrofit.create(RecipeService::class.java)
    }
}


interface RecipeService {

    @GET("recipe.json")
    fun loadRecipes(): Call<List<Recipe>>
}
