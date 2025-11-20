package recipe

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.mx7ueb.databinding.RecipeDetailBinding

class RecipeDetailActivity : AppCompatActivity() {

    lateinit var binding: RecipeDetailBinding
    lateinit var db: RecipeDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        db = RecipeDatabase.buildDb(this)

        binding = RecipeDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val recipeID = intent.getLongExtra(EXTRA_ID, -1L)

        val ingredients: ArrayList<String> =
            intent.getStringArrayListExtra(EXTRA_INGREDIENTS) ?: arrayListOf()

        val ingredientsText = ingredients.joinToString(
            separator = "\n",
            transform = { item -> "- $item" }
        )
        binding.ingredients.text = "Hozzávalók:\n$ingredientsText"

        val recipe = db.recipeDao().getDataById(recipeID)

        binding.name.text = recipe?.name
        binding.difficulty.text = "Nehézség: ${recipe?.difficulty}"
        binding.ingredients.text = ingredientsText
        binding.description.text = recipe?.description
    }

    companion object {
        const val EXTRA_ID = "id"
        const val EXTRA_INGREDIENTS = "ingredients"
    }
}