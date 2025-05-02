package vcmsa.projects.loginpage.data

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import vcmsa.projects.loginpage.CategoryElements.Category
import vcmsa.projects.loginpage.CategoryElements.CategoryDao
import vcmsa.projects.loginpage.ExpenseElements.Expense

// So this is where we define the database and tell Room what tables (entities) we’re working with
@Database(entities = [Category::class, Expense::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    // These are like the gateways to each table ,they give us access to run queries and stuff
    abstract fun categoryDao(): CategoryDao
    abstract fun expenseDao(): ExpenseDao

    companion object {
        // Making sure this stays the one and only instance of the database (no duplicates)
        @Volatile
        private var INSTANCE: AppDatabase? = null

        private const val TAG = "AppDatabase"  // Tag for log messages

        // This function is called whenever we need to use the database
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                // If we reach this point, it means the database hasn’t been created yet, so let’s sort that out
                Log.d(TAG, "Initializing new database instance...")

                val instance = Room.databaseBuilder(
                    context.applicationContext,  // Just using app context to avoid leaks
                    AppDatabase::class.java,     // Pointing to this database class
                    "app_database"               // This is the actual name of the database file
                )
                    .fallbackToDestructiveMigration() // If there’s a version mismatch, just reset the DB (use with care)
                    .addCallback(object : RoomDatabase.Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            // This gets called once when the DB is first made
                            Log.i(TAG, "Database created successfully")
                        }

                        override fun onOpen(db: SupportSQLiteDatabase) {
                            super.onOpen(db)
                            // This fires every time the DB is opened
                            Log.i(TAG, "Database opened")
                        }
                    })
                    .build()

                // Save it so we can reuse it next time
                INSTANCE = instance
                Log.d(TAG, "Database instance assigned")

                instance
            }
        }
    }
}
