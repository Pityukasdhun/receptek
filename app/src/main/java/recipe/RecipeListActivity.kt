package recipe

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mx7ueb.databinding.RecipeListBinding
import com.example.mx7ueb.databinding.RowRecipeBinding
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Ignore
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RecipeListActivity : AppCompatActivity() {

    lateinit var binding: RecipeListBinding

    val api: RecipeService by lazy { RecipeNetwork().recipeService }

    lateinit var db: RecipeDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = RecipeListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = RecipeDatabase.buildDb(this)

        binding.recyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)


        api.loadRecipes().enqueue(object:Callback<List<Recipe>> {
            override fun onResponse(call: Call<List<Recipe>?>, response: Response<List<Recipe>?>) {
                if (response.isSuccessful) {
                    val list = response.body() ?: emptyList()
                    db.recipeDao().insert(list)
                    updateAdapter(list)
                } else {
                    // vmi hiba tortent mutassuk meg a usernek.
                    val list = db.recipeDao().getData()
                    updateAdapter(list)
                }
            }

            override fun onFailure(call: Call<List<Recipe>?>, t: Throwable) {
                val list = db.recipeDao().getData()
                updateAdapter(list)
            }
        })
    }
    fun updateAdapter(list: List<Recipe>) {
        binding.recyclerView.adapter = RecipeAdapter(
            list,
            onItemClick = {
                val intent = Intent(this, RecipeDetailActivity::class.java)
                intent.putExtra(RecipeDetailActivity.EXTRA_ID, it.id)

                intent.putStringArrayListExtra(
                    RecipeDetailActivity.EXTRA_INGREDIENTS,
                    ArrayList(it.ingredients)
                )

                startActivity(intent)
            }
        )
    }
}


class RecipeAdapter(val list: List<Recipe>, val onItemClick: (Recipe) -> Unit) :
    RecyclerView.Adapter<RecipeViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        val binding = RowRecipeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RecipeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        val item = list[position]
        holder.binding.name.text = item.name
        holder.binding.duration.text = "Elkészítési idő: ${item.duration} perc" // lehetne context
        holder.binding.difficulty.text = "Nehézség: ${item.difficulty}"
        holder.binding.root.setOnClickListener {
            onItemClick(item)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}

class RecipeViewHolder(val binding: RowRecipeBinding) : RecyclerView.ViewHolder(binding.root)

@Entity
data class Recipe(
    @PrimaryKey val id: Long,
    val name: String,
    val duration: Int,
    val difficulty: String,
    val description: String,

    @Ignore val ingredients: List<String> = emptyList()
) {
    // Másodlagos konstruktor a Room miatt
    constructor(
        id: Long,
        name: String,
        duration: Int,
        difficulty: String,
        description: String
    ) : this(id, name, duration, difficulty, description, emptyList())
}