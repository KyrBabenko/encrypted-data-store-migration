# Migrating from Encrypted SharedPreferences to Jetpack DataStore

![Kotlin](https://img.shields.io/badge/Kotlin-1.8.0-blueviolet)
![Android](https://img.shields.io/badge/Android-DataStore-green)
![Encryption](https://img.shields.io/badge/Security-Encryption-orange)

This repository contains the code accompanying the article  
[Migrating from Encrypted SharedPreferences to Jetpack DataStore](https://medium.com/@kyr.babenko/migrating-from-encrypted-sharedpreferences-to-jetpack-datastore-d4bb20f609a6).  
It showcases a step-by-step transition from **Encrypted SharedPreferences** to **Jetpack DataStore**.

## 📌 About the Project
Migrating from **Encrypted SharedPreferences** to **Jetpack DataStore** improves data security and performance in Android apps.  
This repository demonstrates each migration step in different branches:

1. **[branch-1-initial-setup](https://github.com/KyrBabenko/encrypted-data-store-migration/tree/branch-1-initial-setup)** – Initial implementation using `EncryptedSharedPreferences`.
2. **[branch-2-introduce-datastore](https://github.com/KyrBabenko/encrypted-data-store-migration/tree/branch-2-introduce-datastore)** – Introduced `DataStore` without migration.
3. **[branch-3-migration-setup](https://github.com/KyrBabenko/encrypted-data-store-migration/tree/branch-3-migration-setup)** – Set up migration logic from `EncryptedSharedPreferences` to `DataStore`.
4. **[branch-4-migration-implementation](https://github.com/KyrBabenko/encrypted-data-store-migration/tree/branch-4-migration-implementation)** – Implemented migration with error handling.
5. **[branch-5-final-version](https://github.com/KyrBabenko/encrypted-data-store-migration/tree/branch-5-final-version)** – Final version with tests and optimized data handling.

## 🚀 Final Version (branch `branch-5-final-version`)
The final version includes:
- ✅ Fully implemented migration from `EncryptedSharedPreferences` to `Jetpack DataStore`.
- ✅ Improved error handling during migration.
- ✅ Optimized data reading and writing processes.

### 📂 Project Structure
```
📂 app
 ├── 📂 data
 │    ├── SharedPrefsManager.kt  // Handles EncryptedSharedPreferences
 │    ├── DataStoreManager.kt    // Handles Jetpack DataStore
 │    ├── DataMigration.kt       // Migration logic
 │    └── DataModel.proto        // Proto DataStore schema
 ├── 📂 ui
 │    ├── MainActivity.kt        // Demonstrates data usage
 │    └── SettingsFragment.kt    // Settings screen using DataStore
 ├── 📂 tests
 │    ├── DataStoreTest.kt       // Unit tests for DataStore
 │    ├── MigrationTest.kt       // Unit tests for migration logic
```

## 🔧 Installation & Running
1. Clone the repository:
   ```sh
   git clone https://github.com/KyrBabenko/encrypted-data-store-migration.git
   ```
2. Checkout the final branch:
   ```sh
   git checkout branch-5-final-version
   ```
3. Open the project in **Android Studio** and run it.

## 🛠️ Technologies Used
- **Kotlin**
- **Jetpack DataStore (Proto DataStore)**
- **EncryptedSharedPreferences**
- **Coroutines & Flow**

## 📖 Read the Article
A detailed migration guide is available in the [Medium article](https://medium.com/@kyr.babenko/migrating-from-encrypted-sharedpreferences-to-jetpack-datastore-d4bb20f609a6).

## 📝 License
This project is distributed under the **MIT** license.
