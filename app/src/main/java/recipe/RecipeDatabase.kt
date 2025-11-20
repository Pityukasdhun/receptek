package recipe

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [Recipe::class],
    version = 1
)
abstract class RecipeDatabase : RoomDatabase() {

    companion object {
        fun buildDb(context: Context) =
            Room.databaseBuilder(context, RecipeDatabase::class.java, "recipe.db")
                .allowMainThreadQueries()
                .build()
    }

    abstract fun recipeDao(): RecipeDao
}

@Dao
interface RecipeDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(data: List<Recipe>)

    @Query("SELECT * FROM recipe")
    fun getData(): List<Recipe>

    @Query("SELECT * FROM recipe WHERE id = :id")
    fun getDataById(id: Long): Recipe?

}