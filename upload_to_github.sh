#!/bin/bash

# GitHub Repository Upload Script for AI Tarot Reader
# Run this script after creating a GitHub repository

echo "=== AI Tarot Reader GitHub Upload Script ==="

# Check if GitHub repository URL is provided
if [ -z "$1" ]; then
    echo "Usage: $0 <github-repository-url>"
    echo "Example: $0 https://github.com/yourusername/AI-Tarot-Reader.git"
    exit 1
fi

REPO_URL=$1

# Add the remote repository
echo "Adding GitHub remote..."
git remote add origin $REPO_URL

# Rename branch from master to main (GitHub standard)
echo "Renaming branch to 'main'..."
git branch -M main

# Push to GitHub
echo "Pushing to GitHub..."
git push -u origin main

echo "=== Upload Complete! ==="
echo "Your AI Tarot Reader project has been successfully uploaded to:"
echo $REPO_URL
echo ""
echo "Next steps:"
echo "1. Visit your GitHub repository to verify the upload"
echo "2. Add a README.md description if not already present"
echo "3. Set up GitHub Pages for documentation (optional)"