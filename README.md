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
4. **[branch-4-migration-implementation](https://github.com/KyrBabenko/encrypted-data-store-migration/tree/branch-4-migration-implementation)** – Implemented migration logic.
5. **[branch-5-final-version](https://github.com/KyrBabenko/encrypted-data-store-migration/tree/branch-5-final-version)** – Final version with a complete migration process.

## 🚀 Final Version (branch `branch-5-final-version`)
The final version includes:
- ✅ Fully implemented migration from `EncryptedSharedPreferences` to `Jetpack DataStore`.
- ✅ Secure encryption using **AES-GCM**.
- ✅ Proper key management with **MasterKey** and a keyset similar to **Tink** library.

### 🔹 How to Run the Migration Process
1. **Initial Setup**
    - Open `AppComponent.kt` and set the preferences provider to `SharedPreferencesProvider`:
      ```kotlin
      mainActivity.preferences = sharedPreferencesProvider
      ```
    - Run the application and enter some data to be stored.

2. **Switch to DataStore with Migration**
    - Open `AppComponent.kt` and replace the preferences provider with `DataStorePreferencesProvider`:
      ```kotlin
      mainActivity.preferences = dataStorePreferencesProvider
      ```
    - Run the application again. The stored data will be migrated to `Jetpack DataStore`.

3. **Verify Migration Success**
    - Check the logs using the tag `"TAG11"`. The logs will show which data has been successfully migrated to `DataStore`.

### 🔐 Security Details (branch `branch-5-final-version`)
The final version ensures secure data storage by implementing:
- **AES-GCM encryption** for securing stored values.
- **MasterKey creation** for key management, similar to how **Tink** library handles encryption.
- **AES-256 encryption for key protection**, ensuring that encryption keys remain secure.
- **Encrypted keyset storage**, making sure keys are safely managed and protected.

#### 🔹 Key & Value Encryption Details
- **Keys encryption (AES-256-SIV-CMAC)**:
    - Keys are encrypted using **AES-256-SIV-CMAC**, where the IV (initialization vector) is **deterministically computed**.
    - This means that for the same input, the ciphertext will always be the same.
    - The underlying mode is **AES/CTR/NoPadding**, a stream cipher mode **without built-in integrity checks**.

- **Values encryption (AES-256-GCM)**:
    - Values are encrypted using **AES-256-GCM**, where the IV is **randomly generated** for each encryption operation.
    - This ensures that even for the same input, the ciphertext will always be different.
    - The underlying mode is **AES/GCM/NoPadding**, which includes **built-in authentication (tag)** to prevent data tampering.

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
- **AES-256-SIV-CMAC for key encryption**
- **AES-256-GCM for value encryption**
- **Coroutines & Flow**

## 📖 Read the Article
A detailed migration guide is available in the [Medium article](https://medium.com/@kyr.babenko/migrating-from-encrypted-sharedpreferences-to-jetpack-datastore-d4bb20f609a6).

## 📝 License
This project is released under an **open-source license** with no restrictions.
~~~~