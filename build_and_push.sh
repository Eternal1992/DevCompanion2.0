#!/data/data/com.termux/files/usr/bin/bash

echo "🔄 Committing and pushing changes to GitHub..."

# Navigate to project directory
cd ~/storage/downloads/termux/DevCompanion2.0 || exit

# Add all changes
git add .

# Commit with timestamp message
git commit -m "🔄 Auto commit from Termux on $(date '+%Y-%m-%d %H:%M:%S')"

# Push to GitHub
git push origin main

echo "✅ Changes pushed to GitHub."

echo "🚀 Triggering GitHub Actions build..."
echo "Wait a few minutes and check the 'Actions' tab on your GitHub repo for the new APK."
