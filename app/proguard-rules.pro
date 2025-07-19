# Keep application classes
-keep class com.devcompanion.** { *; }

# Keep ViewBinding generated classes
-keep class **.databinding.* { *; }

# Uncomment if you start using Firebase or any SDK that requires ProGuard rules
#-keep class com.google.firebase.** { *; }