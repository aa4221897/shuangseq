# Retrofit
-keep class com.example.lotteryprediction.data.remote.** { *; }
-keep class com.example.lotteryprediction.network.** { *; }

# Room
-keep class * extends androidx.room.RoomDatabase
-keep class * extends androidx.room.Entity

# Hilt
-keep class * extends dagger.hilt.android.internal.managers.ViewComponentManager { *; }
-keep class * extends dagger.hilt.internal.GeneratedComponentManagerHolder { *; }
-keep class dagger.hilt.internal.aggregatedroot.codegen.* { *; }

# DeepSeek API models
-keep class com.example.lotteryprediction.network.DeepSeekResponse { *; }
-keep class com.example.lotteryprediction.network.Message { *; }
-keep class com.example.lotteryprediction.network.Choice { *; }
-keep class com.example.lotteryprediction.network.Usage { *; }
